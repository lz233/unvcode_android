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
 * @date: Oct 6, 2005
 */

package com.google.code.appengine.awt.image;


import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.*;
import com.google.code.appengine.awt.geom.Point2D;
import com.google.code.appengine.awt.geom.Rectangle2D;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.BufferedImageOp;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.ImagingOpException;
import com.google.code.appengine.awt.image.IndexColorModel;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RasterOp;
import com.google.code.appengine.awt.image.SampleModel;
import com.google.code.appengine.awt.image.WritableRaster;



public class RescaleOp implements BufferedImageOp, RasterOp {
    private float scaleFactors[];
    private float offsets[];
    private RenderingHints hints;

    static {
        // TODO
        //System.loadLibrary("imageops");
    }

    public RescaleOp(float[] scaleFactors, float[] offsets, RenderingHints hints) {
        int numFactors = Math.min(scaleFactors.length, offsets.length);

        this.scaleFactors = new float[numFactors];
        this.offsets = new float[numFactors];

        System.arraycopy(scaleFactors, 0, this.scaleFactors, 0, numFactors);
        System.arraycopy(offsets, 0, this.offsets, 0, numFactors);

        this.hints = hints;
    }

    public RescaleOp(float scaleFactor, float offset, RenderingHints hints) {
        scaleFactors = new float[1];
        offsets = new float[1];

        scaleFactors[0] = scaleFactor;
        offsets[0] = offset;

        this.hints = hints;
    }

    public final int getNumFactors() {
        return scaleFactors.length;
    }

    public final RenderingHints getRenderingHints() {
        return hints;
    }

    public final float[] getScaleFactors(float[] scaleFactors) {
        if (scaleFactors == null) {
            scaleFactors = new float[this.scaleFactors.length];
        }

        int minLength = Math.min(scaleFactors.length, this.scaleFactors.length);
        System.arraycopy(this.scaleFactors, 0, scaleFactors, 0, minLength);
        return scaleFactors;
    }

    public final float[] getOffsets(float[] offsets) {
        if (offsets == null) {
            offsets = new float[this.offsets.length];
        }

        int minLength = Math.min(offsets.length, this.offsets.length);
        System.arraycopy(this.offsets, 0, offsets, 0, minLength);
        return offsets;
    }

    public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }

        dstPt.setLocation(srcPt);
        return dstPt;
    }

    public final Rectangle2D getBounds2D(Raster src) {
        return src.getBounds();
    }

    public final Rectangle2D getBounds2D(BufferedImage src) {
        return getBounds2D(src.getRaster());
    }

    public WritableRaster createCompatibleDestRaster(Raster src) {
        return src.createCompatibleWritableRaster();
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if (dstCM == null) {
            dstCM = src.getColorModel();
        }

        if (dstCM instanceof IndexColorModel) {
            dstCM = ColorModel.getRGBdefault();
        }

        WritableRaster r =
                dstCM.isCompatibleSampleModel(src.getSampleModel()) ?
                src.getRaster().createCompatibleWritableRaster(src.getWidth(), src.getHeight()) :
                dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight());

        return new BufferedImage(
                dstCM,
                r,
                dstCM.isAlphaPremultiplied(),
                null
        );
    }

    public final WritableRaster filter(Raster src, WritableRaster dst) {
        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        } else {
            if (src.getNumBands() != dst.getNumBands()) {
                // awt.21D=Number of src bands ({0}) does not match number of dst bands ({1})
                throw new IllegalArgumentException(Messages.getString("awt.21D", //$NON-NLS-1$
                        src.getNumBands(), dst.getNumBands()));
            }
        }

        if (
                this.scaleFactors.length != 1 &&
                this.scaleFactors.length != src.getNumBands()
        ) {
            // awt.21E=Number of scaling constants is not equal to the number of bands
            throw new IllegalArgumentException(Messages.getString("awt.21E")); //$NON-NLS-1$
        }

        // TODO
        //if (ippFilter(src, dst, BufferedImage.TYPE_CUSTOM, false) != 0)
            if (slowFilter(src, dst, false) != 0) {
                // awt.21F=Unable to transform source
                throw new ImagingOpException (Messages.getString("awt.21F")); //$NON-NLS-1$
            }

        return dst;
    }

    private final int slowFilter(Raster src, WritableRaster dst, boolean skipAlpha) {
        SampleModel sm = src.getSampleModel();

        int numBands = src.getNumBands();
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();

        int srcMinX = src.getMinX();
        int srcMinY = src.getMinY();
        int dstMinX = dst.getMinX();
        int dstMinY = dst.getMinY();

        int[] maxValues = new int[numBands];
        int[] masks = new int[numBands];
        int[] sampleSizes = sm.getSampleSize();

        for (int i=0; i < numBands; i++){
            maxValues[i] = (1 << sampleSizes[i]) - 1;
            masks[i] = ~(maxValues[i]);
        }

        // Processing bounds
        float[] pixels = null;
        pixels = src.getPixels(srcMinX, srcMinY, srcWidth, srcHeight, pixels);

        // Cycle over pixels to be calculated
        if (skipAlpha) { // Always suppose that alpha channel is the last band
            if (scaleFactors.length > 1) {
                for (int i = 0; i < pixels.length; ){
                    for (int bandIdx = 0; bandIdx < numBands-1; bandIdx++, i++){
                        pixels[i] = pixels[i] * scaleFactors[bandIdx] + offsets[bandIdx];
                        // Check for overflow now
                        if (((int)pixels[i] & masks[bandIdx]) != 0) {
                            if (pixels[i] < 0) {
                                pixels[i] = 0;
                            } else {
                                pixels[i] = maxValues[bandIdx];
                            }
                        }
                    }

                    i++;
                }
            } else {
                for (int i = 0; i < pixels.length; ){
                    for (int bandIdx = 0; bandIdx < numBands-1; bandIdx++, i++){
                        pixels[i] = pixels[i] * scaleFactors[0] + offsets[0];
                        // Check for overflow now
                        if (((int)pixels[i] & masks[bandIdx]) != 0) {
                            if (pixels[i] < 0) {
                                pixels[i] = 0;
                            } else {
                                pixels[i] = maxValues[bandIdx];
                            }
                        }
                    }

                    i++;
                }
            }
        } else {
            if (scaleFactors.length > 1) {
                for (int i = 0; i < pixels.length; ){
                    for (int bandIdx = 0; bandIdx < numBands; bandIdx++, i++){
                        pixels[i] = pixels[i] * scaleFactors[bandIdx] + offsets[bandIdx];
                        // Check for overflow now
                        if (((int)pixels[i] & masks[bandIdx]) != 0) {
                            if (pixels[i] < 0) {
                                pixels[i] = 0;
                            } else {
                                pixels[i] = maxValues[bandIdx];
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < pixels.length; ){
                    for (int bandIdx = 0; bandIdx < numBands; bandIdx++, i++){
                        pixels[i] = pixels[i] * scaleFactors[0] + offsets[0];
                        // Check for overflow now
                        if (((int)pixels[i] & masks[bandIdx]) != 0) {
                            if (pixels[i] < 0) {
                                pixels[i] = 0;
                            } else {
                                pixels[i] = maxValues[bandIdx];
                            }
                        }
                    }
                }
            }
        }

        dst.setPixels(dstMinX, dstMinY, srcWidth, srcHeight, pixels);

        return 0;
    }

    public final BufferedImage filter(BufferedImage src, BufferedImage dst) {
        ColorModel srcCM = src.getColorModel();

        if (srcCM instanceof IndexColorModel) {
            // awt.220=Source should not have IndexColorModel
            throw new IllegalArgumentException(Messages.getString("awt.220")); //$NON-NLS-1$
        }

        // Check if the number of scaling factors matches the number of bands
        int nComponents = srcCM.getNumComponents();
        boolean skipAlpha;
        if (srcCM.hasAlpha()) {
            if (scaleFactors.length == 1 || scaleFactors.length == nComponents-1) {
                skipAlpha = true;
            } else if (scaleFactors.length == nComponents) {
                skipAlpha = false;
            } else {
                // awt.21E=Number of scaling constants is not equal to the number of bands
                throw new IllegalArgumentException(Messages.getString("awt.21E")); //$NON-NLS-1$
            }
        } else if (scaleFactors.length == 1 || scaleFactors.length == nComponents) {
            skipAlpha = false;
        } else {
            // awt.21E=Number of scaling constants is not equal to the number of bands
            throw new IllegalArgumentException(Messages.getString("awt.21E")); //$NON-NLS-1$
        }

        BufferedImage finalDst = null;
        if (dst == null) {
            dst = createCompatibleDestImage(src, srcCM);
        } else if (!srcCM.equals(dst.getColorModel())) {
            // Treat BufferedImage.TYPE_INT_RGB and BufferedImage.TYPE_INT_ARGB as same
            if (
                    !((src.getType() == BufferedImage.TYPE_INT_RGB ||
                       src.getType() == BufferedImage.TYPE_INT_ARGB) &&
                      (dst.getType() == BufferedImage.TYPE_INT_RGB ||
                       dst.getType() == BufferedImage.TYPE_INT_ARGB))
            ) {
                finalDst = dst;
                dst = createCompatibleDestImage(src, srcCM);
            }
        }

        // TODO
        //if (ippFilter(src.getRaster(), dst.getRaster(), src.getType(), skipAlpha) != 0)
            if (slowFilter(src.getRaster(), dst.getRaster(), skipAlpha) != 0) {
                // awt.21F=Unable to transform source
                throw new ImagingOpException (Messages.getString("awt.21F")); //$NON-NLS-1$
            }

        if (finalDst != null) {
            Graphics2D g = finalDst.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(dst, 0, 0, null);
        } else {
            finalDst = dst;
        }

        return finalDst;
    }
}
