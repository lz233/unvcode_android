package com.google.code.appengine.awt.font.sfntly;

import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.font.LineMetrics;

public class SfntlyLineMetrics extends LineMetrics {

    // array of baseline offsets
    float[] baselineOffsets;

    // the number of characters to measure
    int numChars;

    // baseline index of the font corresponding to this line metrics
    int baseLineIndex;

    // underline thickness
    float underlineThickness;

    // underline offset
    float underlineOffset;

    // strikethrough thickness
    float strikethroughThickness;

    // strikethrough offset
    float strikethroughOffset;

    // External leading
    float leading;


    // Ascent of the font
    float ascent;

    // Descent of the font
    float descent;

    // Width of the widest char in the font
    float maxCharWidth;

    // units per EM square in font value
    int units_per_EM = 0;

   

    public SfntlyLineMetrics(){

    }

    /**
     * Returns offset of the baseline.
     */
    @Override
    public float[] getBaselineOffsets() {
        // XXX: at the moment there only horizontal metrics are taken into
        // account. If there is no baseline information in TrueType font
        // file default values used: {0, -ascent, (-ascent+descent)/2}

        return baselineOffsets;
    }

    /**
     * Returns a number of chars in specified text
     */
    @Override
    public int getNumChars() {
        return numChars;
    }

    /**
     * Returns index of the baseline, one of predefined constants.
     */
    @Override
    public int getBaselineIndex() {
        // Baseline index is the deafult baseline index value
        // taken from the TrueType table "BASE".
        return baseLineIndex;
    }

    /**
     * Returns thickness of the Underline.
     */
    @Override
    public float getUnderlineThickness() {
        return underlineThickness;
    }

    /**
     * Returns offset of the Underline.
     */
    @Override
    public float getUnderlineOffset() {
        return underlineOffset;
    }

    /**
     * Returns thickness of the Strikethrough line.
     */
    @Override
    public float getStrikethroughThickness() {
        return strikethroughThickness;
    }

    /**
     * Returns offset of the Strikethrough line.
     */
    @Override
    public float getStrikethroughOffset() {
        return strikethroughOffset;
    }

    /**
     * Returns the leading.
     */
    @Override
    public float getLeading() {
        return leading;
    }

    /**
     * Returns the height of the font.
     */
    @Override
    public float getHeight() {
        return ascent + descent + leading;
    }

    /**
     * Returns the descent.
     */
    @Override
    public float getDescent() {
        return descent;
    }

    /**
     * Returns the ascent.
     */
    @Override
    public float getAscent() {
        return ascent;
    }


    @Override
    public Object clone(){
        try{
            return super.clone();
        }catch (CloneNotSupportedException e){
            return null;
        }
    }

}
