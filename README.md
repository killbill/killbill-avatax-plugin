# AvaTax plugin
![Maven Central](https://img.shields.io/maven-central/v/org.kill-bill.billing.plugin.java/avatax-plugin?color=blue&label=Maven%20Central)

Kill Bill tax plugin for [Avalara AvaTax](http://www.avalara.com/products/avatax/) and [Avalara Tax Rates API](http://taxratesapi.avalara.com/).

This integration delegates computation of sales taxes to Avalara, which will appear directly on Kill Bill invoices.

## Kill Bill compatibility

| Plugin version | Kill Bill version | AvaTax API      |
| -------------: | ----------------: | --------------: |
| 0.1.y          | 0.14.z            | Legacy REST API |
| 0.2.y          | 0.15.z            | Legacy REST API |
| 0.3.y          | 0.16.z            | Legacy REST API |
| 0.4.y          | 0.18.z            | Legacy REST API |
| 0.5.y          | 0.19.z            | Legacy REST API |
| 0.6.y          | 0.20.z            | Legacy REST API |
| 0.7.y          | 0.22.z            | Legacy REST API |
| 0.8.y          | 0.22.z            | REST API        |

We've upgraded numerous dependencies in 0.8.x (required for Java 11 support).

## Requirements

The plugin needs a database. The latest version of the schema can be found [here](https://github.com/killbill/killbill-avatax-plugin/blob/master/src/main/resources/ddl.sql).

## Development

To install the plugin from sources:

```
mvn clean install -DskipTests=true
kpm install_java_plugin avatax --from-source-file=target/avatax-plugin-0.8.0-SNAPSHOT.jar --destination=/var/tmp/bundles
```

You must then enable globally the plugin in Kill Bill (`killbill.properties` file):

```
org.killbill.invoice.plugin=killbill-avatax
```

## Configuration

### Avalara AvaTax

The following properties are required:

* `org.killbill.billing.plugin.avatax.url`: AvaTax endpoint (e.g. https://sandbox-rest.avatax.com/api/v2)
* `org.killbill.billing.plugin.avatax.accountId`: your AvaTax account number
* `org.killbill.billing.plugin.avatax.licenseKey`: your license key

The following properties are optional:

* `org.killbill.billing.plugin.avatax.companyCode`: your default company code (can be passed using the plugin property `companyCode`)
* `org.killbill.billing.plugin.avatax.commitDocuments`: whether invoices should be committed to Avalara

### TaxRates API

The TaxRates API is a free-to-use, no cost option for estimating sales tax rates. Any customer can request a [free AvaTax account](https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Free/RequestFreeTrial/) and make use of the TaxRates API.

The following properties are required:

* `org.killbill.billing.plugin.avatax.taxratesapi.url`: Tax Rates API endpoint (e.g. https://sandbox-rest.avatax.com/api/v2)
* `org.killbill.billing.plugin.avatax.taxratesapi.accountId`: your account ID
* `org.killbill.billing.plugin.avatax.taxratesapi.licenseKey`: your license Key

You can pass the `rateType` plugin property to specify which rate(s) to take into account.

### Common properties

For both APIs, the following properties are optional:

* `org.killbill.billing.plugin.avatax.proxyHost`: proxy host
* `org.killbill.billing.plugin.avatax.proxyPort`: proxy port
* `org.killbill.billing.plugin.avatax.strictSSL`: if false, unverified certificates are trusted

These properties can be specified globally via System Properties or on a per tenant basis:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.avatax.url=XXX
org.killbill.billing.plugin.avatax.accountId=YYY
org.killbill.billing.plugin.avatax.licenseKey=ZZZ' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-avatax
```

## AvaTax tax calculation details

Taxes are calculated by default using the address specified on the Kill Bill account (set as the `shipTo` element). In case your current e-commerce application doesn't validate addresses, you can use [Avalara's Address Validation service](https://developer.avalara.com/avatax/address-validation/) to do it (Avalara will implicitly validate addresses during the tax calculation and fail the invoice creation if the address is invalid).

You can also specify a location code per line item by passing the `locationCode_<INVOICE_ITEM_ID>` plugin property.

Kill Bill will send to Avalara all invoice line items: make sure to configure in the plugin the tax codes associated with your catalog products (see below).

Dry run invoices will contain a preview of the tax items (the associated document won't be committed to Avalara).

Here is how the main Avalara fields map to Kill Bill:

* Customer Code is mapped to the Kill Bill account external key if present, the account id otherwise
* Description is mapped to the Kill Bill invoice id
* Line item number is mapped to the Kill Bill invoice item id
* Line item code is mapped to the Kill Bill invoice item description, or the plan, phase or usage name (first non null)
* Line item Ref1 is mapped to the Kill Bill invoice item id
* Line item Ref2 is mapped to the Kill Bill invoice id

Documents in Avalara are not automatically voided (as this will depend on your dunning configuration).

See also the [AvaTax Rails mountable engine](https://github.com/killbill/killbill-avatax-ui), which helps you administrate the plugin.

### Marking an account as tax exempt

Set the `customerUsageType` custom field on the account object (e.g. `E` for charitable or benevolent organizations).

See [Handling tax exempt customers](https://help.avalara.com/Avalara_AvaTax_Update/Options_for_exempting_customers) for more details.

The plugin doesn't yet integrate with [Avalara CertCapture](https://www.avalara.com/us/en/products/exemption-certificates.html) to manage exemption certificates, but feel free to [get in touch](https://groups.google.com/forum/#!forum/killbilling-users) to see this feature added.

### Setting tax codes

There are several ways to configure tax codes:

* Set the `taxCode_<INVOICE_ITEM_ID>` plugin property
* Set the `taxCode` custom field on the invoice item object
* Store the tax code in the plugin for each product in your catalog:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: application/json' \
     -d '{"productName":"Super","taxCode":"DC010200"}' \
     http://127.0.0.1:8080/plugins/killbill-avatax/taxCodes
```

To list all tax codes configured in the plugin:

```
curl -v \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     http://127.0.0.1:8080/plugins/killbill-avatax/taxCodes
```

To find a particular tax code for a product configured in the plugin:

```
curl -v \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     http://127.0.0.1:8080/plugins/killbill-avatax/taxCodes/Super
```

To remove a tax code for a product configured in the plugin:

```
curl -v \
     -X DELETE \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     http://127.0.0.1:8080/plugins/killbill-avatax/taxCodes/Super
```

## About

Kill Bill is the leading Open-Source Subscription Billing & Payments Platform. For more information about the project, go to https://killbill.io/.
