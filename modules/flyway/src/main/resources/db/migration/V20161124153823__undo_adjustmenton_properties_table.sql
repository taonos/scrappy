ALTER TABLE properties
    ALTER COLUMN bathrooms TYPE integer ;
ALTER TABLE properties
    ALTER COLUMN bathrooms SET NOT NULL;

ALTER TABLE properties
    ALTER COLUMN person_capacity TYPE integer ;
ALTER TABLE properties
    ALTER COLUMN person_capacity SET NOT NULL;