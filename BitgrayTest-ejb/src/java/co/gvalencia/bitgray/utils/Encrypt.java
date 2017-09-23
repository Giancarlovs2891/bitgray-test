/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author giancarlovs
 */
public class Encrypt {
    
    public static String toMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(texto.getBytes());
            byte byteData[] = md.digest();
            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println("Digest(in hex format):: " + sb.toString());
            //convert the byte to hex format method 2
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            System.out.println("Digest(in hex format):: " + hexString.toString());
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encrypt.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
