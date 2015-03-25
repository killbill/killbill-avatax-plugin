/*
 * Copyright 2015 Groupon, Inc
 * Copyright 2015 The Billing Project, LLC
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

// Nested in GetTaxResult object
public class TaxDetail {

    public double Rate;
    public double Tax;
    public double Taxable;
    public String Country;
    public String Region;
    public String JurisCode;
    public String JurisType;
    public String JurisName;
    public String TaxName;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxDetail{");
        sb.append("Rate=").append(Rate);
        sb.append(", Tax=").append(Tax);
        sb.append(", Taxable=").append(Taxable);
        sb.append(", Country='").append(Country).append('\'');
        sb.append(", Region='").append(Region).append('\'');
        sb.append(", JurisCode='").append(JurisCode).append('\'');
        sb.append(", JurisType='").append(JurisType).append('\'');
        sb.append(", JurisName='").append(JurisName).append('\'');
        sb.append(", TaxName='").append(TaxName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TaxDetail taxDetail = (TaxDetail) o;

        if (Double.compare(taxDetail.Rate, Rate) != 0) {
            return false;
        }
        if (Double.compare(taxDetail.Tax, Tax) != 0) {
            return false;
        }
        if (Double.compare(taxDetail.Taxable, Taxable) != 0) {
            return false;
        }
        if (Country != null ? !Country.equals(taxDetail.Country) : taxDetail.Country != null) {
            return false;
        }
        if (JurisCode != null ? !JurisCode.equals(taxDetail.JurisCode) : taxDetail.JurisCode != null) {
            return false;
        }
        if (JurisName != null ? !JurisName.equals(taxDetail.JurisName) : taxDetail.JurisName != null) {
            return false;
        }
        if (JurisType != null ? !JurisType.equals(taxDetail.JurisType) : taxDetail.JurisType != null) {
            return false;
        }
        if (Region != null ? !Region.equals(taxDetail.Region) : taxDetail.Region != null) {
            return false;
        }
        if (TaxName != null ? !TaxName.equals(taxDetail.TaxName) : taxDetail.TaxName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(Rate);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Tax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Taxable);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (Country != null ? Country.hashCode() : 0);
        result = 31 * result + (Region != null ? Region.hashCode() : 0);
        result = 31 * result + (JurisCode != null ? JurisCode.hashCode() : 0);
        result = 31 * result + (JurisType != null ? JurisType.hashCode() : 0);
        result = 31 * result + (JurisName != null ? JurisName.hashCode() : 0);
        result = 31 * result + (TaxName != null ? TaxName.hashCode() : 0);
        return result;
    }
}
