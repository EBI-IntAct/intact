/* SET DOC OFF */
/*************************************************************

  Package:    IntAct

  Purpose:    Create dummy data for IntAct core

              Postgres version

  $Date$
  $Locker$

  *************************************************************/

INSERT INTO IA_biosource (ac, taxid, shortlabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
              '-1',
              'in vitro',
              ac
       FROM IA_Institution
       WHERE shortLabel='EBI';
