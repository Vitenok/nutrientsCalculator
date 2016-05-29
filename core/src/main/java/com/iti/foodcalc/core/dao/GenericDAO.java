package com.iti.foodcalc.core.dao;

import com.iti.foodcalc.core.entity.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
@Repository
abstract class GenericDAO<T> {
    private final Class<T> persistentClass;
    @Autowired
    private SessionFactory sessionFactory;

    public GenericDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(persistentClass);
    }

    public void saveOrUpdate(T t) {
        getSession().saveOrUpdate(t);
    }

    public void saveOrUpdateAll(List<T> ts) {
        for (T t : ts) {
            saveOrUpdate(t);
        }
    }

    public T findById(int id) {
        return (T) createCriteria().add(Restrictions.eq("id", id)).uniqueResult();
    }

    public List<T> findByIds(List<Integer> ids) {
        return createCriteria()
                .add(Restrictions.in("id", ids))
                .list();
    }

    public List<T> findAll() {
        return createCriteria().list();
    }

/*    public void delete(int id) {
        getSession().createQuery("delete from " + persistentClass.getName() + " where id = :id").setInteger("id", id).executeUpdate();
    }

    public void delete(T t) {
        getSession().delete(t);
    }*/
}
