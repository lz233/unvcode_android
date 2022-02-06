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


package org.apache.harmony.x.imageio.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.imageio.spi.ImageOutputStreamSpi;
import com.google.code.appengine.imageio.stream.FileCacheImageOutputStream;
import com.google.code.appengine.imageio.stream.ImageOutputStream;
import com.google.code.appengine.imageio.stream.MemoryCacheImageOutputStream;

public class OutputStreamIOSSpi extends ImageOutputStreamSpi {
    private static final String vendor = "Apache";

    private static final String ver = "0.1";

    public OutputStreamIOSSpi() {
        super(vendor, ver, OutputStream.class);
    }

    @Override
    public ImageOutputStream createOutputStreamInstance(Object output, boolean useCache, File cacheDir) throws IOException {
        if (output instanceof OutputStream) {
//            if (useCache) {
//                return new FileCacheImageOutputStream((OutputStream) output, cacheDir);
//            } else {
                return new MemoryCacheImageOutputStream((OutputStream) output);
//            }
        }
        throw new IllegalArgumentException(Messages.getString("imageio.85"));
    }

    @Override
    public String getDescription(Locale locale) {
        return "Output Stream IOS Spi";
    }

    @Override
    public boolean canUseCacheFile() {
        return true;
    }
}
