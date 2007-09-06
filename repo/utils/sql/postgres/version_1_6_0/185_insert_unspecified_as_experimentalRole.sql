create or replace FUNCTION  insert_unspecified_as_experimentalrole() RETURNS VOID AS $$ 
declare
     identity_ac ia_controlledvocab.ac%TYPE;
     psimi_ac ia_controlledvocab.ac%TYPE;
     unspecified_exprole_ac ia_controlledvocab.ac%TYPE;
     my_owner_ac ia_controlledvocab.ac%TYPE;
     pubmed_ac ia_controlledvocab.ac%TYPE;
     primaryref_ac ia_controlledvocab.ac%TYPE;
     
begin
--     dbms_output.enable(1000000000000);

     ------------------
     -- get psimi_ac --
     ------------------
     select ac
     into psimi_ac
     from ia_controlledvocab
     where shortlabel = 'psi-mi';
     
     ------------------
     -- get owner_ac --
     ------------------
     select owner_ac
     into my_owner_ac
     from ia_controlledvocab
     where shortlabel = 'psi-mi';
     
     ---------------------
     -- get identity_ac --
     ---------------------
     select ac
     into identity_ac
     from ia_controlledvocab
     where shortlabel = 'identity';

          
     -------------------
     -- get pubmed_ac --
     -------------------
     select cv.ac
     into pubmed_ac
     from ia_controlledvocab cv, ia_controlledvocab_xref x
     where cv.ac = x.parent_ac
     and x.primaryId = 'MI:0446'
     and x.database_ac = psimi_ac
     and x.qualifier_ac = identity_ac;

      -------------------
      -- get pubmed_ac --
      -------------------
      select cv.ac
      into primaryref_ac
      from ia_controlledvocab cv, ia_controlledvocab_xref x
      where cv.ac = x.parent_ac
      and x.primaryId = 'MI:0358'
      and x.database_ac = psimi_ac
      and x.qualifier_ac = identity_ac;

     -------------------------------------------------------------
     -- Insert "unspecified role" exprole_ac in ia_controlledvocab --
     -------------------------------------------------------------     
     insert into ia_controlledvocab (ac, 
                         owner_ac, 
                         objclass, 
                         shortlabel, 
                         fullname)
     values ('EBI-' || nextval('intact_ac'), 
          my_owner_ac, 
          'uk.ac.ebi.intact.model.CvExperimentalRole', 
          'unspecified role',
          'unspecified role');

     ------------------------------------
     -- Get the unspecified_exprole_ac --
     ------------------------------------
     select ac
     into unspecified_exprole_ac
     from ia_controlledvocab 
     where shortlabel = 'unspecified role'
     and objclass = 'uk.ac.ebi.intact.model.CvExperimentalRole';

     ---------------------------------------------------------------
     -- Add the psi-mi identity xref to the "unspecified role" cv --
     ---------------------------------------------------------------
     insert into ia_controlledvocab_xref (parent_ac, 
                              ac, 
                              qualifier_ac, 
                              database_ac, 
                              owner_ac, 
                              primaryId)
     values (unspecified_exprole_ac, 
          'EBI-' || nextval('intact_ac'), 
          identity_ac, 
          psimi_ac, 
          my_owner_ac, 
          'MI:0499');

        ------------------------------------------------------------------
        -- Add the psi-mi primary-ref xref to the "unspecified role" cv --
        ------------------------------------------------------------------
     insert into ia_controlledvocab_xref (parent_ac,
                                     ac,
                                     qualifier_ac,
                                     database_ac,
                                     owner_ac,
                                     primaryId)
     values (unspecified_exprole_ac,
             'EBI-' || nextval('intact_ac'),
             primaryref_ac,
             pubmed_ac,
             my_owner_ac, 
          '14755292');

end; 
$$ LANGUAGE plpgsql;


SELECT  insert_unspecified_as_experimentalrole() ;

