/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:   Anonymous PL/SQL block allowing to update regularly the BioSource Statistics table
             for the statisticView Application


  $Date$
  $Author$
  $Locker$
  
*************************************************************/

declare
  cursor c_bin (b_taxid ia_biosource.taxid%TYPE)
  IS SELECT count(distinct(component1.interaction_ac)) AS binary_interactions
          FROM ia_interactor i1
             , ia_component component1
          WHERE 2=(SELECT COUNT(*)
                   FROM ia_component component2
                   WHERE component1.interaction_ac = component2.interaction_ac)
          AND i1.biosource_ac in (SELECT ac FROM ia_biosource WHERE taxid = b_taxid)
          AND component1.interactor_ac = i1.ac
   ;
   cursor c_prot (b_taxid ia_biosource.taxid%TYPE)
   IS
     SELECT count(distinct(i2.ac)) AS proteins
          FROM ia_interactor i2
          WHERE i2.biosource_ac in (SELECT ac FROM ia_biosource where taxid = b_taxid)
     AND  i2.objclass ='uk.ac.ebi.intact.model.ProteinImpl'
   ;
   cnt_bin INTEGER;
   cnt_prot INTEGER;
BEGIN
   FOR r in (SELECT DISTINCT taxid,shortlabel FROM ia_biosource b1 where length(shortlabel) =
(select min(length(shortlabel)) from ia_biosource b2 where b1.taxid=b2.taxid) order by 2) LOOP
	OPEN c_bin (r.taxid);
       FETCH c_bin INTO cnt_bin;
       CLOSE c_bin;
	OPEN c_prot (r.taxid);
       FETCH c_prot INTO cnt_prot;
       CLOSE c_prot;
     INSERT INTO ia_biosourcestatistics(ac, taxid, shortlabel, protein_number, binary_interactions)
     VALUES (Intact_statistics_seq.nextval, r.taxid, r.shortlabel, cnt_bin, cnt_prot);
   END LOOP;
END;

