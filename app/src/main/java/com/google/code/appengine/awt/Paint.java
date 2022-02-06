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

import com.google.code.appengine.awt.PaintContext;
import com.google.code.appengine.awt.Rectangle;
import com.google.code.appengine.awt.RenderingHints;
import com.google.code.appengine.awt.Transparency;
import com.google.code.appengine.awt.geom.AffineTransform;
import com.google.code.appengine.awt.geom.Rectangle2D;
import com.google.code.appengine.awt.image.ColorModel;

/**
 * Paint
 */
public interface Paint extends Transparency {
    PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
            Rectangle2D userBounds, AffineTransform xform,
            RenderingHints hints);
}
