select cv.ac, cv.objclass, cv.shortlabel, x.primaryid
from ia_controlledvocab cv, ia_controlledvocab_xref x, ia_controlledvocab db
where (cv.objclass like '%ExperimentalRole%' or cv.objclass like '%BiologicalRole%')
      and x.parent_ac = cv.ac
      and x.database_ac = db.ac
      and db.shortlabel = 'psi-mi'
order by objclass, cv.shortlabel;