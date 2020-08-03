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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/LineItemModel/
public class LineItemModel {

    public String number;
    public String itemCode;
    public BigDecimal quantity;
    public BigDecimal amount;
    public String taxCode;
    public String customerUsageType;
    public String description;
    public Boolean discounted;
    public Boolean taxIncluded;
    public String ref1;
    public String ref2;
    public String businessIdentificationNo;
    public TaxOverrideModel taxOverride;
    public AddressesModel addresses;

    @Override
    public String toString() {
        return "LineItemModel{" +
               "number='" + number + '\'' +
               ", itemCode='" + itemCode + '\'' +
               ", quantity=" + quantity +
               ", amount=" + amount +
               ", taxCode='" + taxCode + '\'' +
               ", customerUsageType='" + customerUsageType + '\'' +
               ", description='" + description + '\'' +
               ", discounted=" + discounted +
               ", taxIncluded=" + taxIncluded +
               ", ref1='" + ref1 + '\'' +
               ", ref2='" + ref2 + '\'' +
               ", businessIdentificationNo='" + businessIdentificationNo + '\'' +
               ", taxOverride=" + taxOverride +
               ", addresses=" + addresses +
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

        final LineItemModel that = (LineItemModel) o;

        if (number != null ? !number.equals(that.number) : that.number != null) {
            return false;
        }
        if (itemCode != null ? !itemCode.equals(that.itemCode) : that.itemCode != null) {
            return false;
        }
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) {
            return false;
        }
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) {
            return false;
        }
        if (taxCode != null ? !taxCode.equals(that.taxCode) : that.taxCode != null) {
            return false;
        }
        if (customerUsageType != null ? !customerUsageType.equals(that.customerUsageType) : that.customerUsageType != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (discounted != null ? !discounted.equals(that.discounted) : that.discounted != null) {
            return false;
        }
        if (taxIncluded != null ? !taxIncluded.equals(that.taxIncluded) : that.taxIncluded != null) {
            return false;
        }
        if (ref1 != null ? !ref1.equals(that.ref1) : that.ref1 != null) {
            return false;
        }
        if (ref2 != null ? !ref2.equals(that.ref2) : that.ref2 != null) {
            return false;
        }
        if (businessIdentificationNo != null ? !businessIdentificationNo.equals(that.businessIdentificationNo) : that.businessIdentificationNo != null) {
            return false;
        }
        if (taxOverride != null ? !taxOverride.equals(that.taxOverride) : that.taxOverride != null) {
            return false;
        }
        return addresses != null ? addresses.equals(that.addresses) : that.addresses == null;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (itemCode != null ? itemCode.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (taxCode != null ? taxCode.hashCode() : 0);
        result = 31 * result + (customerUsageType != null ? customerUsageType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (discounted != null ? discounted.hashCode() : 0);
        result = 31 * result + (taxIncluded != null ? taxIncluded.hashCode() : 0);
        result = 31 * result + (ref1 != null ? ref1.hashCode() : 0);
        result = 31 * result + (ref2 != null ? ref2.hashCode() : 0);
        result = 31 * result + (businessIdentificationNo != null ? businessIdentificationNo.hashCode() : 0);
        result = 31 * result + (taxOverride != null ? taxOverride.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }
}
