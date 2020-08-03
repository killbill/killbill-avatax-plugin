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

package org.killbill.billing.plugin.avatax.core;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jooby.MediaType;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.Status;
import org.jooby.mvc.Body;
import org.jooby.mvc.DELETE;
import org.jooby.mvc.GET;
import org.jooby.mvc.Local;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillClock;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxTaxCodesRecord;
import org.killbill.billing.tenant.api.Tenant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

@Singleton
// Handle /plugins/killbill-avatax/taxCodes
// Used by Kaui: https://github.com/killbill/killbill-avatax-ui/blob/master/lib/avatax/client.rb
@Path("/taxCodes")
public class AvaTaxServlet {

    private final AvaTaxDao dao;
    private final OSGIKillbillClock clock;

    @Inject
    public AvaTaxServlet(final AvaTaxDao dao, final OSGIKillbillClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    @GET
    public Result getTaxCodes(@Local @Named("killbill_tenant") final Tenant tenant) throws SQLException {
        final List<AvataxTaxCodesRecord> taxCodesRecords = dao.getTaxCodes(tenant.getId());

        final List<TaxCodeJson> taxCodes = Lists.<AvataxTaxCodesRecord, TaxCodeJson>transform(taxCodesRecords,
                                                                                              new Function<AvataxTaxCodesRecord, TaxCodeJson>() {
                                                                                                  @Override
                                                                                                  public TaxCodeJson apply(final AvataxTaxCodesRecord record) {
                                                                                                      return record == null ? null : new TaxCodeJson(record.getProductName(), record.getTaxCode());
                                                                                                  }
                                                                                              });
        return Results.ok(taxCodes).type(MediaType.json);
    }

    @GET
    @Path("/{productName}")
    public Result getTaxCode(@Named("productName") final String productName,
                             @Local @Named("killbill_tenant") final Tenant tenant) throws SQLException {
        final String taxCode = dao.getTaxCode(productName, tenant.getId());
        final TaxCodeJson taxCodeJson = new TaxCodeJson(productName, taxCode);
        return Results.ok(taxCodeJson).type(MediaType.json);
    }

    @POST
    public Result createTaxCode(@Body final TaxCodeJson taxCodeJson,
                                @Local @Named("killbill_tenant") final Tenant tenant) throws SQLException {
        dao.setTaxCode(taxCodeJson.productName, taxCodeJson.taxCode, clock.getClock().getUTCNow(), tenant.getId());
        return Results.with(Status.CREATED).header("location", "/plugins/killbill-avatax/taxCodes/" + taxCodeJson.productName);
    }

    @DELETE
    @Path("/{productName}")
    public Result deleteTaxCode(@Named("productName") final String productName,
                                @Local @Named("killbill_tenant") final Tenant tenant) throws SQLException {
        dao.setTaxCode(productName, null, clock.getClock().getUTCNow(), tenant.getId());
        return Results.ok();
    }

    private static final class TaxCodeJson {

        public String productName;
        public String taxCode;

        @JsonCreator
        public TaxCodeJson(@JsonProperty("productName") final String productName, @JsonProperty("taxCode") final String taxCode) {
            this.productName = productName;
            this.taxCode = taxCode;
        }

        @Override
        public String toString() {
            return "TaxCodeJson{" +
                   "productName='" + productName + '\'' +
                   ", taxCode='" + taxCode + '\'' +
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

            final TaxCodeJson that = (TaxCodeJson) o;

            if (productName != null ? !productName.equals(that.productName) : that.productName != null) {
                return false;
            }
            return taxCode != null ? taxCode.equals(that.taxCode) : that.taxCode == null;
        }

        @Override
        public int hashCode() {
            int result = productName != null ? productName.hashCode() : 0;
            result = 31 * result + (taxCode != null ? taxCode.hashCode() : 0);
            return result;
        }
    }
}
