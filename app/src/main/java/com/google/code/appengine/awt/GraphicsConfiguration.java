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
 * @author Alexey A. Petrenko
 */
package com.google.code.appengine.awt;


import org.apache.harmony.awt.internal.nls.Messages;

import com.google.code.appengine.awt.AWTException;
import com.google.code.appengine.awt.BufferCapabilities;
import com.google.code.appengine.awt.GraphicsDevice;
import com.google.code.appengine.awt.ImageCapabilities;
import com.google.code.appengine.awt.Rectangle;
import com.google.code.appengine.awt.geom.AffineTransform;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.VolatileImage;


public abstract class GraphicsConfiguration {
   /***************************************************************************
    *
    *  Constructors
    *
    ***************************************************************************/

    protected GraphicsConfiguration() {
    }


   /***************************************************************************
    *
    *  Abstract methods
    *
    ***************************************************************************/


    public abstract BufferedImage createCompatibleImage(int width, int height);

    public abstract BufferedImage createCompatibleImage(int width, int height, int transparency);

    public abstract VolatileImage createCompatibleVolatileImage(int width, int height);

    public abstract VolatileImage createCompatibleVolatileImage(int width, int height, int transparency);

    public abstract Rectangle getBounds();

    public abstract ColorModel getColorModel();

    public abstract ColorModel getColorModel(int transparency);

    public abstract AffineTransform getDefaultTransform();

    public abstract GraphicsDevice getDevice();

    public abstract AffineTransform getNormalizingTransform();


    /***************************************************************************
    *
    *  Public methods
    *
    ***************************************************************************/


    public VolatileImage createCompatibleVolatileImage(int width, int height,
            ImageCapabilities caps) throws AWTException {
        VolatileImage res = createCompatibleVolatileImage(width, height);
        if (!res.getCapabilities().equals(caps)) {
            // awt.14A=Can not create VolatileImage with specified capabilities
            throw new AWTException(Messages.getString("awt.14A")); //$NON-NLS-1$
        }
        return res;
    }

    public VolatileImage createCompatibleVolatileImage(int width, int height,
            ImageCapabilities caps, int transparency) throws AWTException {
        VolatileImage res = createCompatibleVolatileImage(width, height, transparency);
        if (!res.getCapabilities().equals(caps)) {
            // awt.14A=Can not create VolatileImage with specified capabilities
            throw new AWTException(Messages.getString("awt.14A")); //$NON-NLS-1$
        }
        return res;
    }

    public BufferCapabilities getBufferCapabilities() {
        return new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false),
                BufferCapabilities.FlipContents.UNDEFINED);
    }

    public ImageCapabilities getImageCapabilities() {
        return new ImageCapabilities(false);
    }
}
