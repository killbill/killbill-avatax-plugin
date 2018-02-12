/* We cannot use timestamp in MySQL because of the implicit TimeZone conversions it does behind the scenes */
DROP DOMAIN IF EXISTS datetime CASCADE;
CREATE DOMAIN datetime AS timestamp without time zone;

DROP DOMAIN IF EXISTS longtext CASCADE;
CREATE DOMAIN longtext AS text;
