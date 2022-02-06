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
 * @author Sergey I. Salishev
 */
package com.google.code.appengine.imageio.event;

import java.util.EventListener;

import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.imageio.ImageReader;



/**
 * @author Sergey I. Salishev
 */
public interface IIOReadUpdateListener extends EventListener {

    void imageUpdate(ImageReader source, BufferedImage theImage, int minX,
            int minY, int width, int height, int periodX, int periodY,
            int[] bands);
    
    void passComplete(ImageReader source, BufferedImage theImage);
    
    void passStarted(ImageReader source, BufferedImage theImage, int pass,
            int minPass, int maxPass, int minX, int minY, int periodX,
            int periodY, int[] bands);

    void thumbnailPassComplete(ImageReader source, BufferedImage theImage);
    
    void thumbnailPassStarted(ImageReader source, BufferedImage theThumbnail,
            int pass, int minPass, int maxPass, int minX, int minY,
            int periodX, int periodY, int[] bands);
    
    void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail,
            int minX, int minY, int width, int height, int periodX,
            int periodY, int[] bands);
}

