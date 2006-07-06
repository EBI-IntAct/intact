/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/

/*************************************************************

  Package:    IntAct core

  Purpose:    Clean test data out following a Unit Test Problem

  Usage:      sqlplus username/password@INSTANCE @cleanup_test_data.sql

  $Date$
  $Locker$

  *************************************************************/

SET DOC OFF

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






