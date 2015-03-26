AvaTax plugin
=============

Kill Bill tax plugin for [Avalara AvaTax](http://www.avalara.com/products/avatax/).

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22avatax-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:avatax-plugin`.

Requirements
------------

The plugin needs a database. The latest version of the schema can be found here: https://raw.github.com/killbill/killbill-avatax-plugin/master/src/main/resources/ddl.sql.

Configuration
-------------

### Avalara AvaTax

The following System Properties are required:

* `org.killbill.billing.plugin.avatax.url`: AvaTax endpoint (e.g. https://development.avalara.net)
* `org.killbill.billing.plugin.avatax.accountNumber`: your AvaTax account number
* `org.killbill.billing.plugin.avatax.licenseKey`: your license key

The following System Properties are optional:

* `org.killbill.billing.plugin.avatax.companyCode`: your default company code (can be passed using the plugin property `companyCode`)

### Tax Rates API

This plugin also supports the free [Avalara Tax Rates API](http://taxratesapi.avalara.com/).

The following System Properties are required:

* `org.killbill.billing.plugin.avatax.taxratesapi.url`: Tax Rates API endpoint (e.g. https://taxrates.api.avalara.com)
* `org.killbill.billing.plugin.avatax.taxratesapi.apiKey`: your API Key

You can pass the `rateType` plugin property to specify which rate(s) to take into account.

### Common properties

For both APIs, the following System Properties are optional:

* `org.killbill.billing.plugin.avatax.proxyHost`: proxy host
* `org.killbill.billing.plugin.avatax.proxyPort`: proxy port
* `org.killbill.billing.plugin.avatax.strictSSL`: if false, unverified certificates are trusted
