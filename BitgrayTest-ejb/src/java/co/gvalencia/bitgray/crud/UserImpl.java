/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.crud;

import co.gvalencia.bitgray.entities.User;
import co.gvalencia.bitgray.utils.Encrypt;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author giancarlovs
 */
@Stateless
public class UserImpl implements UserEJb{
    
    @PersistenceContext(unitName = "Bitgray-PU")
    EntityManager em;

    @Override
    public int login(String username, String password) {
        try {
            String queryStr = "SELECT u FROM User u "
                    + "WHERE u.username = :username "
                    + "AND u.password = :password";
            Query query = em.createQuery(queryStr);
            query.setParameter("username", username);
            query.setParameter("password", Encrypt.toMD5(password));
            if(query.getResultList().size()>0){
                return 200;
            }else{
                return 404;
            }
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 500;
        }
    }
    
}
