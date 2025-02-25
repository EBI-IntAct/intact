package uk.ac.ebi.intact.dataexchange.psimi.xml.exchange;

import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.IntactEntry;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.File;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import psidev.psi.mi.xml.xmlindex.IndexedEntry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.PsimiXmlForm;
import psidev.psi.mi.xml.PsimiXmlVersion;

/**
 * Psi Exchange definition.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface PsiExchange {

    PersisterStatistics importIntoIntact(InputStream psiXmlStream) throws PersisterException;

    PersisterStatistics importIntoIntact(IndexedEntry entry) throws ImportException;

    PersisterStatistics importIntoIntact(Interaction interaction);

    PersisterStatistics importIntoIntact(EntrySet entrySet) throws ImportException;

    void importIntoIntact(IntactEntry entry) throws PersisterException;

    OutputStream exportToPsiXml(IntactEntry... intactEntries);

    void exportToPsiXml(Writer writer, IntactEntry... intactEntries);

    void exportToPsiXml(File file, IntactEntry... intactEntries);

    EntrySet exportToEntrySet(IntactEntry... intactEntries);

    public PsimiXmlForm getXmlForm();

    public void setXmlForm(PsimiXmlForm xmlForm);

    public PsimiXmlVersion getPsiVersion();

    public void setPsiVersion(PsimiXmlVersion psiVersion);
}
