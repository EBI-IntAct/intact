/*
 * Copyright 2001-2008 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.cvutils;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;


import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvUtils {

    private static final Log log = LogFactory.getLog( CvUtils.class );

    private CvUtils() {
    }

    /**
     * Returns the id of the parent which is common to the IDs provided. If none is found, it returns null.
     *
     * @param ontology An ontology of CvTerms
     * @param ids      the children IDs
     * @return The id of the parent
     */
    public static String findLowestCommonAncestor( List<CvDagObject> ontology, String... ids ) {

        if ( ids.length < 2 ) {
            throw new IllegalArgumentException( "At least two IDs have to be provided to find a common parent" );
        }

        Collection<CvDagObject> children = new ArrayList<CvDagObject>( ids.length );

        for ( String id : ids ) {

            List<CvDagObject> terms = searchById( ontology, id );

            if ( terms == null ) {
                throw new IllegalArgumentException( "Term with id '" + id + "' was not found in the ontology provided" );
            }

            children.addAll( terms );
        }

        CvDagObject parent = findLowestCommonAncestor( children.toArray( new CvDagObject[children.size()] ) );

        if ( parent != null ) {
            return parent.getIdentifier();
        }

        return null;
    }

    /**
     * Returns the id of the parent which is common to the children provided. If none is found, it returns null.
     *
     * @param children A CvTerms with a common parent
     * @return the common parent for the children
     */
    public static CvDagObject findLowestCommonAncestor( CvDagObject... children ) {
        if ( children.length < 2 ) {
            throw new IllegalArgumentException( "At least two children have to be provided to find a common parent" );
        }

        Multimap<String, String> cvMap = new LinkedListMultimap<String, String>();

        // get all the parents for each child
        for ( CvDagObject child : children ) {
            cvMap.put( child.getIdentifier(), child.getIdentifier() );
            cvMap.putAll( child.getIdentifier(), findAllParentsForTerm( child ) );
        }

        // calculate the common parents by interesecting all the collections of parents for each child
        List<String> commonParents = null;

        for ( Collection<String> parents : cvMap.asMap().values() ) {
            List<String> parentsList = new LinkedList<String>( parents );
            if ( commonParents == null ) {
                commonParents = parentsList;
            } else {
                commonParents = ListUtils.intersection( commonParents, parentsList );
            }

        }

        // the first child of the commonParents collection is the lowest common ancestor
        if ( !commonParents.isEmpty() ) {
            String parentId = commonParents.iterator().next();
            return findParentById( children[0], parentId );
        }

        return null;
    }

    private static void findParents( final List<CvDagObject> parents, final CvDagObject child, final boolean recursive ) {
        for ( CvDagObject parent : child.getParents() ) {
            parents.add( parent );
            if( recursive && !parent.getParents().isEmpty() ) {
                findParents( parents, parent, recursive );
            }
        }
    }

    public static List<CvDagObject> findParents( final CvDagObject child, final boolean recursive ) {
        List<CvDagObject> parents = Lists.newArrayList();
        findParents( parents, child, recursive );
        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + parents.size() + " parents for term " + child.getIdentifier() );
        }
        return parents;
    }

    private static void findChildren( final List<CvDagObject> children, final CvDagObject parent, final boolean recursive ) {
        for ( CvDagObject child : parent.getChildren() ) {
            children.add( child );
            if( recursive && !child.hasChildren() ) {
                findChildren( children, child, recursive );
            }
        }
    }

    public static List<CvDagObject> findChildren( final CvDagObject parent, final boolean recursive ) {
        List<CvDagObject> children = Lists.newArrayList();
        findChildren( children, parent, recursive );
        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + children.size() + " parents for term " + parent.getIdentifier() );
        }
        return children;
    }

    protected static List<String> findAllParentsForTerm( CvDagObject child ) {
        Set<String> parents = new LinkedHashSet<String>();

        // creates a set of IDs from the parents. If the parent id is found,
        // the id is removed from the set and added to the end
        for ( CvDagObject parent : child.getParents() ) {

            if ( parents.contains( parent.getIdentifier() ) ) {
                parents.remove( parent.getIdentifier() );
            }
            parents.add( parent.getIdentifier() );

            for ( String parentId : findAllParentsForTerm( parent ) ) {
                if ( parents.contains( parentId ) ) {
                    parents.remove( parentId );
                }
                parents.add( parentId );
            }
        }

        return new LinkedList<String>( parents );
    }

    private static CvDagObject findParentById( CvDagObject child, String parentId ) {
        if ( parentId.equals( child.getIdentifier() ) ) {
            return child;
        }

        for ( CvDagObject parent : child.getParents() ) {
            if ( parentId.equals( parent.getIdentifier() ) ) {
                return parent;
            }

            CvDagObject candidate = findParentById( parent, parentId );

            if ( candidate != null ) {
                return candidate;
            }
        }

        return null;
    }

    private static List<CvDagObject> searchById( List<CvDagObject> ontology, String termId ) {
        List<CvDagObject> terms = new ArrayList<CvDagObject>();

        for ( CvDagObject cv : ontology ) {
            if ( termId.equals( cv.getIdentifier() ) ) {
                terms.add( cv );
            }
        }
        return terms;

    }

    /**
     * @return List of Cvs that come from IntAct (do not have an MI identifier)
     */
    public static List<CvObject> getCvsInIntactNotInPsi() {
        return getCvsInIntactNotInPsi(Collections.EMPTY_LIST);
    }

    /**
     * @param exclusionList list of cv classes that are to be excluded   eg: uk.ac.ebi.intact.model.CvCellType
     * @return List of Cvs with No MiIdentifiers
     */
    public static List<CvObject> getCvsInIntactNotInPsi(Collection<String> exclusionList) {

        if(exclusionList==null || exclusionList.size()==0) {
            //create a default exclusionList
            exclusionList = new ArrayList<String>();
            exclusionList.add( "uk.ac.ebi.intact.model.CvCellType" );
            exclusionList.add( "uk.ac.ebi.intact.model.CvTissue" );
        }

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();
        List<CvObject> allCvs = daof.getCvObjectDao().getAll();

        List<CvObject> cvsNotInPsi = new ArrayList<CvObject>();

        for ( CvObject cv : allCvs ) {
            if ( cv.getIdentifier() != null && cv.getIdentifier().startsWith( "IA:" )) {

                if(!exclusionList.contains( cv.getObjClass() )){
                  cvsNotInPsi.add( cv );
                }
            }    
        }

        return cvsNotInPsi;
    }

    /**
     * Gets the CVs from the database created after a given date.
     * @param date cutoff date
     * @return List of cvs added after the given date excluding the date
     */
    public static List<CvObject> getCvsAddedAfter( Date date ) {
         return getCvsAddedAfter( date, Collections.EMPTY_LIST );
    }

    /**
     * Gets the CVs from the database created after a given date.
     * @param date cutoff date
     * @param exclusionList list of cv classes that are to be excluded   eg: uk.ac.ebi.intact.model.CvCellType
     * @return List of cvs added after the given date excluding the date
     */
    public static List<CvObject> getCvsAddedAfter( Date date, Collection<String> exclusionList ) {

        if ( date == null ) {
            throw new NullPointerException( "You must give a non null date" );
        }

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();
        List<CvObject> allCvs = daof.getCvObjectDao().getAll();

        List<CvObject> cvsAddedAfter = new ArrayList<CvObject>();

        for ( CvObject cv : allCvs ) {

            if ( cv.getCreated() != null && cv.getCreated().after( date ) ) {
                if ( exclusionList == null || exclusionList.size() == 0 ) {
                    cvsAddedAfter.add( cv );
                } else {
                    if ( !exclusionList.contains( cv.getObjClass() ) )
                        cvsAddedAfter.add( cv );
                }
            }
        }

        return cvsAddedAfter;
    }

    /**
     * Gets the CVs from the database created before a given date.
     * @param date cutoff date
     * @return List of cvs added after the given date excluding the date
     */
    public static List<CvObject> getCVsAddedBefore( Date date ) {
         return getCVsAddedBefore( date, Collections.EMPTY_LIST );
    }

    /**
     * Gets the CVs from the database created before a given date.
     * @param date  cutoff date
     * @param exclusionList list of cv classes that are to be excluded   eg: uk.ac.ebi.intact.model.CvCellType
     * @return List of cvs added before the given date excluding the date
     */
    public static List<CvObject> getCVsAddedBefore( Date date, Collection<String> exclusionList ) {

        if ( date == null ) {
            throw new NullPointerException( "You must give a non null date" );
        }

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();
        List<CvObject> allCvs = daof.getCvObjectDao().getAll();

        List<CvObject> cvsAddedBefore = new ArrayList<CvObject>();

        for ( CvObject cv : allCvs ) {

            if ( cv.getCreated() != null && cv.getCreated().before( date ) ) {
               if ( exclusionList == null || exclusionList.size() == 0 ) {
                    cvsAddedBefore.add( cv );
                } else {
                    if ( !exclusionList.contains( cv.getObjClass() ) )
                        cvsAddedBefore.add( cv );
                }
            }
        }

        return cvsAddedBefore;
    }


}