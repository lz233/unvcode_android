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
package org.apache.sanselan.icc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.sanselan.common.BinaryFileParser;
import org.apache.sanselan.common.byteSources.ByteSource;
import org.apache.sanselan.common.byteSources.ByteSourceArray;
import org.apache.sanselan.common.byteSources.ByteSourceFile;
import org.apache.sanselan.util.CachingInputStream;
import org.apache.sanselan.util.Debug;

import com.google.code.appengine.awt.color.ICC_Profile;


public class IccProfileParser extends BinaryFileParser implements IccConstants
{
    public IccProfileParser()
    {
        this.setByteOrder(BYTE_ORDER_NETWORK);
    }

    public IccProfileInfo getICCProfileInfo(ICC_Profile icc_profile)
    {
        if (icc_profile == null)
            return null;

        return getICCProfileInfo(new ByteSourceArray(icc_profile.getData()));
    }

    public IccProfileInfo getICCProfileInfo(byte bytes[])
    {
        if (bytes == null)
            return null;

        return getICCProfileInfo(new ByteSourceArray(bytes));
    }

    public IccProfileInfo getICCProfileInfo(File file)
    {
        if (file == null)
            return null;

        return getICCProfileInfo(new ByteSourceFile(file));
    }

    public IccProfileInfo getICCProfileInfo(ByteSource byteSource)
    {

        InputStream is = null;

        try
        {

            IccProfileInfo result;
            {
                is = byteSource.getInputStream();

                result = readICCProfileInfo(is);
            }

            if (result == null)
                return null;

            is.close();
            is = null;

            for (int i = 0; i < result.tags.length; i++)
            {
                IccTag tag = result.tags[i];
                byte bytes[] = byteSource.getBlock(tag.offset, tag.length);
                //                Debug.debug("bytes: " + bytes.length);
                tag.setData(bytes);
                //                tag.dump("\t" + i + ": ");
            }
            //            result.fillInTagData(byteSource);

            return result;
        }
        catch (Exception e)
        {
            //            Debug.debug("Error: " + file.getAbsolutePath());
            Debug.debug(e);
        }
        finally
        {
            try
            {
                if (is != null)
                    is.close();
            }
            catch (Exception e)
            {
                Debug.debug(e);
            }

        }

        if (debug)
            Debug.debug();

        return null;
    }

    private IccProfileInfo readICCProfileInfo(InputStream is)
    {
        CachingInputStream cis = new CachingInputStream(is);
        is = cis;

        if (debug)
            Debug.debug();

        //                setDebug(true);

        //        if (debug)
        //            Debug.debug("length: " + length);

        try
        {
            int ProfileSize = read4Bytes("ProfileSize", is,
                    "Not a Valid ICC Profile");

            //            if (length != ProfileSize)
            //            {
            //                //                Debug.debug("Unexpected Length data expected: " + Integer.toHexString((int) length)
            //                //                        + ", encoded: " + Integer.toHexString(ProfileSize));
            //                //                Debug.debug("Unexpected Length data: " + length
            //                //                        + ", length: " + ProfileSize);
            //                //                throw new Error("asd");
            //                return null;
            //            }

            int CMMTypeSignature = read4Bytes("Signature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("CMMTypeSignature", CMMTypeSignature);

            int ProfileVersion = read4Bytes("ProfileVersion", is,
                    "Not a Valid ICC Profile");

            int ProfileDeviceClassSignature = read4Bytes(
                    "ProfileDeviceClassSignature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ProfileDeviceClassSignature",
                        ProfileDeviceClassSignature);

            int ColorSpace = read4Bytes("ColorSpace", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ColorSpace", ColorSpace);

            int ProfileConnectionSpace = read4Bytes("ProfileConnectionSpace",
                    is, "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ProfileConnectionSpace", ProfileConnectionSpace);

            skipBytes(is, 12, "Not a Valid ICC Profile");

            int ProfileFileSignature = read4Bytes("ProfileFileSignature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ProfileFileSignature", ProfileFileSignature);

            int PrimaryPlatformSignature = read4Bytes(
                    "PrimaryPlatformSignature", is, "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("PrimaryPlatformSignature",
                        PrimaryPlatformSignature);

            int VariousFlags = read4Bytes("ProfileFileSignature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ProfileFileSignature", ProfileFileSignature);

            int DeviceManufacturer = read4Bytes("ProfileFileSignature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("DeviceManufacturer", DeviceManufacturer);

            int DeviceModel = read4Bytes("DeviceModel", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("DeviceModel", DeviceModel);

            skipBytes(is, 8, "Not a Valid ICC Profile");

            int RenderingIntent = read4Bytes("RenderingIntent", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("RenderingIntent", RenderingIntent);

            skipBytes(is, 12, "Not a Valid ICC Profile");

            int ProfileCreatorSignature = read4Bytes("ProfileCreatorSignature",
                    is, "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("ProfileCreatorSignature",
                        ProfileCreatorSignature);

            byte ProfileID[] = null;
            skipBytes(is, 16, "Not a Valid ICC Profile");
            //            readByteArray("ProfileID", 16, is,
            //                    "Not a Valid ICC Profile");
            //            if (debug)
            //                System.out
            //                        .println("ProfileID: '" + new String(ProfileID) + "'");

            skipBytes(is, 28, "Not a Valid ICC Profile");

            //            this.setDebug(true);

            int TagCount = read4Bytes("TagCount", is, "Not a Valid ICC Profile");

            //            ArrayList tags = new ArrayList();
            IccTag tags[] = new IccTag[TagCount];

            for (int i = 0; i < TagCount; i++)
            {
                int TagSignature = read4Bytes("TagSignature[" + i + "]", is,
                        "Not a Valid ICC Profile");
                //                Debug.debug("TagSignature t "
                //                        + Integer.toHexString(TagSignature));

                //                this.printCharQuad("TagSignature", TagSignature);
                int OffsetToData = read4Bytes("OffsetToData[" + i + "]", is,
                        "Not a Valid ICC Profile");
                int ElementSize = read4Bytes("ElementSize[" + i + "]", is,
                        "Not a Valid ICC Profile");

                IccTagType fIccTagType = getIccTagType(TagSignature);
                //                if (fIccTagType == null)
                //                    throw new Error("oops.");

                //                System.out
                //                        .println("\t["
                //                                + i
                //                                + "]: "
                //                                + ((fIccTagType == null)
                //                                        ? "unknown"
                //                                        : fIccTagType.name));
                //                Debug.debug();

                IccTag tag = new IccTag(TagSignature, OffsetToData,
                        ElementSize, fIccTagType);
                //                tag.dump("\t" + i + ": ");
                tags[i] = tag;
                //                tags .add(tag);
            }

            {
                // read stream to end, filling cache.
                while (is.read() >= 0)
                    ;
            }

            byte data[] = cis.getCache();

            if (data.length < ProfileSize)
                throw new IOException("Couldn't read ICC Profile.");

            IccProfileInfo result = new IccProfileInfo(data, ProfileSize,
                    CMMTypeSignature, ProfileVersion,
                    ProfileDeviceClassSignature, ColorSpace,
                    ProfileConnectionSpace, ProfileFileSignature,
                    PrimaryPlatformSignature, VariousFlags, DeviceManufacturer,
                    DeviceModel, RenderingIntent, ProfileCreatorSignature,
                    ProfileID, tags);

            if (debug)
                Debug.debug("issRGB: " + result.issRGB());

            return result;
        }
        catch (Exception e)
        {
            Debug.debug(e);
        }

        return null;
    }

    private IccTagType getIccTagType(int quad)
    {
        for (int i = 0; i < TagTypes.length; i++)
            if (TagTypes[i].signature == quad)
                return TagTypes[i];

        return null;
    }

    public Boolean issRGB(ICC_Profile icc_profile)
    {
        if (icc_profile == null)
            return null;

        return issRGB(new ByteSourceArray(icc_profile.getData()));
    }

    public Boolean issRGB(byte bytes[])
    {
        if (bytes == null)
            return null;

        return issRGB(new ByteSourceArray(bytes));
    }

    public Boolean issRGB(File file)
    {
        if (file == null)
            return null;

        return issRGB(new ByteSourceFile(file));
    }

    public Boolean issRGB(ByteSource byteSource)
    {
        try
        {
            if (debug)
                Debug.debug();

            //            setDebug(true);

            //            long length = byteSource.getLength();
            //
            //            if (debug)
            //                Debug.debug("length: " + length);

            InputStream is = byteSource.getInputStream();

            int ProfileSize = read4Bytes("ProfileSize", is,
                    "Not a Valid ICC Profile");

            //            if (length != ProfileSize)
            //                return null;

            this.skipBytes(is, 4 * 5);

            skipBytes(is, 12, "Not a Valid ICC Profile");

            this.skipBytes(is, 4 * 3);

            int DeviceManufacturer = read4Bytes("ProfileFileSignature", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("DeviceManufacturer", DeviceManufacturer);

            int DeviceModel = read4Bytes("DeviceModel", is,
                    "Not a Valid ICC Profile");
            if (debug)
                printCharQuad("DeviceModel", DeviceModel);

            boolean result = ((DeviceManufacturer == IEC) && (DeviceModel == sRGB));

            return new Boolean(result);
        }
        catch (Exception e)
        {
            Debug.debug(e);
        }

        return null;
    }

}