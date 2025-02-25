package uk.ac.ebi.intact.update.persistence.dao.impl;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.update.model.protein.update.UpdateProcess;
import uk.ac.ebi.intact.update.model.protein.update.events.ProteinEvent;
import uk.ac.ebi.intact.update.model.protein.update.events.range.UpdatedRange;
import uk.ac.ebi.intact.update.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.update.persistence.UpdateProcessDao;

import java.util.Date;

/**
 * Unit test for UpdateProcessDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/03/11</pre>
 */

public class UpdateProcessDaoImplTest extends UpdateBasicTestCase {

    @Test
    public void create_update_process(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();

        processDao.persist(process);
        Assert.assertEquals(1, processDao.countAll());
    }

    @Test
    public void update_date_process(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();
        Date dateBefore = process.getDate();

        processDao.persist(process);

        long id = process.getId();

        Assert.assertEquals(1, processDao.countAll());

        Date newDate = new Date(10);

        Assert.assertNotSame(dateBefore, newDate);
        process.setDate(newDate);

        processDao.update(process);

        Assert.assertEquals(newDate, processDao.getById(id).getDate());
    }

    @Test
    public void update_event_process(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();

        processDao.persist(process);

        Assert.assertEquals(1, processDao.countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinEventDao(ProteinEvent.class).countAll());

        ProteinEvent evt = getMockBuilder().createDefaultProteinEvent();

        process.addEvent(evt);

        processDao.update(process);

        Assert.assertEquals(1, processDao.countAll());
        Assert.assertEquals(1, getDaoFactory().getProteinEventDao(ProteinEvent.class).countAll());
    }

    @Test
    public void update_range_process(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();

        processDao.persist(process);

        Assert.assertEquals(1, processDao.countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinEventDao(ProteinEvent.class).countAll());

        UpdatedRange range = getMockBuilder().createInvalidRange();

        process.addRangeUpdate(range);

        processDao.update(process);

        Assert.assertEquals(1, processDao.countAll());
        Assert.assertEquals(1, getDaoFactory().getUpdatedRangeDao(UpdatedRange.class).countAll());
    }

    @Test
    public void delete_update_process(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();

        UpdatedRange range = getMockBuilder().createInvalidRange();

        process.addRangeUpdate(range);

        ProteinEvent evt = getMockBuilder().createDefaultProteinEvent();

        process.addEvent(evt);

        processDao.persist(process);
        Assert.assertEquals(1, processDao.countAll());

        processDao.delete(process);
        Assert.assertEquals(0, processDao.countAll());
        Assert.assertEquals(0, getDaoFactory().getUpdatedRangeDao(UpdatedRange.class).countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinEventDao(ProteinEvent.class).countAll());
    }

    @Test
    public void get_by_date(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();
        Date dateBefore = process.getDate();

        processDao.persist(process);

        Long id = process.getId();

        Assert.assertEquals(1, processDao.countAll());
        Assert.assertEquals(1, processDao.getAllUpdateProcessesByDate(dateBefore).size());
        Assert.assertEquals(0, processDao.getAllUpdateProcessesByDate(new Date(1)).size());
        Assert.assertEquals(id, processDao.getAllUpdateProcessesByDate(dateBefore).iterator().next().getId());
    }

    @Test
    public void get_before_after_date(){
        UpdateProcessDao processDao = getDaoFactory().getUpdateProcessDao();

        UpdateProcess process = getMockBuilder().createUpdateProcess();
        Date date = new Date(3);
        process.setDate(new Date(System.currentTimeMillis()));

        UpdateProcess process2 = getMockBuilder().createUpdateProcess();
        Date date1 = new Date(1);
        process2.setDate(date1);

        UpdateProcess process3 = getMockBuilder().createUpdateProcess();
        Date date2 = new Date(2);
        process3.setDate(date2);

        Assert.assertTrue(date1.before(date2));
        Assert.assertTrue(date2.before(date));
        Assert.assertFalse(date1.equals(date2));
        Assert.assertFalse(date2.equals(date));

        processDao.persist(process);
        processDao.persist(process2);
        processDao.persist(process3);

        Long id = process.getId();

        Assert.assertEquals(3, processDao.countAll());
        Assert.assertEquals(2, processDao.getAllUpdateProcessesBeforeDate(date2).size());
        Assert.assertEquals(1, processDao.getAllUpdateProcessesAfterDate(date).size());
        Assert.assertEquals(id, processDao.getAllUpdateProcessesAfterDate(date).iterator().next().getId());
    }
}
