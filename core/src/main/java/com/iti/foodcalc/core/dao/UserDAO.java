package com.iti.foodcalc.core.dao;

import com.iti.foodcalc.core.entity.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends GenericDAO<User> {
    public User find(String name, String socialNetwork, String socialNetworkId) {
        return (User) createCriteria().
                add(Restrictions.eq("name", name)).
                add(Restrictions.eq("socialNetwork", socialNetwork)).
                add(Restrictions.eq("socialNetworkId", socialNetworkId)).
                uniqueResult();
    }
}
