/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.business;

import uk.ac.ebi.intact.business.IntactHelper;

/**
 * This interface represents an Intact user.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), modified by Chris Lewington
 * @version $Id$
 */
public interface IntactUserIF
        extends uk.ac.ebi.intact.application.commons.business.IntactUserI {

    public void setHelpLink(String link);

    public String getHelpLink();

    public void setSearchValue(String value);

    public String getSearchValue();

    public void setSearchClass(String searchClass);

    public String getSearchClass();

    public int getSelectedChunk();

    public void setSelectedChunk(int selectedChunk);

    public String getBinaryValue();

    public IntactHelper getIntactHelper();

    public void setBinaryValue(String binaryValue);

    public String getView();

    public void setView(String viewValue);

    public String getFilter();

    public void setFilter(String filterValue);
}
