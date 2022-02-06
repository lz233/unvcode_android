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
/**
 * @author Oleg V. Khaschansky
 *
 * @date: Jul 22, 2005
 */

package org.apache.harmony.awt.gl.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.*;
import com.google.code.appengine.awt.color.ColorSpace;
import com.google.code.appengine.awt.image.*;


public class PngDecoder extends ImageDecoder {

    private static final int hintflags =
            ImageConsumer.SINGLEFRAME | // PNG is a static image
            ImageConsumer.TOPDOWNLEFTRIGHT | // This order is only one possible
            ImageConsumer.COMPLETESCANLINES; // Don't deliver incomplete scanlines

    // Each pixel is a grayscale sample.
    private static final int PNG_COLOR_TYPE_GRAY = 0;
    // Each pixel is an R,G,B triple.
    private static final int PNG_COLOR_TYPE_RGB = 2;
    // Each pixel is a palette index, a PLTE chunk must appear.
    private static final int PNG_COLOR_TYPE_PLTE = 3;
    // Each pixel is a grayscale sample, followed by an alpha sample.
    private static final int PNG_COLOR_TYPE_GRAY_ALPHA = 4;
    // Each pixel is an R,G,B triple, followed by an alpha sample.
    private static final int PNG_COLOR_TYPE_RGBA = 6;

    private static final int MIN_BUFFER_SIZE = 4096;
    private static final int MAX_BUFFER_SIZE = 2097152;
    private int buffer_size;
    private byte buffer[];

    // Buffers for decoded image data
    byte byteOut[];
    int intOut[];

    // Native pointer to png decoder data
    private long hNativeDecoder;

    int imageWidth, imageHeight;
    int colorType;
    int bitDepth;
    byte cmap[];

    boolean transferInts; // Is transfer type int?.. or byte?
    int dataElementsPerPixel = 1;

    ColorModel cm;

    int updateFromScanline; // First scanline to update
    int numScanlines; // Number of scanlines to update

    private native long decode(byte[] input, int bytesInBuffer, long hDecoder);

    private static native void releaseNativeDecoder(long hDecoder);

    public PngDecoder(DecodingImageSource src, InputStream is) {
        super(src, is);
        try {
            int available_bytes = is.available();
            if (available_bytes < MIN_BUFFER_SIZE) {
                buffer_size = MIN_BUFFER_SIZE;
            } else if (available_bytes > MAX_BUFFER_SIZE) {
                buffer_size = MAX_BUFFER_SIZE;
            } else {
                buffer_size = available_bytes;
            }
        } catch (IOException e) {
            buffer_size = MIN_BUFFER_SIZE;
        }
        buffer = new byte[buffer_size];
    }

    @Override
    public void decodeImage() throws IOException {
        try {
            int bytesRead = 0;
            // Read from the input stream
            for (;;) {
                bytesRead = inputStream.read(buffer, 0, buffer_size);

                if (bytesRead < 0) {
                    // Break, nothing to read from buffer, image truncated?
                    releaseNativeDecoder(hNativeDecoder);
                    break;
                }

                hNativeDecoder = decode(buffer, bytesRead, hNativeDecoder);

                returnData();

                // OK, we decoded all the picture in the right way...
                if (hNativeDecoder == 0) {
                    break;
                }
            }

            imageComplete(ImageConsumer.STATICIMAGEDONE);
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            imageComplete(ImageConsumer.IMAGEERROR);
            throw e;
        } finally {
            closeStream();
        }
    }

    @SuppressWarnings("unused")
    private void returnHeader() { // Called from native code
        setDimensions(imageWidth, imageHeight);

        switch (colorType) {
            case PNG_COLOR_TYPE_GRAY: {
                if (bitDepth != 8 && bitDepth != 4 && bitDepth != 2 && bitDepth != 1) {
                    // awt.3C=Unknown PNG color type
                    throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
                }

                // Create gray color model
                int numEntries = 1 << bitDepth;
                int scaleFactor = 255 / (numEntries-1);
                byte comps[] = new byte[numEntries];
                for (int i = 0; i < numEntries; i++) {
                    comps[i] = (byte) (i * scaleFactor);
                }
                cm = new IndexColorModel(/*bitDepth*/8, numEntries, comps, comps, comps);

                transferInts = false;
                break;
            }

            case PNG_COLOR_TYPE_RGB: {
                if (bitDepth != 8) {
                    // awt.3C=Unknown PNG color type
                    throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
                }

                cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);

                transferInts = true;
                break;
            }

            case PNG_COLOR_TYPE_PLTE: {
                if (bitDepth != 8 && bitDepth != 4 && bitDepth != 2 && bitDepth != 1) {
                    // awt.3C=Unknown PNG color type
                    throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
                }

                cm = new IndexColorModel(/*bitDepth*/8, cmap.length / 3, cmap, 0, false);

                transferInts = false;
                break;
            }

            case PNG_COLOR_TYPE_GRAY_ALPHA: {
                if (bitDepth != 8) {
                    // awt.3C=Unknown PNG color type
                    throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
                }

                cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                        true, false,
                        Transparency.TRANSLUCENT,
                        DataBuffer.TYPE_BYTE);

                transferInts = false;
                dataElementsPerPixel = 2;
                break;
            }

            case PNG_COLOR_TYPE_RGBA: {
                if (bitDepth != 8) {
                    // awt.3C=Unknown PNG color type
                    throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
                }

                cm = ColorModel.getRGBdefault();

                transferInts = true;
                break;
            }
            default:
                // awt.3C=Unknown PNG color type
                throw new IllegalArgumentException(Messages.getString("awt.3C")); //$NON-NLS-1$
        }

        // Create output buffer
        if (transferInts) {
            intOut = new int[imageWidth * imageHeight];
        } else {
            byteOut = new byte[imageWidth * imageHeight * dataElementsPerPixel];
        }

        setColorModel(cm);

        setHints(hintflags);
        setProperties(new Hashtable<Object, Object>()); // Empty
    }

    // Send the data to the consumer
    private void returnData() {
        // Send 1 or more scanlines to the consumer.
        if (numScanlines > 0) {
            // Native decoder could have returned
            // some data from the next pass, handle it here
            int pass1, pass2;
            if (updateFromScanline + numScanlines > imageHeight) {
                pass1 = imageHeight - updateFromScanline;
                pass2 = updateFromScanline + numScanlines - imageHeight;
            } else {
                pass1 = numScanlines;
                pass2 = 0;
            }

            transfer(updateFromScanline, pass1);
            if (pass2 != 0) {
                transfer(0, pass2);
            }
        }
    }

    private void transfer(int updateFromScanline, int numScanlines) {
        if (transferInts) {
            setPixels(
                    0, updateFromScanline,
                    imageWidth, numScanlines,
                    cm, intOut,
                    updateFromScanline * imageWidth,
                    imageWidth
            );
        } else {
            setPixels(
                    0, updateFromScanline,
                    imageWidth, numScanlines,
                    cm, byteOut,
                    updateFromScanline * imageWidth * dataElementsPerPixel,
                    imageWidth * dataElementsPerPixel
            );
        }
    }
}
