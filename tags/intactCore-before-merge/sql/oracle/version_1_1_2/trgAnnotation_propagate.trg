CREATE OR REPLACE TRIGGER trgAnnotation_propagate
	AFTER UPDATE
	ON IA_ANNOTATION
	FOR EACH ROW
BEGIN
	 /*in case of INSERTING there cannot be already an record in IA_EXP2ANNOT
	    .. because of foreign key constraint with Cascade from IA_INT2EXP to IA_INTERACTOR 
		AND ac is primary key in IA_INTERACTOR, so there cannot be already a record
		with this value */

		UPDATE IA_EXP2ANNOT
		SET UPDATED = SYSDATE
		WHERE ANNOTATION_AC= :OLD.ac
		OR 	  ANNOTATION_AC =:NEW.ac;

		UPDATE IA_INT2ANNOT
		SET UPDATED = SYSDATE
		WHERE ANNOTATION_AC= :OLD.ac
		OR 	  ANNOTATION_AC =:NEW.ac;

		UPDATE IA_FEATURE2ANNOT
		SET UPDATED = SYSDATE
		WHERE ANNOTATION_AC= :OLD.ac
		OR 	  ANNOTATION_AC =:NEW.ac;
	
	-- IN CASE OF DELETING, ALREADY CASCADE VIA FOREIGN KEY for record in ..2ANNOT
END;
/
