/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.PolymerImpl;
import uk.ac.ebi.intact.model.SequenceChunk;
import uk.ac.ebi.intact.persistence.dao.PolymerDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@SuppressWarnings( {"unchecked"} )
public class PolymerDaoImpl<T extends PolymerImpl> extends InteractorDaoImpl<T> implements PolymerDao<T> {

    public PolymerDaoImpl( Class<T> entityClass, Session session, IntactSession intactSession ) {
        super( entityClass, session, intactSession );
    }

    public String getSequenceByPolymerAc( String polymerAc ) {
        List<String> seqChunks = getSession().createCriteria( SequenceChunk.class )
                .createAlias( "parent", "p" )
                .add( Restrictions.eq( "p.ac", polymerAc ) )
                .addOrder( Order.asc( "sequenceIndex" ) )
                .setProjection( Property.forName( "sequenceChunk" ) )
                .list();

        StringBuffer sb = new StringBuffer( seqChunks.size() * PolymerImpl.MAX_SEQ_LENGTH_PER_CHUNK );

        for ( String seqChunk : seqChunks ) {
            sb.append( seqChunk );
        }

        return sb.toString();
    }
}
