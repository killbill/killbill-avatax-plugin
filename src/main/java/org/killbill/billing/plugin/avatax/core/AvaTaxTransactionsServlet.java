/*
 * Copyright 2020-2021 Equinix, Inc
 * Copyright 2014-2021 The Billing Project, LLC
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

package org.killbill.billing.plugin.avatax.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jooby.MediaType;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.mvc.GET;
import org.jooby.mvc.Local;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.model.TransactionModel;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxResponsesRecord;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.util.entity.Entity;

import com.google.inject.Inject;

@Singleton
// Handle /plugins/killbill-avatax/transactions
@Path("/transactions")
public class AvaTaxTransactionsServlet {

    private final AvaTaxConfigurationHandler avaTaxConfigurationHandler;
    private final AvaTaxDao dao;

    @Inject
    public AvaTaxTransactionsServlet(final AvaTaxConfigurationHandler avaTaxConfigurationHandler,
                                     final AvaTaxDao dao) {
        this.avaTaxConfigurationHandler = avaTaxConfigurationHandler;
        this.dao = dao;
    }

    @GET
    public Result getTransactionsByInvoiceId(@Named("kbInvoiceId") final UUID kbInvoiceId,
                                             @Local @Named("killbill_tenant") final Tenant tenant) throws AvaTaxClientException, SQLException {
        final List<AvataxResponsesRecord> responses = dao.getSuccessfulResponses(kbInvoiceId, tenant.getId());
        final Collection<String> docCodes = new HashSet<String>();
        for (final AvataxResponsesRecord response : responses) {
            docCodes.add(response.getDocCode());
        }

        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(tenant.getId());
        final Collection<TransactionModel> transactions = new ArrayList<TransactionModel>(docCodes.size());
        for (final String docCode : docCodes) {
            final TransactionModel transactionModel = avaTaxClient.getTransactionByCode(docCode);
            transactions.add(transactionModel);
        }

        return Results.ok(transactions).type(MediaType.json);
    }

    @GET
    @Path("/{transactionCode}")
    public Result getTransaction(@Named("transactionCode") final String transactionCode,
                                 @Local @Named("killbill_tenant") final Optional<Tenant> tenant) throws AvaTaxClientException {
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(tenant.map(Entity::getId).orElse(null));
        final TransactionModel transactionModel = avaTaxClient.getTransactionByCode(transactionCode);
        return Results.ok(transactionModel).type(MediaType.json);
    }

    @POST
    @Path("/{transactionCode}/commit")
    public Result commitTransaction(@Named("transactionCode") final String transactionCode,
                                    @Local @Named("killbill_tenant") final Optional<Tenant> tenant) throws AvaTaxClientException {
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(tenant.map(Entity::getId).orElse(null));
        final TransactionModel transactionModel = avaTaxClient.commitTransaction(transactionCode);
        return Results.ok(transactionModel).type(MediaType.json);
    }

    @POST
    @Path("/{transactionCode}/void")
    public Result voidTransaction(@Named("transactionCode") final String transactionCode,
                                  @Local @Named("killbill_tenant") final Optional<Tenant> tenant) throws AvaTaxClientException {
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(tenant.map(Entity::getId).orElse(null));
        final TransactionModel transactionModel = avaTaxClient.voidTransaction(transactionCode);
        return Results.ok(transactionModel).type(MediaType.json);
    }
}
