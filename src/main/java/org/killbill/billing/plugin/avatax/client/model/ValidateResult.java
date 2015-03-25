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

import java.util.Arrays;

public class ValidateResult {

    public Address Address;
    public CommonResponse.SeverityLevel ResultCode;
    public CommonResponse.Message[] Messages;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ValidateResult{");
        sb.append("Address=").append(Address);
        sb.append(", ResultCode=").append(ResultCode);
        sb.append(", Messages=").append(Arrays.toString(Messages));
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

        final ValidateResult that = (ValidateResult) o;

        if (Address != null ? !Address.equals(that.Address) : that.Address != null) {
            return false;
        }
        if (!Arrays.equals(Messages, that.Messages)) {
            return false;
        }
        if (ResultCode != that.ResultCode) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Address != null ? Address.hashCode() : 0;
        result = 31 * result + (ResultCode != null ? ResultCode.hashCode() : 0);
        result = 31 * result + (Messages != null ? Arrays.hashCode(Messages) : 0);
        return result;
    }
}
