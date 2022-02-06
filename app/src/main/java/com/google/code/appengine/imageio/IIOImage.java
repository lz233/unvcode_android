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
 * @author Rustem V. Rafikov
 */
package com.google.code.appengine.imageio;

import java.util.List;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.Raster;
import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.imageio.metadata.IIOMetadata;


public class IIOImage {

    protected RenderedImage image;
    protected Raster raster;
    protected List<? extends BufferedImage> thumbnails;
    protected IIOMetadata metadata;

    public IIOImage(RenderedImage image, List<? extends BufferedImage> thumbnails, IIOMetadata metadata) {
        if (image == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.27"));
        }
        this.raster = null;
        this.image = image;
        this.thumbnails = thumbnails;
        this.metadata = metadata;
    }

    public IIOImage(Raster raster, List<? extends BufferedImage> thumbnails, IIOMetadata metadata) {
        if (raster == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.5F"));
        }
        this.image = null;
        this.raster = raster;
        this.thumbnails = thumbnails;
        this.metadata = metadata;
    }

    public RenderedImage getRenderedImage() {
        return image;
    }

    public void setRenderedImage(RenderedImage image) {
        if (image == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.27"));
        }
        raster = null;
        this.image = image;
    }

    public boolean hasRaster() {
        return raster != null;
    }

    public Raster getRaster() {
        return raster;
    }

    public void setRaster(Raster raster) {
        if (raster == null) {
            throw new IllegalArgumentException(Messages.getString("imageio.5F"));
        }
        image = null;
        this.raster = raster;
    }

    public int getNumThumbnails() {
        return thumbnails != null ? thumbnails.size() : 0;
    }

    public BufferedImage getThumbnail(int index) {
        if (thumbnails != null) {
            return thumbnails.get(index);
        }
        throw new IndexOutOfBoundsException(Messages.getString("imageio.60"));
    }

    public List<? extends BufferedImage> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<? extends BufferedImage> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public IIOMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(IIOMetadata metadata) {
        this.metadata = metadata;
    }
}
