package uk.ac.ebi.intact;

import uk.ac.ebi.intact.config.IntactPersistence;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.context.IntactContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundJpa {

    public static void main(String[] args) throws Exception {

        /*
        EntityManagerFactory emf = IntactPersistence.createEntityManagerFactoryInMemory();
        {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(new Institution("la1"));
        em.getTransaction().commit();

        Query q = em.createQuery("from Institution");
        System.out.println(q.getResultList());
        }

        System.out.println("LALALALALALAL\n\n\n\n\n");

       // EntityManagerFactory emf = Persistence.createEntityManagerFactory("intact-core-mem");

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(new Institution("la2"));
        em.getTransaction().commit();

        Query q = em.createQuery("from Institution");
        System.out.println(q.getResultList()); */

        IntactContext context = IntactContext.getCurrentInstance();
        context.getDataContext().beginTransaction();
        System.out.println(context.getDataContext().getDaoFactory().getInstitutionDao().getAll());
        context.getDataContext().commitTransaction();
    }
}
