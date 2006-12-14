-- new fields in IA_Component table

SELECT  'Altering table "IA_Component"';
ALTER TABLE IA_Component  ADD COLUMN identMethod_ac VARCHAR(30)  NULL constraint fk_Component$identMethod  REFERENCES IA_ControlledVocab(ac);

ALTER TABLE IA_Component  ADD COLUMN experimentalrole_ac VARCHAR(30)  NULL constraint fk_Component$experimentalrole  REFERENCES IA_ControlledVocab(ac);

ALTER TABLE IA_Component  ADD COLUMN biologicalrole_ac VARCHAR(30)  NULL constraint fk_Component$biologicalrole  REFERENCES IA_ControlledVocab(ac);



