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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TransactionAddressModel/
public class TransactionAddressModel {

    public String id;
    public String transactionId;
    public String city;
    public String region;
    public String country;
    public String postalCode;
    public String latitude;
    public String longitude;
    public String taxRegionId;

    @Override
    public String toString() {
        return "TransactionAddressModel{" +
               "id='" + id + '\'' +
               ", transactionId='" + transactionId + '\'' +
               ", city='" + city + '\'' +
               ", region='" + region + '\'' +
               ", country='" + country + '\'' +
               ", postalCode='" + postalCode + '\'' +
               ", latitude='" + latitude + '\'' +
               ", longitude='" + longitude + '\'' +
               ", taxRegionId='" + taxRegionId + '\'' +
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

        final TransactionAddressModel that = (TransactionAddressModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (transactionId != null ? !transactionId.equals(that.transactionId) : that.transactionId != null) {
            return false;
        }
        if (city != null ? !city.equals(that.city) : that.city != null) {
            return false;
        }
        if (region != null ? !region.equals(that.region) : that.region != null) {
            return false;
        }
        if (country != null ? !country.equals(that.country) : that.country != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
            return false;
        }
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) {
            return false;
        }
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) {
            return false;
        }
        return taxRegionId != null ? taxRegionId.equals(that.taxRegionId) : that.taxRegionId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (taxRegionId != null ? taxRegionId.hashCode() : 0);
        return result;
    }
}
