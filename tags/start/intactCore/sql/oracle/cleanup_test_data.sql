SET DOC OFF
/*************************************************************

  Package:    IntAct core

  Purpose:    Clean test data out following a Unit Test Problem

  Usage:      sqlplus username/password@INSTANCE @cleanup_test_data.sql

  $Date$
  $Locker$

  *************************************************************/

-- same SQL, different tables
Prompt Cleaning up unit test data....
PROMPT ... Interactor
DELETE FROM Interactor WHERE ac LIKE 'EBITEST%';

PROMPT ... Experiment
DELETE FROM Experiment WHERE ac LIKE 'EBITEST%';

PROMPT ... Biosource
DELETE FROM BioSource WHERE ac LIKE 'EBITEST%';

PROMPT ... Xref
DELETE FROM Xref WHERE ac LIKE 'EBITEST%';

PROMPT ... ControlledVocab
DELETE FROM ControlledVocab WHERE ac LIKE 'EBITEST%';

PROMPT ... Institution
DELETE FROM Institution WHERE ac LIKE 'EBITEST%';

commit;

exit;






