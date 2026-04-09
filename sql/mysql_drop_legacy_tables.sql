-- Drops legacy backup tables created by mysql_schema_repair.sql
-- Run this only after validating data in the new tables.

USE donationdb;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS inventory_legacy;
DROP TABLE IF EXISTS requests_legacy;
DROP TABLE IF EXISTS donations_legacy;
DROP TABLE IF EXISTS users_legacy;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Legacy tables removed successfully.' AS result;
