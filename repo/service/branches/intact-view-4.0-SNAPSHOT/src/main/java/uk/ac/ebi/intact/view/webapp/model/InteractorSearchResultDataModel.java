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
import org.primefaces.model.SortOrder;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrSearcher;
import uk.ac.ebi.intact.dataexchange.psimi.solr.InteractorIdCount;
import uk.ac.ebi.intact.model.InteractorImpl;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorSearchResultDataModel extends LazyDataModel<InteractorWrapper> {

    private static final Log log = LogFactory.getLog(InteractorSearchResultDataModel.class);

    private SolrQuery solrQuery;

    private String[] interactorTypeMis;

    private List<InteractorWrapper> idCounts;
    private int totalCount=0;

    private int firstResult = 0;

    private IntactSolrSearcher solrSearcher;

    private LRUMap interactorCache;

    public InteractorSearchResultDataModel(SolrServer solrServer, SolrQuery solrQuery, String interactorTypeMi)  {
        this(solrServer, solrQuery, new String[] {interactorTypeMi});
    }

    public InteractorSearchResultDataModel(SolrServer solrServer, SolrQuery solrQuery, String[] interactorTypeMis)  {

        this.solrSearcher = new IntactSolrSearcher(solrServer);
        this.solrQuery = solrQuery != null ? solrQuery.getCopy() : solrQuery;
        this.interactorTypeMis = interactorTypeMis;

        idCounts = new ArrayList<InteractorWrapper>();
        this.interactorCache = new LRUMap();

        countTotalResults();
    }

    private void countTotalResults(){
        if (solrQuery == null) {
            // add a error message
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage facesMessage = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Query is empty", "Cannot fetch any results because query is empty. Please enter a query." );
            context.addMessage( null, facesMessage );
        }
        else {

            totalCount = this.solrSearcher.countAllInteractors(solrQuery, this.interactorTypeMis);
        }
    }

    @Override
    public List<InteractorWrapper> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        if (pageSize == 0) pageSize = 20;

        if (solrQuery == null) {
            // add a error message
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage facesMessage = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Query is empty", "Cannot fetch any results because query is empty. Please enter a query." );
            context.addMessage( null, facesMessage );
        }

        if (log.isDebugEnabled()) log.debug("Fetching interactors for query: "+solrQuery);

        final Multimap<String,InteractorIdCount> idCountMultimap = solrSearcher.searchInteractors(solrQuery, interactorTypeMis, first, pageSize);

        idCounts.clear();

        wrap(idCountMultimap.values(), first, pageSize);

        return idCounts;
    }

    public int getRowCount() {
        if (idCounts == null) {
            this.load(0,0, null, SortOrder.ASCENDING, null);
        }
        return totalCount;
    }

    public InteractorWrapper getRowData() {
        if (idCounts == null) {
            return null;
        }

        if (!isRowAvailable()) {
            throw new IllegalArgumentException("row is unavailable");
        }

        InteractorWrapper idCount = idCounts.get(getRowIndex());

        return idCount;
    }

    private void wrap(Collection<InteractorIdCount> idCount, int first, int pageSize) {


        String key = first+"_"+pageSize;
        if (this.interactorCache.containsKey(key)){
            this.idCounts.addAll((List) this.interactorCache.get(key));
        }
        else {
            IntactContext context=IntactContext.getCurrentInstance();
            DataContext dataContext = context.getDataContext();
            DaoFactory factory = context.getDaoFactory();

            TransactionStatus transactionStatus = dataContext.beginTransaction();

            Map<String, Long> idCountMap = new HashMap<String, Long>(idCount.size());
            for (InteractorIdCount count : idCount){
                idCountMap.put(count.getAc(), count.getCount());
            }

            List<InteractorImpl> interactors = factory.getInteractorDao().getByAc(idCountMap.keySet());

            for (InteractorImpl interactor : interactors){
                InteractorWrapper wrapper = new InteractorWrapper(interactor, idCountMap.get(interactor.getAc()));

                this.idCounts.add(wrapper);
            }

            dataContext.commitTransaction(transactionStatus);

            this.interactorCache.put(key, new ArrayList<InteractorWrapper>(this.idCounts));
        }
    }

    public Object getWrappedData() {
        return idCounts;
    }

    @Override
    public InteractorWrapper getRowData(String rowKey) {
        for (InteractorWrapper iic : idCounts) {
            if (iic.getAc() != null && iic.getAc().equals(rowKey)) {
                return iic;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(InteractorWrapper object) {
        if (object.getAc() != null) {
            return object.getAc();
        }

        return null;
    }

    public List<InteractorWrapper> getInteractorIdCounts() {
        return idCounts;
    }

    public SolrQuery getSearchQuery() {
        return solrQuery;
    }

    public int getFirstResult() {
        return firstResult;
    }

}