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
public class TaxAddress {

    public String Address;
    public String AddressCode;
    public String City;
    public String Region;
    public String Country;
    public String PostalCode;
    public String Latitude;
    public String Longitude;
    public String TaxRegionId;
    public String JurisCode;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxAddress{");
        sb.append("Address='").append(Address).append('\'');
        sb.append(", AddressCode='").append(AddressCode).append('\'');
        sb.append(", City='").append(City).append('\'');
        sb.append(", Region='").append(Region).append('\'');
        sb.append(", Country='").append(Country).append('\'');
        sb.append(", PostalCode='").append(PostalCode).append('\'');
        sb.append(", Latitude='").append(Latitude).append('\'');
        sb.append(", Longitude='").append(Longitude).append('\'');
        sb.append(", TaxRegionId='").append(TaxRegionId).append('\'');
        sb.append(", JurisCode='").append(JurisCode).append('\'');
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

        final TaxAddress that = (TaxAddress) o;

        if (Address != null ? !Address.equals(that.Address) : that.Address != null) {
            return false;
        }
        if (AddressCode != null ? !AddressCode.equals(that.AddressCode) : that.AddressCode != null) {
            return false;
        }
        if (City != null ? !City.equals(that.City) : that.City != null) {
            return false;
        }
        if (Country != null ? !Country.equals(that.Country) : that.Country != null) {
            return false;
        }
        if (JurisCode != null ? !JurisCode.equals(that.JurisCode) : that.JurisCode != null) {
            return false;
        }
        if (Latitude != null ? !Latitude.equals(that.Latitude) : that.Latitude != null) {
            return false;
        }
        if (Longitude != null ? !Longitude.equals(that.Longitude) : that.Longitude != null) {
            return false;
        }
        if (PostalCode != null ? !PostalCode.equals(that.PostalCode) : that.PostalCode != null) {
            return false;
        }
        if (Region != null ? !Region.equals(that.Region) : that.Region != null) {
            return false;
        }
        if (TaxRegionId != null ? !TaxRegionId.equals(that.TaxRegionId) : that.TaxRegionId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Address != null ? Address.hashCode() : 0;
        result = 31 * result + (AddressCode != null ? AddressCode.hashCode() : 0);
        result = 31 * result + (City != null ? City.hashCode() : 0);
        result = 31 * result + (Region != null ? Region.hashCode() : 0);
        result = 31 * result + (Country != null ? Country.hashCode() : 0);
        result = 31 * result + (PostalCode != null ? PostalCode.hashCode() : 0);
        result = 31 * result + (Latitude != null ? Latitude.hashCode() : 0);
        result = 31 * result + (Longitude != null ? Longitude.hashCode() : 0);
        result = 31 * result + (TaxRegionId != null ? TaxRegionId.hashCode() : 0);
        result = 31 * result + (JurisCode != null ? JurisCode.hashCode() : 0);
        return result;
    }
}
