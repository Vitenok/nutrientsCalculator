package com.iti.foodCalculator.dao;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

abstract class GenericDAO<T> implements Serializable {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("food_composition");
    private static EntityManager em = emf.createEntityManager();
    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;

    }

    public void beginTransaction() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    public void commit() {
        em.getTransaction().commit();
    }

    public void rollback() {
        em.getTransaction().rollback();
    }

    public void closeTransaction() {
        em.close();
    }

    public void commitAndCloseTransaction() {
        commit();
        closeTransaction();
    }

    public void flush() {
        em.flush();
    }

    public void joinTransaction() {
        em = emf.createEntityManager();
        em.joinTransaction();
    }

    public void save(T entity) {
        em.persist(entity);
    }

    public void delete(T entity) {
        T entityToBeRemoved = em.merge(entity);

        em.remove(entityToBeRemoved);
    }

    public T update(T entity) {
        return em.merge(entity);
    }

    public T find(int entityID) {
        return em.find(entityClass, entityID);
    }


    public T findReferenceOnly(int entityID) {
        return em.getReference(entityClass, entityID);
    }

    public List<T> findAll() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    protected List<T> findResultList(String namedQuery, Map<String, Object> parameters) {
        List<T> resultList = null;
        try {
            Query query = em.createNamedQuery(namedQuery);
            resultList = (List<T>) query.getResultList();

        } catch (NoResultException e) {
            System.out.println("No result found for named query: " + namedQuery);
        } catch (Exception e) {
            System.out.println("Error while running query: " + e.getMessage());
            e.printStackTrace();
        }
        return resultList;
    }
}
