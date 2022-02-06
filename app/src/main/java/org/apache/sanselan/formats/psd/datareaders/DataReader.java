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
package org.apache.sanselan.formats.psd.datareaders;

import java.io.IOException;
import java.io.InputStream;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.BinaryConstants;
import org.apache.sanselan.common.BinaryFileParser;
import org.apache.sanselan.formats.psd.ImageContents;
import org.apache.sanselan.formats.psd.dataparsers.DataParser;

import com.google.code.appengine.awt.image.BufferedImage;


public abstract class DataReader implements BinaryConstants
{
    protected final DataParser dataParser;

    public DataReader(DataParser fDataParser)
    {
        this.dataParser = fDataParser;
    }

    public abstract void readData(InputStream is, BufferedImage bi,
            ImageContents imageContents, BinaryFileParser bfp)
            throws ImageReadException, IOException;

    public void dump()
    {
        dataParser.dump();
    }
}