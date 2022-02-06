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

import com.google.code.appengine.imageio.ImageTranscoder;
import com.google.code.appengine.imageio.spi.IIOServiceProvider;
import com.google.code.appengine.imageio.spi.RegisterableService;


public abstract class ImageTranscoderSpi extends IIOServiceProvider
        implements RegisterableService {

    protected ImageTranscoderSpi() {
    }

    public ImageTranscoderSpi(String vendorName, String version) {
        super(vendorName, version);
    }

    public abstract String getReaderServiceProviderName();

    public abstract String getWriterServiceProviderName();

    public abstract ImageTranscoder createTranscoderInstance();
}
