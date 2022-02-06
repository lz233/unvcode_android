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

import org.apache.sanselan.color.ColorConversions;
import org.apache.sanselan.formats.psd.ImageContents;

public class DataParserCMYK extends DataParser
{
    protected int getRGB(int data[][][], int x, int y,
            ImageContents imageContents)
    {
        int sc = 0xff & data[0][y][x];
        int sm = 0xff & data[1][y][x];
        int sy = 0xff & data[2][y][x];
        int sk = 0xff & data[3][y][x];

        // CRAZY adobe has to store the bytes in reverse form.
        sc = 255 - sc;
        sm = 255 - sm;
        sy = 255 - sy;
        sk = 255 - sk;

        int rgb = ColorConversions.convertCMYKtoRGB(sc, sm, sy, sk);

        return rgb;
    }

    public int getBasicChannelsCount()
    {
        return 4;
    }

}