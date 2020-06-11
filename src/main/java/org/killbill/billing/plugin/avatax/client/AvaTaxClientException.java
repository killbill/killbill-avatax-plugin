/*
 * Copyright 2015-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2015-2020 The Billing Project, LLC
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

package org.killbill.billing.plugin.avatax.client;

import org.killbill.billing.plugin.avatax.client.model.AvaTaxErrors;

public class AvaTaxClientException extends Exception {

    private AvaTaxErrors errors = null;

    public AvaTaxClientException(final Exception e) {
        super(e);
    }

    public AvaTaxClientException(final String message) {
        super(message);
    }

    public AvaTaxClientException(final String message, final Exception e) {
        super(message, e);
    }

    public AvaTaxClientException(final AvaTaxErrors errors, final Exception e) {
        super(e);
        this.errors = errors;
    }

    public AvaTaxErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return errors == null ? super.toString() : "AvaTaxClientException{" +
                                                   "errors=" + errors +
                                                   '}';
    }
}
