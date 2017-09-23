/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.crud;

import co.gvalencia.bitgray.entities.Phone;
import co.gvalencia.bitgray.entities.Recharge;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author giancarlovs
 */
public interface PhoneEjb {
    
    Phone get(int id);
    
    Phone get(String number);
    
    List<Phone> listPhones ();
    
    Phone create (Phone phone);
    
    Phone edit(Phone phone);
    
    HashMap recharge (Recharge recharge);
    
    List<Recharge> rechargeList(int phoneId);
    
}
