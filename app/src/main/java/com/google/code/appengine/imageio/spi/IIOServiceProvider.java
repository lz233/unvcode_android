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
package com.google.code.appengine.imageio.spi;

import java.util.Locale;


import org.apache.harmony.x.imageio.internal.nls.Messages;

import com.google.code.appengine.imageio.spi.RegisterableService;
import com.google.code.appengine.imageio.spi.ServiceRegistry;

public abstract class IIOServiceProvider implements RegisterableService {

    protected String vendorName;
    protected String version;

    public IIOServiceProvider(String vendorName, String version) {
        if (vendorName == null) {
            throw new NullPointerException(Messages.getString("imageio.5A"));
        }
        if (version == null) {
            throw new NullPointerException(Messages.getString("imageio.5B"));
        }
        this.vendorName = vendorName;
        this.version = version;
    }

    public IIOServiceProvider() {
        // the default impl. does nothing
    }

    public void onRegistration(ServiceRegistry registry, Class<?> category) {
        // the default impl. does nothing
    }

    public void onDeregistration(ServiceRegistry registry, Class<?> category) {
        // the default impl. does nothing
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVersion() {
        return version;
    }

    public abstract String getDescription(Locale locale);
}
