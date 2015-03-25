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

public class CancelTaxResult {

    public CommonResponse.SeverityLevel ResultCode;
    public String TransactionId;
    public String DocId;
    public CommonResponse.Message[] Messages;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CancelTaxResult{");
        sb.append("ResultCode=").append(ResultCode);
        sb.append(", TransactionId='").append(TransactionId).append('\'');
        sb.append(", DocId='").append(DocId).append('\'');
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

        final CancelTaxResult that = (CancelTaxResult) o;

        if (DocId != null ? !DocId.equals(that.DocId) : that.DocId != null) {
            return false;
        }
        if (!Arrays.equals(Messages, that.Messages)) {
            return false;
        }
        if (ResultCode != that.ResultCode) {
            return false;
        }
        if (TransactionId != null ? !TransactionId.equals(that.TransactionId) : that.TransactionId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = ResultCode != null ? ResultCode.hashCode() : 0;
        result = 31 * result + (TransactionId != null ? TransactionId.hashCode() : 0);
        result = 31 * result + (DocId != null ? DocId.hashCode() : 0);
        result = 31 * result + (Messages != null ? Arrays.hashCode(Messages) : 0);
        return result;
    }
}
