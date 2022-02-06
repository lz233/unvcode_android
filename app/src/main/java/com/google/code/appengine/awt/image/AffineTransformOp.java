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
 * @author Oleg V. Khaschansky, Denis M. Kishenko
 */

package com.google.code.appengine.awt.image;


import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.*;
import com.google.code.appengine.awt.geom.AffineTransform;
import com.google.code.appengine.awt.geom.NoninvertibleTransformException;
import com.google.code.appengine.awt.geom.Point2D;
import com.google.code.appengine.awt.geom.Rectangle2D;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.BufferedImageOp;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.ImagingOpException;
import com.google.code.appengine.awt.image.IndexColorModel;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RasterFormatException;
import com.google.code.appengine.awt.image.RasterOp;
import com.google.code.appengine.awt.image.WritableRaster;


public class AffineTransformOp implements BufferedImageOp, RasterOp {
    public static final int TYPE_NEAREST_NEIGHBOR = 1;
    public static final int TYPE_BILINEAR = 2;
    public static final int TYPE_BICUBIC = 3;

    private int iType; // interpolation type
    private AffineTransform at;
    private RenderingHints hints;

    static {
        // TODO - uncomment
        //System.loadLibrary("imageops");
    }

    public AffineTransformOp(AffineTransform xform, RenderingHints hints) {
        this(xform, TYPE_NEAREST_NEIGHBOR);
        this.hints = hints;

        if (hints != null) {
            Object hint = hints.get(RenderingHints.KEY_INTERPOLATION);
            if (hint != null) {
                // Nearest neighbor is default
                if (hint == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    this.iType = TYPE_BILINEAR;
                } else if (hint == RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
                    this.iType = TYPE_BICUBIC;
                }
            } else {
                hint = hints.get(RenderingHints.KEY_RENDERING);
                // Determine from rendering quality
                if (hint == RenderingHints.VALUE_RENDER_QUALITY) {
                    this.iType = TYPE_BILINEAR;
                // For speed use nearest neighbor
                }
            }
        }
    }

    public AffineTransformOp(AffineTransform xform, int interp) {
        if (Math.abs(xform.getDeterminant()) <= Double.MIN_VALUE) {
            // awt.24F=Unable to invert transform {0}
            throw new ImagingOpException(Messages.getString("awt.24F", xform)); //$NON-NLS-1$
        }

        this.at = (AffineTransform) xform.clone();

        if (interp != TYPE_NEAREST_NEIGHBOR && interp != TYPE_BILINEAR && interp != TYPE_BICUBIC) {
            // awt.250=Unknown interpolation type: {0}
            throw new IllegalArgumentException(Messages.getString("awt.250", interp)); //$NON-NLS-1$
        }

        this.iType = interp;
    }

    public final int getInterpolationType() {
        return iType;
    }

    public final RenderingHints getRenderingHints() {
        if (hints == null) {
            Object value = null;

            switch (iType) {
                case TYPE_NEAREST_NEIGHBOR:
                    value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    break;
                case TYPE_BILINEAR:
                    value = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                    break;
                case TYPE_BICUBIC:
                    value = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                    break;
                default:
                    value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            }

            hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, value);
        }

        return hints;
    }

    public final AffineTransform getTransform() {
        return (AffineTransform) at.clone();
    }

    public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return at.transform(srcPt, dstPt);
    }

    public final Rectangle2D getBounds2D(BufferedImage src) {
        return getBounds2D(src.getRaster());
    }

    public final Rectangle2D getBounds2D(Raster src) {
        // We position source raster to (0,0) even if it is translated child raster.
        // This means that we need only width and height of the src
        int width = src.getWidth();
        int height = src.getHeight();

        float[] corners = {
            0, 0,
            width, 0,
            width, height,
            0, height
        };

        at.transform(corners, 0, corners, 0, 4);

        Rectangle2D.Float bounds = new Rectangle2D.Float(corners[0], corners[1], 0 , 0);
        bounds.add(corners[2], corners[3]);
        bounds.add(corners[4], corners[5]);
        bounds.add(corners[6], corners[7]);

        return bounds;
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        Rectangle2D newBounds = getBounds2D(src);

        // Destination image should include (0,0) + positive part
        // of the area bounded by newBounds (in source coordinate system).
        double dstWidth = newBounds.getX() + newBounds.getWidth();
        double dstHeight = newBounds.getY() + newBounds.getHeight();

        if (dstWidth <= 0 || dstHeight <= 0) {
            // awt.251=Transformed width ({0}) and height ({1}) should be greater than 0
            throw new RasterFormatException(
                    Messages.getString("awt.251", dstWidth, dstHeight)); //$NON-NLS-1$
        }

        if (destCM != null) {
            return new BufferedImage(destCM,
                    destCM.createCompatibleWritableRaster((int)dstWidth, (int)dstHeight),
                    destCM.isAlphaPremultiplied(),
                    null
            );
        }

        ColorModel cm = src.getColorModel();

        // Interpolation other than NN doesn't make any sense for index color
        if (iType != TYPE_NEAREST_NEIGHBOR && cm instanceof IndexColorModel) {
            return new BufferedImage((int)dstWidth, (int)dstHeight, BufferedImage.TYPE_INT_ARGB);
        }

        // OK, we can get source color model
        return new BufferedImage(cm,
                src.getRaster().createCompatibleWritableRaster((int)dstWidth, (int)dstHeight),
                cm.isAlphaPremultiplied(),
                null
        );
    }

    public WritableRaster createCompatibleDestRaster (Raster src) {
        // Here approach is other then in createCompatibleDestImage -
        // destination should include only
        // transformed image, but not (0,0) in source coordinate system

        Rectangle2D newBounds = getBounds2D(src);
        return src.createCompatibleWritableRaster(
                (int) newBounds.getX(), (int) newBounds.getY(),
                (int) newBounds.getWidth(), (int)newBounds.getHeight()
        );
    }


    public final BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (src == dst) {
            // awt.252=Source can't be same as the destination
            throw new IllegalArgumentException(Messages.getString("awt.252")); //$NON-NLS-1$
        }

        ColorModel srcCM = src.getColorModel();
        BufferedImage finalDst = null;

        if (
                srcCM instanceof IndexColorModel &&
                (iType != TYPE_NEAREST_NEIGHBOR || srcCM.getPixelSize() % 8 != 0)
        ) {
            src = ((IndexColorModel)srcCM).convertToIntDiscrete(src.getRaster(), true);
            srcCM = src.getColorModel();
        }

        if (dst == null) {
            dst = createCompatibleDestImage(src, srcCM);
        } else {
            if (!srcCM.equals(dst.getColorModel())) {
                // Treat BufferedImage.TYPE_INT_RGB and BufferedImage.TYPE_INT_ARGB as same
                if (
                   !(
                     (src.getType() == BufferedImage.TYPE_INT_RGB ||
                      src.getType() == BufferedImage.TYPE_INT_ARGB) &&
                     (dst.getType() == BufferedImage.TYPE_INT_RGB ||
                      dst.getType() == BufferedImage.TYPE_INT_ARGB)
                    )
                ) {
                    finalDst = dst;
                    dst = createCompatibleDestImage(src, srcCM);
                }
            }
        }

        // Skip alpha channel for TYPE_INT_RGB images
        if (slowFilter(src.getRaster(), dst.getRaster()) != 0) {
            // awt.21F=Unable to transform source
            throw new ImagingOpException (Messages.getString("awt.21F")); //$NON-NLS-1$
        // TODO - uncomment
        //if (ippFilter(src.getRaster(), dst.getRaster(), src.getType()) != 0)
            //throw new ImagingOpException ("Unable to transform source");
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

    public final WritableRaster filter(Raster src, WritableRaster dst) {
        if (src == dst) {
            // awt.252=Source can't be same as the destination
            throw new IllegalArgumentException(Messages.getString("awt.252")); //$NON-NLS-1$
        }

        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        } else if (src.getNumBands() != dst.getNumBands()) {
            // awt.253=Different number of bands in source and destination
            throw new IllegalArgumentException(Messages.getString("awt.253")); //$NON-NLS-1$
        }

        if (slowFilter(src, dst) != 0) {
            // awt.21F=Unable to transform source
            throw new ImagingOpException(Messages.getString("awt.21F")); //$NON-NLS-1$
        // TODO - uncomment
        //if (ippFilter(src, dst, BufferedImage.TYPE_CUSTOM) != 0)
        //    throw new ImagingOpException("Unable to transform source");
        }

        return dst;
    }

    private int slowFilter(Raster src, WritableRaster dst) {
        // TODO: make correct interpolation
        // TODO: what if there are different data types?

        Rectangle srcBounds = src.getBounds();
        Rectangle dstBounds = dst.getBounds();
        Rectangle normDstBounds = new Rectangle(0, 0, dstBounds.width, dstBounds.height);
        Rectangle bounds = getBounds2D(src).getBounds().intersection(normDstBounds);

        AffineTransform inv = null;
        try {
             inv = at.createInverse();
        } catch (NoninvertibleTransformException e) {
            return -1;
        }

        double[] m = new double[6];
        inv.getMatrix(m);

        int minSrcX = srcBounds.x;
        int minSrcY = srcBounds.y;
        int maxSrcX = srcBounds.x + srcBounds.width;
        int maxSrcY = srcBounds.y + srcBounds.height;

        int minX = bounds.x + dstBounds.x;
        int minY = bounds.y + dstBounds.y;
        int maxX = minX + bounds.width;
        int maxY = minY + bounds.height;

        int hx = (int)(m[0] * 256);
        int hy = (int)(m[1] * 256);
        int vx = (int)(m[2] * 256);
        int vy = (int)(m[3] * 256);
        int sx = (int)(m[4] * 256) + hx * bounds.x + vx * bounds.y + (srcBounds.x) * 256;
        int sy = (int)(m[5] * 256) + hy * bounds.x + vy * bounds.y + (srcBounds.y) * 256;

        vx -= hx * bounds.width;
        vy -= hy * bounds.width;

        if (src.getTransferType() == dst.getTransferType()) {
            for (int y = minY; y < maxY; y++) {
                for (int x = minX; x < maxX; x++) {
                    int px = sx >> 8;
                    int py = sy >> 8;
                    if (px >= minSrcX && py >= minSrcY && px < maxSrcX && py < maxSrcY) {
                        Object val = src.getDataElements(px , py , null);
                        dst.setDataElements(x, y, val);
                    }
                    sx += hx;
                    sy += hy;
                }
                sx += vx;
                sy += vy;
            }
        } else {
            float pixel[] = null;
            for (int y = minY; y < maxY; y++) {
                for (int x = minX; x < maxX; x++) {
                    int px = sx >> 8;
                    int py = sy >> 8;
                    if (px >= minSrcX && py >= minSrcY && px < maxSrcX && py < maxSrcY) {
                        pixel = src.getPixel(px, py, pixel);
                        dst.setPixel(x, y, pixel);
                    }
                    sx += hx;
                    sy += hy;
                }
                sx += vx;
                sy += vy;
            }
        }

        return 0;
    }
}