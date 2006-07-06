/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.binaryInteractions;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.goDensity.business.data.DbTools;
import uk.ac.ebi.intact.application.goDensity.business.data.Key2HashSet;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvComponentRole;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Xref;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * CalcBinInteractionData can be used to generate binary interactions from
 * intAct core model. The generated binary interactions will be stored in a
 * seperate redundant (speed) table which look like this:<br>
 * bait | prey | gobait | goprey <br>
 * <br>
 * bait is a fictive bait protein within a binary interacction.
 * prey is the corresponding proteins to the bait.
 * go-bait/prey are the corresponding goIds to the proteins within a interaction
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class CalcBinInteractionData extends Key2HashSet {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * IntactHelper for search
     */
    private IntactHelper _helper;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * Constructor of CalcBinaryInteractionData<br>
     * The parameter CalcBinaryInteractionData(helper, true, true, false) is for us
     * at the moment the best way to generate BinaryInteractions with biological
     * significant sense.
     * @param helper IntactHelper instance
     * @param ifABait if there is a bait for a Interaction (Complex) then:<br>
     *        - true: crossproduct (spokes model)<br>
     *        - false: allVsAll (clique model)
     * @param ifNoBait is there is no bait for a Interaction (Complex) then:<br>
     *        - true: crossproduct (choose random bait)<br>
     *        - false: allVsAll (clique model)
     * @param baitBait if there are more baits for this Interaction (Complex)<br>
     *        (btw: no real biological sense, but for the case there are ...)<br>
     *        - true: connect if more bait: clique on bait-bait <br>
     *        - false: don't connect bait-bait
     */
    public CalcBinInteractionData(IntactHelper helper,
                                  boolean ifABait,
                                  boolean ifNoBait,
                                  boolean baitBait) throws IntactException {
        _helper = helper;

        String BAIT = null;
        try {
            CvComponentRole bait = (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait");
            BAIT = bait.getAc();
            logger.debug("BAIT AC = " + BAIT);
        } catch (IntactException e) {
            throw new IntactException("get AC for BAIT failed!", e);
        }

        // getting all Interactions from Intact model
        Collection allInteractions = new ArrayList();
        try {
            allInteractions = _helper.search("uk.ac.ebi.intact.model.Interaction", "ac", "*");
            logger.info("search for Interactions was successful; allInteractions.size() = " + allInteractions.size());
        } catch (IntactException e) {
            throw new IntactException("search for Interaction \"*\" failed!", e);
        }

        // for all Interactions the bait and prey components will be determined and stored.
        // depending on the parameters of this method, the binary data will be calculated in the right way
        for (Iterator itInteractions = allInteractions.iterator(); itInteractions.hasNext();) {
            Interaction aInteraction = (Interaction) itInteractions.next();
            logger.debug("\n\n - - - - - > aInteraction" + aInteraction.getAc());
            ArrayList someBaitComponents = new ArrayList();
            ArrayList someOtherComponents = new ArrayList();
            Collection someComponents = aInteraction.getComponents();
            Iterator itSomeComponents = someComponents.iterator();
            while (itSomeComponents.hasNext()) {
                Component component = (Component) itSomeComponents.next();
                if (component.getCvComponentRoleAc().equals(BAIT))
                    someBaitComponents.add(component.getInteractorAc());
                else
                    someOtherComponents.add(component.getInteractorAc());
            }
            logger.debug("components#: " + someComponents.size());
            logger.debug("baits: " + someBaitComponents);
            logger.debug("preys: " + someOtherComponents);

            // if there are some bait components in this interaction
            if (someBaitComponents.size() > 0) {
                if (ifABait) {
                    // crossproduct between all bait and preys
                    logger.debug("crossproduct between baits and preys");
                    this.crossProduct(someBaitComponents, someOtherComponents, baitBait);
                } else {
                    // all versus all between all bait and preys
                    logger.debug("all against all for bait and prey");
                    this.allVsAll(someBaitComponents, someOtherComponents);
                }
            }
            // if there is NO bait component in this interaction
            else {
                if (ifNoBait) {
                    // crossproduct between one choosen prey and all others
                    logger.debug("crossproduct between one choosen bait out of preys and all preys");
                    this.crossProduct(null, someOtherComponents, baitBait);
                } else {
                    // all versus all between all preys
                    logger.debug("all against all for preys only");
                    this.allVsAll(null, someOtherComponents);
                }
            } //else
        } //for all interactions
    }

    /**
     * privte Constructor
     */
    private CalcBinInteractionData() {
        // nothing
    }

    /**
     * populate database with binary interaction data - but only if bait and prey are annotated by go.
     * Every bait-prey-gobait-goprey entry is unique and will be modeled only once, even if the data in the model
     * itself would provide duplicate data.
     * @param addIfProteinNoGoId if bait and/or prey have no annotated GO:ID, then for:<br>
     * - true -> only bait, prey will be added to db and goBait, goPrey are ""<br>
     * - false -> if either bait or prey or both have no GO:ID, then this pair is not added to db!
     */

    public void dbPopulate(boolean addIfProteinNoGoId) throws SQLException, IntactException, KeyNotFoundException {
        PreparedStatement ps = DbTools.getInstance().getCon().prepareStatement("INSERT INTO ia_goDens_binary VALUES (? , ?, ? , ?)");

        Enumeration enumBaits = this.getKeys();
        // iterate over all baits (represented as Strings like "EBI-123")
        while (enumBaits.hasMoreElements()) {
            String aBait = (String) enumBaits.nextElement();

            // get for this single bait the "real" Interactor and for this all Xrefs (incl. GO-terms)
            Collection someBaitXrefs = ((Interactor) _helper.getObjectByAc(Interactor.class, aBait)).getXrefs();

            // get for this bait all possible preys and iterate over it
            Collection somePreys = this.getValueByKey(aBait);
            Iterator itSomePreys = somePreys.iterator();
            while (itSomePreys.hasNext()) {
                String aPrey = (String) itSomePreys.next();

                // get for this single prey the "real" Interactor and for this all Xrefs (incl. GO-terms)
                Collection somePreyXrefs = ((Interactor) _helper.getObjectByAc(Interactor.class, aPrey)).getXrefs();

                // now, iterate over all bait xrefs which are a goterm and within this loop iterate over all
                // prey xrefs which are a goterm and fill the table.
                // Each of these 4 while loops represent one column within the table.
                boolean goAnnotation = false;
                Iterator itSomeBaitXrefs = someBaitXrefs.iterator();
                while (itSomeBaitXrefs.hasNext()) {
                    Xref baitXref = (Xref) itSomeBaitXrefs.next();
                    Iterator itSomePreyXrefs = somePreyXrefs.iterator();
                    while (itSomePreyXrefs.hasNext()) {
                        Xref preyXref = (Xref) itSomePreyXrefs.next();
                        // if both proteins have a go annotation, prot-prot and go-go interactions will be stored to db
                        if (baitXref.getPrimaryId().startsWith("GO:") && (preyXref.getPrimaryId().startsWith("GO:"))) {
                            String goBait = baitXref.getPrimaryId();
                            String goPrey = preyXref.getPrimaryId();

                            ps.setString(1, aBait);
                            ps.setString(2, aPrey);
                            ps.setString(3, goBait);
                            ps.setString(4, goPrey);
                            logger.debug("insert db; aBait:" + aBait + " aPrey:" + aPrey + " goBait:" + goBait + " goPrey:" + goPrey);

                            ps.executeUpdate();
                            goAnnotation = true;

                        } // if
                    } // while 4 - itSomePreyXrefs
                } // while 3 - itSomeBaitXrefs
                // if at lease one of the proteins has NO GO Xref, only the prot-prot-interaction will be stored to db.
                // the GO-GO interaction will be empty ...
                if (!goAnnotation && addIfProteinNoGoId) {

                    ps.setString(1, aBait);
                    ps.setString(2, aPrey);
                    ps.setString(3, "");
                    ps.setString(4, "");
                    logger.debug("insert db; aBait:" + aBait + " aPrey:" + aPrey + " goBait:\"\" goPrey:\"\"");

                    ps.executeUpdate();
                }
            } // while 2 - itSomePreys
        } // while 1 - someBaits
        try {
            ps.close();
        } catch (SQLException e) {
        }
        logger.info("db was populated with binary interaction data");
    }

    // =======================================================================
    // Private Methods
    // =======================================================================

    /**
     * Calculate the crossproduct between baits and preys.
     * If there are no baits, the first prey will be choosen as an default bait.
     * @param someBaits baits of interaction
     * @param somePreys preys of interaction
     * @param connectBaitBait if true, bait/bait will be connected as all versus all additionally.
     */
    private void crossProduct(ArrayList someBaits, ArrayList somePreys, boolean connectBaitBait) {
        if (someBaits == null && somePreys.size() >= 2) {
            //choose for random the first element and declare it as a bait
            String baitProteinAc = (String) somePreys.get(0);
            //make crossproduct between the choosen bait and all other preys
            for (int i = 1; i < somePreys.size(); i++) {
                this.add(baitProteinAc, ((String) somePreys.get(i)));
                logger.debug(baitProteinAc + " <-> " + somePreys.get(i));
            }
        } else {
            //crossproduct between all baits and preys
            for (int i = 0; i < someBaits.size(); i++) {
                for (int j = 0; j < somePreys.size(); j++) {
                    this.add(((String) someBaits.get(i)), ((String) somePreys.get(j)));
                    logger.debug(someBaits.get(i) + " <-> " + somePreys.get(j));
                }
            }
            if (connectBaitBait) {
                //all versus all inbetween the baits
                this.allVsAll(someBaits);
            }
        }
    }

    /**
     * Calculate all versus all reltions.
     * That means, all baits inbetween are connected,
     * all preys inbetween are connected and
     * additionally all baits with all preys are connected.
     * Be carefull in using - very big data set ...
     * @param someBaits
     * @param somePreys
     */
    private void allVsAll(ArrayList someBaits, ArrayList somePreys) {
        if (someBaits == null) {
            this.allVsAll(somePreys);
        } else {
            for (int i = 0; i < someBaits.size(); i++) {
                for (int j = 0; j < somePreys.size(); j++) {
                    this.add(((String) someBaits.get(i)), ((String) somePreys.get(j)));
                }
            } //outer for
            this.allVsAll(someBaits);
            this.allVsAll(somePreys);
        } //else
    }

    /**
     * Calculate all versus all relations.
     * That means, all elements in this collection will be connected
     * @param oneType
     */
    private void allVsAll(ArrayList oneType) {
        for (int i = 0; i < oneType.size(); i++) {
            for (int j = 0; j < oneType.size(); j++) {
                if (i != j) {
                    this.add(((String) oneType.get(i)), ((String) oneType.get(j)));
                }
            } //inner for
        } //outer for
    }

}
