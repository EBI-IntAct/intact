PROMPT "Updating the View IA_EXP_PUBMED_NOT_FULLY_ACCEPT..."

  CREATE OR REPLACE FORCE VIEW "INTACT"."IA_EXP_PUBMED_NOT_FULLY_ACCEPT" ("PRIMARYID", "AC", "SHORTLABEL", "CREATED", "CREATED_USER", "UPDATED", "USERSTAMP", "BIOSOURCE_AC", "DETECTMETHOD_AC", "IDENTMETHOD_AC", "RELATEDEXPERIMENT_AC", "OWNER_AC", "FULLNAME", "DEPRECATED") AS
  select x.primaryid
       ,e.ac
       ,e.shortlabel
      ,e.created
      ,e.created_user
      ,e.updated
      ,e.userstamp
      ,e.biosource_ac
      ,e.detectmethod_ac
      ,e.identmethod_ac
      ,e.relatedexperiment_ac
      ,e.owner_ac
      ,e.fullname
      ,e.deprecated
   FROM ia_experiment e
        ,ia_xref x
        , ia_controlledvocab q
        , ia_controlledvocab db
   WHERE e.ac = x.parent_ac
   AND   x.database_ac = db.ac
   AND   db.shortlabel = 'pubmed'
   AND   x.qualifier_ac = q.ac
   AND	 q.shortlabel = 'primary-reference'
   AND	 x.primaryId in
         (   SELECT DISTINCT x.primaryId  /* select the distinct pubmedid's from experiments which have not an annotation accepted  */
             FROM ia_experiment e
                  , ia_xref x
                  , ia_controlledvocab q
                  , ia_controlledvocab db
             WHERE e.created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS')
             and   e.ac = x.parent_ac
             AND   x.database_ac = db.ac
             AND   db.shortlabel = 'pubmed'
             AND   x.qualifier_ac = q.ac
             AND   q.shortlabel = 'primary-reference'
             AND  e.ac NOT IN
                ( SELECT e.ac  /* select all experiments having an annotation( accepted )  */
                  FROM ia_experiment e
                       ,ia_exp2annot e2a
                       ,ia_controlledvocab cv
                       ,ia_annotation annot
                  WHERE e.ac = e2a.experiment_ac
          		    AND   e2a.annotation_ac = annot.ac
          		    AND   annot.topic_ac = cv.ac
          		    AND	  cv.shortlabel IN ('accepted')
                )
            UNION
            SELECT DISTINCT x.primaryId  /* select the distinct pubmedid's from experiments which have annotation on hold  */
             FROM ia_experiment e
                  , ia_xref x
                  , ia_controlledvocab q
                  , ia_controlledvocab db
                  ,ia_exp2annot e2a
                  ,ia_controlledvocab cv
                  ,ia_annotation annot
             WHERE e.ac = x.parent_ac
             AND   x.database_ac = db.ac
             AND   db.shortlabel = 'pubmed'
             AND   x.qualifier_ac = q.ac
             AND   q.shortlabel = 'primary-reference'
             AND   e.ac = e2a.experiment_ac
             AND   e2a.annotation_ac = annot.ac
    		     AND   annot.topic_ac = cv.ac
    		     AND	 cv.shortlabel IN ('on-hold')

          )
order by primaryid, shortlabel
;
