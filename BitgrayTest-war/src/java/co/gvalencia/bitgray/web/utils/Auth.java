/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.web.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.nio.charset.Charset;

/**
 *
 * @author giancarlovs
 */
public class Auth {

    private final String usr;
    private final String pwd;

    public Auth(String authString) {
        String base64Credentials = authString.substring("Basic".length()).trim();
        String credentials = new String(Base64.decode(base64Credentials), Charset.forName("UTF-8"));
        // credentials = username:password
        String[] values = credentials.split(":");
        usr = values[0];
        pwd = values[1];
    }

    public String getUsr() {
        return usr;
    }

    public String getPwd() {
        return pwd;
    }
    
}
