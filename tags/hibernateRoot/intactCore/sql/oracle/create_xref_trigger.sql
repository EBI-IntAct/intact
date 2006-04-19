--
-- That trigger checks if the value inserted in the column ia_xref.parent_ac are valid as
-- they can come from different tables:
--     * ia_interactor
--     * ia_experiment
--     * ia_biosource
--     * ia_controlledvocab
--     * ia_feature
--
-- We raise an error if no corresponding object is found.
--

CREATE OR REPLACE TRIGGER TRG_XREF_PARENTAC_CHECK
BEFORE INSERT OR UPDATE
OF parent_ac ON ia_xref
FOR EACH ROW

DECLARE
	v_parent_found IA_XREF.parent_ac%TYPE;

BEGIN

	--
	-- Look for interactor
	--
	SELECT count(1) INTO v_parent_found
	FROM ia_interactor
	where :new.parent_ac = ac;

	IF( v_parent_found = 0 ) THEN

		--
		-- Look for experiment
		--
		SELECT count(1) INTO v_parent_found
		FROM ia_experiment
		where :new.parent_ac = ac;

		IF( v_parent_found = 0 ) THEN

            --
            -- Look for controlled vocabulary
            --
            SELECT count(1) INTO v_parent_found
            FROM ia_controlledvocab
            where :new.parent_ac = ac;

            IF( v_parent_found = 0 ) THEN

                --
                -- Look for biosource
                --
                SELECT count(1) INTO v_parent_found
                FROM ia_bioSource
                where :new.parent_ac = ac;

                IF( v_parent_found = 0 ) THEN

                    --
                    -- Look for feature
                    --
                    SELECT count(1) INTO v_parent_found
                    FROM ia_feature
                    where :new.parent_ac = ac;

                    IF( v_parent_found = 0 ) THEN

                        --
                        -- Nothing found, raise a data integrity error
                        --

                        RAISE_APPLICATION_ERROR( -20001, 'The object having the AC: '||:new.parent_ac||' could not be found amongst interactor, biosource, experiment, feature and controlled vocabulary.' );

                    END IF;

                END IF;

            END IF;

		END IF;

	END IF;

END;
/