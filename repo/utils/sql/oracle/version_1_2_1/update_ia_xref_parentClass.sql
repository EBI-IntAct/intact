--
-- That procedure update the table IA_XREF so the new column parentClass, gets filled.
-- FYI, a parent can originate from the following tables:
--     * ia_interactor
--     * ia_experiment
--     * ia_biosource
--     * ia_controlledvocab
--     * ia_feature
--
-- We delete the Xref if not parent can be found.
--

CREATE OR REPLACE PROCEDURE proc_update_xref AS

    -- cursor over all Xrefs
    CURSOR xref_cur IS
    SELECT ac, parent_ac
    FROM ia_xref;

    -- values read from the cursor
    v_xref_ac ia_xref.ac%TYPE;
    v_xref_parent ia_xref.parent_ac%TYPE;

    -- count of rows
    v_count INTEGER;

    -- specific type of interactor (dicriminator field)
    v_interactor_type ia_interactor.objclass%TYPE;

    -- specific type of CV (dicriminator field)
    v_controlledvocab_type ia_controlledvocab.objclass%TYPE;

    -- AC field of the different tables
    v_interactor_ac ia_interactor.ac%TYPE;
    v_experiment_ac ia_experiment.ac%TYPE;
    v_controlledvocab_ac ia_controlledvocab.ac%TYPE;
    v_biosource_ac ia_biosource.ac%TYPE;
    v_feature_ac ia_feature.ac%TYPE;
    v_publication_ac ia_publication.ac%TYPE;

BEGIN

  OPEN xref_cur;
  LOOP
    FETCH xref_cur INTO v_xref_ac, v_xref_parent;
    EXIT WHEN xref_cur % NOTFOUND;

    -- and now process a single Xref...

    --
    -- Look for interactor
    --
    SELECT COUNT(1)
    INTO v_count
    FROM ia_interactor
    WHERE ac = v_xref_parent;

    IF(v_count <> 0) THEN

      -- update Xref with interaction type

      UPDATE ia_xref
      SET parentclass = 'uk.ac.ebi.intact.model.Interactor'
      WHERE ac = v_xref_ac;

    ELSE

      --
      -- Look for experiment
      --
      SELECT COUNT(1)
      INTO v_count
      FROM ia_experiment
      WHERE ac = v_xref_parent;

      IF(v_count <> 0) THEN

        -- update Xref with experiment type

        UPDATE ia_xref
        SET parentclass = 'uk.ac.ebi.intact.model.Experiment'
        WHERE ac = v_xref_ac;

      ELSE

        --
        -- Look for controlled vocabulary
        --
        SELECT COUNT(1)
        INTO v_count
        FROM ia_controlledvocab
        WHERE ac = v_xref_parent;

        IF(v_count <> 0) THEN

          -- update Xref with ia_controlledvocab type

          UPDATE ia_xref
          SET parentclass = 'uk.ac.ebi.intact.model.CvObject'
          WHERE ac = v_xref_ac;

        ELSE

          --
          -- Look for biosource
          --
          SELECT COUNT(1)
          INTO v_count
          FROM ia_biosource
          WHERE ac = v_xref_parent;

          IF(v_count <> 0) THEN

            -- update Xref with biosource type

            UPDATE ia_xref
            SET parentclass = 'uk.ac.ebi.intact.model.BioSource'
            WHERE ac = v_xref_ac;

          ELSE

            --
            -- Look for feature
            --
            SELECT COUNT(1)
            INTO v_count
            FROM ia_feature
            WHERE ac = v_xref_parent;

            IF(v_count <> 0) THEN

              -- update Xref with feature type

              UPDATE ia_xref
              SET parentclass = 'uk.ac.ebi.intact.model.Feature'
              WHERE ac = v_xref_ac;

            ELSE

              --
              -- Look for publication
              --
              SELECT COUNT(1)
              INTO v_count
              FROM ia_publication
              WHERE ac = v_xref_parent;

              IF(v_count <> 0) THEN

                -- update Xref with publication type

                UPDATE ia_xref
                SET parentclass = 'uk.ac.ebi.intact.model.Publication'
                WHERE ac = v_xref_ac;

              ELSE

                --
                -- Nothing found, delete this orphan xref
                --

                DBMS_OUTPUT.PUT_LINE('Deleting orphan Xref...');
                DBMS_OUTPUT.PUT_LINE('DELETE FROM ia_xref WHERE ac = ''v_xref_ac''');

              END IF;  -- publications
            END IF;  -- feature
          END IF;  -- biosource
        END IF;  -- controlledvocab
      END IF;  -- experiemnt
    END IF; -- interactor
  END LOOP;

CLOSE xref_cur;

END;


begin
    dbms_output.enable(1000000000000);
    proc_update_xref;
    commit;
end;
/

