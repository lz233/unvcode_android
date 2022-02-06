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
package com.google.code.appengine.imageio.spi;


import java.io.IOException;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.imageio.ImageTypeSpecifier;
import com.google.code.appengine.imageio.ImageWriter;
import com.google.code.appengine.imageio.spi.ImageReaderWriterSpi;
import com.google.code.appengine.imageio.stream.ImageInputStream;


public abstract class ImageWriterSpi extends ImageReaderWriterSpi {

    public static final Class[] STANDARD_OUTPUT_TYPE = new Class[] {ImageInputStream.class};

    protected Class[] outputTypes;
    protected String[] readerSpiNames;

    protected ImageWriterSpi() {
        // the default impl. does nothing
    }

    public ImageWriterSpi(String vendorName, String version, String[] names,
                             String[] suffixes, String[] MIMETypes,
                             String pluginClassName,
                             Class[] outputTypes, String[] readerSpiNames,
                             boolean supportsStandardStreamMetadataFormat,
                             String nativeStreamMetadataFormatName,
                             String nativeStreamMetadataFormatClassName,
                             String[] extraStreamMetadataFormatNames,
                             String[] extraStreamMetadataFormatClassNames,
                             boolean supportsStandardImageMetadataFormat,
                             String nativeImageMetadataFormatName,
                             String nativeImageMetadataFormatClassName,
                             String[] extraImageMetadataFormatNames,
                             String[] extraImageMetadataFormatClassNames) {
        super(vendorName, version, names, suffixes, MIMETypes, pluginClassName,
                supportsStandardStreamMetadataFormat, nativeStreamMetadataFormatName,
                nativeStreamMetadataFormatClassName, extraStreamMetadataFormatNames,
                extraStreamMetadataFormatClassNames, supportsStandardImageMetadataFormat,
                nativeImageMetadataFormatName, nativeImageMetadataFormatClassName,
                extraImageMetadataFormatNames, extraImageMetadataFormatClassNames);

        if (outputTypes == null || outputTypes.length == 0) {
            throw new NullPointerException(Messages.getString("imageio.59"));
        }

        this.outputTypes = outputTypes;
        this.readerSpiNames = readerSpiNames;
    }

    public boolean isFormatLossless() {
        return true;
    }

    public Class[] getOutputTypes() {
        return outputTypes;
    }

    public abstract boolean canEncodeImage(ImageTypeSpecifier type);

    public boolean canEncodeImage(RenderedImage im) {
        return canEncodeImage(ImageTypeSpecifier.createFromRenderedImage(im));
    }

    public ImageWriter createWriterInstance() throws IOException {
        return createWriterInstance(null);
    }

    public abstract ImageWriter createWriterInstance(Object extension) throws IOException;

    public boolean isOwnWriter(ImageWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.96"));
        }
        
        return writer.getClass().getName().equals(pluginClassName);
    }

    public String[] getImageReaderSpiNames() {
        return readerSpiNames;
    }
}
