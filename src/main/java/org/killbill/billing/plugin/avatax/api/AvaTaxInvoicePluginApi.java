/*
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
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

package org.killbill.billing.plugin.avatax.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.killbill.billing.ObjectType;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.api.invoice.PluginInvoicePluginApi;
import org.killbill.billing.plugin.avatax.core.AvaTaxConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.clock.Clock;
import org.killbill.killbill.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class AvaTaxInvoicePluginApi extends PluginInvoicePluginApi {

    private final AvaTaxTaxCalculator calculator;

    public AvaTaxInvoicePluginApi(final AvaTaxConfigurationHandler avaTaxConfigurationHandler,
                                  final AvaTaxDao dao,
                                  final OSGIKillbillAPI killbillApi,
                                  final OSGIConfigPropertiesService configProperties,
                                  final OSGIKillbillLogService logService,
                                  final Clock clock) {
        super(killbillApi, configProperties, logService, clock);
        this.calculator = new AvaTaxTaxCalculator(avaTaxConfigurationHandler, dao, clock);
    }

    @Override
    public List<InvoiceItem> getAdditionalInvoiceItems(final Invoice invoice, final Iterable<PluginProperty> properties, final CallContext context) {
        final Collection<PluginProperty> pluginProperties = new ArrayList<PluginProperty>(ImmutableList.<PluginProperty>copyOf(properties));

        checkForTaxExemption(invoice, pluginProperties, context);

        return getAdditionalTaxInvoiceItems(calculator, invoice, pluginProperties, context);
    }

    private void checkForTaxExemption(final Invoice invoice, final Collection<PluginProperty> properties, final TenantContext context) {
        // Overridden by plugin properties?
        if (PluginProperties.findPluginPropertyValue(AvaTaxTaxCalculator.CUSTOMER_USAGE_TYPE, properties) != null) {
            return;
        }

        final List<CustomField> customFields = killbillAPI.getCustomFieldUserApi().getCustomFieldsForObject(invoice.getAccountId(), ObjectType.ACCOUNT, context);
        final CustomField customField = Iterables.<CustomField>tryFind(customFields,
                                                                       new Predicate<CustomField>() {
                                                                           @Override
                                                                           public boolean apply(final CustomField customField) {
                                                                               return AvaTaxTaxCalculator.CUSTOMER_USAGE_TYPE.equals(customField.getFieldName());
                                                                           }
                                                                       }).orNull();

        if (customField != null) {
            properties.add(new PluginProperty(AvaTaxTaxCalculator.CUSTOMER_USAGE_TYPE, customField.getFieldValue(), false));
        }
    }
}
