/* SET DOC OFF */
/*************************************************************

  Package:    IntAct

  Purpose:    Create dummy data for IntAct core

              Postgres version

  $Date: 2006-05-23 09:51:31 +0100 (dt, 23 mai 2006) $
  $Locker$

  *************************************************************/

INSERT INTO IA_biosource (ac, taxid, shortlabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
              '-1',
              'in vitro',
              ac
       FROM IA_Institution
       WHERE shortLabel='EBI';
