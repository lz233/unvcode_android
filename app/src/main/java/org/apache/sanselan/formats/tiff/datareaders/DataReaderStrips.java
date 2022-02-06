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
package org.apache.sanselan.formats.tiff.datareaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.BitInputStream;
import org.apache.sanselan.formats.tiff.TiffImageData;
import org.apache.sanselan.formats.tiff.photometricinterpreters.PhotometricInterpreter;

import com.google.code.appengine.awt.image.BufferedImage;


public final class DataReaderStrips extends DataReader
{

    private final int bitsPerPixel;
    private final int width, height;
    private final int compression;
    private final int rowsPerStrip;

    private final TiffImageData.Strips imageData;

    public DataReaderStrips(PhotometricInterpreter photometricInterpreter,
            int bitsPerPixel, int bitsPerSample[], int predictor,
            int samplesPerPixel, int width, int height, int compression,
            int rowsPerStrip, TiffImageData.Strips imageData)
    {
        super(photometricInterpreter, bitsPerSample, predictor, samplesPerPixel);

        this.bitsPerPixel = bitsPerPixel;
        this.width = width;
        this.height = height;
        this.compression = compression;
        this.rowsPerStrip = rowsPerStrip;
        this.imageData = imageData;
    }

    private void interpretStrip(BufferedImage bi, byte bytes[],
            int pixels_per_strip) throws ImageReadException, IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BitInputStream bis = new BitInputStream(bais);

        for (int i = 0; i < pixels_per_strip; i++)
        {
            int samples[] = getSamplesAsBytes(bis);

            if ((x < width) && (y < height))
            {
                samples = applyPredictor(samples, x);

                photometricInterpreter.interpretPixel(bi, samples, x, y);
            }

            x++;
            if (x >= width)
            {
                x = 0;
                y++;
                bis.flushCache();
                if (y >= height)
                    break;
            }
        }
    }

    private int x = 0, y = 0;

    public void readImageData(BufferedImage bi) throws ImageReadException,
            IOException
    {
        for (int strip = 0; strip < imageData.strips.length; strip++)
        {
            int rowsRemaining = height - (strip * rowsPerStrip);
            int rowsInThisStrip = Math.min(rowsRemaining, rowsPerStrip);
            int pixelsPerStrip = rowsInThisStrip * width;
            int bytesPerStrip = ((pixelsPerStrip * bitsPerPixel) + 7) / 8;

            byte compressed[] = imageData.strips[strip].data;

            byte decompressed[] = decompress(compressed, compression,
                    bytesPerStrip);

            interpretStrip(bi, decompressed, pixelsPerStrip);

        }
    }

}