CREATE OR REPLACE VIEW IA_EXP_PUBMED_NOT_FULLY_ACCEPT AS
select e.*
       ,x.primaryid
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
          );


