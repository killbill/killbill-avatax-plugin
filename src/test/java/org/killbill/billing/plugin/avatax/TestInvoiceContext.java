/*
 * Copyright 2020-2022 Equinix, Inc
 * Copyright 2014-2022 The Billing Project, LLC
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

package org.killbill.billing.plugin.avatax;

import java.util.List;

import org.joda.time.LocalDate;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.plugin.api.InvoiceContext;
import org.killbill.billing.plugin.api.PluginCallContext;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.killbill.billing.util.callcontext.CallContext;

public class TestInvoiceContext extends PluginCallContext implements InvoiceContext {

    private final LocalDate targetDate;
    private final Invoice invoice;
    private final List<Invoice> existingInvoices;
    private final boolean isDryRun;
    private final boolean isRescheduled;

    public TestInvoiceContext(final LocalDate targetDate,
                              final Invoice invoice,
                              final List<Invoice> existingInvoices,
                              final boolean isDryRun,
                              final boolean isRescheduled,
                              final CallContext context) {
        super(AvaTaxActivator.PLUGIN_NAME, context.getCreatedDate(), context.getAccountId(), context.getTenantId());
        this.targetDate = targetDate;
        this.invoice = invoice;
        this.existingInvoices = existingInvoices;
        this.isDryRun = isDryRun;
        this.isRescheduled = isRescheduled;
    }

    @Override
    public LocalDate getTargetDate() {
        return targetDate;
    }

    @Override
    public Invoice getInvoice() {
        return invoice;
    }

    @Override
    public List<Invoice> getExistingInvoices() {
        return existingInvoices;
    }

    @Override
    public boolean isDryRun() {
        return isDryRun;
    }

    @Override
    public boolean isRescheduled() {
        return isRescheduled;
    }

}
