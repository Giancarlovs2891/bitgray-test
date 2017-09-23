/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.crud;

import co.gvalencia.bitgray.entities.User;
import java.util.List;

/**
 *
 * @author giancarlovs
 */
public interface UserEJb {
    
    int login (String username, String password);
    
}
