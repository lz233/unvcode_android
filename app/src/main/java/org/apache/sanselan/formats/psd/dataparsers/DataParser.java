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
package org.apache.sanselan.formats.psd.dataparsers;


import org.apache.sanselan.formats.psd.ImageContents;
import org.apache.sanselan.formats.psd.PSDHeaderInfo;

import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.DataBuffer;


public abstract class DataParser
{
    public final void parseData(int data[][][], BufferedImage bi,
            ImageContents imageContents)
    {
        DataBuffer buffer = bi.getRaster().getDataBuffer();

        PSDHeaderInfo header = imageContents.header;
        int width = header.Columns;
        int height = header.Rows;

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                int rgb = getRGB(data, x, y, imageContents);
                buffer.setElem(y * width + x, rgb);
            }

    }

    protected abstract int getRGB(int data[][][], int x, int y,
            ImageContents imageContents);

    public abstract int getBasicChannelsCount();

    public void dump()
    {

    }

}