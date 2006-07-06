package uk.ac.ebi.intact.application.search3.struts.view.beans;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Michael Kleen
 * @version TooLargeViewBean.java Date: Feb 25, 2005 Time: 4:26:31 PM
 */
public class TooLargeViewBean {

    private Collection<SingleResultViewBean> someSingleResultViewBeans;

    public TooLargeViewBean() {
        this.someSingleResultViewBeans = new ArrayList<SingleResultViewBean>();
    }

    public void add( final SingleResultViewBean aSingleResultViewBean ) {
        this.someSingleResultViewBeans.add( aSingleResultViewBean );
    }

    public Collection<SingleResultViewBean> getSingleResults() {
        return this.someSingleResultViewBeans;
    }

}
