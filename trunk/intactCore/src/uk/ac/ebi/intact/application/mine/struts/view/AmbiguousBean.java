/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.struts.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.apache.struts.action.ActionForm;

import uk.ac.ebi.intact.application.mine.business.Constants;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Protein;

/**
 * The <tt>AmbiguousBean</tt> is used when the search of the MiNe application
 * returns an ambiguous result. It stores for each search class (
 * <tt>Protein</tt>,<tt>Interaction</tt> and <tt>Experiments</tt>) a
 * collection of the found results.
 * 
 * @author Andreas Groscurth
 */
public class AmbiguousBean extends ActionForm {
    // stores all found proteins of the search
    private Collection proteins = null;
    // stores all found interactions of the search
    private Collection interactions = null;
    // stores all found experiments of the search
    private Collection experiments = null;
    private static String context;

    /**
     * Returns the number of all found results. This is the sum of the size of
     * all three collections.
     * 
     * @return the number of found results.
     */
    public int getNumberOfResults() {
        return proteins.size() + interactions.size() + experiments.size();
    }

    /**
     * Returns all found results which are an <tt>Experiment</tt>
     * 
     * @return Returns the experiments.
     */
    public Collection getExperiments() {
        return experiments;
    }

    /**
     * Sets the found results which are an <tt>Experiment</tt>
     * 
     * @param experiments The experiments to set.
     */
    public void setExperiments(Collection experiments) {
        this.experiments = experiments;
    }

    /**
     * Returns all found results which are an <tt>Interaction</tt>
     * 
     * @return Returns the interactions.
     */
    public Collection getInteractions() {
        return interactions;
    }

    /**
     * Sets the found results which are an <tt>Interaction</tt>
     * 
     * @param interactions The interactions to set.
     */
    public void setInteractions(Collection interactions) {
        this.interactions = interactions;
    }

    /**
     * Returns all found results which are a <tt>Protein</tt>
     * 
     * @return Returns the proteins.
     */
    public Collection getProteins() {
        return proteins;
    }

    /**
     * Returns wether the is an ambiguous result. This happens when there are
     * more than one protein was found or at least one experiment or
     * interaction.
     * 
     * @return wether the search was ambiguous
     */
    public boolean hasAmbiguousResult() {
        return proteins.size() > 1 || proteins.size() == 0
                || interactions.size() != 0 || experiments.size() != 0;
    }

    /**
     * Sets the found results which are a <tt>Protein</tt>
     * 
     * @param proteins The proteins to set.
     */
    public void setProteins(Collection proteins) {
        this.proteins = proteins;
    }

    /**
     * Prints the results as html to the given writer.
     * 
     * @param out the writer to write html
     * @param contextPath the contextpath of the application
     */
    public void printHTML(Writer out, String contextPath) {
        try {
            context = contextPath;
            int interactorNumber = getNumberOfResults();
            // if no results where found
            if (interactorNumber == 0) {
                out.write("<i>no results</i><br>");
            }
            // if more results than allowed were found
            else if (interactorNumber > Constants.MAX_NUMBER_RESULTS) {
                out.write("<i>more than " + Constants.MAX_NUMBER_RESULTS);
                out.write(" results, please refine your query</i><br>");
            }
            else {
                out.write("<table width=\"100%\" bgcolor=\"#336666\">");
                Iterator iter;
                // all found proteins are displayed
                for (iter = proteins.iterator(); iter.hasNext();) {
                    printProtein(out, (Protein) iter.next());
                }
                // all found interactions are displayed
                for (iter = interactions.iterator(); iter.hasNext();) {
                    printInteraction(out, (Interaction) iter.next());
                }
                // all found eperiments are displayed
                for (iter = experiments.iterator(); iter.hasNext();) {
                    printExperiment(out, (Experiment) iter.next());
                }
                out.write("</table><br>\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints all results which are an <tt>Experimeriment</tt>. Each
     * experiment is decomposed into its interaction which are then decomposed
     * into their components.
     * 
     * @param out the writer
     * @param exp the experiment to display
     * @throws IOException when something failed with the writer
     */
    private void printExperiment(Writer out, Experiment exp) throws IOException {
        Collection interactions = exp.getInteractions();
        // if the number of interactions is greater than allowed
        if (interactions.size() > Constants.MAX_INTERACTION_SIZE) {
            out.write("<i>" + exp.getAc() + " has more than "
                    + Constants.MAX_INTERACTION_SIZE
                    + " number of interactions</i>");
            return;
        }

        // every interaction is displayed
        for (Iterator iter = interactions.iterator(); iter.hasNext();) {
            printInteraction(out, (Interaction) iter.next());
        }
    }

    /**
     * Prints all results which are an <tt>Interaction</tt>. Each interaction
     * is decomposed into its components.
     * 
     * @param out the writer
     * @param in the interaction to display
     * @throws IOException when something failed with the writer
     */
    private void printInteraction(Writer out, Interaction in)
            throws IOException {
        Collection components = in.getComponents();
        // if an interaction has more than the allowed number of components
        if (components.size() > Constants.MAX_INTERACTION_SIZE) {
            out.write("<i>" + in.getAc() + " has more than "
                    + Constants.MAX_INTERACTION_SIZE
                    + " number of interactors</i>");
            return;
        }
        Interactor inter;
        // every component is displayed
        for (Iterator iter = components.iterator(); iter.hasNext();) {
            inter = ((Component) iter.next()).getInteractor();
            if (inter instanceof Protein) {
                printProtein(out, (Protein) inter);
            }
            else if (inter instanceof Interaction) {
                printInteraction(out, (Interaction) inter);
            }
        }

    }

    /**
     * Prints the information of a protein to the writer. The text is formatted
     * via HTML.
     * 
     * @param out The writer
     * @param prot the protein to display
     * @throws IOException when something failed with the writer
     */
    private void printProtein(Writer out, Protein prot) throws IOException {
        String ac = prot.getAc();

        out.write("<tr bgcolor=\"#eeeeee\">");
        out.write("<td class=\"objectClass\"><nobr>");
        // a checkbox is added so that the user can select the protein for the
        // algorithm
        out.write("<input type=\"checkbox\" name=\"" + ac + "\"");
        // if only one protein is given the checkbox is checked by default
        if (!hasAmbiguousResult()) {
            out.write(" checked");
        }
        out.write(">");

        out.write("<b>Protein</b>");
        out.write("<a href=\"" + context + Constants.HELP_LINK
                + "search.TableLayout\"");
        out.write(" target=\"new\"><sup><b><font color=\"red\">?</font>");
        out.write("</b></sup></a></nobr></td>");

        // the ac number is added
        out.write("<td><a href=\"" + context + Constants.HELP_LINK
                + "BasicObject.ac\"");
        out.write("target=\"new\">Ac:</a>" + ac + "</td>");

        // the shortlabel is added
        out.write("<td><a href=\"" + context + Constants.HELP_LINK
                + "AnnotatedObject.shortLabel\"");
        out.write(" target=\"new\">Name:</a> ");
        out.write("<a href=\"" + context + "/search/do/search?searchString="
                + ac + "&searchClass=Protein\">");
        out.write("<b><i>" + prot.getShortLabel() + "</i></b></a></td>");
        out.write("<td>" + prot.getFullName() + "</td>");
        out.write("</tr>");
    }
}