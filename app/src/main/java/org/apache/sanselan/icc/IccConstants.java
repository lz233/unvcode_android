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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.BinaryConstants;
import org.apache.sanselan.common.BinaryInputStream;

public interface IccConstants
{
    public final static int IEC = (((0xff & 'I') << 24) | ((0xff & 'E') << 16)
            | ((0xff & 'C') << 8) | ((0xff & ' ') << 0));
    public final static int sRGB = (((0xff & 's') << 24) | ((0xff & 'R') << 16)
            | ((0xff & 'G') << 8) | ((0xff & 'B') << 0));

    public static final IccTagDataType descType = new IccTagDataType(
            "descType", 0x64657363)
    {
        public void dump(String prefix, byte bytes[])
                throws ImageReadException, IOException
        {
            BinaryInputStream bis = new BinaryInputStream(
                    new ByteArrayInputStream(bytes),
                    BinaryConstants.BYTE_ORDER_NETWORK);
            bis.read4Bytes("type_signature", "ICC: corrupt tag data");

            //            bis.setDebug(true);
            bis.read4Bytes("ignore", "ICC: corrupt tag data");
            int string_length = bis.read4Bytes("string_length",
                    "ICC: corrupt tag data");

            //            bis.readByteArray("ignore", bytes.length -12, "none");
            String s = new String(bytes, 12, string_length - 1);
            System.out.println(prefix + "s: '" + s + "'");
        }

    };

    public static final IccTagDataType dataType = new IccTagDataType(
            "dataType", 0x64617461)
    {
        public void dump(String prefix, byte bytes[])
                throws ImageReadException, IOException
        {
            BinaryInputStream bis = new BinaryInputStream(
                    new ByteArrayInputStream(bytes),
                    BinaryConstants.BYTE_ORDER_NETWORK);
            bis.read4Bytes("type_signature", "ICC: corrupt tag data");
        }

    };

    public static final IccTagDataType multiLocalizedUnicodeType = new IccTagDataType(
            "multiLocalizedUnicodeType", (0x6D6C7563))
    {
        public void dump(String prefix, byte bytes[])
                throws ImageReadException, IOException
        {
            BinaryInputStream bis = new BinaryInputStream(
                    new ByteArrayInputStream(bytes),
                    BinaryConstants.BYTE_ORDER_NETWORK);
            bis.read4Bytes("type_signature", "ICC: corrupt tag data");
        }

    };

    public static final IccTagDataType signatureType = new IccTagDataType(
            "signatureType", ((0x73696720)))
    {
        public void dump(String prefix, byte bytes[])
                throws ImageReadException, IOException
        {
            BinaryInputStream bis = new BinaryInputStream(
                    new ByteArrayInputStream(bytes),
                    BinaryConstants.BYTE_ORDER_NETWORK);
            bis.read4Bytes("type_signature", "ICC: corrupt tag data");
            bis.read4Bytes("ignore", "ICC: corrupt tag data");
            int thesignature = bis.read4Bytes("thesignature ",
                    "ICC: corrupt tag data");
            System.out.println(prefix
                    + "thesignature: "
                    + Integer.toHexString(thesignature)
                    + " ("
                    + new String(new byte[]{
                            (byte) (0xff & (thesignature >> 24)),
                            (byte) (0xff & (thesignature >> 16)),
                            (byte) (0xff & (thesignature >> 8)),
                            (byte) (0xff & (thesignature >> 0)),
                    }) + ")");
        }

    };

    public static final IccTagDataType textType = new IccTagDataType(
            "textType", 0x74657874)
    {
        public void dump(String prefix, byte bytes[])
                throws ImageReadException, IOException
        {
            BinaryInputStream bis = new BinaryInputStream(
                    new ByteArrayInputStream(bytes),
                    BinaryConstants.BYTE_ORDER_NETWORK);
            bis.read4Bytes("type_signature", "ICC: corrupt tag data");
            bis.read4Bytes("ignore", "ICC: corrupt tag data");
            String s = new String(bytes, 8, bytes.length - 8);
            System.out.println(prefix + "s: '" + s + "'");
        }

    };

    public static final IccTagDataType IccTagDataTypes[] = {
            descType, dataType, multiLocalizedUnicodeType, signatureType,
            textType,
    };
    //    public static final IccTagDataType dataType = new IccTagDataType("dataType",
    //            0x64617461
    //    );

    public static final IccTagType AToB0Tag = new IccTagType("AToB0Tag",
            "lut8Type or lut16Type or lutAtoBType", 0x41324230
    //            "This tag defines a color transform from Device to PCS using lookup table tag element structures. The processing mechanisms are described in lut8Type or lut16Type or lutAtoBType.");
    );

    //    public static final IccTagType AToB0Tag = new IccTagType(
    //    "AToB0Tag",
    //    "lut8Type or lut16Type or lutAtoBType",
    //    "A2B0 (41324230h)",
    //    "This tag defines a color transform from Device to PCS using lookup table tag element structures. The processing",
    //    "mechanisms are described in lut8Type or lut16Type or lutAtoBType.",
    //    );

    public static final IccTagType AToB1Tag = new IccTagType("AToB1Tag",
            "lut8Type or lut16Type or lutAtoBType", 0x41324231
    //    "This tag defines a color transform from Device to PCS using lookup table tag element structures. The processing",
    //    "mechanisms are described in lut8Type or lut16Type or lutAtoBType.",
    );

    public static final IccTagType AToB2Tag = new IccTagType("AToB2Tag",
            "lut8Type or lut16Type or lutAtoBType", 0x41324232
    //    "This tag defines a color transform from Device to PCS using lookup table tag element structures. The processing",
    //    "mechanisms are described in lut8Type or lut16Type or lutAtoBType.",
    );

    public static final IccTagType blueMatrixColumnTag = new IccTagType(
            "blueMatrixColumnTag", "XYZType", 0x6258595A
    //    "The third column in the matrix used in TRC/matrix transforms.",
    );

    public static final IccTagType blueTRCTag = new IccTagType("blueTRCTag",
            "curveType or parametricCurveType", 0x62545243
    //    "Blue channel tone reproduction curve. The first element represents no colorant (white) or phosphors",
    //    "(black) and the last element represents 100 percent colorant (blue) or 100 percent phosphor (blue).",
    );

    public static final IccTagType BToA0Tag = new IccTagType("BToA0Tag",
            "lut8Type or lut16Type or lutBtoAType", 0x42324130
    //    "This tag defines a color transform from PCS to Device using the lookup table tag element structures. The",
    //    "processing mechanisms are described in lut8Type or lut16Type or lutBtoAType.",
    );

    public static final IccTagType BToA1Tag = new IccTagType("BToA1Tag",
            "lut8Type or lut16Type or lutBtoAType", 0x42324131
    //    "This tag defines a color transform from PCS to Device using the lookup table tag element structures. The",
    //    "processing mechanisms are described in lut8Type or lut16Type or lutBtoAType.",
    );

    public static final IccTagType BToA2Tag = new IccTagType("BToA2Tag",
            "lut8Type or lut16Type or lutBtoAType", 0x42324132
    //    "This tag defines a color transform from PCS to Device using the lookup table tag element structures. The",
    //    "processing mechanisms are described in lut8Type or lut16Type or lutBtoAType.",
    );

    public static final IccTagType calibrationDateTimeTag = new IccTagType(
            "calibrationDateTimeTag", "dateTimeType", 0x63616C74
    //    "Profile calibration date and time. Initially, this tag matches the contents of the profile header's creation",
    //    "date/time field. This allows applications and utilities to verify if this profile matches a vendor's profile and",
    //    "how recently calibration has been performed.",
    );

    public static final IccTagType charTargetTag = new IccTagType(
            "charTargetTag", "textType", 0x74617267
    //    "This tag contains the name of the registered characterization data set, or it contains the measurement data",
    //    "for a characterization target. This tag is provided so that distributed utilities can identify the underlying",
    //    "characterization data, create transforms \"on the fly\" or check the current performance against the original",
    //    "device performance.",
    //    "The first seven characters of the text shall identify the nature of the characterization data.",
    //    "If the first seven characters are \"ICCHDAT\", then the remainder of the text shall be a single space followed",
    //    "by the Reference Name of a characterization data set in the ICC Characterization Data Registry and terminated",
    //    "with a NULL byte (00h). The Reference Name in the text must match exactly (including case) the",
    //    "Reference Name in the registry.",
    //    "If the first seven characters match one of the identifiers defined in an ANSI or ISO standard, then the tag",
    //    "embeds the exact data file format defined in that standard. Each of these file formats contains an identifying",
    //    "character string as the first seven characters of the format, allowing an external parser to determine",
    //    "which data file format is being used. This provides the facilities to include a wide range of targets using a",
    //    "variety of measurement specifications in a standard manner.",
    //    "NOTE: It is highly recommended that the profileDescriptionTag also include an identification of the characterization",
    //    "data that was used in the creation of the profile (e.g. \"Based on CGATS TR 001\").",
    );

    public static final IccTagType chromaticAdaptationTag = new IccTagType(
            "chromaticAdaptationTag", "s15Fixed16ArrayType", 0x63686164
    //    "This tag converts an XYZ color, measured at a device's specific illumination conditions, to an XYZ color in",
    //    "the PCS illumination conditions after complete adaptation.",
    //    "The tag reflects a survey of the currently used methods of conversion, all of which can be formulated as a",
    //    "matrix transformation (see Annex E). Such a 3 by 3 chromatic adaptation matrix is organized as a 9-element",
    //    "array of signed 15.16 numbers (s15Fixed16ArrayType tag). Similarly as in the other occurrences of a",
    //    "3 by 3 matrix in the ICC tags, the dimension corresponding to the matrix rows varies least rapidly while the",
    //    "one corresponding to the matrix columns varies most rapidly.",
    //    "(19)",
    //    "(20)",
    //    "array a0 a1 a2 a3 a4 a5 a6 a7 a8 =",
    //    "Xpcs",
    //    "Ypcs",
    //    "Zpcs",
    //    "a0 a1 a2",
    //    "a3 a4 a5",
    //    "a6 a7 a8",
    //    "Xsrc",
    //    "Ysrc",
    //    "Zsrc",
    //    "=",
    //    "Where XYZsrc represents the measured value in the actual device viewing condition and XYZpcs represents",
    //    "the chromatically adapted value in the PCS.",
    //    "The chromatic adaptation matrix is a combination of three separate conversions:",
    //    "1) Conversion of source CIE XYZ tristimulus values to cone response tristimulus values.",
    //    "2) Adjustment of the cone response values for an observer's chromatic adaptation.",
    //    "3) Conversion of the adjusted cone response tristimulus back to CIE XYZ values.",
    );

    public static final IccTagType chromaticityTag = new IccTagType(
            "chromaticityTag", "chromaticityType", 0x6368726D
    //    "The data and type of phosphor/colorant chromaticity set.",
    );

    public static final IccTagType colorantOrderTag = new IccTagType(
            "colorantOrderTag", "colorantOrderType", 0x636C726F
    //    "This tag specifies the laydown order of colorants.",
    );

    public static final IccTagType colorantTableTag = new IccTagType(
            "colorantTableTag", "colorantTableType", 0x636C7274
    //    "This tag identifies the colorants used in the profile by a unique name and an XYZ or L*a*b* value.",
    //    "This is a required tag for profiles where the color space defined in the header is xCLR, where x is one of",
    //    "the allowed numbers from 2 through Fh, per Table 13. See Section 6.3.3.2, Section 6.3.4.1.",
    );

    public static final IccTagType copyrightTag = new IccTagType(
            "copyrightTag", "multiLocalizedUnicodeType", 0x63707274
    //    "This tag contains the text copyright information for the profile.",
    );

    public static final IccTagType deviceMfgDescTag = new IccTagType(
            "deviceMfgDescTag", "multiLocalizedUnicodeType", 0x646D6E64
    //    "Structure containing invariant and localizable versions of the device manufacturer for display. The content",
    //    "of this structure is described in 6.5.12.",
    );

    public static final IccTagType deviceModelDescTag = new IccTagType(
            "deviceModelDescTag", "multiLocalizedUnicodeType", 0x646D6464
    //    "Structure containing invariant and localizable versions of the device model for display. The content of this",
    //    "structure is described in 6.5.12.",
    );

    public static final IccTagType gamutTag = new IccTagType("gamutTag",
            "lut8Type or lut16Type or lutBtoAType", 0x67616D74
    //    "Out of gamut tag. The processing mechanisms are described in lut8Type or lut16Type or lutBtoAType.",
    //    "This tag takes PCS values as its input and produces a single channel of output. If the output value is 0, the",
    //    "PCS color is in-gamut. If the output is non-zero, the PCS color is out-of-gamut, with the output value �n+1�",
    //    "being at least as far out of gamut as the output value n.",
    );

    public static final IccTagType grayTRCTag = new IccTagType("grayTRCTag",
            "curveType or parametricCurveType", 0x6B545243
    //    "Gray tone reproduction curve. The tone reproduction curve provides the necessary information to convert",
    //    "between a single device channel and the CIEXYZ encoding of the profile connection space. The first element",
    //    "represents black and the last element represents white.",
    );

    public static final IccTagType greenMatrixColumnTag = new IccTagType(
            "greenMatrixColumnTag", "XYZType", 0x6758595A
    //    "The second column in the matrix used in TRC/matrix transforms.",
    );

    public static final IccTagType greenTRCTag = new IccTagType(
    //    "6.4.21 ",
            "greenTRCTag", "curveType or parametricCurveType", 0x67545243
    //    "Green channel tone reproduction curve. The first element represents no colorant (white) or phosphors",
    //    "(black) and the last element represents 100 percent colorant (green) or 100 percent phosphor (green).",
    );

    public static final IccTagType luminanceTag = new IccTagType(
    //    "6.4.22 ",
            "luminanceTag", "XYZType", 0x6C756D69
    //    "Absolute luminance of emissive devices in candelas per square meter as described by the Y channel. The",
    //    "X and Z channels are ignored in all cases.",
    );

    public static final IccTagType measurementTag = new IccTagType(
    //    "6.4.23 ",
            "measurementTag", "measurementType", 0x6D656173
    //    "Alternative measurement specification such as a D65 illuminant instead of the default D50.",
    );

    public static final IccTagType mediaBlackPointTag = new IccTagType(
    //    "6.4.24 ",
            "mediaBlackPointTag", "XYZType", 0x626B7074
    //    "This tag specifies the media black point and contains the CIE 1931 XYZ colorimetry of the black point of",
    //    "the actual medium.",
    //    "NOTE Previous revisions of this specification contained an error indicating that this tag is used to calculate",
    //    "ICC-absolute colorimetry. This is not the case.",
    );

    public static final IccTagType mediaWhitePointTag = new IccTagType(
    //    "6.4.25 ",
            "mediaWhitePointTag", "XYZType", 0x77747074
    //    "This tag, which is used for generating ICC-absolute colorimetric intent, specifies the XYZ tristimulus values",
    //    "of the media white point. If the media is measured under an illumination source which has a chromaticity",
    //    "other than D50, the measured values must be adjusted to D50 using the chromaticAdaptationTag matrix",
    //    "before recording in the tag. For reflecting and transmitting media, the tag values are specified relative to",
    //    "the perfect diffuser (which is normalized to a Y value of 1,0) for illuminant D50. For displays, the values",
    //    "specified must be those of D50 (i.e. 0,9642, 1,0 0,8249) normalized such that Y = 1,0.",
    //    "See Annex A for a more complete description of the use of the media white point.",
    );

    public static final IccTagType namedColor2Tag = new IccTagType(
    //    "6.4.26 ",
            "namedColor2Tag", "namedColor2Type", 0x6E636C32
    //    "Named color information providing a PCS and optional device representation for a list of named colors.",
    );

    public static final IccTagType outputResponseTag = new IccTagType(
    //    "6.4.27 ",
            "outputResponseTag", "responseCurveSet16Type", 0x72657370
    //    "Structure containing a description of the device response for which the profile is intended. The content of",
    //    "this structure is described in 6.5.16.",
    //    "NOTE The user's attention is called to the possibility that the use of this tag for device calibration may",
    //    "require use of an invention covered by patent rights. By publication of this specification, no position is",
    //    "taken with respect to the validity of this claim or of any patent rights in connection therewith. The patent",
    //    "holder has, however, filed a statement of willingness to grant a license under these rights on reasonable",
    //    "and nondiscriminatory terms and conditions to applicants desiring to obtain such a license. Details may be",
    //    "obtained from the publisher.",
    );

    public static final IccTagType preview0Tag = new IccTagType(
    //    "6.4.28 ",
            "preview0Tag", "lut8Type or lut16Type or lutBtoAType", 0x70726530
    //    "Preview transformation from PCS to device space and back to the PCS. The processing mechanisms are",
    //    "described in lut8Type or lut16Type or lutBtoAType.",
    //    "This tag contains the combination of tag B2A0 and tag A2B1.",
    );

    public static final IccTagType preview1Tag = new IccTagType(
    //    "6.4.29 ",
            "preview1Tag", "lut8Type or lut16Type or lutBtoAType", 0x70726531
    //    "Preview transformation from the PCS to device space and back to the PCS. The processing mechanisms",
    //    "are described in lut8Type or lut16Type or lutBtoAType.",
    //    "This tag contains the combination of tag B2A1 and tag A2B1.",
    );

    public static final IccTagType preview2Tag = new IccTagType(
    //    "6.4.30 ",
            "preview2Tag", "lut8Type or lut16Type or lutBtoAType", 0x70726532
    //    "Preview transformation from PCS to device space and back to the PCS. The processing mechanisms are",
    //    "described in lut8Type or lut16Type or lutBtoAType.",
    //    "This tag contains the combination of tag B2A2 and tag A2B1.",
    );

    public static final IccTagType profileDescriptionTag = new IccTagType(
    //    "6.4.31 ",
            "profileDescriptionTag", "multiLocalizedUnicodeType", 0x64657363
    //    "Structure containing invariant and localizable versions of the profile description for display. The content of",
    //    "this structure is described in 6.5.12. This invariant description has no fixed relationship to the actual profile",
    //    "disk file name.",
    );

    public static final IccTagType profileSequenceDescTag = new IccTagType(
    //    "6.4.32 ",
            "profileSequenceDescTag", "profileSequenceDescType", 0x70736571
    //    "Structure containing a description of the profile sequence from source to destination, typically used with",
    //    "the DeviceLink profile. The content of this structure is described in 6.5.15.",
    );

    public static final IccTagType redMatrixColumnTag = new IccTagType(
    //    "6.4.33 ",
            "redMatrixColumnTag", "XYZType", 0x7258595A
    //    "The first column in the matrix used in TRC/matrix transforms.",
    );

    public static final IccTagType redTRCTag = new IccTagType(
    //    "6.4.34 ",
            "redTRCTag", "curveType or parametricCurveType", 0x72545243
    //    "Red channel tone reproduction curve. The first element represents no colorant (white) or phosphors",
    //    "(black) and the last element represents 100 percent colorant (red) or 100 percent phosphor (red).",
    );

    public static final IccTagType technologyTag = new IccTagType(
    //    "6.4.35 ",
            "technologyTag", "signatureType", 0x74656368
    //    "Device technology information such as CRT, Dye Sublimation, etc. The encoding is such that:",
    );

    public static final IccTagType viewingCondDescTag = new IccTagType(
    //    "6.4.36 ",
            "viewingCondDescTag", "multiLocalizedUnicodeType", 0x76756564
    //    "Structure containing invariant and localizable versions of the viewing conditions. The content of this structure",
    //    "is described in 6.5.12.",

    );

    public static final IccTagType viewingConditionsTag = new IccTagType(
    //    "6.4.37 ",
            "viewingConditionsTag", "viewingConditionsType", 0x76696577
    //    "Viewing conditions parameters. The content of this structure is described in 6.5.25.",
    );

    //    public static final IccTagType  = new IccTagType(
    //            //    "6.4.37 ",
    //                    "viewingConditionsTag", "viewingConditionsType", 0x76696577
    //            //    "Viewing conditions parameters. The content of this structure is described in 6.5.25.",
    //            );

    public static final IccTagType TagTypes[] = {
            AToB0Tag, AToB1Tag, AToB2Tag, blueMatrixColumnTag, blueTRCTag,
            BToA0Tag, BToA1Tag, BToA2Tag, calibrationDateTimeTag,
            charTargetTag, chromaticAdaptationTag, chromaticityTag,
            colorantOrderTag, colorantTableTag, copyrightTag, deviceMfgDescTag,
            deviceModelDescTag, gamutTag, grayTRCTag, greenMatrixColumnTag,
            greenTRCTag, luminanceTag, measurementTag, mediaBlackPointTag,
            mediaWhitePointTag, namedColor2Tag, outputResponseTag, preview0Tag,
            preview1Tag, preview2Tag, profileDescriptionTag,
            profileSequenceDescTag, redMatrixColumnTag, redTRCTag,
            technologyTag, viewingCondDescTag, viewingConditionsTag,
    };

}
