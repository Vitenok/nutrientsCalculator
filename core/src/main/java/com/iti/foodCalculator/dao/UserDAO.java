package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("usersDAO")
@Transactional
public class UserDAO extends GenericDAO<User> {
    public User find(String name, String socialNetwork, String token) {
        return (User) createCriteria().
                add(Restrictions.eq("name", name)).
                add(Restrictions.eq("socialNetwork", socialNetwork)).
                add(Restrictions.eq("token", token)).
                uniqueResult();
    }

    public void save(User user) {
        getSession().save(user);
    }
}
