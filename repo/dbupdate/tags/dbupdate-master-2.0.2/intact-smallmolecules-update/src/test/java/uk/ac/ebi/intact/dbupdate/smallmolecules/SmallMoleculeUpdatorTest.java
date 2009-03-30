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
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.SmallMolecule;
import uk.ac.ebi.intact.model.SmallMoleculeImpl;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SmallMoleculeUpdatorTest extends IntactBasicTestCase {

    @Ignore
    @Test
    public void updateSmallMolecule() throws SmallMoleculeUpdatorException, IntactTransactionException {
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daoFactory = dataContext.getDaoFactory();

        dataContext.beginTransaction();

        final SmallMolecule smallMolecule = getMockBuilder().createSmallMolecule( "CHEBI:18348", "noname" );
        CvTopic testCvTopic = CvObjectUtils.createCvObject(smallMolecule.getOwner(), CvTopic.class, "MI:1111", "test annotation");

        PersisterHelper.saveOrUpdate( testCvTopic );

        Annotation annotation = new Annotation( smallMolecule.getOwner(), testCvTopic, "blabla" );
        smallMolecule.addAnnotation( annotation );

        PersisterHelper.saveOrUpdate( smallMolecule );

        Assert.assertEquals(1,daoFactory.getInteractorDao( SmallMoleculeImpl.class ).countAll());

        SmallMoleculeImpl byShortLabel = daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByShortLabel( "noname" );
        Assert.assertEquals("noname",byShortLabel.getShortLabel());

        dataContext.commitTransaction();

        SmallMoleculeUpdator updator = new SmallMoleculeUpdator(new ChebiProcessor());
        updator.startUpdate();

        byShortLabel = daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByShortLabel( "noname" );
        Assert.assertNull(byShortLabel);

        byShortLabel=daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByShortLabel( "newname" );
        Assert.assertNotNull( byShortLabel );

        Assert.assertEquals("CHEBI:18348",byShortLabel.getXrefs().iterator().next().getPrimaryId());
        Assert.assertEquals(2,byShortLabel.getAnnotations().size());
        //Assert.assertEquals("inchi id",byShortLabel.getAnnotations().iterator().next().getCvTopic().getShortLabel());


    }

    





}
