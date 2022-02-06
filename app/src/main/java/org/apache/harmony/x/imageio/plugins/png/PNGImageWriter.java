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
 * @author Viskov Nikolay
 */
package org.apache.harmony.x.imageio.plugins.png;

import java.io.File;
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

import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.imageio.IIOImage;
import com.google.code.appengine.imageio.ImageTypeSpecifier;
import com.google.code.appengine.imageio.ImageWriteParam;
import com.google.code.appengine.imageio.ImageWriter;
import com.google.code.appengine.imageio.metadata.IIOMetadata;
import com.google.code.appengine.imageio.spi.ImageWriterSpi;
import com.google.code.appengine.imageio.stream.ImageOutputStream;


public class PNGImageWriter extends ImageWriter {
    private static int[][] BAND_OFFSETS = {
            {}, {
                0 }, {
                    0, 1 }, {
                    0, 1, 2 }, {
                    0, 1, 2, 3 } };

    // Each pixel is a grayscale sample.
    private static final int PNG_COLOR_TYPE_GRAY = 0;
    // Each pixel is an R,G,B triple.
    private static final int PNG_COLOR_TYPE_RGB = 2;
    // Each pixel is a palette index, a PLTE chunk must appear.
    private static final int PNG_COLOR_TYPE_PLTE = 3;
    // Each pixel is a grayscale sample, followed by an alpha sample.
    private static final int PNG_COLOR_TYPE_GRAY_ALPHA = 4;
    // Each pixel is an R,G,B triple, followed by an alpha sample.
    private static final int PNG_COLOR_TYPE_RGBA = 6;


    public PNGImageWriter(ImageWriterSpi iwSpi) {
        super(iwSpi);
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata arg0, ImageWriteParam arg1) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata arg0, ImageTypeSpecifier arg1, ImageWriteParam arg2) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier arg0, ImageWriteParam arg1) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam arg0) throws NotImplementedException {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage iioimage, ImageWriteParam param) throws IOException {
        if (output == null) {
            throw new IllegalStateException(Messages.getString("imageio.81"));
        }
        if (iioimage == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.82"));
        }
        if (iioimage.hasRaster() && !canWriteRasters()) {
            throw new UnsupportedOperationException(Messages.getString("imageio.83"));
        }// ImageOutputStreamImpl

        RenderedImage image = iioimage.getRenderedImage();

        try {
        	Map params = new HashMap();
        	Sanselan.writeImage((BufferedImage) image,
        			wrapOutput(getOutput()),
        			ImageFormat.IMAGE_FORMAT_PNG,
        			params);
        }
        catch (ImageWriteException e) {
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

	public ImageWriteParam getDefaultWriteParam() {
        return new PNGImageWriterParam();
    }
}
