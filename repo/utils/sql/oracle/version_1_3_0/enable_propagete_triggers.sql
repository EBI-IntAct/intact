PROMPT "Enabling update date propagation triggers..."

ALTER TRIGGER trgAlias_propagate         ENABLE;
ALTER TRIGGER trgAnnotation_propagate    ENABLE;
ALTER TRIGGER trgExp2Annot_propagate     ENABLE;
ALTER TRIGGER trgFeature_propagate       ENABLE;
ALTER TRIGGER trgFeature2annot_propagate ENABLE;
ALTER TRIGGER trgInt2Annot_propagate     ENABLE;
ALTER TRIGGER trgINT2EXP_propagate       ENABLE;
ALTER TRIGGER trgInteractor_propagate    ENABLE;
ALTER TRIGGER trgRange_propagate         ENABLE;
ALTER TRIGGER trgXref_propagate          ENABLE;

