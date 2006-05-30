--
-- That trigger checks if the value inserted in the column ia_xref.parent_ac are valid as
-- they can come from different tables:
--     * ia_interactor
--     * ia_experiment
--     * ia_biosource
--     * ia_controlledvocab
--     * feature
--     * publication
--
-- We raise an error if no corresponding object is found.
--

CREATE OR REPLACE FUNCTION check_xref_parentAc () RETURNS TRIGGER AS '
DECLARE
	v_parent_found ia_xref.parent_ac%TYPE;

BEGIN

    --
    -- Look for interactor
    --
    SELECT count(1) INTO v_parent_found
    FROM ia_interactor
    where NEW.parent_ac = ac;

    IF v_parent_found = 0 THEN

        --
        -- Look for experiment
        --
        SELECT count(1) INTO v_parent_found
        FROM ia_experiment
        where NEW.parent_ac = ac;

        IF v_parent_found = 0 THEN

            --
            -- Look for controlled vocabulary
            --
            SELECT count(1) INTO v_parent_found
            FROM ia_controlledvocab
            where NEW.parent_ac = ac;

            IF v_parent_found = 0 THEN

                --
                -- Look for biosource
                --
                SELECT count(1) INTO v_parent_found
                FROM ia_bioSource
                where NEW.parent_ac = ac;

                IF v_parent_found = 0 THEN

                    --
                    -- Look for feature
                    --
                    SELECT count(1) INTO v_parent_found
                    FROM ia_feature
                    where NEW.parent_ac = ac;

                    IF v_parent_found = 0 THEN

                        --
                        -- Look for publication
                        --
                        SELECT count(1) INTO v_parent_found
                        FROM ia_publication
                        where NEW.parent_ac = ac;

                        IF v_parent_found = 0 THEN

                            --
                            -- Nothing found, raise a data integrity error
                            --

                            RAISE EXCEPTION  ''The object having the AC: % could not be found amongst interactor, biosource, experiment, feature and controlled vocabulary.'', NEW.parent_ac;

                        END IF;

                    END IF;

                END IF;

            END IF;

        END IF;

    END IF;

    RETURN NEW;

END;
' LANGUAGE 'plpgsql';


-- drop trigger if already existing
DROP TRIGGER trg_xref_parentac_check ON ia_xref;

-- create the trigger that uses the function defined above.
CREATE TRIGGER trg_xref_parentac_check
BEFORE INSERT OR UPDATE ON ia_xref
FOR EACH ROW
EXECUTE PROCEDURE check_xref_parentAc();
