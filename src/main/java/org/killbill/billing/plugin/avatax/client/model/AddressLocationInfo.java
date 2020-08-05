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

package org.killbill.billing.plugin.avatax.client.model;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/AddressLocationInfo/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class AddressLocationInfo {

    public String locationCode;
    public String line1;
    public String line2;
    public String line3;
    public String city;
    public String region;
    public String postalCode;
    public String country;
    public String county;
    public BigDecimal latitude;
    public BigDecimal longitude;

    @Override
    public String toString() {
        return "AddressLocationInfo{" +
               "locationCode='" + locationCode + '\'' +
               ", line1='" + line1 + '\'' +
               ", line2='" + line2 + '\'' +
               ", line3='" + line3 + '\'' +
               ", city='" + city + '\'' +
               ", region='" + region + '\'' +
               ", postalCode='" + postalCode + '\'' +
               ", country='" + country + '\'' +
               ", County='" + county + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AddressLocationInfo that = (AddressLocationInfo) o;

        if (locationCode != null ? !locationCode.equals(that.locationCode) : that.locationCode != null) {
            return false;
        }
        if (line1 != null ? !line1.equals(that.line1) : that.line1 != null) {
            return false;
        }
        if (line2 != null ? !line2.equals(that.line2) : that.line2 != null) {
            return false;
        }
        if (line3 != null ? !line3.equals(that.line3) : that.line3 != null) {
            return false;
        }
        if (city != null ? !city.equals(that.city) : that.city != null) {
            return false;
        }
        if (region != null ? !region.equals(that.region) : that.region != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
            return false;
        }
        if (country != null ? !country.equals(that.country) : that.country != null) {
            return false;
        }
        if (county != null ? !county.equals(that.county) : that.county != null) {
            return false;
        }
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) {
            return false;
        }
        return longitude != null ? longitude.equals(that.longitude) : that.longitude == null;
    }

    @Override
    public int hashCode() {
        int result = locationCode != null ? locationCode.hashCode() : 0;
        result = 31 * result + (line1 != null ? line1.hashCode() : 0);
        result = 31 * result + (line2 != null ? line2.hashCode() : 0);
        result = 31 * result + (line3 != null ? line3.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }
}
