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
package org.apache.sanselan.formats.tiff.photometricinterpreters;

import java.io.IOException;

import org.apache.sanselan.ImageReadException;

import com.google.code.appengine.awt.image.BufferedImage;


public abstract class PhotometricInterpreter
{
    protected final int samplesPerPixel;
    protected final int bitsPerSample[];
    protected final int predictor;
    protected final int width;
    protected final int height;

    public PhotometricInterpreter(int fSamplesPerPixel, int fBitsPerSample[],
            int Predictor, int width, int height)
    {
        this.samplesPerPixel = fSamplesPerPixel;
        this.bitsPerSample = fBitsPerSample;
        this.predictor = Predictor;
        this.width = width;
        this.height = height;
    }

    public abstract void interpretPixel(BufferedImage bi, int samples[], int x,
            int y) throws ImageReadException, IOException;

    public void dumpstats() throws ImageReadException, IOException
    {

    }

}