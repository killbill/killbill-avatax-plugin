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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/AddressesModel/
public class AddressesModel {

    public AddressLocationInfo singleLocation;
    public AddressLocationInfo shipFrom;
    public AddressLocationInfo shipTo;
    public AddressLocationInfo pointOfOrderOrigin;
    public AddressLocationInfo pointOfOrderAcceptance;

    @Override
    public String toString() {
        return "AddressesModel{" +
               "singleLocation=" + singleLocation +
               ", shipFrom=" + shipFrom +
               ", shipTo=" + shipTo +
               ", pointOfOrderOrigin=" + pointOfOrderOrigin +
               ", pointOfOrderAcceptance=" + pointOfOrderAcceptance +
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

        final AddressesModel that = (AddressesModel) o;

        if (singleLocation != null ? !singleLocation.equals(that.singleLocation) : that.singleLocation != null) {
            return false;
        }
        if (shipFrom != null ? !shipFrom.equals(that.shipFrom) : that.shipFrom != null) {
            return false;
        }
        if (shipTo != null ? !shipTo.equals(that.shipTo) : that.shipTo != null) {
            return false;
        }
        if (pointOfOrderOrigin != null ? !pointOfOrderOrigin.equals(that.pointOfOrderOrigin) : that.pointOfOrderOrigin != null) {
            return false;
        }
        return pointOfOrderAcceptance != null ? pointOfOrderAcceptance.equals(that.pointOfOrderAcceptance) : that.pointOfOrderAcceptance == null;
    }

    @Override
    public int hashCode() {
        int result = singleLocation != null ? singleLocation.hashCode() : 0;
        result = 31 * result + (shipFrom != null ? shipFrom.hashCode() : 0);
        result = 31 * result + (shipTo != null ? shipTo.hashCode() : 0);
        result = 31 * result + (pointOfOrderOrigin != null ? pointOfOrderOrigin.hashCode() : 0);
        result = 31 * result + (pointOfOrderAcceptance != null ? pointOfOrderAcceptance.hashCode() : 0);
        return result;
    }
}
