/*
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2014-2020 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.avatax.client;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;

public abstract class ClientUtils {

    public static Integer getIntegerProperty(final Properties properties, final String key) {
        final String property = properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + key);
        return Strings.isNullOrEmpty(property) ? null : Integer.valueOf(property);
    }

    public static Boolean getBooleanProperty(final Properties properties, final String key) {
        final String property = properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + key);
        return Strings.isNullOrEmpty(property) ? true : Boolean.valueOf(property);
    }

    public static ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return objectMapper;
    }
}
