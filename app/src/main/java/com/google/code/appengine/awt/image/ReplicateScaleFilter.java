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

import java.util.Hashtable;

import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.ImageFilter;


public class ReplicateScaleFilter extends ImageFilter {

    protected int srcWidth;

    protected int srcHeight;

    protected int destWidth;

    protected int destHeight;

    protected int[] srcrows;

    protected int[] srccols;

    protected Object outpixbuf;

    public ReplicateScaleFilter(int width, int height) {
        if(width == 0 || height == 0) {
            // awt.234=Width or Height equals zero
            throw new IllegalArgumentException(Messages.getString("awt.234")); //$NON-NLS-1$
        }

        this.destWidth = width;
        this.destHeight = height;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Hashtable<?, ?> props) {
        Hashtable<Object, Object> fprops;
        if(props == null) {
            fprops = new Hashtable<Object, Object>();
        } else {
            fprops = (Hashtable<Object, Object>) props.clone();
        }
        String propName = "Rescale Filters"; //$NON-NLS-1$
        String prop = "destWidth=" + destWidth + "; " +  //$NON-NLS-1$ //$NON-NLS-2$
        "destHeight=" + destHeight; //$NON-NLS-1$
        Object o = fprops.get(propName);
        if(o != null){
            if(o instanceof String){
                prop = (String)o + "; " + prop; //$NON-NLS-1$
            }else{
                prop =  o.toString() + "; " + prop; //$NON-NLS-1$
            }
        }
        fprops.put(propName, prop);
        consumer.setProperties(fprops);
     }

    // setPixels methods produce pixels according to Java API Specification
    @Override
    public void setPixels(int x, int y, int w, int h, 
            ColorModel model, int[] pixels, int off, int scansize) {
        
        if(srccols == null) {
            initArrays();
        }
        int buff[];
        if(outpixbuf == null || !(outpixbuf instanceof int[])){
            buff = new int[destWidth];
            outpixbuf = buff;
        }else{
            buff = (int[])outpixbuf;
        }

        int wa = (srcWidth - 1) >>> 1;
        int ha = (srcHeight - 1) >>> 1;
        int dstX = (x * destWidth + wa) / srcWidth;
        int dstY = (y * destHeight + ha) / srcHeight;

        int sx, sy, dx, dy;
        dy = dstY;
        while((dy < destHeight) && ((sy = srcrows[dy]) < y + h)){
            dx = dstX;
            int srcOff = off + (sy - y) * scansize;
            while((dx < destWidth) && ((sx = srccols[dx]) < x + w)){
                buff[dx] = pixels[srcOff + (sx - x)];
                dx++;
            }

            consumer.setPixels(dstX, dy, dx - dstX, 1, model, buff, 
                    dstX, destWidth);
            dy++;
        }
    }

    @Override
    public void setPixels(int x, int y, int w, int h, 
            ColorModel model, byte[] pixels, int off, int scansize) {
        
        if(srccols == null) {
            initArrays();
        }
        byte buff[];
        if(outpixbuf == null || !(outpixbuf instanceof byte[])){
            buff = new byte[destWidth];
            outpixbuf = buff;
        }else{
            buff = (byte[])outpixbuf;
        }

        int wa = (srcWidth - 1) >>> 1;
        int ha = (srcHeight - 1) >>> 1;
        int dstX = (x * destWidth + wa) / srcWidth;
        int dstY = (y * destHeight + ha) / srcHeight;

        int sx, sy, dx, dy;
        dy = dstY;
        while((dy < destHeight) && ((sy = srcrows[dy]) < y + h)){
            dx = dstX;
            int srcOff = off + (sy - y) * scansize;
            while((dx < destWidth) && ((sx = srccols[dx]) < x + w)){
                buff[dx] = pixels[srcOff + (sx - x)];
                dx++;
            }

            consumer.setPixels(dstX, dy, dx - dstX, 1, model, buff, 
                    dstX, destWidth);
            dy++;
        }
    }

    @Override
    public void setDimensions(int w, int h) {
        srcWidth = w;
        srcHeight = h;

        if(destWidth < 0 && destHeight < 0){
            destWidth = srcWidth;
            destHeight = srcHeight;
        }else if(destWidth < 0){
            destWidth = destHeight * srcWidth / srcHeight;
        }else if(destHeight < 0){
            destHeight = destWidth * srcHeight / srcWidth;
        }
        consumer.setDimensions(destWidth, destHeight);
    }

    /**
     * Initialization of srccols and srcrows arrays
     */
    private void initArrays(){
        if ((destWidth < 0) || (destHeight < 0)) {
            throw new IndexOutOfBoundsException();
        }
        
        srccols = new int[destWidth];
        int ca = srcWidth >>> 1;
        for(int i = 0; i < destWidth; i++){
            srccols[i] = (i * srcWidth + ca) / destWidth;
        }

        srcrows = new int[destHeight];
        int ra = srcHeight >>> 1;
        for(int i = 0; i < destHeight; i++){
            srcrows[i] = (i * srcHeight + ra) / destHeight;
        }
    }

}


