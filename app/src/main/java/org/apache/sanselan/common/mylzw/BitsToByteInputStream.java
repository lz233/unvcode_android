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
package org.apache.sanselan.common.mylzw;

import java.io.IOException;
import java.io.InputStream;

public class BitsToByteInputStream extends InputStream
{
    private final MyBitInputStream is;
    private final int desiredDepth;

    public BitsToByteInputStream(MyBitInputStream is, int desiredDepth)
    {
        this.is = is;
        this.desiredDepth = desiredDepth;
    }

    public int read() throws IOException
    {
        return readBits(8);
    }

    public int readBits(int bitCount) throws IOException
    {
        int i = is.readBits(bitCount);
        if (bitCount < desiredDepth)
            i <<= (desiredDepth - bitCount);
        else if (bitCount > desiredDepth)
            i >>= (bitCount - desiredDepth);

        return i;
    }

    public int[] readBitsArray(int sampleBits, int length) throws IOException
    {
        int result[] = new int[length];

        for (int i = 0; i < length; i++)
            result[i] = readBits(sampleBits);

        return result;
    }
}