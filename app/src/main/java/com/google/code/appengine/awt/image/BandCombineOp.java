/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Oleg V. Khaschansky
 *
 * @date: Sep 20, 2005
 */

package com.google.code.appengine.awt.image;

import java.util.Arrays;

import org.apache.harmony.awt.gl.AwtImageBackdoorAccessor;
import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.*;
import com.google.code.appengine.awt.geom.Point2D;
import com.google.code.appengine.awt.geom.Rectangle2D;
import com.google.code.appengine.awt.image.ComponentSampleModel;
import com.google.code.appengine.awt.image.DataBuffer;
import com.google.code.appengine.awt.image.ImagingOpException;
import com.google.code.appengine.awt.image.PixelInterleavedSampleModel;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RasterOp;
import com.google.code.appengine.awt.image.SampleModel;
import com.google.code.appengine.awt.image.SinglePixelPackedSampleModel;
import com.google.code.appengine.awt.image.WritableRaster;


public class BandCombineOp implements RasterOp {
    static final int offsets3c[] = {16, 8, 0};
    static final int offsets4ac[] = {16, 8, 0, 24};
    static final int masks3c[] = {0xFF0000, 0xFF00, 0xFF};
    static final int masks4ac[] = {0xFF0000, 0xFF00, 0xFF, 0xFF000000};
    private static final int piOffsets[] = {0, 1, 2};
    private static final int piInvOffsets[] = {2, 1, 0};

    private static final int TYPE_BYTE3C = 0;
    private static final int TYPE_BYTE4AC = 1;
    private static final int TYPE_USHORT3C = 2;
    private static final int TYPE_SHORT3C = 3;

    private int mxWidth;
    private int mxHeight;
    private float matrix[][];
    private RenderingHints rHints;

    static {
        // XXX - todo
        //System.loadLibrary("imageops");
    }

    public BandCombineOp(float matrix[][], RenderingHints hints) {
        this.mxHeight = matrix.length;
        this.mxWidth = matrix[0].length;
        this.matrix = new float[mxHeight][mxWidth];

        for (int i=0; i<mxHeight; i++){
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, mxWidth);
        }

        this.rHints = hints;
    }

    public final RenderingHints getRenderingHints(){
        return this.rHints;
    }

    public final float[][] getMatrix() {
        float res[][] = new float[mxHeight][mxWidth];

        for (int i=0; i<mxHeight; i++) {
            System.arraycopy(matrix[i], 0, res[i], 0, mxWidth);
        }

        return res;
    }

    public final Point2D getPoint2D (Point2D srcPoint, Point2D dstPoint) {
        if (dstPoint == null) {
            dstPoint = new Point2D.Float();
        }

        dstPoint.setLocation(srcPoint);
        return dstPoint;
    }

    public final Rectangle2D getBounds2D(Raster src){
        return src.getBounds();
    }

    public WritableRaster createCompatibleDestRaster (Raster src) {
        int numBands = src.getNumBands();
        if (mxWidth != numBands && mxWidth != (numBands+1) || numBands != mxHeight) {
            // awt.254=Number of bands in the source raster ({0}) is
            //          incompatible with the matrix [{1}x{2}]
            throw new IllegalArgumentException(Messages.getString("awt.254", //$NON-NLS-1$
                    new Object[]{numBands, mxWidth, mxHeight}));
        }

        return src.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
    }

    public WritableRaster filter(Raster src, WritableRaster dst) {
        int numBands = src.getNumBands();

        if (mxWidth != numBands && mxWidth != (numBands+1)) {
            // awt.254=Number of bands in the source raster ({0}) is
            //          incompatible with the matrix [{1}x{2}]
            throw new IllegalArgumentException(
                    Messages.getString("awt.254", //$NON-NLS-1$
                    new Object[]{numBands, mxWidth, mxHeight}));
        }

        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        } else if (dst.getNumBands() != mxHeight) {
            // awt.255=Number of bands in the destination raster ({0}) is incompatible with the matrix [{1}x{2}]
            throw new IllegalArgumentException(Messages.getString("awt.255", //$NON-NLS-1$
                    new Object[]{dst.getNumBands(), mxWidth, mxHeight}));
        }

        // XXX - todo
        //if (ippFilter(src, dst) != 0)
        if (verySlowFilter(src, dst) != 0) {
            // awt.21F=Unable to transform source
            throw new ImagingOpException (Messages.getString("awt.21F")); //$NON-NLS-1$
        }

        return dst;
    }

    private static final class SampleModelInfo {
        int channels;
        int channelsOrder[];
        int stride;
    }

    private final SampleModelInfo checkSampleModel(SampleModel sm) {
        SampleModelInfo ret = new SampleModelInfo();

        if (sm instanceof PixelInterleavedSampleModel) {
            // Check PixelInterleavedSampleModel
            if (sm.getDataType() != DataBuffer.TYPE_BYTE) {
                return null;
            }

            ret.channels = sm.getNumBands();
            ret.stride = ((ComponentSampleModel) sm).getScanlineStride();
            ret.channelsOrder = ((ComponentSampleModel) sm).getBandOffsets();

        } else if (sm instanceof SinglePixelPackedSampleModel) {
            // Check SinglePixelPackedSampleModel
            SinglePixelPackedSampleModel sppsm1 = (SinglePixelPackedSampleModel) sm;

            ret.channels = sppsm1.getNumBands();
            if (sppsm1.getDataType() != DataBuffer.TYPE_INT) {
                return null;
            }

            // Check sample models
            for (int i=0; i<ret.channels; i++) {
                if (sppsm1.getSampleSize(i) != 8) {
                    return null;
                }
            }

            ret.channelsOrder = new int[ret.channels];
            int bitOffsets[] = sppsm1.getBitOffsets();
            for (int i=0; i<ret.channels; i++) {
                if (bitOffsets[i] % 8 != 0) {
                    return null;
                }

                ret.channelsOrder[i] = bitOffsets[i] / 8;
            }

            ret.channels = 4;
            ret.stride = sppsm1.getScanlineStride() * 4;
        } else {
            return null;
        }

        return ret;
    }

    private int verySlowFilter(Raster src, WritableRaster dst) {
        int numBands = src.getNumBands();

        int srcMinX = src.getMinX();
        int srcY = src.getMinY();

        int dstMinX = dst.getMinX();
        int dstY = dst.getMinY();

        int dX = src.getWidth();//< dst.getWidth() ? src.getWidth() : dst.getWidth();
        int dY = src.getHeight();//< dst.getHeight() ? src.getHeight() : dst.getHeight();

        float sample;
        int srcPixels[] = new int[numBands*dX*dY];
        int dstPixels[] = new int[mxHeight*dX*dY];

        srcPixels = src.getPixels(srcMinX, srcY, dX, dY, srcPixels);

        if (numBands == mxWidth) {
            for (int i=0, j=0; i<srcPixels.length; i+=numBands) {
                for (int dstB = 0; dstB < mxHeight; dstB++) {
                    sample = 0f;
                    for (int srcB = 0; srcB < numBands; srcB++) {
                        sample += matrix[dstB][srcB] * srcPixels[i+srcB];
                    }
                    dstPixels[j++] = (int) sample;
                }
            }
        } else {
            for (int i=0, j=0; i<srcPixels.length; i+=numBands) {
                for (int dstB = 0; dstB < mxHeight; dstB++) {
                    sample = 0f;
                    for (int srcB = 0; srcB < numBands; srcB++) {
                        sample += matrix[dstB][srcB] * srcPixels[i+srcB];
                    }
                    dstPixels[j++] = (int) (sample + matrix[dstB][numBands]);
                }
            }
        }

        dst.setPixels(dstMinX, dstY, dX, dY, dstPixels);

        return 0;
    }
}
