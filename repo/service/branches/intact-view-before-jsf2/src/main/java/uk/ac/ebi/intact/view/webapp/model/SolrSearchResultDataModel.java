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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.apache.myfaces.trinidad.model.SortableModel;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.CrossReference;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrSearcher;
import uk.ac.ebi.intact.dataexchange.psimi.solr.SolrSearchResult;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;
import uk.ac.ebi.intact.view.webapp.util.MitabFunctions;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SolrSearchResultDataModel extends SortableModel implements Serializable {

    private static final Log log = LogFactory.getLog(SolrSearchResultDataModel.class);

    private static String DEFAULT_SORT_COLUMN = "rigid";

    private SolrQuery solrQuery;
    private SolrServer solrServer;

    private SolrSearchResult result;
    private int rowIndex = -1;
    private int firstResult = 0;

    private String sortColumn = DEFAULT_SORT_COLUMN;
    private SolrQuery.ORDER sortOrder = SolrQuery.ORDER.asc;

    private Map<String,SolrQuery.ORDER> columnSorts;


    public SolrSearchResultDataModel(SolrServer solrServer, SolrQuery solrQuery) {
        if (solrQuery == null) {
            throw new IllegalArgumentException("Trying to create data model with a null SolrQuery");
        }

        this.solrServer = solrServer;
        this.solrQuery = solrQuery.getCopy();

        columnSorts = new HashMap<String, SolrQuery.ORDER>(16);

        setRowIndex(0);
        fetchResults();

        setWrappedData(result);
    }

    protected void fetchResults() {
        if (solrQuery == null) {
            throw new IllegalStateException("Trying to fetch results for a null SolrQuery");
        }

        solrQuery.setStart(firstResult)
            .setFacet(true)
            .setFacetMissing(true)
            .addFacetField(FieldNames.EXPANSION)
            .addFacetField("interactorType_id");

        for (Map.Entry<String,SolrQuery.ORDER> colSortEntry : columnSorts.entrySet()) {
            solrQuery.addSortField(colSortEntry.getKey(), colSortEntry.getValue());
        }

        if (log.isDebugEnabled()) {
            try {
                log.debug("Fetching results: "+ URLDecoder.decode(solrQuery.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        IntactSolrSearcher searcher = new IntactSolrSearcher(solrServer);
        result = searcher.search(solrQuery);
    }

    public int getRowCount() {
        return Long.valueOf(result.getTotalCount()).intValue();
    }

    public Object getRowData() {
        if (result == null) {
            return null;
        }

        if (!isRowWithinResultRange()) {
            firstResult = getRowIndex();
            fetchResults();
        }

        if (!isRowAvailable()) {
            throw new IllegalArgumentException("row is unavailable");
        }

        final IntactBinaryInteraction binaryInteraction = getInteraction(rowIndex);

        flipIfNecessary(binaryInteraction);

        return binaryInteraction;
    }

    private IntactBinaryInteraction getInteraction(int rowIndex) {
        List<IntactBinaryInteraction> interactions = new ArrayList<IntactBinaryInteraction>(result.getBinaryInteractionList());

        final IntactBinaryInteraction binaryInteraction = interactions.get(rowIndex - solrQuery.getStart());
        return binaryInteraction;
    }

    private void flipIfNecessary(IntactBinaryInteraction binaryInteraction) {
        final ExtendedInteractor interactorA = binaryInteraction.getInteractorA();
        final ExtendedInteractor interactorB = binaryInteraction.getInteractorB();

        final boolean matchesA = matchesQuery(interactorA);
        final boolean matchesB = matchesQuery(interactorB);

        if (matchesA && !matchesB) {
            // nothing
        } else if (!matchesA && matchesB) {
            binaryInteraction.flip();
        } else if (!MitabFunctions.isSmallMolecule(interactorA) && MitabFunctions.isSmallMolecule(interactorB)) {
            binaryInteraction.flip();
        } else {
            final String interactorAName = MitabFunctions.getInteractorDisplayName(interactorA);
            final String interactorBName = MitabFunctions.getInteractorDisplayName(interactorB);

            if (interactorAName.compareTo(interactorBName) > 0) {
                binaryInteraction.flip();
            }
        }
    }

    private boolean matchesQuery(ExtendedInteractor interactor) {
        String queries[] = solrQuery.getQuery().split(" ");

        for (String query : queries) {
            if ("NOT".equalsIgnoreCase(query) ||
                "AND".equalsIgnoreCase(query) ||
                "OR".equalsIgnoreCase(query) ||
                 query.contains(":")) {
                continue;
            }
            if (matchesQueryAliases(query, interactor.getAliases())) {
                return true;
            } else if (matchesQueryXrefs(query, interactor.getIdentifiers())) {
                return true;
            }else if (matchesQueryXrefs(query, interactor.getAlternativeIdentifiers())) {
                return true;
            }
        }
        
        return false;
    }

    private boolean matchesQueryAliases(String query, Collection<Alias> aliases) {
        for (Alias alias : aliases) {
            if (alias.getName().toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesQueryXrefs(String query, Collection<CrossReference> xrefs) {
        for (CrossReference xref : xrefs) {
            if (xref.getIdentifier().toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSameThanPrevious() {
        if (getRowIndex() > solrQuery.getStart()) {
            final IntactBinaryInteraction previousInteraction = getInteraction(getRowIndex() - 1);
            final IntactBinaryInteraction currentInteraction = getInteraction(getRowIndex());

            final String previousInteractorAName = MitabFunctions.getIntactIdentifierFromCrossReferences(previousInteraction.getInteractorA().getIdentifiers());
            final String previousInteractorBName = MitabFunctions.getIntactIdentifierFromCrossReferences(previousInteraction.getInteractorB().getIdentifiers());
            final String currentInteractorAName = MitabFunctions.getIntactIdentifierFromCrossReferences(currentInteraction.getInteractorA().getIdentifiers());
            final String currentInteractorBName = MitabFunctions.getIntactIdentifierFromCrossReferences(currentInteraction.getInteractorB().getIdentifiers());

            return previousInteractorAName.equalsIgnoreCase(currentInteractorAName) &&
                   previousInteractorBName.equalsIgnoreCase(currentInteractorBName);

        }
        
        return false;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public Object getWrappedData() {
        return result;
    }

    public boolean isRowAvailable() {
        if (result == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < result.getTotalCount();
    }

    protected boolean isRowWithinResultRange() {
        return (getRowIndex() >= firstResult) && (getRowIndex() < (firstResult+solrQuery.getRows()));
    }

    public void setRowIndex(int rowIndex) {
        if (rowIndex < -1) {
            throw new IllegalArgumentException("illegal rowIndex " + rowIndex);
        }
        int oldRowIndex = rowIndex;
        this.rowIndex = rowIndex;
        if (result != null && oldRowIndex != this.rowIndex) {
            Object data = isRowAvailable() ? getRowData() : null;
            DataModelEvent event = new DataModelEvent(this, this.rowIndex, data);
            DataModelListener[] listeners = getDataModelListeners();
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].rowSelected(event);
            }
        }
    }

    @Override
    public void setSortCriteria(List<SortCriterion> criteria) {
        if ((criteria == null) || (criteria.isEmpty())) {
            this.sortColumn = DEFAULT_SORT_COLUMN;
            columnSorts.clear();
        }
        else {
            // only use the first criterion
            SortCriterion criterion = criteria.get(0);

            this.sortColumn = criterion.getProperty();

            if (columnSorts.containsKey(sortColumn)) {
                SolrQuery.ORDER currentOrder = columnSorts.get(sortColumn);
                sortOrder = currentOrder.reverse();
            } else {
                sortOrder = SolrQuery.ORDER.asc;
            }
            columnSorts.put(sortColumn, sortOrder);

            if (log.isDebugEnabled())
                log.debug("\tSorting by '" + criterion.getProperty() + "' " + (criterion.isAscending() ? "ASC" : "DESC"));
        }

        fetchResults();
    }

    /**
     * Checks to see if the underlying collection is sortable by the given property.
     *
     * @param property The name of the property to sort the underlying collection by.
     * @return true, if the property implements java.lang.Comparable
     */
    @Override
    public boolean isSortable(String property) {
        return true;
    }

    @Override
    public Object getRowKey() {
        return isRowAvailable()
               ? getRowIndex()
               : null;
    }

    @Override
    public void setRowKey(Object key) {
        if (key == null) {
            setRowIndex(-1);
        } else {
            setRowIndex((Integer) key);
        }
    }


    public SolrSearchResult getResult() {
        return result;
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
