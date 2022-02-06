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


import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.image.DataBuffer;
import com.google.code.appengine.awt.image.DataBufferByte;
import com.google.code.appengine.awt.image.DataBufferInt;
import com.google.code.appengine.awt.image.DataBufferUShort;
import com.google.code.appengine.awt.image.MultiPixelPackedSampleModel;
import com.google.code.appengine.awt.image.RasterFormatException;
import com.google.code.appengine.awt.image.SampleModel;


public class MultiPixelPackedSampleModel extends SampleModel {

    private int pixelBitStride;

    private int scanlineStride;

    private int dataBitOffset;

    private int bitMask;

    private int dataElementSize;

    private int pixelsPerDataElement;

    public MultiPixelPackedSampleModel(int dataType, int w, int h,
            int numberOfBits, int scanlineStride, int dataBitOffset) {

        super(dataType, w, h, 1);
        if (dataType != DataBuffer.TYPE_BYTE &&
               dataType != DataBuffer.TYPE_USHORT &&
               dataType != DataBuffer.TYPE_INT) {
            // awt.61=Unsupported data type: {0}
            throw new IllegalArgumentException(Messages.getString("awt.61", //$NON-NLS-1$
                    dataType));
        }

        this.scanlineStride = scanlineStride;
        if(numberOfBits == 0) {
            // awt.20C=Number of Bits equals to zero
            throw new RasterFormatException(Messages.getString("awt.20C")); //$NON-NLS-1$
        }
        this.pixelBitStride = numberOfBits;
        this.dataElementSize = DataBuffer.getDataTypeSize(dataType);
        if(dataElementSize % pixelBitStride != 0) {
            // awt.20D=The number of bits per pixel is not a power of 2 or pixels span data element boundaries
            throw new RasterFormatException(Messages.getString("awt.20D")); //$NON-NLS-1$
        }

        if(dataBitOffset % numberOfBits != 0) {
            // awt.20E=Data Bit offset is not a multiple of pixel bit stride
            throw new RasterFormatException(Messages.getString("awt.20E")); //$NON-NLS-1$
        }
        this.dataBitOffset = dataBitOffset;

        this.pixelsPerDataElement = dataElementSize / pixelBitStride;
        this.bitMask = (1 << numberOfBits) - 1;
    }

    public MultiPixelPackedSampleModel(int dataType, int w, int h,
            int numberOfBits) {

        this(dataType, w, h, numberOfBits, (numberOfBits * w +
               DataBuffer.getDataTypeSize(dataType) - 1) /
               DataBuffer.getDataTypeSize(dataType), 0);
    }

    @Override
    public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        switch (getTransferType()) {
        case DataBuffer.TYPE_BYTE:
            byte bdata[];
            if (obj == null) {
                bdata = new byte[1];
            } else {
                bdata = (byte[]) obj;
            }
            bdata[0] = (byte) getSample(x, y, 0, data);
            obj = bdata;
            break;
        case DataBuffer.TYPE_USHORT:
            short sdata[];
            if (obj == null) {
                sdata = new short[1];
            } else {
                sdata = (short[]) obj;
            }
            sdata[0] = (short) getSample(x, y, 0, data);
            obj = sdata;
            break;
        case DataBuffer.TYPE_INT:
            int idata[];
            if (obj == null) {
                idata = new int[1];
            } else {
                idata = (int[]) obj;
            }
            idata[0] = getSample(x, y, 0, data);
            obj = idata;
            break;
        }

        return obj;
    }

    @Override
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        setSample(x, y, obj, data, 1, 0);
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof MultiPixelPackedSampleModel)) {
            return false;
        }

        MultiPixelPackedSampleModel model = (MultiPixelPackedSampleModel) o;
        return this.width == model.width &&
               this.height == model.height &&
               this.numBands == model.numBands &&
               this.dataType == model.dataType &&
               this.pixelBitStride == model.pixelBitStride &&
               this.bitMask == model.bitMask &&
               this.pixelsPerDataElement == model.pixelsPerDataElement &&
               this.dataElementSize == model.dataElementSize &&
               this.dataBitOffset == model.dataBitOffset &&
               this.scanlineStride == model.scanlineStride;
    }

    @Override
    public SampleModel createSubsetSampleModel(int bands[]) {
        if (bands != null && bands.length != 1) {
            // awt.20F=Number of bands must be only 1
            throw new RasterFormatException(Messages.getString("awt.20F")); //$NON-NLS-1$
        }
        return createCompatibleSampleModel(width, height);
    }

    @Override
    public SampleModel createCompatibleSampleModel(int w, int h) {
        return new MultiPixelPackedSampleModel(dataType, w, h, pixelBitStride);
    }

    @Override
    public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }
        int pixel[];
        if (iArray == null) {
            pixel = new int[numBands];
        } else {
            pixel = iArray;
        }

        pixel[0] = getSample(x, y, 0, data);
        return pixel;
    }

    @Override
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
        setSample(x, y, iArray, data, 2, 0);
    }

    @Override
    public int getSample(int x, int y, int b, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height || b != 0) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        int bitnum = dataBitOffset + x * pixelBitStride;
        int elem = data.getElem(y * scanlineStride + bitnum / dataElementSize);
        int shift = dataElementSize - (bitnum & (dataElementSize - 1)) -
                pixelBitStride;

        return (elem >> shift) & bitMask;
    }

    @Override
    public void setSample(int x, int y, int b, int s, DataBuffer data) {
        if (b != 0) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.63")); //$NON-NLS-1$
        }

        setSample(x, y, null, data, 3, s);
    }

    @Override
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;
        int size = scanlineStride * height;

        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            dataBuffer = new DataBufferByte(size + (dataBitOffset + 7) / 8);
            break;
        case DataBuffer.TYPE_USHORT:
            dataBuffer = new DataBufferUShort(size + (dataBitOffset + 15) / 16);
            break;
        case DataBuffer.TYPE_INT:
            dataBuffer = new DataBufferInt(size + (dataBitOffset + 31) / 32);
            break;
        }
        return dataBuffer;
    }

    public int getOffset(int x, int y) {
        return y * scanlineStride + (x * pixelBitStride + dataBitOffset) /
               dataElementSize;
    }

    @Override
    public int getSampleSize(int band) {
        return pixelBitStride;
    }

    public int getBitOffset(int x) {
        return (x * pixelBitStride + dataBitOffset) % dataElementSize;
    }

    @Override
    public int[] getSampleSize() {
        int sampleSizes[] = { pixelBitStride };
        return sampleSizes;
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
        hash ^= scanlineStride;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= pixelBitStride;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= dataBitOffset;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= bitMask;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= dataElementSize;
        tmp = hash >>> 24;
        hash <<= 8;
        hash |= tmp;
        hash ^= pixelsPerDataElement;
        return hash;
    }

    @Override
    public int getTransferType() {
        if (pixelBitStride > 16) {
            return DataBuffer.TYPE_INT;
        } else if (pixelBitStride > 8) {
            return DataBuffer.TYPE_USHORT;
        } else {
            return DataBuffer.TYPE_BYTE;
        }
    }

    public int getScanlineStride() {
        return scanlineStride;
    }

    public int getPixelBitStride() {
        return pixelBitStride;
    }

    @Override
    public int getNumDataElements() {
        return 1;
    }

    public int getDataBitOffset() {
        return dataBitOffset;
    }

    /**
     * This method is used by other methods of this class. The behaviour of
     * this method depends on the method which has been invoke this one. The
     * argument methodId is used to choose valid behaviour in a particular case.
     * If methodId is equal to 1 it means that this method has been invoked by
     * the setDataElements() method, 2 - means setPixel(), and setSample() in
     * any other cases.
     */
    private void setSample(final int x, final int y, final Object obj,
            final DataBuffer data, final int methodId, int s) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            // awt.63=Coordinates are not in bounds
            throw new ArrayIndexOutOfBoundsException(Messages
                    .getString("awt.63")); //$NON-NLS-1$
        }

        final int bitnum = dataBitOffset + x * pixelBitStride;
        final int idx = y * scanlineStride + bitnum / dataElementSize;
        final int shift = dataElementSize - (bitnum & (dataElementSize - 1))
                - pixelBitStride;
        final int mask = ~(bitMask << shift);
        int elem = data.getElem(idx);

        switch (methodId) {
        case 1: {                        // Invoked from setDataElements()
            switch (getTransferType()) {
            case DataBuffer.TYPE_BYTE:
                s = ((byte[]) obj)[0] & 0xff;
                break;
            case DataBuffer.TYPE_USHORT:
                s = ((short[]) obj)[0] & 0xffff;
                break;
            case DataBuffer.TYPE_INT:
                s = ((int[]) obj)[0];
                break;
            }
            break;
        }
        case 2: {                        // Invoked from setPixel()
            s = ((int[]) obj)[0];
            break;
        }
        }

        elem &= mask;
        elem |= (s & bitMask) << shift;
        data.setElem(idx, elem);
    }
}

