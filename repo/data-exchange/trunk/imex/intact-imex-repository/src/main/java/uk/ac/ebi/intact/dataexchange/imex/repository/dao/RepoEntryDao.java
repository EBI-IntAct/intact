/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
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
package uk.ac.ebi.intact.dataexchange.imex.repository.dao;

import org.joda.time.DateTime;
import uk.ac.ebi.intact.dataexchange.imex.repository.model.RepoEntry;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: EntrySetDao.java 9304 2007-08-06 11:10:23Z baranda $
 */
public interface RepoEntryDao
{

    void save(RepoEntry repoEntry);

    //void remove(Provider provider);

    //void update(Provider provider);

    List<RepoEntry> findAll();

    //Provider findById(Long id);

    RepoEntry findByPmid(String name);

    List<RepoEntry> findImportable();

    List<RepoEntry> findModifiedAfter(DateTime dateTime);
}