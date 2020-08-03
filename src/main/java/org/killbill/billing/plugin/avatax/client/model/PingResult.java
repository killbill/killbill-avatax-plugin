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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/PingResultModel/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class PingResult {

    public String version;
    public boolean authenticated;
    public String authenticationType;
    public String authenticatedUserName;
    public int authenticatedUserId;
    public int authenticatedAccountId;
    public String crmid;

    @Override
    public String toString() {
        return "PingResult{" +
               "version='" + version + '\'' +
               ", authenticated=" + authenticated +
               ", authenticationType='" + authenticationType + '\'' +
               ", authenticatedUserName='" + authenticatedUserName + '\'' +
               ", authenticatedUserId=" + authenticatedUserId +
               ", authenticatedAccountId=" + authenticatedAccountId +
               ", crmid='" + crmid + '\'' +
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

        final PingResult that = (PingResult) o;

        if (authenticated != that.authenticated) {
            return false;
        }
        if (authenticatedUserId != that.authenticatedUserId) {
            return false;
        }
        if (authenticatedAccountId != that.authenticatedAccountId) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }
        if (authenticationType != null ? !authenticationType.equals(that.authenticationType) : that.authenticationType != null) {
            return false;
        }
        if (authenticatedUserName != null ? !authenticatedUserName.equals(that.authenticatedUserName) : that.authenticatedUserName != null) {
            return false;
        }
        return crmid != null ? crmid.equals(that.crmid) : that.crmid == null;
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (authenticated ? 1 : 0);
        result = 31 * result + (authenticationType != null ? authenticationType.hashCode() : 0);
        result = 31 * result + (authenticatedUserName != null ? authenticatedUserName.hashCode() : 0);
        result = 31 * result + authenticatedUserId;
        result = 31 * result + authenticatedAccountId;
        result = 31 * result + (crmid != null ? crmid.hashCode() : 0);
        return result;
    }
}
