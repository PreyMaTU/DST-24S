/*
 In the initialize phase this script will be called to delete and recreate your tables.

 Afterwards in the generate-sources phase jooq will scan your existing tables in your database and
 generate sources from it.
 */

-- This will delete the tables every time the sql script is called
DROP TABLE IF EXISTS preference, rider_preference;

-- Add here your sql statements to create the tables "preference" and "rider_preference"
CREATE TABLE rider_preference (
    rider_id BIGINT PRIMARY KEY NOT NULL,
    vehicle_class TEXT,
    area TEXT
);

CREATE TABLE preference (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    rider_id BIGINT,
    pref_key TEXT,
    pref_value TEXT,
    FOREIGN KEY (rider_id) REFERENCES rider_preference(rider_id)
);
