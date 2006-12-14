-- Triggers necessary while the old ia_ALIAS and the OLD ones coexist, so no constraints
-- are violated

CREATE OR REPLACE FUNCTION ia_component_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_component_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_COMPONENT_ALIAS_DELETE BEFORE DELETE ON IA_COMPONENT
FOR EACH ROW EXECUTE PROCEDURE ia_component_alias_delete();



CREATE OR REPLACE FUNCTION ia_biosource_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_biosource_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_BIOSOURCE_ALIAS_DELETE BEFORE DELETE ON IA_BIOSOURCE
FOR EACH ROW EXECUTE PROCEDURE ia_biosource_alias_delete();



CREATE OR REPLACE FUNCTION ia_controlledvocab_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_controlledvocab_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_CVOBJECT_ALIAS_DELETE BEFORE DELETE ON IA_CONTROLLEDVOCAB
FOR EACH ROW EXECUTE PROCEDURE ia_controlledvocab_alias_delete();



CREATE OR REPLACE FUNCTION ia_experiment_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_experiment_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_EXPERIMENT_ALIAS_DELETE BEFORE DELETE ON IA_EXPERIMENT
FOR EACH ROW EXECUTE PROCEDURE ia_experiment_alias_delete();



CREATE OR REPLACE FUNCTION ia_feature_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_feature_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_FEATURE_ALIAS_DELETE BEFORE DELETE ON IA_FEATURE
FOR EACH ROW EXECUTE PROCEDURE ia_feature_alias_delete();



CREATE OR REPLACE FUNCTION ia_publication_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_publication_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_PUB_ALIAS_DELETE BEFORE DELETE ON IA_PUBLICATION
FOR EACH ROW EXECUTE PROCEDURE ia_publication_alias_delete();



CREATE OR REPLACE FUNCTION ia_interactor_alias_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_interactor_alias
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_INTERACTOR_ALIAS_DELETE BEFORE DELETE ON IA_INTERACTOR
FOR EACH ROW EXECUTE PROCEDURE ia_interactor_alias_delete();



