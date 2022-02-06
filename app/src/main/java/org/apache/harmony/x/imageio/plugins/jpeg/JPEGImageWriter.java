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
 * @author Rustem V. Rafikov
 */
package org.apache.harmony.x.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;



import org.apache.harmony.luni.util.NotImplementedException;
import org.apache.harmony.x.imageio.internal.OutputStreamWrapper;
import org.apache.harmony.x.imageio.internal.nls.Messages;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;

import com.google.code.appengine.awt.color.ColorSpace;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.IndexColorModel;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.awt.image.WritableRaster;
import com.google.code.appengine.imageio.IIOImage;
import com.google.code.appengine.imageio.ImageTypeSpecifier;
import com.google.code.appengine.imageio.ImageWriteParam;
import com.google.code.appengine.imageio.ImageWriter;
import com.google.code.appengine.imageio.metadata.IIOMetadata;
import com.google.code.appengine.imageio.plugins.jpeg.JPEGImageWriteParam;
import com.google.code.appengine.imageio.spi.ImageWriterSpi;
import com.google.code.appengine.imageio.stream.ImageOutputStream;


/**
 * @author Rustem V. Rafikov
 */
public class JPEGImageWriter extends ImageWriter {

    private Raster sourceRaster;
    private WritableRaster scanRaster;
    private int srcXOff = 0;
    private int srcYOff = 0;
    private int srcWidth;
    private int srcHeight;

    //-- y step for image subsampling
    private int deltaY = 1;
    //-- x step for image subsampling
    private int deltaX = 1;

    private ImageOutputStream ios;

    public JPEGImageWriter(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    @Override
    public void write(IIOMetadata iioMetadata, IIOImage iioImage, ImageWriteParam param)
            throws IOException {

        if (ios == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.7F"));
        }

        RenderedImage img = null;
        if (!iioImage.hasRaster()) {
            img = iioImage.getRenderedImage();
            if (img instanceof BufferedImage) {
                sourceRaster = ((BufferedImage) img).getRaster();
            } else {
                sourceRaster = img.getData();
            }
        } else {
            sourceRaster = iioImage.getRaster();
        }

        Map params = new HashMap();
        try {
        	
			Sanselan.writeImage((BufferedImage)img,
					wrapOutput(ios),//(OutputStream)ios,
					ImageFormat.IMAGE_FORMAT_JPEG,
					params);
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private OutputStream wrapOutput(Object output) {
		if(output instanceof OutputStream) {
			return (OutputStream)output;
		} else if(output instanceof ImageOutputStream){
			return new OutputStreamWrapper((ImageOutputStream) output);
		} else {
			throw new UnsupportedOperationException(output.getClass().getName());
		}
	}
    

    @Override
    public void dispose() {
        super.dispose();
        ios = null;
    }


    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata iioMetadata, ImageWriteParam imageWriteParam) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata iioMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public void setOutput(Object output) {
        super.setOutput(output);
        ios = (ImageOutputStream) output;
        sourceRaster = null;
        scanRaster = null;
        srcXOff = 0;
        srcYOff = 0;
        srcWidth = 0;
        srcHeight = 0;
        deltaY = 1;
    }

    /**
     * Callback for getting a next scanline
     * @param scanline scan line number
     */
    @SuppressWarnings("unused")
    private void getScanLine(int scanline) {
        Raster child = sourceRaster.createChild(srcXOff,
                srcYOff + scanline * deltaY, srcWidth, 1, 0, 0, null);

        scanRaster.setRect(child);
        // broadcast the current percentage of image completion
        processImageProgress((float) scanline / (float) srcHeight * 100.0f);
    }

    /**
     * Maps color space types to IJG color spaces
     * @param image
     * @return
     */
    private int getSourceCSType(RenderedImage image) {
        int type = JPEGConsts.JCS_UNKNOW;
        ColorModel cm = image.getColorModel();

        if (null == cm) {
            return type;
        }

        if (cm instanceof IndexColorModel) {
            // TODO: implement
            throw new UnsupportedOperationException(Messages.getString("imageio.80"));
        }

        boolean hasAlpha = cm.hasAlpha();
        ColorSpace cs = cm.getColorSpace();
        switch(cs.getType()) {
            case ColorSpace.TYPE_GRAY:
                type = JPEGConsts.JCS_GRAYSCALE;
                break;
           case ColorSpace.TYPE_RGB:
                type = hasAlpha ? JPEGConsts.JCS_RGBA : JPEGConsts.JCS_RGB;
                break;
           case ColorSpace.TYPE_YCbCr:
                type = hasAlpha ? JPEGConsts.JCS_YCbCrA : JPEGConsts.JCS_YCbCr;
                break;
           case ColorSpace.TYPE_3CLR:
                 type = hasAlpha ? JPEGConsts.JCS_YCCA : JPEGConsts.JCS_YCC;
                 break;
           case ColorSpace.TYPE_CMYK:
                  type = JPEGConsts.JCS_CMYK;
                  break;
        }
        return type;
    }

    /**
     * Returns destination color space.
     * (YCbCr[A] for RGB)
     *
     * @param image
     * @return
     */
    private int getDestinationCSType(RenderedImage image) {
        int type = JPEGConsts.JCS_UNKNOW;
        ColorModel cm = image.getColorModel();
        if (null != cm) {
            boolean hasAlpha = cm.hasAlpha();
            ColorSpace cs = cm.getColorSpace();

            switch(cs.getType()) {
                case ColorSpace.TYPE_GRAY:
                    type = JPEGConsts.JCS_GRAYSCALE;
                    break;
               case ColorSpace.TYPE_RGB:
                    type = hasAlpha ? JPEGConsts.JCS_YCbCrA : JPEGConsts.JCS_YCbCr;
                    break;
               case ColorSpace.TYPE_YCbCr:
                    type = hasAlpha ? JPEGConsts.JCS_YCbCrA : JPEGConsts.JCS_YCbCr;
                    break;
               case ColorSpace.TYPE_3CLR:
                     type = hasAlpha ? JPEGConsts.JCS_YCCA : JPEGConsts.JCS_YCC;
                     break;
               case ColorSpace.TYPE_CMYK:
                      type = JPEGConsts.JCS_CMYK;
                      break;
            }
        }
        return type;
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new JPEGImageWriteParam(getLocale());
    }
}
