package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;

/**
 * @author Michael Kleen
 * @version SingleResultViewBean.java Date: Dec 15, 2004 Time: 10:43:16 AM
 */
public class SingleResultViewBean {
    private final String intactType;
    private final String helpLink;
    private final String searchLink;
    private final String searchString;
    private final int count;


    public SingleResultViewBean( final String intactType, final int count, final String helpLink,
                                 final String searchLink, final String searchString ) {

        this.intactType = intactType;
        this.count = count;
        this.helpLink = helpLink;
        this.searchLink = searchLink;
        this.searchString = searchString.replaceAll( "\\'", "" );

    }

    public String getIntactName() {
        return this.intactType;
    }

    private String getSearchType() {
        if ( intactType.equalsIgnoreCase( "Controlled vocabulary term" ) ) {
            return "CvObject";
        } else {
            return this.intactType;
        }
    }

    public String getHelpURL() {

        if ( intactType.equalsIgnoreCase( "Protein" ) ) {
            return helpLink + "Interactor";
        }
        if ( intactType.equalsIgnoreCase( "Interaction" ) ) {
            return helpLink + "Interaction";
        }
        if ( intactType.equalsIgnoreCase( "Experiment" ) ) {
            return helpLink + "Experiment";
        }
        if ( intactType.equalsIgnoreCase( "Controlled vocabulary term" ) ) {
            return helpLink + "CVS";
        } else {
            return helpLink + "search.TableLayout";
        }
    }

    public String getCount() {
        return new Integer( count ).toString();
    }

    public String getSearchLink() {
        if ( count < SearchConstants.MAXIMUM_RESULT_SIZE ) {
            return this.searchLink + this.searchString + "&searchClass=" + this.getSearchType();
        } else {
            return "-";
        }
    }

    public String getSearchName() {
        if ( intactType.equalsIgnoreCase( "Protein" ) ) {
            return "Select by Protein";
        }
        if ( intactType.equalsIgnoreCase( "Interaction" ) ) {
            return "Select by Interaction";
        }
        if ( intactType.equalsIgnoreCase( "Experiment" ) ) {
            return "Select by Experiment";
        }
        if ( intactType.equalsIgnoreCase( "Controlled vocabulary term" ) ) {
            return "Select by Controlled vocabulary";
        } else {
            return "-";
        }
    }

    // inserted the following method (afrie)
    public String getSearchObject() {
        if ( intactType.equalsIgnoreCase( "Protein" ) ) {
            return "protein";
        }
        if ( intactType.equalsIgnoreCase( "Interaction" ) ) {
            return "interaction";
        }
        if ( intactType.equalsIgnoreCase( "Experiment" ) ) {
            return "experiment";
        }
        if ( intactType.equalsIgnoreCase( "Controlled vocabulary term" ) ) {
            return "cv";
        } else {
            return "-";
        }
    }

    public boolean isSearchable() {
        if ( count < SearchConstants.MAXIMUM_RESULT_SIZE ) {
            return true;
        } else {
            return false;
        }
    }
}
