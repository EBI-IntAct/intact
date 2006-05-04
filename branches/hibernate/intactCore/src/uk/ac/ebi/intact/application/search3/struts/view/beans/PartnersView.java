/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.search3.struts.view.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represent the partners view. It contains the main Interactor and all the partners.
 * When instantiated, it loads all the information using the hibernate Daos and stores it in
 * collections. Then, the jsp page will access to this bean to show its content.
 * The view contains PartnersViewBean to represent each of the entries.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-May-2006</pre>
 */
public class PartnersView
{
    private static Log log = LogFactory.getLog(PartnersView.class);

    /**
     * The main interactor, the query
     */
    private PartnersViewBean interactorCandidate;

    /**
     * Url template to access uniprot
     */
    private String uniprotUrlTemplate;

    /**
     * Primary identity (in a protein, its uniprot AC)
     */
    private String primaryIdXrefIdentity;

    /**
     * The partners of the main interactor
     */
    private List<PartnersViewBean> interactionPartners;

    public PartnersView(Interactor interactor, String helpLink, String searchUrl, String contextPath)
    {
        ProteinDao proteinDao = DaoFactory.getProteinDao();

        // Using the ac, we retrieve the uniprot url template (by using the identity "Xref")
        uniprotUrlTemplate = proteinDao.getUniprotUrlTemplateByProteinAc(interactor.getAc());

        // Load the list of partners ACs, each partner has a list with the interaction ACs
        Map<String,List<String>> partnersWithInteractionAcs = proteinDao.getPartnersWithInteractionAcsByProteinAc(interactor.getAc());

        // This set will contain all the ACs for the main interactor
        // Iterates throug all the interactions from the previous query, to create a collection with unique ACs
        Set<String> interactionAcsForMainInteractor = new HashSet<String>(partnersWithInteractionAcs.size());

        for (List<String> allInteractionsAc : partnersWithInteractionAcs.values())
        {
            interactionAcsForMainInteractor.addAll(allInteractionsAc);
        }

        // Creates the main interactor PartnersViewBean
        interactorCandidate = createPartnersViewBean(interactor, helpLink, searchUrl, contextPath, interactionAcsForMainInteractor);

        // We iterate through the map to retrieve all the partners, with their interaction ACs,
        // and create a PArtnerViewBean with each entry
        interactionPartners = new ArrayList<PartnersViewBean>();

        for (Map.Entry<String,List<String>> entry : partnersWithInteractionAcs.entrySet())
        {
            String partnerInteractorAc = entry.getKey();

            if (!partnerInteractorAc.equals(interactor.getAc()))
            {
                // We retrieve each interactor from the database, to create the bean
                Interactor partnerInteractor = DaoFactory.getInteractorDao().getByAc(partnerInteractorAc);
                PartnersViewBean bean = createPartnersViewBean(partnerInteractor, helpLink, searchUrl, contextPath, entry.getValue());
                interactionPartners.add(bean);
            }
        }

    }

    private PartnersViewBean createPartnersViewBean(Interactor interactor, String helpLink, String searchUrl, String contextPath, Collection<String> interactionAcs)
    {
         PartnersViewBean bean = new PartnersViewBean(interactor,helpLink, searchUrl, contextPath);

        // Bean creation
        List<String> geneNames = DaoFactory.getProteinDao().getGeneNamesByProteinAc(interactor.getAc());
        bean.setGeneNames(geneNames);

        // All this setters set the information necessary for the view
        bean.setNumberOfInteractions(interactionAcs.size());
        bean.setUniprotAc(getUniprotAc(interactor));
        bean.setIdentityXrefURL(getIdentityXrefUrl(interactor));
        bean.setInteractorSearchURL(getInteractorSearchURL(searchUrl, interactor));
        bean.setInteractorPartnerUrl(getInteractorPartnerURL(searchUrl, interactor));
        bean.setInteractionsSearchURL(getInteractionsSearchURL(searchUrl, interactionAcs));

        return bean;
    }

    /**
     * Provides this Protein's Uniprot AC (ie its Xref to Uniprot).
     *
     * @return String the Protein's Uniprot AC.
     */
    public String getUniprotAc(Interactor interactor) {
       return DaoFactory.getProteinDao().getUniprotAcByProteinAc(interactor.getAc());
    }

    /**
     * Gets the identity Xref url, replacing the accession number with the current value
     */
    private String getIdentityXrefUrl(Interactor interactor)
    {
        return SearchReplace.replace(uniprotUrlTemplate, "${ac}", getPrimaryIdXrefIdentity(interactor));
    }

    private String getPrimaryIdXrefIdentity(Interactor interactor)
    {
        if (primaryIdXrefIdentity == null)
        {
            primaryIdXrefIdentity = DaoFactory.getProteinDao().getUniprotAcByProteinAc(interactor.getAc());
        }

        return  primaryIdXrefIdentity;
    }


    /**
     * Returns a fully populated URL to perform a search for all this Protein's Interactions. ISSUE: This is used in the
     * mockups to fdisplay DETAIL. This would be unmanageable for large Interaction lists spread across Experiments.
     * DESIGN DECISION: make th link go to the main 'simple' page to list the Interactions *there*, so users can then
     * choose which detail they want.
     *
     * @return String The complete search URL to perform a (possibly multiple) search for this Protein's Interactions
     */
    private String getInteractionsSearchURL(String searchUrl, Collection<String> interactionAcs) {

        String interactionSearchURL = "";

        // now create the URL-based searchquery
        StringBuffer sb = new StringBuffer();
        for ( Iterator<String> it = interactionAcs.iterator(); it.hasNext(); ) {
            String interactionAc = it.next();
            sb.append( interactionAc );
            if ( it.hasNext() ) {
                sb.append( "," );
            }

            interactionSearchURL = searchUrl + sb.toString();
        }
        return interactionSearchURL;
    }


    private String getInteractorSearchURL(String searchUrl, Interactor interactor) {
        if( interactor instanceof Protein ) {
            return getSimpleSearchURL(searchUrl, interactor) + "&amp;searchClass=Protein&amp;view=single" +
                   "&filter=ac";
        }else
            return getSimpleSearchURL(searchUrl, interactor) + "&amp;searchClass=NucleicAcid&amp;view=single" +
                   "&filter=ac";

    }

    private String getInteractorPartnerURL(String searchUrl, Interactor interactor) {
        if (interactor instanceof Protein)
        {
            return getSimpleSearchURL(searchUrl,interactor) + "&amp;searchClass=Protein&amp;view=partner" +
                    "&filter=ac";
        }
        else
        {
            return getSimpleSearchURL(searchUrl, interactor) + "&amp;searchClass=NucleicAcid&amp;view=partner" +
                    "&filter=ac";
        }

    }

    /**
     * Provides a String representation of a URL to perform a basic search on this Protein's AC. Thus a general search
     * is performed using this Proterin's AC.
     *
     * @return String a String representation of a search URL link
     */
    private String getSimpleSearchURL(String searchUrl, Interactor interactor) {

        return searchUrl + interactor.getAc() + "&filter=ac";
    }

    public PartnersViewBean getInteractorCandidate()
    {
        return interactorCandidate;
    }

    public void setInteractorCandidate(PartnersViewBean interactorCandidate)
    {
        this.interactorCandidate = interactorCandidate;
    }

    public List<PartnersViewBean> getInteractionPartners()
    {
        return interactionPartners;
    }

    public void setInteractionPartners(List<PartnersViewBean> interactionPartners)
    {
        this.interactionPartners = interactionPartners;
    }

}
