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
 * Created on 10.11.2005
 *
 */
package org.apache.harmony.awt.gl;


import org.apache.harmony.awt.gl.color.LUTColorConverter;
import org.apache.harmony.awt.gl.image.DataBufferListener;
import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.Rectangle;
import com.google.code.appengine.awt.color.ColorSpace;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.DataBuffer;
import com.google.code.appengine.awt.image.WritableRaster;



/**
 * This class represent Surface for different types of Images (BufferedImage, 
 * OffscreenImage and so on) 
 */
public class ImageSurface extends Surface implements DataBufferListener {

    int surfaceType;
    int csType;
    ColorModel cm;
    WritableRaster raster;
    Object data;
    
    boolean needToRefresh = true;
    boolean dataTaken = false;
    
    AwtImageBackdoorAccessor ba = AwtImageBackdoorAccessor.getInstance();

    public ImageSurface(ColorModel cm, WritableRaster raster){
        this(cm, raster, Surface.getType(cm, raster));
    }

    public ImageSurface(ColorModel cm, WritableRaster raster, int type){
        if (!cm.isCompatibleRaster(raster)) {
            // awt.4D=The raster is incompatible with this ColorModel
            throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
        }
        this.cm = cm;
        this.raster = raster;
        surfaceType = type;

        DataBuffer db = raster.getDataBuffer();
        data = ba.getData(db);
        ba.addDataBufferListener(db, this);
        ColorSpace cs = cm.getColorSpace();
        transparency = cm.getTransparency();
        width = raster.getWidth();
        height = raster.getHeight();
        addDirtyRegion(new Rectangle(0, 0, width, height));

        // For the moment we can build natively only images which have 
        // sRGB, Linear_RGB, Linear_Gray Color Space and type different
        // from BufferedImage.TYPE_CUSTOM
        if(cs == LUTColorConverter.sRGB_CS){
            csType = sRGB_CS;
        }else if(cs == LUTColorConverter.LINEAR_RGB_CS){
            csType = Linear_RGB_CS;
        }else if(cs == LUTColorConverter.LINEAR_GRAY_CS){
            csType = Linear_Gray_CS;
        }else{
            csType = Custom_CS;
        }
    }

    @Override
    public ColorModel getColorModel() {
        return cm;
    }

    @Override
    public WritableRaster getRaster() {
        return raster;
    }

    @Override
    public Object getData(){
        return data;
    }

    @Override
    public boolean isNativeDrawable(){
        return false;
    }

    @Override
    public int getSurfaceType() {
        return surfaceType;
    }

    @Override
    public synchronized void dispose() {
        ba.removeDataBufferListener(raster.getDataBuffer());
    }

    /**
     * Supposes that new raster is compatible with an old one
     * @param r
     */
    public void setRaster(WritableRaster r) {
        raster = r;
        DataBuffer db = r.getDataBuffer();
        data = ba.getData(db);
        ba.addDataBufferListener(db, this);
        this.width = r.getWidth();
        this.height = r.getHeight();
    }

    @Override
    public long lock() {
        // TODO
        return 0;
    }

    @Override
    public void unlock() {
        //TODO
    }

    @Override
    public Surface getImageSurface() {
        return this;
    }

    public void dataChanged() {
        needToRefresh = true;
        clearValidCaches();
    }

    public void dataTaken() {
        dataTaken = true;
        needToRefresh = true;
        clearValidCaches();
    }
    
    public void dataReleased(){
        dataTaken = false;
        needToRefresh = true;
        clearValidCaches();
    }
    
    @Override
    public void invalidate(){
        needToRefresh = true;
        clearValidCaches();
    }
    
    @Override
    public void validate(){
        if(!needToRefresh) {
            return;
        }
        if(!dataTaken){
            needToRefresh = false;
            AwtImageBackdoorAccessor ba = AwtImageBackdoorAccessor.getInstance();
            ba.validate(raster.getDataBuffer());
        }
        releaseDurtyRegions();
        
    }
    
    @Override
    public boolean invalidated(){
        return needToRefresh | dataTaken;
    }
}
