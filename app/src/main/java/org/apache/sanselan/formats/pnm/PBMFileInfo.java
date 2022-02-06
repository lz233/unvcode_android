/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sanselan.formats.pnm;

import java.io.IOException;
import java.io.InputStream;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;

public class PBMFileInfo extends FileInfo
{
    public PBMFileInfo(int width, int height, boolean RAWBITS)
    {
        super(width, height, RAWBITS);
    }

    public int getNumComponents()
    {
        return 1;
    }

    public int getBitDepth()
    {
        return 1;
    }

    public ImageFormat getImageType()
    {
        return ImageFormat.IMAGE_FORMAT_PBM;
    }

    public int getColorType()
    {
        return ImageInfo.COLOR_TYPE_BW;
    }

    public String getImageTypeDescription()
    {
        return "PBM: portable bitmap fileformat";
    }

    public String getMIMEType()
    {
        return "image/x-portable-bitmap";
    }

    protected void newline()
    {
        bitcache = 0;
        bits_in_cache = 0;
    }


    private int bitcache = 0;
    private int bits_in_cache = 0;

    public int getRGB(InputStream is) throws IOException
    {
        if (bits_in_cache < 1)
        {
            int bits = is.read();
            if (bits < 0)
                throw new IOException("PBM: Unexpected EOF");
            bitcache = 0xff & bits;
            bits_in_cache += 8;
        }

        int bit = 0x1 & (bitcache >> 7);
        bitcache <<= 1;
        bits_in_cache--;

        if (bit == 0)
            return 0xffffffff;
        if (bit == 1)
            return 0xff000000;
        throw new IOException("PBM: bad bit: " + bit);
    }

    public int getRGB(WhiteSpaceReader wsr) throws IOException
    {
        int bit = Integer.parseInt(wsr.readtoWhiteSpace());
        if (bit == 0)
            return 0xff000000;
        if (bit == 1)
            return 0xffffffff;
        throw new IOException("PBM: bad bit: " + bit);
    }

}