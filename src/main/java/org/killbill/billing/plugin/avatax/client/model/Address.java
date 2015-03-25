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

import java.math.BigDecimal;

public class Address {

    public enum AddressType {
        F, // Firm or company address
        G, // General Delivery address
        H, // High-rise or business complex
        P, // PO box address
        R, // Rural route address
        S; // Street or residential address
    }

    // Address can be determined for tax calculation by Line1, City, Region, PostalCode, Country OR Latitude/Longitude OR TaxRegionId
    public String AddressCode; // Input for GetTax only, not by address validation
    public String Line1;
    public String Line2;
    public String Line3;
    public String City;
    public String Region;
    public String PostalCode;
    public String Country;
    public String County; // Output for ValidateAddress only
    public String FipsCode; // Output for ValidateAddress only
    public String CarrierRoute; // Output for ValidateAddress only
    public String PostNet; // Output for ValidateAddress only
    public AddressType AddressType; // Output for ValidateAddress only
    public BigDecimal Latitude; // Input for GetTax only
    public BigDecimal Longitude; // Input for GetTax only
    public String TaxRegionId; // Input for GetTax only

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Address{");
        sb.append("AddressCode='").append(AddressCode).append('\'');
        sb.append(", Line1='").append(Line1).append('\'');
        sb.append(", Line2='").append(Line2).append('\'');
        sb.append(", Line3='").append(Line3).append('\'');
        sb.append(", City='").append(City).append('\'');
        sb.append(", Region='").append(Region).append('\'');
        sb.append(", PostalCode='").append(PostalCode).append('\'');
        sb.append(", Country='").append(Country).append('\'');
        sb.append(", County='").append(County).append('\'');
        sb.append(", FipsCode='").append(FipsCode).append('\'');
        sb.append(", CarrierRoute='").append(CarrierRoute).append('\'');
        sb.append(", PostNet='").append(PostNet).append('\'');
        sb.append(", AddressType=").append(AddressType);
        sb.append(", Latitude=").append(Latitude);
        sb.append(", Longitude=").append(Longitude);
        sb.append(", TaxRegionId='").append(TaxRegionId).append('\'');
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

        final Address address = (Address) o;

        if (AddressCode != null ? !AddressCode.equals(address.AddressCode) : address.AddressCode != null) {
            return false;
        }
        if (AddressType != address.AddressType) {
            return false;
        }
        if (CarrierRoute != null ? !CarrierRoute.equals(address.CarrierRoute) : address.CarrierRoute != null) {
            return false;
        }
        if (City != null ? !City.equals(address.City) : address.City != null) {
            return false;
        }
        if (Country != null ? !Country.equals(address.Country) : address.Country != null) {
            return false;
        }
        if (County != null ? !County.equals(address.County) : address.County != null) {
            return false;
        }
        if (FipsCode != null ? !FipsCode.equals(address.FipsCode) : address.FipsCode != null) {
            return false;
        }
        if (Latitude != null ? !Latitude.equals(address.Latitude) : address.Latitude != null) {
            return false;
        }
        if (Line1 != null ? !Line1.equals(address.Line1) : address.Line1 != null) {
            return false;
        }
        if (Line2 != null ? !Line2.equals(address.Line2) : address.Line2 != null) {
            return false;
        }
        if (Line3 != null ? !Line3.equals(address.Line3) : address.Line3 != null) {
            return false;
        }
        if (Longitude != null ? !Longitude.equals(address.Longitude) : address.Longitude != null) {
            return false;
        }
        if (PostNet != null ? !PostNet.equals(address.PostNet) : address.PostNet != null) {
            return false;
        }
        if (PostalCode != null ? !PostalCode.equals(address.PostalCode) : address.PostalCode != null) {
            return false;
        }
        if (Region != null ? !Region.equals(address.Region) : address.Region != null) {
            return false;
        }
        if (TaxRegionId != null ? !TaxRegionId.equals(address.TaxRegionId) : address.TaxRegionId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = AddressCode != null ? AddressCode.hashCode() : 0;
        result = 31 * result + (Line1 != null ? Line1.hashCode() : 0);
        result = 31 * result + (Line2 != null ? Line2.hashCode() : 0);
        result = 31 * result + (Line3 != null ? Line3.hashCode() : 0);
        result = 31 * result + (City != null ? City.hashCode() : 0);
        result = 31 * result + (Region != null ? Region.hashCode() : 0);
        result = 31 * result + (PostalCode != null ? PostalCode.hashCode() : 0);
        result = 31 * result + (Country != null ? Country.hashCode() : 0);
        result = 31 * result + (County != null ? County.hashCode() : 0);
        result = 31 * result + (FipsCode != null ? FipsCode.hashCode() : 0);
        result = 31 * result + (CarrierRoute != null ? CarrierRoute.hashCode() : 0);
        result = 31 * result + (PostNet != null ? PostNet.hashCode() : 0);
        result = 31 * result + (AddressType != null ? AddressType.hashCode() : 0);
        result = 31 * result + (Latitude != null ? Latitude.hashCode() : 0);
        result = 31 * result + (Longitude != null ? Longitude.hashCode() : 0);
        result = 31 * result + (TaxRegionId != null ? TaxRegionId.hashCode() : 0);
        return result;
    }
}
