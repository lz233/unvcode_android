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

import java.util.Vector;

import com.google.code.appengine.awt.Rectangle;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.awt.image.SampleModel;
import com.google.code.appengine.awt.image.WritableRaster;


public interface RenderedImage {

    public Object getProperty(String name);

    public WritableRaster copyData(WritableRaster raster);

    public Raster getData(Rectangle rect);

    public Vector<RenderedImage> getSources();

    public String[] getPropertyNames();

    public SampleModel getSampleModel();

    public Raster getTile(int tileX, int tileY);

    public Raster getData();

    public ColorModel getColorModel();

    public int getWidth();

    public int getTileWidth();

    public int getTileHeight();

    public int getTileGridYOffset();

    public int getTileGridXOffset();

    public int getNumYTiles();

    public int getNumXTiles();

    public int getMinY();

    public int getMinX();

    public int getMinTileY();

    public int getMinTileX();

    public int getHeight();

}

