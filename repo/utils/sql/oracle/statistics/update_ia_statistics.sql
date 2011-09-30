/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Anonymous PL/SQL block allowing to update regularly the IntAct statistics table (ia_statistics).

  Usage:      sqlplus username/password@instance @programme.sql

  Input parameter:
       p_start : the date of the first row to be created (non mandatory, can be set to NULL)
       v_num_day : the number of days between two statistics

  Note: that script doesn't recreate or update a stat if it already exists.
        to do so, you should clean the table first and regenerate it all.

  $Date$
  $Author$
  $Locker$

  
*************************************************************/

WHENEVER SQLERROR EXIT FAILURE ROLLBACK

SET   SERVEROUT ON
SET   FEEDBACK OFF
SET   VERIFY OFF
SET   LINES 150
SET   PAGES 20000
SET   DOC OFF


DECLARE

    v_protein_number        IA_Statistics.protein_number%TYPE;
    v_interaction_number    IA_Statistics.interaction_number%TYPE;
    v_interactions          IA_Statistics.binary_interactions%TYPE;
    v_components            IA_Statistics.binary_interactions%TYPE;
    v_binary_interactions   IA_Statistics.binary_interactions%TYPE;
    v_complex_interactions  IA_Statistics.complex_interactions%TYPE;
    v_experiment_number     IA_Statistics.experiment_number%TYPE;
    v_term_number           IA_Statistics.term_number%TYPE;
    v_publications          IA_Statistics.publication_count%TYPE;

    v_date    DATE;                             /* Current date               */
    p_start   VARCHAR2( 10 ) := '2003-08-02';   /* Starting date              */
    v_num_day NUMBER := 7;                      /* day interval betweem stats */
    i         NUMBER;
    v_negative_binary NUMBER;
    v_self_binary NUMBER;
    v_negative_self NUMBER;
BEGIN

    DBMS_OUTPUT.enable( 1000000 );
    /* get latest date from database */ 
    IF p_start IS NULL THEN
        DBMS_OUTPUT.PUT_LINE( 'No start date given, get one from the oldest Controlled vocabulary' );
        SELECT MIN( created ) INTO v_date
        FROM ia_controlledvocab;
    ELSE
        DBMS_OUTPUT.PUT_LINE( 'Use the given start date: ' || p_start );
        v_date := TO_DATE( p_start, 'yyyy-mm-dd' );
    END IF;

    -- v_date := TO_DATE( '2004-01-01', 'yyyy-mm-dd' );
    DBMS_OUTPUT.PUT_LINE( 'Using start date: ' || to_char( v_date ) );


    LOOP
        -- Would be nice to check what is the last date before to exit the loop,
        -- if it's close enough from the current date, generate the stat line at SYSDATE.
        -- eg. if v_date - SYSDATE < 2 days ?

        -- The loop is exited at the end

        DBMS_OUTPUT.PUT_LINE( 'LOOP: ' || to_char( v_date ) );

        -- Check if that line is already present in the table, if so skip it !
        SELECT count(*) INTO i
        FROM ia_statistics
        WHERE timestamp = v_date;

        IF i = 0 THEN

            -- collect the statistics
            --
            SELECT count(*) INTO v_protein_number
            FROM IA_Interactor
            WHERE created <= v_date AND
                  objclass like '%Protein%';

            SELECT count(*) INTO v_interaction_number
            FROM ia_interactor
            WHERE created <= v_date AND
                  objclass like '%Interaction%';

            SELECT count(ac) INTO v_components
            FROM ia_component
            WHERE created <= v_date;

            SELECT count(ac) INTO v_interactions
            FROM ia_interactor
            WHERE objClass like '%Interaction%' AND
                  created <= v_date;

           SELECT count(distinct comp.ac) - 1 INTO v_negative_binary 
           FROM ia_interactor i, ia_int2annot ia, ia_annotation a, ia_controlledvocab c, ia_component comp
           WHERE i.objclass like '%Interaction%'
           AND i.ac = ia.interactor_ac
           AND ia.annotation_ac = a.ac
           AND a.topic_ac = c.ac
           AND c.shortlabel = 'negative'
           AND comp.interaction_ac = i.ac
           AND i.created <= v_date;

           SELECT count(distinct c.interaction_ac) INTO v_self_binary
           FROM ia_component c
           WHERE   
           (
           c.stoichiometry > 2 OR c.stoichiometry = 2
           )
           AND c.interaction_ac IN
           (
           SELECT distinct comp.interaction_ac
           FROM ia_component comp
           GROUP BY comp.interaction_ac
           HAVING count(comp.ac) = 1
           )
           AND c.created <= v_date;

           SELECT count(distinct c.interaction_ac) INTO v_negative_self
           FROM ia_interactor i, ia_int2annot ia, ia_annotation a, ia_controlledvocab cont, ia_component c
           WHERE i.objclass like '%Interaction%'
           AND i.ac = ia.interactor_ac
           AND ia.annotation_ac = a.ac
           AND a.topic_ac = cont.ac
           AND cont.shortlabel = 'negative'
           AND c.interaction_ac = i.ac
           AND
           (
           c.stoichiometry > 2 OR c.stoichiometry = 2
           )
           AND c.interaction_ac IN
           (
           SELECT distinct comp.interaction_ac
           FROM ia_component comp
           GROUP BY comp.interaction_ac
           HAVING count(comp.ac) = 1
           )
           AND c.created <= v_date;

            v_binary_interactions := v_components - v_interactions + v_self_binary - (v_negative_binary + v_negative_self);

            SELECT count( distinct interaction_ac ) INTO v_complex_interactions
            FROM IA_Component c1
            WHERE interaction_ac IN (SELECT interaction_ac
                                     FROM IA_Component c2
                                     WHERE c2.created <= v_date AND                   -- date filter
                                           c1.interaction_ac = c2.interaction_ac
                                     GROUP BY interaction_ac
                                     HAVING count( * ) > 2
                                    );

            SELECT count(*) INTO v_experiment_number
            FROM IA_Experiment
            WHERE created <= v_date;

            SELECT count(*) INTO v_term_number
            FROM IA_ControlledVocab
            WHERE created <= v_date;

            SELECT count( distinct x.primaryId ) INTO v_publications
            FROM ia_experiment e, ia_experiment_xref x, ia_controlledvocab q, ia_controlledvocab db
            WHERE e.shortlabel like '%' and
                  e.ac = x.parent_ac and
                  x.database_ac = db.ac and
                  db.shortlabel = 'pubmed' and
                  x.qualifier_ac = q.ac and
                  q.shortlabel = 'primary-reference' and
                  e.created <= v_date;

            DBMS_OUTPUT.PUT_LINE( 'CREATE: ' || to_char( v_date )||': '||to_char(v_interaction_number)||' '||to_char(v_binary_interactions)||' '||to_char(v_complex_interactions)||' '||to_char(v_experiment_number)||' '||to_char(v_term_number) ||' '||to_char(v_publications));

            INSERT INTO ia_statistics
            (timestamp, interaction_number, binary_interactions, protein_number, complex_interactions, experiment_number, 
             term_number, publication_count )
            VALUES
            (v_date, v_interaction_number, v_binary_interactions, v_protein_number, v_complex_interactions, v_experiment_number, 
             v_term_number, v_publications );

        END IF; -- case where the line as to be created

        -- check if the date is bigger than the sysdate and exit the loop in that case
        EXIT WHEN v_date > SYSDATE;

        -- next date
        v_date := v_date + v_num_day;

    END LOOP;

    -- Check if the latest date (ie. the overcome the current date)

    commit;

END;

/

exit
