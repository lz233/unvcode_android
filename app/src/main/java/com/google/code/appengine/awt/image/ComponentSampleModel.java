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
 * @author Igor V. Stolyarov
 */
package com.google.code.appengine.awt.image;

import java.util.Arrays;

import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.image.ComponentSampleModel;
import com.google.code.appengine.awt.image.DataBuffer;
import com.google.code.appengine.awt.image.DataBufferByte;
import com.google.code.appengine.awt.image.DataBufferDouble;
import com.google.code.appengine.awt.image.DataBufferFloat;
import com.google.code.appengine.awt.image.DataBufferInt;
import com.google.code.appengine.awt.image.DataBufferShort;
import com.google.code.appengine.awt.image.DataBufferUShort;
import com.google.code.appengine.awt.image.RasterFormatException;
import com.google.code.appengine.awt.image.SampleModel;


public class ComponentSampleModel extends SampleModel {

    protected int bandOffsets[];

    protected int bankIndices[];

    protected int numBands;

    protected int numBanks;

    protected int scanlineStride;

    protected int pixelStride;

    public ComponentSampleModel(int dataType, int w, int h, int pixelStride,
            int scanlineStride, int bankIndices[], int bandOffsets[]) {

        super(dataType, w, h, bandOffsets.length);

        if (pixelStride < 0) {
            // awt.24B=Pixel stride must be >= 0
            throw new IllegalArgumentException(Messages.getString("awt.24B")); //$NON-NLS-1$
        }

        if (scanlineStride < 0) {
            // awt.24C=Scanline stride must be >= 0
            throw new IllegalArgumentException(Messages.getString("awt.24C")); //$NON-NLS-1$
        }

        if (bankIndices.length != bandOffsets.length) {
            // awt.24D=Bank Indices length must be equal Bank Offsets length
            throw new IllegalArgumentException(Messages.getString("awt.24D")); //$NON-NLS-1$
        }

        this.pixelStride = pixelStride;
        this.scanlineStride = scanlineStride;
        this.bandOffsets = bandOffsets.clone();
        this.bankIndices = bankIndices.clone();
        this.numBands = bandOffsets.length;

        int maxBank = 0;
        for (int i = 0; i < bankIndices.length; i++) {
            if (bankIndices[i] < 0) {
                // awt.24E=Index of {0} bank must be >= 0
                throw new IllegalArgumentException(Messages.getString("awt.24E", i)); //$NON-NLS-1$
            }
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
        }
        this.numBanks = maxBank + 1;

    }

    public ComponentSampleModel(int dataType, int w, int h, int pixelStride,
            int scanlineStride, int bandOffsets[]) {

        super(dataType, w, h, bandOffsets.length);
        if (pixelStride < 0) {
            // awt.24B=Pixel stride must be >= 0
            throw new IllegalArgumentException(Messages.getString("awt.24B")); //$NON-NLS-1$
        }

        if (scanlineStride < 0) {
            // awt.24C=Scanline stride must be >= 0
            throw new IllegalArgumentException(Messages.getString("awt.24C")); //$NON-NLS-1$
        }

        this.pixelStride = pixelStride;
        this.scanlineStride = scanlineStride;
        this.bandOffsets = bandOffsets.clone();
        this.numBands = bandOffsets.length;
        this.numBanks = 1;

        this.bankIndices = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            bankIndices[i] = 0;
        }
    }

    @Override
    public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            byte bdata[];
            if (obj == null) {
                bdata = new byte[numBands];
            } else {
                bdata = (byte[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                bdata[i] = (byte) getSample(x, y, i, data);
            }

            obj = bdata;
            break;

        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_USHORT:
            short sdata[];
            if (obj == null) {
                sdata = new short[numBands];
            } else {
                sdata = (short[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                sdata[i] = (short) getSample(x, y, i, data);
            }

            obj = sdata;
            break;

        case DataBuffer.TYPE_INT:
            int idata[];
            if (obj == null) {
                idata = new int[numBands];
            } else {
                idata = (int[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                idata[i] = getSample(x, y, i, data);
            }

            obj = idata;
            break;

        case DataBuffer.TYPE_FLOAT:
            float fdata[];
            if (obj == null) {
                fdata = new float[numBands];
            } else {
                fdata = (float[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                fdata[i] = getSampleFloat(x, y, i, data);
            }

            obj = fdata;
            break;

        case DataBuffer.TYPE_DOUBLE:
            double ddata[];
            if (obj == null) {
                ddata = new double[numBands];
            } else {
                ddata = (double[]) obj;
            }

            for (int i = 0; i < numBands; i++) {
                ddata[i] = getSampleDouble(x, y, i, data);
            }

            obj = ddata;
            break;
        }

        return obj;
    }

    @Override
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            byte barr[] = (byte[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, barr[i] & 0xff, data);
            }
            break;

        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_USHORT:
            short sarr[] = (short[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, sarr[i] & 0xffff, data);
            }
            break;

        case DataBuffer.TYPE_INT:
            int iarr[] = (int[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, iarr[i], data);
            }
            break;

        case DataBuffer.TYPE_FLOAT:
            float farr[] = (float[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, farr[i], data);
            }
            break;

        case DataBuffer.TYPE_DOUBLE:
            double darr[] = (double[]) obj;
            for (int i = 0; i < numBands; i++) {
                setSample(x, y, i, darr[i], data);
            }
            break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof ComponentSampleModel)) {
            return false;
        }
        ComponentSampleModel model = (ComponentSampleModel) o;
        return this.width == model.width && this.height == model.height &&
               this.numBands == model.numBands &&
               this.dataType == model.dataType &&
               Arrays.equals(this.bandOffsets, model.bandOffsets) &&
               Arrays.equals(this.bankIndices, model.bankIndices) &&
               this.numBands == model.numBands &&
               this.numBanks == model.numBanks &&
               this.scanlineStride == model.scanlineStride &&
               this.pixelStride == model.pixelStride;
    }

    @Override
    public SampleModel createSubsetSampleModel(int bands[]) {
        if (bands.length > this.numBands) {
            // awt.64=The number of the bands in the subset is greater than the number of bands in the sample model
            throw new RasterFormatException(Messages.getString("awt.64")); //$NON-NLS-1$
        }

        int indices[] = new int[bands.length];
        int offsets[] = new int[bands.length];

        for (int i = 0; i < bands.length; i++) {
            indices[i] = bankIndices[bands[i]];
            offsets[i] = bandOffsets[bands[i]];
        }

        return new ComponentSampleModel(dataType, width, height, pixelStride,
                scanlineStride, indices, offsets);

    }

    @Override
    public SampleModel createCompatibleSampleModel(int w, int h) {
        return new ComponentSampleModel(dataType, w, h, pixelStride,
                pixelStride * w, bankIndices, bandOffsets);
    }

    @Override
    public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {
        int pixel[];

        if (iArray == null) {
            pixel = new int[numBands];
        } else {
            pixel = iArray;
        }

        for (int i = 0; i < numBands; i++) {
            pixel[i] = getSample(x, y, i, data);
        }

        return pixel;
    }

    @Override
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
        for (int i = 0; i < numBands; i++) {
            setSample(x, y, i, iArray[i], data);
        }
    }

    @Override
    public int getSample(int x, int y, int b, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElem(bankIndices[b], y * scanlineStride +
                x * pixelStride + bandOffsets[b]);
    }

    @Override
    public float getSampleFloat(int x, int y, int b, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElemFloat(bankIndices[b], y * scanlineStride +
                x * pixelStride + bandOffsets[b]);
    }

    @Override
    public double getSampleDouble(int x, int y, int b, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        return data.getElemDouble(bankIndices[b], y * scanlineStride +
                x * pixelStride + bandOffsets[b]);
    }

    @Override
    public int[] getPixels(int x, int y, int w, int h, int iArray[],
            DataBuffer data) {
        if (x < 0 || y < 0 || x > this.width || x + w > this.width
                || y > this.height || y + h > this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        int pixels[] = null;
        int idx = 0;

        if (iArray == null) {
            pixels = new int[w * h * numBands];
        } else {
            pixels = iArray;
        }

        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                for (int n = 0; n < numBands; n++) {
                    pixels[idx++] = getSample(j, i, n, data);
                }
            }
        }

        return pixels;
    }

    @Override
    public void setPixels(int x, int y, int w, int h, int iArray[],
            DataBuffer data) {
        if (x < 0 || y < 0 || x + w > this.width || y + h > this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        int idx = 0;
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                for (int n = 0; n < numBands; n++) {
                    setSample(j, i, n, iArray[idx++], data);
                }
            }
        }
    }

    @Override
    public void setSample(int x, int y, int b, int s, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElem(bankIndices[b], y * scanlineStride + x * pixelStride
                + bandOffsets[b], s);
    }

    @Override
    public int[] getSamples(int x, int y, int w, int h, int b, int iArray[],
            DataBuffer data) {
        if (x < 0 || y < 0 || x + w > this.width || y + h > this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        int samples[];
        int idx = 0;

        if (iArray == null) {
            samples = new int[w * h];
        } else {
            samples = iArray;
        }
        
        if (data == null) {
            // awt.295=data is null
            throw new NullPointerException(Messages.getString("awt.295")); //$NON-NLS-1$
        }

        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                samples[idx++] = getSample(j, i, b, data);
            }
        }

        return samples;
    }

    @Override
    public void setSamples(int x, int y, int w, int h, int b, int iArray[],
            DataBuffer data) {
        if (x < 0 || y < 0 || x + w > this.width || y + h > this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        int idx = 0;
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                setSample(j, i, b, iArray[idx++], data);
            }
        }
    }

    @Override
    public void setSample(int x, int y, int b, float s, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElemFloat(bankIndices[b], y * scanlineStride +
                x * pixelStride + bandOffsets[b], s);
    }

    @Override
    public void setSample(int x, int y, int b, double s, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        data.setElemDouble(bankIndices[b], y * scanlineStride +
                x * pixelStride + bandOffsets[b], s);
    }

    @Override
    public DataBuffer createDataBuffer() {
        DataBuffer data = null;

        int maxOffset = bandOffsets[0];
        for (int i = 1; i < bandOffsets.length; i++) {
            if (bandOffsets[i] > maxOffset) {
                maxOffset = bandOffsets[i];
            }
        }
        int size = (height - 1) * scanlineStride +
        (width - 1) * pixelStride + maxOffset + 1;

        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            data = new DataBufferByte(size, numBanks);
            break;
        case DataBuffer.TYPE_SHORT:
            data = new DataBufferShort(size, numBanks);
            break;
        case DataBuffer.TYPE_USHORT:
            data = new DataBufferUShort(size, numBanks);
            break;
        case DataBuffer.TYPE_INT:
            data = new DataBufferInt(size, numBanks);
            break;
        case DataBuffer.TYPE_FLOAT:
            data = new DataBufferFloat(size, numBanks);
            break;
        case DataBuffer.TYPE_DOUBLE:
            data = new DataBufferDouble(size, numBanks);
            break;
        }

        return data;

    }

    public int getOffset(int x, int y, int b) {
        return y * scanlineStride + x * pixelStride + bandOffsets[b];
    }

    public int getOffset(int x, int y) {
        return y * scanlineStride + x * pixelStride + bandOffsets[0];
    }

    @Override
    public final int getSampleSize(int band) {
        return DataBuffer.getDataTypeSize(dataType);
    }

    @Override
    public final int[] getSampleSize() {
        int sampleSizes[] = new int[numBands];
        int size = DataBuffer.getDataTypeSize(dataType);

        for (int i = 0; i < numBands; i++) {
            sampleSizes[i] = size;
        }
        return sampleSizes;
    }

    public final int[] getBankIndices() {
        return bankIndices.clone();
    }

    public final int[] getBandOffsets() {
        return bandOffsets.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int tmp = 0;

        hash = width;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= height;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= numBands;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= dataType;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        for (int element : bandOffsets) {
            hash ^= element;
            tmp = hash >>> 24;
            hash <<= 8;
            hash |= tmp;
        }
        for (int element : bankIndices) {
            hash ^= element;
            tmp = hash >>> 24;
            hash <<= 8;
            hash |= tmp;
        }
        hash ^= pixelStride;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;

        hash ^= scanlineStride;
        return hash;
    }

    public final int getScanlineStride() {
        return scanlineStride;
    }

    public final int getPixelStride() {
        return pixelStride;
    }

    @Override
    public final int getNumDataElements() {
        return numBands;
    }

}



