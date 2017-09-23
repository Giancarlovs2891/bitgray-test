/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.crud;

import co.gvalencia.bitgray.entities.Phone;
import co.gvalencia.bitgray.entities.PricePerMinute;
import co.gvalencia.bitgray.entities.Recharge;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author giancarlovs
 */
@Stateless
public class PhoneImpl implements PhoneEjb {

    @PersistenceContext(unitName = "Bitgray-PU")
    EntityManager em;

    @Override
    public Phone get(int id) {
        return em.find(Phone.class, id);
    }

    @Override
    public Phone get(String number) {
        try {
            String queryStr = "SELECT p FROM Phone p "
                    + "WHERE p.number = :number";
            Query query = em.createQuery(queryStr);
            query.setParameter("number", number);
            System.err.println("Lista "+query.getResultList());
            if(query.getResultList().size()>0){
                return (Phone) query.getResultList().get(0);
            }else{
                return null;
            }
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Phone> listPhones() {
        try {
            String queryStr = "SELECT p FROM Phone p";
            Query query = em.createQuery(queryStr);
            return query.getResultList();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public Phone create(Phone phone) {
        try {
            Date date = new Date();
            phone.setStatus(1);
            phone.setMinutesTotal(0);
            phone.setMinutesUsed(0);
            phone.setCreatedAt(date);
            phone.setUpdatedAt(date);
            em.persist(phone);
            em.flush();

            return phone;
        } catch (Exception ex) {
            Logger.getLogger(Phone.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Phone edit(Phone phone) {
        em.merge(phone);
        return phone;
    }

    @Override
    public HashMap recharge(Recharge recharge) {
        try {
            
            Date date = new Date();
            Double bonus = getBonus10(recharge.getPhoneId(), recharge.getValue());
            int bonusOk = 0;
            HashMap<String, Integer> response = new HashMap<>();
            if(bonus>recharge.getValue()){
                bonusOk=1;
                response.put("bonus", 10);
            }else {
                response.put("bonus", 0);
            }
            recharge.setValue(bonus);
            recharge.setCreatedAt(date);
            em.persist(recharge);
            em.flush();
            Phone phone = recharge.getPhoneId();
            int rechargedMinutes = recharge.getValue().intValue() / getPricePerMinute().intValue();
            phone.setMinutesTotal(phone.getMinutesTotal() + rechargedMinutes);
            edit(phone);
            recharge.setPhoneId(phone);
            
            
            response.put("recharge", 201);
            return response;
        } catch (Exception ex) {
            Logger.getLogger(Recharge.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Recharge> rechargeList(int phoneId) {
        try {
            Phone phone = get(phoneId);
            String queryStr = "SELECT r FROM Recharge r "
                    + "WHERE r.phoneId = :phoneId "
                    + "ORDER BY r.id";
            Query query = em.createQuery(queryStr);
            query.setParameter("phoneId", phone);
            return query.getResultList();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    private Double getPricePerMinute() {
        try {
            String queryStr = "SELECT p FROM PricePerMinute p WHERE p.status=1";
            Query query = em.createQuery(queryStr);
            PricePerMinute ppm = (PricePerMinute) query.getResultList().get(0);
            return ppm.getValue();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    private Double getBonus10(Phone phone, Double value) {
        List<Recharge> recharges = rechargeList(phone.getId());
        Double averageRecharge = 0.0;
        if (recharges.size()>0) {
            Date first = recharges.get(recharges.size() - 1).getCreatedAt();
            Date second = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Boolean sameDate = sdf.format(first).equals(sdf.format(second));
            System.err.println(sameDate);
            if (sameDate==false) {
                for (Recharge recharge : recharges) {
                    averageRecharge += recharge.getValue();
                }
                averageRecharge = averageRecharge / recharges.size() - 1;
                if (value > averageRecharge) {
                    value += value * 0.10;
                }
            }
        }

        return value;
    }

}
