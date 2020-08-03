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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TransactionSummary/
public class TransactionSummary {

    public double rate;
    public double tax;
    public double taxable;
    public String country;
    public String region;
    public String jurisCode;
    public String jurisType;
    public String jurisName;
    public String taxName;

    @Override
    public String toString() {
        return "TransactionSummary{" +
               "rate=" + rate +
               ", tax=" + tax +
               ", taxable=" + taxable +
               ", country='" + country + '\'' +
               ", region='" + region + '\'' +
               ", jurisCode='" + jurisCode + '\'' +
               ", jurisType='" + jurisType + '\'' +
               ", jurisName='" + jurisName + '\'' +
               ", taxName='" + taxName + '\'' +
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

        final TransactionSummary that = (TransactionSummary) o;

        if (Double.compare(that.rate, rate) != 0) {
            return false;
        }
        if (Double.compare(that.tax, tax) != 0) {
            return false;
        }
        if (Double.compare(that.taxable, taxable) != 0) {
            return false;
        }
        if (country != null ? !country.equals(that.country) : that.country != null) {
            return false;
        }
        if (region != null ? !region.equals(that.region) : that.region != null) {
            return false;
        }
        if (jurisCode != null ? !jurisCode.equals(that.jurisCode) : that.jurisCode != null) {
            return false;
        }
        if (jurisType != null ? !jurisType.equals(that.jurisType) : that.jurisType != null) {
            return false;
        }
        if (jurisName != null ? !jurisName.equals(that.jurisName) : that.jurisName != null) {
            return false;
        }
        return taxName != null ? taxName.equals(that.taxName) : that.taxName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(rate);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(taxable);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (jurisCode != null ? jurisCode.hashCode() : 0);
        result = 31 * result + (jurisType != null ? jurisType.hashCode() : 0);
        result = 31 * result + (jurisName != null ? jurisName.hashCode() : 0);
        result = 31 * result + (taxName != null ? taxName.hashCode() : 0);
        return result;
    }
}
