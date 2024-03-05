/*
 In the initialize phase this script will be called to delete and recreate your tables.

 Afterwards in the generate-sources phase jooq will scan your existing tables in your database and
 generate sources from it.
 */

-- This will delete the tables every time the sql script is called
DROP TABLE IF EXISTS preference, rider_preference;

-- Add here your sql statements to create the tables "preference" and "rider_preference"
