  CREATE OR REPLACE FORCE VIEW "INTACT"."V_EXPERIMENT_SUBMITTED" ("AC", "SHORTLABEL", "CREATED", "SUBMITTED_BY", "PUBMED", "INTERACTION_COUNT") AS 
  select e.ac, e.shortlabel, e.created, a.description as submitted_by, x.primaryid as pubmed, count(i2e.interaction_ac) as interaction_count
from ia_experiment e, ia_exp2annot e2a, ia_controlledvocab topic_i, ia_annotation a,
     ia_experiment_xref x, ia_controlledvocab db, ia_controlledvocab q, ia_int2exp i2e
where e.ac = e2a.experiment_ac
      and e.ac = i2e.experiment_ac
      and e2a.annotation_ac = a.ac
      and a.topic_ac = topic_i.ac
      and topic_i.shortlabel in ('submitted')
      and e.ac = x.parent_ac
      and x.database_ac = db.ac
      and db.shortlabel = 'pubmed'
      and x.qualifier_ac = q.ac
      and q.shortlabel = 'primary-reference'
group by e.ac, e.shortlabel, e.created, a.description, x.primaryid;
