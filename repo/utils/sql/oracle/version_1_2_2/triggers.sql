-- Those triggers are to allow to delete cross referenced object.  Before deleting a
-- cross referenced object you need first to delete the xreference that are attached to it. That
-- what those triggers do.

PROMPT Creating trigger "TRG_IA_BIOSOURCE_XREF_DELETE"
create or replace TRIGGER TRG_IA_BIOSOURCE_XREF_DELETE BEFORE DELETE ON IA_BIOSOURCE
FOR EACH ROW
BEGIN
  DELETE FROM ia_biosource_xref
  WHERE parent_ac = :OLD.ac;
END;
/

PROMPT Creating trigger "TRG_IA_CVOBJECT_XREF_DELETE"
create or replace TRIGGER TRG_IA_CVOBJECT_XREF_DELETE BEFORE DELETE ON IA_CONTROLLEDVOCAB
FOR EACH ROW
BEGIN
  DELETE FROM ia_controlledvocab_xref
  WHERE parent_ac = :OLD.ac;
END;
/

PROMPT Creating trigger "TRG_IA_EXPERIMENT_XREF_DELETE"
create or replace TRIGGER TRG_IA_EXPERIMENT_XREF_DELETE BEFORE DELETE ON IA_EXPERIMENT
FOR EACH ROW
BEGIN
  DELETE FROM ia_experiment_xref
  WHERE parent_ac = :OLD.ac;
END;
/

PROMPT Creating trigger "TRG_IA_FEATURE_XREF_DELETE"
create or replace TRIGGER TRG_IA_FEATURE_XREF_DELETE BEFORE DELETE ON IA_FEATURE
FOR EACH ROW
BEGIN
  DELETE FROM ia_feature_xref
  WHERE parent_ac = :OLD.ac;
END;
/

PROMPT Creating trigger "TRG_IA_PUBLICATION_XREF_DELETE"
create or replace TRIGGER TRG_IA_PUBLICATION_XREF_DELETE BEFORE DELETE ON IA_PUBLICATION
FOR EACH ROW
BEGIN
  DELETE FROM ia_publication_xref
  WHERE parent_ac = :OLD.ac;
END;
/

PROMPT Creating trigger "TRG_IA_INTERACTOR_XREF_DELETE"
create or replace TRIGGER TRG_IA_INTERACTOR_XREF_DELETE BEFORE DELETE ON IA_INTERACTOR
FOR EACH ROW
BEGIN
  DELETE FROM ia_interactor_xref
  WHERE parent_ac = :OLD.ac;
END;
/