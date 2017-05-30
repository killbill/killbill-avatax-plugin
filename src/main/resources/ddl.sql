DROP TABLE IF EXISTS avatax_responses;
CREATE TABLE avatax_responses (
  record_id int(11) UNSIGNED NOT NULL auto_increment,
  kb_account_id char(36) NOT NULL,
  kb_invoice_id char(36) NOT NULL,
  kb_invoice_item_ids longtext DEFAULT NULL,
  doc_code varchar(255) DEFAULT NULL,
  doc_date datetime DEFAULT NULL,
  timestamp datetime DEFAULT NULL,
  total_amount numeric(15,9) DEFAULT NULL,
  total_discount numeric(15,9) DEFAULT NULL,
  total_exemption numeric(15,9) DEFAULT NULL,
  total_taxable numeric(15,9) DEFAULT NULL,
  total_tax numeric(15,9) DEFAULT NULL,
  total_tax_calculated numeric(15,9) DEFAULT NULL,
  tax_date datetime DEFAULT NULL,
  tax_lines longtext DEFAULT NULL,
  tax_summary longtext DEFAULT NULL,
  tax_addresses longtext DEFAULT NULL,
  result_code varchar(255) DEFAULT NULL,
  messages longtext DEFAULT NULL,
  additional_data longtext DEFAULT NULL,
  created_date datetime NOT NULL,
  kb_tenant_id char(36) NOT NULL,
  PRIMARY KEY(record_id)
) /*! ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX avatax_responses_kb_account_id ON avatax_responses(kb_account_id);
CREATE INDEX avatax_responses_kb_invoice_id ON avatax_responses(kb_invoice_id);

DROP TABLE IF EXISTS avatax_tax_codes;
CREATE TABLE avatax_tax_codes (
  record_id int(11) unsigned NOT NULL auto_increment,
  product_name varchar(255) NOT NULL,
  tax_code varchar(255) NOT NULL,
  created_date datetime NOT NULL,
  kb_tenant_id char(36) NOT NULL,
  PRIMARY KEY(record_id)
) /*! ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX avatax_tax_codes_product_name ON avatax_tax_codes(product_name);
CREATE UNIQUE INDEX avatax_tax_codes_product_name_tax_code ON avatax_tax_codes(product_name, tax_code);
