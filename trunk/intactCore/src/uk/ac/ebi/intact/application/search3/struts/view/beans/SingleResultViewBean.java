package uk.ac.ebi.intact.application.search3.struts.view.beans;

/**
 * @author Michael Kleen
 * @version SingleResultViewBean.java Date: Dec 15, 2004 Time: 10:43:16 AM
 */
public class SingleResultViewBean {
    private String intactType;
    private String link;
    private String count;

    public SingleResultViewBean(String intactType, String count, String link) {
        this.intactType = intactType;
        this.count = count;
        this.link = link;

    }

    public String getIntactName() {
        return this.intactType;
    }

    public String getHelpURL() {

        if (intactType.equalsIgnoreCase("Protein")) {
            return link + "Interactor";
        }
        if (intactType.equalsIgnoreCase("Interaction")) {
            return link + "Interaction";
        }
        if (intactType.equalsIgnoreCase("Experiment")) {
            return link + "Experiment";
        }
        if (intactType.equalsIgnoreCase("Controlled vocabulary term")) {
            return link + "cvs";
        }
        else {
            return link + "search.TableLayout";
        }
    }

    public String getCount() {
        return count;
    }

}
