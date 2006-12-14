-- Triggers necessary while the old ia_XREF and the OLD ones coexist, so no constraints
-- are violated

CREATE OR REPLACE FUNCTION ia_component_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_component_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_COMPONENT_XREF_DELETE BEFORE DELETE ON IA_COMPONENT
FOR EACH ROW EXECUTE PROCEDURE ia_component_xref_delete();



CREATE OR REPLACE FUNCTION ia_biosource_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_biosource_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_BIOSOURCE_XREF_DELETE BEFORE DELETE ON IA_BIOSOURCE
FOR EACH ROW EXECUTE PROCEDURE ia_biosource_xref_delete();



CREATE OR REPLACE FUNCTION ia_controlledvocab_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_controlledvocab_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_CVOBJECT_XREF_DELETE BEFORE DELETE ON IA_CONTROLLEDVOCAB
FOR EACH ROW EXECUTE PROCEDURE ia_controlledvocab_xref_delete();



CREATE OR REPLACE FUNCTION ia_experiment_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_experiment_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_EXPERIMENT_XREF_DELETE BEFORE DELETE ON IA_EXPERIMENT
FOR EACH ROW EXECUTE PROCEDURE ia_experiment_xref_delete();



CREATE OR REPLACE FUNCTION ia_feature_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_feature_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_FEATURE_XREF_DELETE BEFORE DELETE ON IA_FEATURE
FOR EACH ROW EXECUTE PROCEDURE ia_feature_xref_delete();



CREATE OR REPLACE FUNCTION ia_publication_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_publication_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_PUB_XREF_DELETE BEFORE DELETE ON IA_PUBLICATION
FOR EACH ROW EXECUTE PROCEDURE ia_publication_xref_delete();



CREATE OR REPLACE FUNCTION ia_interactor_xref_delete() RETURNS trigger AS '
BEGIN
  DELETE FROM ia_interactor_xref
  WHERE parent_ac = OLD.ac;
  RETURN OLD;
END
' LANGUAGE plpgsql;

create TRIGGER TRG_IA_INTERACTOR_XREF_DELETE BEFORE DELETE ON IA_INTERACTOR
FOR EACH ROW EXECUTE PROCEDURE ia_interactor_xref_delete();



