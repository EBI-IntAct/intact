package uk.ac.ebi.intact;

import uk.ac.ebi.intact.model.Institution;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundJpa {

    public static void main(String[] args) throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("intact-core-mem");
        {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(new Institution("lalalala"));
        em.getTransaction().commit();

        Query q = em.createQuery("from Institution");
        System.out.println(q.getResultList());
        }

        System.out.println("LALALALALALAL\n\n\n\n\n");

       // EntityManagerFactory emf = Persistence.createEntityManagerFactory("intact-core-mem");

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(new Institution("lalalala"));
        em.getTransaction().commit();

        Query q = em.createQuery("from Institution");
        System.out.println(q.getResultList());
    }
}
