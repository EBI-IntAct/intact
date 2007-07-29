package uk.ac.ebi.intact;

import uk.ac.ebi.intact.model.Institution;

import javax.persistence.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CutreTest
{
    public static void main(String[] args)
    {
        new CutreTest().doSomething();
    }

    public CutreTest(){

    }

    public void doSomething() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("intact-core-mem");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(new Institution("testInst"));
        em.getTransaction().commit();
        
        Query q = em.createQuery("from Institution");

        System.out.println(q.getResultList());


    }
}

