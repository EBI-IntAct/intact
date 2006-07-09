/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:   Anonymous PL/SQL block allowing to update regularly the BioSource Statistics table
             for the statisticView Application


  $Date: 2005-07-25 14:43:41 +0100 (dl, 25 jul 2005) $
  $Author: skerrien $
  $Id: insert_ia_biosourcestatistics.sql 4212 2005-07-25 13:43:41Z skerrien $
  
*************************************************************/

SET   SERVEROUT ON
SET   FEEDBACK OFF
SET   VERIFY OFF
SET   LINES 150
SET   PAGES 20000
SET   DOC OFF


DECLARE

  -- given a biosource taxid, gives a count of binary interaction
  cursor c_interaction (b_taxid ia_biosource.taxid%TYPE)
  IS
          SELECT count( 1 ) AS cnt_interactions
          FROM   ia_interactions i
          WHERE  i.taxid = b_taxid ;


   -- Return a count of protein having the given taxid and being used in at least one interaction
   cursor c_prot (b_taxid ia_biosource.taxid%TYPE)
   IS
        SELECT count(distinct(I.ac)) as PROTEINS_WITH_INTERACTIONS
        FROM ia_interactor I, ia_component C
        WHERE I.objclass like '%Protein%' AND
              C.interactor_ac = I.ac AND
              I.biosource_ac IN ( SELECT ac
                                  FROM ia_biosource
                                  WHERE taxid = b_taxid );

   cnt_component INTEGER;
   cnt_interaction INTEGER;
   cnt_bin INTEGER;
   cnt_prot INTEGER;
   cnt_total INTEGER;

BEGIN

   dbms_output.enable ( 1000000 );
   cnt_total := 0;

   -- select the biosources but only those that have no CellType or Tissue
   -- the work around here is to select from a pool of BioSource having the same taxid
   -- the one with the shortest shortlabel.
   FOR r in (SELECT DISTINCT taxid, shortlabel
             FROM ia_biosource b1
             WHERE length(shortlabel) = (SELECT min( length( shortlabel ) )
                                         FROM ia_biosource b2
                                         WHERE b1.taxid=b2.taxid)
             order by shortlabel)
   LOOP

       OPEN c_interaction (r.taxid);
       FETCH c_interaction INTO cnt_bin;
       CLOSE c_interaction;

       OPEN c_prot (r.taxid);
       FETCH c_prot INTO cnt_prot;
       CLOSE c_prot;

       INSERT INTO ia_biosourcestatistics( ac, taxid, shortlabel, protein_number, binary_interactions )
       VALUES ( Intact_statistics_seq.nextval, r.taxid, r.shortlabel, cnt_prot, cnt_bin );

       if ( cnt_bin > 50 ) THEN

            dbms_output.put_line ( r.shortlabel || '   ' || 'taxid(' || r.taxid || ')' || '  I' || '(' || cnt_bin || ')' || '  P' || '(' || cnt_prot || ')'  );

       END IF;

       cnt_total := cnt_total + cnt_bin;

   END LOOP;

   dbms_output.put_line ( '... (we only displayed biosources having more than 50 interactions)' );
   dbms_output.put_line ( 'Total Interaction Count: ' || cnt_total );

END;
/

exit
