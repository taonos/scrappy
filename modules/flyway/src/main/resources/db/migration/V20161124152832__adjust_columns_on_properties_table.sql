ALTER TABLE properties
    ALTER COLUMN bathrooms TYPE integer ;
ALTER TABLE properties
    ALTER COLUMN bathrooms DROP NOT NULL;

ALTER TABLE properties
    ALTER COLUMN person_capacity TYPE integer ;
ALTER TABLE properties
    ALTER COLUMN person_capacity DROP NOT NULL;