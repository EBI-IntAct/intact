-- Triggers necessary while the old ia_alias and the new ones coexist, so no constraints
-- are violated

PROMPT Creating trigger "TRG_IA_BIOSOURCE_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_BIOSOURCE_ALIAS_DELETE BEFORE DELETE ON IA_BIOSOURCE
FOR EACH ROW
BEGIN
  DELETE FROM ia_biosource_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_CVOBJECT_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_CVOBJECT_ALIAS_DELETE BEFORE DELETE ON IA_CONTROLLEDVOCAB
FOR EACH ROW
BEGIN
  DELETE FROM ia_controlledvocab_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_EXPERIMENT_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_EXPERIMENT_ALIAS_DELETE BEFORE DELETE ON IA_EXPERIMENT
FOR EACH ROW
BEGIN
  DELETE FROM ia_experiment_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_FEATURE_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_FEATURE_ALIAS_DELETE BEFORE DELETE ON IA_FEATURE
FOR EACH ROW
BEGIN
  DELETE FROM ia_feature_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_PUB_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_PUB_ALIAS_DELETE BEFORE DELETE ON IA_PUBLICATION
FOR EACH ROW
BEGIN
  DELETE FROM ia_publication_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_INTERACTOR_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_INTERACTOR_ALIAS_DELETE BEFORE DELETE ON IA_INTERACTOR
FOR EACH ROW
BEGIN
  DELETE FROM ia_interactor_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error


PROMPT Creating trigger "TRG_IA_COMPONENT_ALIAS_DELETE"
create or replace TRIGGER TRG_IA_COMPONENT_ALIAS_DELETE BEFORE DELETE ON IA_COMPONENT
FOR EACH ROW
BEGIN
  DELETE FROM ia_component_alias
  WHERE parent_ac = :OLD.ac;
END;
/
show error

