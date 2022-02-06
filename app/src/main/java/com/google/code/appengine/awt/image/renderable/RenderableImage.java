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
package com.google.code.appengine.awt.image.renderable;

import java.util.Vector;

import com.google.code.appengine.awt.RenderingHints;
import com.google.code.appengine.awt.image.RenderedImage;
import com.google.code.appengine.awt.image.renderable.RenderContext;
import com.google.code.appengine.awt.image.renderable.RenderableImage;


public interface RenderableImage {

    public static final String HINTS_OBSERVED = "HINTS_OBSERVED"; //$NON-NLS-1$

    public Object getProperty(String name);

    public RenderedImage createRendering(RenderContext renderContext);

    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints);

    public Vector<RenderableImage> getSources();

    public String[] getPropertyNames();

    public RenderedImage createDefaultRendering();

    public boolean isDynamic();

    public float getWidth();

    public float getMinY();

    public float getMinX();

    public float getHeight();

}

