--
-- Procedure that splits IA_XREF_AUDIT into multiple tables.
-- NOTE: it might be very slow as the AC colun of the Audit tables are not indexed.
--       it migh be a good idea to create indexes just before and remove them after.
--
CREATE OR REPLACE PROCEDURE proc_split_xref_audit AS 

-- cursor over all Xrefs
CURSOR xref_cur IS
SELECT *
FROM ia_xref_audit;

v_xref_rec ia_xref_audit%ROWTYPE;

-- count of rows
v_count INTEGER;

-- specific type of interactor (dicriminator field)
v_interactor_type ia_interactor_audit.objclass%TYPE;

-- specific type of CV (dicriminator field)
v_controlledvocab_type ia_controlledvocab_audit.objclass%TYPE;

-- AC field of the different tables
v_interactor_ac ia_interactor_audit.ac%TYPE;
v_experiment_ac ia_experiment_audit.ac%TYPE;
v_controlledvocab_ac ia_controlledvocab_audit.ac%TYPE;
v_biosource_ac ia_biosource_audit.ac%TYPE;
v_feature_ac ia_feature_audit.ac%TYPE;
v_publication_ac ia_publication_audit.ac%TYPE;

BEGIN


  dbms_output.enable(1000000000000);

  -- TODO clean up all specific xref tables before to fill them up
  -- no check will be done for duplicated rows !!

  OPEN xref_cur;
  LOOP
      FETCH xref_cur INTO v_xref_rec;
      EXIT WHEN xref_cur % NOTFOUND;

      -- and now process a single Xref...

      --
      -- Look for interactor
      --
      SELECT COUNT(1)
      INTO v_count
      FROM ia_interactor_audit
      WHERE ac = v_xref_rec.PARENT_AC;

      IF(v_count <> 0) THEN

        -- copy Xref into the right table
        INSERT INTO ia_interactor_xref_audit (
            EVENT,
            AC,
            DEPRECATED,
            CREATED,
            UPDATED,
            USERSTAMP,
            QUALIFIER_AC,
            DATABASE_AC,
            PARENT_AC,
            OWNER_AC,
            PRIMARYID,
            SECONDARYID,
            DBRELEASE,
            CREATED_USER
        )
        VALUES (
            v_xref_rec.EVENT,
            v_xref_rec.AC,
            v_xref_rec.DEPRECATED,
            v_xref_rec.CREATED,
            v_xref_rec.UPDATED,
            v_xref_rec.USERSTAMP,
            v_xref_rec.QUALIFIER_AC,
            v_xref_rec.DATABASE_AC,
            v_xref_rec.PARENT_AC,
            v_xref_rec.OWNER_AC,
            v_xref_rec.PRIMARYID,
            v_xref_rec.SECONDARYID,
            v_xref_rec.DBRELEASE,
            NVL(v_xref_rec.CREATED_USER, 'N/A')
        );

      ELSE

        --
        -- Look for experiment
        --
        SELECT COUNT(1)
        INTO v_count
        FROM ia_experiment_audit
        WHERE ac = v_xref_rec.PARENT_AC;

        IF(v_count <> 0) THEN

            -- copy Xref into the right table
            INSERT INTO ia_experiment_xref_audit (
                EVENT,
                AC,
                DEPRECATED,
                CREATED,
                UPDATED,
                USERSTAMP,
                QUALIFIER_AC,
                DATABASE_AC,
                PARENT_AC,
                OWNER_AC,
                PRIMARYID,
                SECONDARYID,
                DBRELEASE,
                CREATED_USER
            )
            VALUES (
                v_xref_rec.EVENT,
                v_xref_rec.AC,
                v_xref_rec.DEPRECATED,
                v_xref_rec.CREATED,
                v_xref_rec.UPDATED,
                v_xref_rec.USERSTAMP,
                v_xref_rec.QUALIFIER_AC,
                v_xref_rec.DATABASE_AC,
                v_xref_rec.PARENT_AC,
                v_xref_rec.OWNER_AC,
                v_xref_rec.PRIMARYID,
                v_xref_rec.SECONDARYID,
                v_xref_rec.DBRELEASE,
                NVL(v_xref_rec.CREATED_USER, 'N/A')
            );

        ELSE

          --
          -- Look for controlled vocabulary
          --
          SELECT COUNT(1)
          INTO v_count
          FROM ia_controlledvocab_audit
          WHERE ac = v_xref_rec.PARENT_AC;

          IF(v_count <> 0) THEN

              -- copy Xref into the right table
              INSERT INTO ia_controlledvocab_xref_audit (
                  EVENT,
                  AC,
                  DEPRECATED,
                  CREATED,
                  UPDATED,
                  USERSTAMP,
                  QUALIFIER_AC,
                  DATABASE_AC,
                  PARENT_AC,
                  OWNER_AC,
                  PRIMARYID,
                  SECONDARYID,
                  DBRELEASE,
                  CREATED_USER
              )
              VALUES (
                  v_xref_rec.EVENT,
                  v_xref_rec.AC,
                  v_xref_rec.DEPRECATED,
                  v_xref_rec.CREATED,
                  v_xref_rec.UPDATED,
                  v_xref_rec.USERSTAMP,
                  v_xref_rec.QUALIFIER_AC,
                  v_xref_rec.DATABASE_AC,
                  v_xref_rec.PARENT_AC,
                  v_xref_rec.OWNER_AC,
                  v_xref_rec.PRIMARYID,
                  v_xref_rec.SECONDARYID,
                  v_xref_rec.DBRELEASE,
                  NVL(v_xref_rec.CREATED_USER, 'N/A')
              );

          ELSE

            --
            -- Look for biosource
            --
            SELECT COUNT(1)
            INTO v_count
            FROM ia_biosource_audit
            WHERE ac = v_xref_rec.PARENT_AC;

            IF(v_count <> 0) THEN

                -- copy Xref into the right table
                INSERT INTO ia_biosource_xref_audit (
                    EVENT,
                    AC,
                    DEPRECATED,
                    CREATED,
                    UPDATED,
                    USERSTAMP,
                    QUALIFIER_AC,
                    DATABASE_AC,
                    PARENT_AC,
                    OWNER_AC,
                    PRIMARYID,
                    SECONDARYID,
                    DBRELEASE,
                    CREATED_USER
                )
                VALUES (
                    v_xref_rec.EVENT,
                    v_xref_rec.AC,
                    v_xref_rec.DEPRECATED,
                    v_xref_rec.CREATED,
                    v_xref_rec.UPDATED,
                    v_xref_rec.USERSTAMP,
                    v_xref_rec.QUALIFIER_AC,
                    v_xref_rec.DATABASE_AC,
                    v_xref_rec.PARENT_AC,
                    v_xref_rec.OWNER_AC,
                    v_xref_rec.PRIMARYID,
                    v_xref_rec.SECONDARYID,
                    v_xref_rec.DBRELEASE,
                    NVL(v_xref_rec.CREATED_USER, 'N/A')
                );

            ELSE

              --
              -- Look for feature
              --
              SELECT COUNT(1)
              INTO v_count
              FROM ia_feature_audit
              WHERE ac = v_xref_rec.PARENT_AC;

              IF(v_count <> 0) THEN

                  -- copy Xref into the right table
                  INSERT INTO ia_feature_xref_audit (
                      EVENT,
                      AC,
                      DEPRECATED,
                      CREATED,
                      UPDATED,
                      USERSTAMP,
                      QUALIFIER_AC,
                      DATABASE_AC,
                      PARENT_AC,
                      OWNER_AC,
                      PRIMARYID,
                      SECONDARYID,
                      DBRELEASE,
                      CREATED_USER
                  )
                  VALUES (
                      v_xref_rec.EVENT,
                      v_xref_rec.AC,
                      v_xref_rec.DEPRECATED,
                      v_xref_rec.CREATED,
                      v_xref_rec.UPDATED,
                      v_xref_rec.USERSTAMP,
                      v_xref_rec.QUALIFIER_AC,
                      v_xref_rec.DATABASE_AC,
                      v_xref_rec.PARENT_AC,
                      v_xref_rec.OWNER_AC,
                      v_xref_rec.PRIMARYID,
                      v_xref_rec.SECONDARYID,
                      v_xref_rec.DBRELEASE,
                      NVL(v_xref_rec.CREATED_USER, 'N/A')
                  );

              ELSE

                --
                -- Look for publication
                --
                SELECT COUNT(1)
                INTO v_count
                FROM ia_publication_audit
                WHERE ac = v_xref_rec.PARENT_AC;

                IF(v_count <> 0) THEN

                    -- copy Xref into the right table
                    INSERT INTO ia_publication_xref_audit (
                        EVENT,
                        AC,
                        DEPRECATED,
                        CREATED,
                        UPDATED,
                        USERSTAMP,
                        QUALIFIER_AC,
                        DATABASE_AC,
                        PARENT_AC,
                        OWNER_AC,
                        PRIMARYID,
                        SECONDARYID,
                        DBRELEASE,
                        CREATED_USER
                    )
                    VALUES (
                        v_xref_rec.EVENT,
                        v_xref_rec.AC,
                        v_xref_rec.DEPRECATED,
                        v_xref_rec.CREATED,
                        v_xref_rec.UPDATED,
                        v_xref_rec.USERSTAMP,
                        v_xref_rec.QUALIFIER_AC,
                        v_xref_rec.DATABASE_AC,
                        v_xref_rec.PARENT_AC,
                        v_xref_rec.OWNER_AC,
                        v_xref_rec.PRIMARYID,
                        v_xref_rec.SECONDARYID,
                        v_xref_rec.DBRELEASE,
                        NVL(v_xref_rec.CREATED_USER, 'N/A')
                    );

                ELSE

                  --
                  -- Look for component
                  --
                  SELECT COUNT(1)
                  INTO v_count
                  FROM ia_component_audit
                  WHERE ac = v_xref_rec.PARENT_AC;
  
                  IF(v_count <> 0) THEN
  
                      -- copy Xref into the right table
                      INSERT INTO ia_component_xref_audit (
                          EVENT,
                          AC,
                          DEPRECATED,
                          CREATED,
                          UPDATED,
                          USERSTAMP,
                          QUALIFIER_AC,
                          DATABASE_AC,
                          PARENT_AC,
                          OWNER_AC,
                          PRIMARYID,
                          SECONDARYID,
                          DBRELEASE,
                          CREATED_USER
                      )
                      VALUES (
                          v_xref_rec.EVENT,
                          v_xref_rec.AC,
                          v_xref_rec.DEPRECATED,
                          v_xref_rec.CREATED,
                          v_xref_rec.UPDATED,
                          v_xref_rec.USERSTAMP,
                          v_xref_rec.QUALIFIER_AC,
                          v_xref_rec.DATABASE_AC,
                          v_xref_rec.PARENT_AC,
                          v_xref_rec.OWNER_AC,
                          v_xref_rec.PRIMARYID,
                          v_xref_rec.SECONDARYID,
                          v_xref_rec.DBRELEASE,
                          NVL(v_xref_rec.CREATED_USER, 'N/A')
                      );
  
                  ELSE
    
                    --
                    -- Nothing found, delete this orphan xref
                    --
  
                    DBMS_OUTPUT.PUT_LINE('Deleting orphan Xref...');
                    DBMS_OUTPUT.PUT_LINE('DELETE FROM ia_xref WHERE ac = ' || v_xref_rec.ac || ''';' );

                  END IF;  -- components
                END IF;  -- publications
              END IF;  -- feature
            END IF;  -- biosource
          END IF;  -- controlledvocab
        END IF;  -- experiemnt
      END IF; -- interactor

  END LOOP;

CLOSE xref_cur;

END;
/

show error

