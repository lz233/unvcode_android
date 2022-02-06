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


package com.google.code.appengine.imageio.stream;

import java.io.*;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.imageio.stream.FileCacheImageOutputStream;
import com.google.code.appengine.imageio.stream.ImageInputStreamImpl;

public class FileCacheImageInputStream extends ImageInputStreamImpl {
    private InputStream is;
    private File file;
    private RandomAccessFile raf;


    public FileCacheImageInputStream(InputStream stream, File cacheDir) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.0A"));
        }
        is = stream;

        if (cacheDir == null || cacheDir.isDirectory()) {
            file = File.createTempFile(FileCacheImageOutputStream.IIO_TEMP_FILE_PREFIX, null, cacheDir);
            file.deleteOnExit();
        } else {
            throw new IllegalArgumentException(Messages.getString("imageio.0B"));
        }

        raf = new RandomAccessFile(file, "rw");
    }

    @Override
    public int read() throws IOException {
        bitOffset = 0;

        if (streamPos >= raf.length()) {
            int b = is.read();

            if (b < 0) {
                return -1;
            }

            raf.seek(streamPos++);
            raf.write(b);
            return b;
        }

        raf.seek(streamPos++);
        return raf.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        bitOffset = 0;

        if (streamPos >= raf.length()) {
            int nBytes = is.read(b, off, len);

            if (nBytes < 0) {
                return -1;
            }

            raf.seek(streamPos);
            raf.write(b, off, nBytes);
            streamPos += nBytes;
            return nBytes;
        }

        raf.seek(streamPos);
        int nBytes = raf.read(b, off, len);
        streamPos += nBytes;
        return nBytes;
    }

    @Override
    public boolean isCached() {
        return true;
    }

    @Override
    public boolean isCachedFile() {
        return true;
    }

    @Override
    public boolean isCachedMemory() {
        return false;
    }

    @Override
    public void close() throws IOException {
        super.close();
        raf.close();
        file.delete();
    }
}
