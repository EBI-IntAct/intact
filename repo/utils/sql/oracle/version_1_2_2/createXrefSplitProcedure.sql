create or replace PROCEDURE proc_split_xref AS -- cursor over all Xrefs
CURSOR xref_cur IS
SELECT *
FROM ia_xref;

v_xref_rec ia_xref%ROWTYPE;

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
      FROM ia_interactor
      WHERE ac = v_xref_rec.PARENT_AC;

      IF(v_count <> 0) THEN

        -- copy Xref into the right table
        INSERT INTO ia_interactor_xref (
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
            v_xref_rec.CREATED_USER
        );

      ELSE

        --
        -- Look for experiment
        --
        SELECT COUNT(1)
        INTO v_count
        FROM ia_experiment
        WHERE ac = v_xref_rec.PARENT_AC;

        IF(v_count <> 0) THEN

            -- copy Xref into the right table
            INSERT INTO ia_experiment_xref (
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
                v_xref_rec.CREATED_USER
            );

        ELSE

          --
          -- Look for controlled vocabulary
          --
          SELECT COUNT(1)
          INTO v_count
          FROM ia_controlledvocab
          WHERE ac = v_xref_rec.PARENT_AC;

          IF(v_count <> 0) THEN

              -- copy Xref into the right table
              INSERT INTO ia_controlledvocab_xref (
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
                  v_xref_rec.CREATED_USER
              );

          ELSE

            --
            -- Look for biosource
            --
            SELECT COUNT(1)
            INTO v_count
            FROM ia_biosource
            WHERE ac = v_xref_rec.PARENT_AC;

            IF(v_count <> 0) THEN

                -- copy Xref into the right table
                INSERT INTO ia_biosource_xref (
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
                    v_xref_rec.CREATED_USER
                );

            ELSE

              --
              -- Look for feature
              --
              SELECT COUNT(1)
              INTO v_count
              FROM ia_feature
              WHERE ac = v_xref_rec.PARENT_AC;

              IF(v_count <> 0) THEN

                  -- copy Xref into the right table
                  INSERT INTO ia_feature_xref (
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
                      v_xref_rec.CREATED_USER
                  );

              ELSE

                --
                -- Look for publication
                --
                SELECT COUNT(1)
                INTO v_count
                FROM ia_publication
                WHERE ac = v_xref_rec.PARENT_AC;

                IF(v_count <> 0) THEN

                    -- copy Xref into the right table
                    INSERT INTO ia_publication_xref (
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
                        v_xref_rec.CREATED_USER
                    );

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
/

exit