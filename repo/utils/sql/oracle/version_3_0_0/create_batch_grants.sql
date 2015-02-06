-------------------------------------
-- Update right on created objects --
-------------------------------------

grant select on BATCH_JOB_INSTANCE to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_INSTANCE to INTACT_CURATOR;

grant select on BATCH_JOB_EXECUTION to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_EXECUTION to INTACT_CURATOR;

grant select on BATCH_JOB_EXECUTION_PARAMS to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_EXECUTION_PARAMS to INTACT_CURATOR;

grant select on BATCH_STEP_EXECUTION to INTACT_SELECT;
grant select, insert,update,delete on BATCH_STEP_EXECUTION to INTACT_CURATOR;

grant select on BATCH_STEP_EXECUTION_CONTEXT to INTACT_SELECT;
grant select, insert,update,delete on BATCH_STEP_EXECUTION_CONTEXT to INTACT_CURATOR;

grant select on BATCH_JOB_EXECUTION_CONTEXT to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_EXECUTION_CONTEXT to INTACT_CURATOR;

grant select on BATCH_STEP_EXECUTION_SEQ to INTACT_SELECT;
grant select, insert,update,delete on BATCH_STEP_EXECUTION_SEQ to INTACT_CURATOR;

grant select on BATCH_JOB_EXECUTION_SEQ to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_EXECUTION_SEQ to INTACT_CURATOR;

grant select on BATCH_JOB_SEQ to INTACT_SELECT;
grant select, insert,update,delete on BATCH_JOB_SEQ to INTACT_CURATOR;

