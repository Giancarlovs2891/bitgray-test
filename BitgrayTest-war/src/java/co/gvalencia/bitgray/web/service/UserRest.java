/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.web.service;

import co.gvalencia.bitgray.crud.UserEJb;
import co.gvalencia.bitgray.web.utils.Auth;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author giancarlovs
 */
@Stateless
@Path("user")
public class UserRest {

    @EJB
    UserEJb userEJb;

    Response response;

    @GET
    @Path("login")
    @Produces({"application/json"})
    public Response login(@Context HttpServletRequest request, @HeaderParam("authorization") String authString) {
        Auth auth = new Auth(authString);
        int login = userEJb.login(auth.getUsr(), auth.getPwd());
        if (login == 200) {
            response = Response.status(200).build();
        } else {
            response = Response.status(404).build();
        }
        return response;
    }

}
