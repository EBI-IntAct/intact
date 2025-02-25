/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.view.webapp.model;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.primefaces.model.LazyDataModel;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrSearcher;
import uk.ac.ebi.intact.dataexchange.psimi.solr.InteractorIdCount;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.InteractorXref;

import javax.persistence.Query;
import java.util.*;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorSearchResultDataModel extends LazyDataModel<InteractorIdCount> {

    private static final Log log = LogFactory.getLog(InteractorSearchResultDataModel.class);

    private static String DEFAULT_SORT_COLUMN = "relevancescore";

    private SolrQuery solrQuery;
    private SolrServer solrServer;

    private String[] interactorTypeMis;

    private List<InteractorIdCount> idCounts;

    private int rowIndex = -1;
    private int firstResult = 0;

    private String sortColumn = DEFAULT_SORT_COLUMN;
    private SolrQuery.ORDER sortOrder = SolrQuery.ORDER.asc;

    private Map<String,List<InteractorIdCount>> cache = new LRUMap(5);
    private Map<String,InteractorWrapper> interactorCache = new LRUMap(200);

    public InteractorSearchResultDataModel(SolrServer solrServer, SolrQuery solrQuery, String interactorTypeMi)  {
        this(solrServer, solrQuery, new String[] {interactorTypeMi});
    }

    public InteractorSearchResultDataModel(SolrServer solrServer, SolrQuery solrQuery, String[] interactorTypeMis)  {
        if (solrQuery == null) {
            throw new IllegalArgumentException("Trying to create data model with a null SolrQuery");
        }

        this.solrServer = solrServer;
        this.solrQuery = solrQuery.getCopy();
        this.interactorTypeMis = interactorTypeMis;
    }


    @Override
    public List<InteractorIdCount> load(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters) {
        if (solrQuery == null) {
            throw new IllegalStateException("Trying to fetch results for a null SolrQuery");
        }

        String cacheKey = solrQuery+"_"+ Arrays.toString(interactorTypeMis);

        if (cache.containsKey(cacheKey)) {
            if (log.isDebugEnabled()) log.debug("Fetching interactors for query (cache hit): "+solrQuery);

            idCounts = cache.get(cacheKey);
        } else {
            if (log.isDebugEnabled()) log.debug("Fetching interactors for query: "+solrQuery);

            IntactSolrSearcher searcher = new IntactSolrSearcher(solrServer);
            final Multimap<String,InteractorIdCount> idCountMultimap = searcher.searchInteractors(solrQuery, interactorTypeMis);
            idCounts = new ArrayList<InteractorIdCount>(idCountMultimap.values());

            cache.put(cacheKey, idCounts);
        }

        return idCounts;
    }

    public int getRowCount() {
        if (idCounts == null) {
            this.load(0,0, null, false, null);
        }
        return idCounts.size();
    }

    public Object getRowData() {
        if (idCounts == null) {
            return null;
        }

        if (!isRowAvailable()) {
            throw new IllegalArgumentException("row is unavailable");
        }

        InteractorIdCount idCount = idCounts.get(getRowIndex());

        if (interactorCache.containsKey(idCount.getAc())) {
            return interactorCache.get(idCount.getAc());
        }

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = dataContext.getDaoFactory()
                .getEntityManager().createQuery("select i from InteractorImpl i where i.ac = :ac");
        query.setParameter("ac", idCount.getAc());

        Interactor interactor = (Interactor) query.getSingleResult();
        Collection<InteractorXref> xrefs = interactor.getXrefs();

        for (InteractorXref xref : xrefs) {
            String ac = xref.getAc();
        }


        InteractorWrapper wrapper = new InteractorWrapper(interactor, idCount.getCount());

        dataContext.commitTransaction(transactionStatus);

        interactorCache.put(idCount.getAc(), wrapper);
        
        return wrapper;
    }

    public Object getWrappedData() {
        return idCounts;
    }

//    public boolean isRowAvailable() {
//        if (idCounts == null) {
//            return false;
//        }
//
//        return rowIndex >= 0 && rowIndex < getRowCount();
//    }
//
//    public void setRowIndex(int rowIndex) {
//        if (rowIndex < -1) {
//            throw new IllegalArgumentException("illegal rowIndex " + rowIndex);
//        }
//        int oldRowIndex = rowIndex;
//        this.rowIndex = rowIndex;
//        if (idCounts != null && oldRowIndex != this.rowIndex) {
//            Object data = isRowAvailable() ? getRowData() : null;
//            DataModelEvent event = new DataModelEvent(this, this.rowIndex, data);
//            DataModelListener[] listeners = getDataModelListeners();
//            for (int i = 0; i < listeners.length; i++) {
//                listeners[i].rowSelected(event);
//            }
//        }
//    }
//
//    public Object getRowKey() {
//        return isRowAvailable()
//               ? getRowIndex()
//               : null;
//    }
//
//    public void setRowKey(Object key) {
//        if (key == null) {
//            setRowIndex(-1);
//        } else {
//            setRowIndex((Integer) key);
//        }
//    }


    public List<InteractorIdCount> getInteractorIdCounts() {
        return idCounts;
    }

    public SolrQuery getSearchQuery() {
        return solrQuery;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public boolean isAscending() {
        return (sortOrder == SolrQuery.ORDER.asc);
    }

}