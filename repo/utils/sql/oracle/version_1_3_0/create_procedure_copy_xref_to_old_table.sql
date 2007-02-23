create or replace PROCEDURE copy_xref_to_old_table AS -- cursor over all Xrefs
    CURSOR xref_cur IS
    select *
                        from ia_biosource_xref
                union
                        select *
                        from ia_interactor_xref
                union
                        select *
                        from ia_feature_xref
                union
                        select *
                        from ia_experiment_xref
                union
                        select *
                        from ia_controlledvocab_xref
                union
                        select *
                        from ia_publication_xref;

v_xref_rec ia_biosource_xref%ROWTYPE;

BEGIN

  -- TODO clean up all specific xref tables before to fill them up
  -- no check will be done for duplicated rows !!
  DBMS_OUTPUT.PUT_LINE('Copying xref from ia_biosourc_xref, ia_interactor_xref, ia_feature_xref... to ia_xref');
  OPEN xref_cur;
  LOOP

      FETCH xref_cur INTO v_xref_rec;
      EXIT WHEN xref_cur % NOTFOUND;


      INSERT INTO ia_xref(
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

        END LOOP;
        commit;
        CLOSE xref_cur;
        DBMS_OUTPUT.PUT_LINE('Copying xref done');

END;
/

show errors