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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.imageio.stream.ImageInputStreamImpl;

public class FileImageInputStream extends ImageInputStreamImpl {
    RandomAccessFile raf;

    @SuppressWarnings({"DuplicateThrows"})
    public FileImageInputStream(File f) throws FileNotFoundException, IOException {
        if (f == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.08"));
        }

        raf = new RandomAccessFile(f, "r");
    }

    public FileImageInputStream(RandomAccessFile raf) {
        if (raf == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.09"));
        }

        this.raf = raf;
    }

    @Override
    public int read() throws IOException {
        bitOffset = 0;

        int res = raf.read();
        if (res != -1) {
            streamPos++;
        }
        return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        bitOffset = 0;

        int numRead = raf.read(b, off, len);
        if (numRead >= 0) {
            streamPos += numRead;
        }

        return numRead;
    }

    @Override
    public long length() {
        try {
            return raf.length();
        } catch(IOException e) {
            return -1L;
        }
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos < getFlushedPosition()) {
            throw new IndexOutOfBoundsException();
        }

        raf.seek(pos);
        streamPos = raf.getFilePointer();
        bitOffset = 0;
    }


    @Override
    public void close() throws IOException {
        super.close();
        raf.close();
    }
}
