AvaTax plugin
=============

Kill Bill tax plugin for [Avalara AvaTax](http://www.avalara.com/products/avatax/) and [Avalara Tax Rates API](http://taxratesapi.avalara.com/).

This integration delegates computation of sales taxes to Avalara, which will appear directly on Kill Bill invoices.

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22avatax-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:avatax-plugin`.

Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.1.y          | 0.14.z            |
| 0.2.y          | 0.15.z            |
| 0.3.y          | 0.16.z            |
| 0.4.y          | 0.18.z            |
| 0.5.y          | 0.19.z            |
| 0.6.y          | 0.20.z            |
| 0.7.y          | 0.22.z            |

Requirements
------------

The plugin needs a database. The latest version of the schema can be found [here](https://github.com/killbill/killbill-avatax-plugin/blob/master/src/main/resources/ddl.sql).

Configuration
-------------

### Avalara AvaTax

The following properties are required:

* `org.killbill.billing.plugin.avatax.url`: AvaTax endpoint (e.g. https://development.avalara.net)
* `org.killbill.billing.plugin.avatax.accountNumber`: your AvaTax account number
* `org.killbill.billing.plugin.avatax.licenseKey`: your license key

The following properties are optional:

* `org.killbill.billing.plugin.avatax.companyCode`: your default company code (can be passed using the plugin property `companyCode`)
* `org.killbill.billing.plugin.avatax.commitDocuments`: whether invoices should be committed to Avalara

### Tax Rates API

The following properties are required:

* `org.killbill.billing.plugin.avatax.taxratesapi.url`: Tax Rates API endpoint (e.g. https://taxrates.api.avalara.com)
* `org.killbill.billing.plugin.avatax.taxratesapi.apiKey`: your API Key

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
org.killbill.billing.plugin.avatax.accountNumber=YYY
org.killbill.billing.plugin.avatax.licenseKey=ZZZ' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-avatax
```

AvaTax tax calculation details
------------------------------

Taxes are calculated using the address specified on the Kill Bill account. In case your current e-commerce application doesn't validate addresses, you can use [Avalara's Address Validation service](http://developer.avalara.com/api-docs/designing-your-integration/address-validation) (a [client implementation](https://github.com/killbill/killbill-avatax-plugin/blob/29136dae9ae8737a39bf99496ffa7d2d9c7384b8/src/main/java/org/killbill/billing/plugin/avatax/client/AvaTaxClient.java#L72) is provided by the plugin) to do it (Avalara will implicitly validate addresses during the tax calculation and fail the invoice creation if the address is invalid).

By default, Kill Bill will send Avalara all invoice line items: make sure to configure in the plugin the tax codes associated with your catalog products (see below).

Dry run invoices will contain a preview of the tax items (the associated document won't be committed to Avalara).

Here is how the main Avalara fields map to Kill Bill:

* Customer Code is mapped to the Kill Bill account external key if present, the account id otherwise
* Purchase Order No is mapped to the Kill Bill invoice id
* Line item code is mapped to the Kill Bill invoice item description, or the plan, phase or usage name (first non null)
* Line item Ref1 is mapped to the Kill Bill invoice item id
* Line item Ref2 is mapped to the Kill Bill invoice id

Documents in Avalara are not automatically voided (as this will depend on your dunning configuration). We do provide a [client implementation](https://github.com/killbill/killbill-avatax-plugin/blob/master/src/main/java/org/killbill/billing/plugin/avatax/client/AvaTaxClient.java#L133) though in case you want to cancel documents from your own plugin.

See also the [AvaTax Rails mountable engine](https://github.com/killbill/killbill-avatax-ui), which helps you administrate the plugin.

### Marking an account as tax exempt

Set the `customerUsageType` custom field on the account object (e.g. `E` for charitable or benevolent organizations).

See [Handling tax exempt customers](http://developer.avalara.com/api-docs/designing-your-integration/handling-tax-exempt-customers) for more details.

The plugin doesn't yet integrate with [Avalara CertCapture](http://www.avalara.com/products/certcapture/) to manage exemption certificates, but feel free to [get in touch](https://groups.google.com/forum/#!forum/killbilling-users) to see this feature added.

### Setting tax codes

There are several ways to configure tax codes:

* For external charges, set the `taxCode` custom field on the invoice item object (e.g. `PC040100` for general clothing products)
* For subscriptions, you can store the tax code for each product in your catalog as follows:

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

See [What Is a Tax Code?](https://help.avalara.com/000_AvaTax_Calc/000AvaTaxCalc_User_Guide/040_Managing_Tax_Profiles/050_Tax_Codes/001_What_is_a_Tax_Code) for more details.
