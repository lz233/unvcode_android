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
package org.apache.sanselan.formats.png.chunks;

import java.io.IOException;

import org.apache.sanselan.formats.png.PngText;

public abstract class PNGTextChunk extends PNGChunk
{

    public PNGTextChunk(int Length, int ChunkType, int CRC, byte bytes[])
            throws IOException
    {
        super(Length, ChunkType, CRC, bytes);

    }

    public abstract String getKeyword();

    public abstract String getText();

    public abstract PngText getContents();

}