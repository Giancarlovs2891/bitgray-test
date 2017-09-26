/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gvalencia.bitgray.web.service;

import co.gvalencia.bitgray.crud.PhoneEjb;
import co.gvalencia.bitgray.crud.UserEJb;
import co.gvalencia.bitgray.entities.CallHistory;
import co.gvalencia.bitgray.entities.Phone;
import co.gvalencia.bitgray.entities.Recharge;
import co.gvalencia.bitgray.web.utils.Auth;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author giancarlovs
 */
@Stateless
@Path("phone")
public class PhoneRest {

    @EJB
    UserEJb userEJb;

    @EJB
    PhoneEjb phoneEjb;

    Response response;

    @GET
    @Path("{phoneNumber}")
    @Produces("application/json")
    public Response gegPhoneInfo(@Context HttpServletRequest request, @HeaderParam("authorization") String authString, @PathParam("phoneNumber") String phoneNumber) {
        Auth auth = new Auth(authString);
        int login = userEJb.login(auth.getUsr(), auth.getPwd());
        JSONObject obj = new JSONObject();
        if (login == 200) {
            Phone phone = phoneEjb.get(phoneNumber);
            if (phone != null) {
                response = Response.status(200).entity(phone).build();
            } else {
                obj.put("status", "error");
                obj.put("msg", "Phone not found");
                response = Response.status(404).entity(obj.toString()).build();
            }
        } else {
            obj.put("status", "error");
            obj.put("msg", "Unauthorized user");
            response = Response.status(401).entity(obj.toString()).build();
        }

        return response;
    }

    @POST
    @Path("create")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createPhone(@Context HttpServletRequest request, @HeaderParam("authorization") String authString, Phone phone) {
        Auth auth = new Auth(authString);
        int login = userEJb.login(auth.getUsr(), auth.getPwd());
        JSONObject obj = new JSONObject();
        if (login == 200) {
            phone = phoneEjb.create(phone);
            if (phone != null) {
                response = Response.status(201).entity(phone).build();
            } else {
                obj.put("status", "error");
                obj.put("msg", "Error while saving, phone number or device id duplicated");
                response = Response.status(500).entity(obj.toString()).build();
            }
        } else {
            obj.put("status", "error");
            obj.put("msg", "Unauthorized user");
            response = Response.status(401).entity(obj.toString()).build();
        }

        return response;
    }

    @POST
    @Path("recharge/{phoneNumber}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response rechargePhone(@Context HttpServletRequest request, @HeaderParam("authorization") String authString, @PathParam("phoneNumber") String phoneNumber, Recharge recharge) {
        Auth auth = new Auth(authString);
        int login = userEJb.login(auth.getUsr(), auth.getPwd());
        JSONObject obj = new JSONObject();
        if (login == 200) {
            Phone phone = phoneEjb.get(phoneNumber);
            if (phone != null) {
                recharge.setPhoneId(phone);
                HashMap rechargeResponse = phoneEjb.recharge(recharge);
                if (rechargeResponse != null) {
                    obj.put("status", "ok");
                    obj.put("msg", rechargeResponse.get("bonus").toString() + "% bonus granted");
                    response = Response.status(201).entity(obj.toString()).build();
                } else {
                    obj.put("status", "error");
                    obj.put("msg", "Error during transaction, please try again");
                    response = Response.status(500).entity(obj.toString()).build();
                }
            } else {
                obj.put("status", "error");
                obj.put("msg", "Phone not found");
                response = Response.status(404).entity(obj.toString()).build();
            }

        } else {
            obj.put("status", "error");
            obj.put("msg", "Unauthorized user");
            response = Response.status(401).entity(obj.toString()).build();
        }

        return response;
    }

    @POST
    @Path("call/start")
    @Produces("application/json")
    public Response startCall(@Context HttpServletRequest request, @HeaderParam("authorization") String authString, CallHistory call) {
        Auth auth = new Auth(authString);
        int phoneAuth = phoneEjb.phoneAuth(auth.getUsr(), auth.getPwd());
        JSONObject obj = new JSONObject();
        if (phoneAuth == 200) {
            HashMap callHistory = phoneEjb.startCall(auth.getUsr(), call.getPhoneTo());
            int status = (Integer) callHistory.get("status");
            switch (status) {
                case 201:
                    response = Response.status(401).entity((CallHistory) callHistory.get("msg")).build();
                    break;

                case 404:
                    obj.put("status", "error");
                    obj.put("msg", "Phone not found");
                    response = Response.status(404).entity(obj.toString()).build();
                    break;

                case 500:
                    obj.put("status", "error");
                    obj.put("msg", "Try again");
                    response = Response.status(500).entity(obj.toString()).build();
                    break;
            }
        } else {
            obj.put("status", "error");
            obj.put("msg", "Unauthorized phone");
            response = Response.status(401).entity(obj.toString()).build();
        }

        return response;
    }

    @POST
    @Path("call/end")
    @Produces("application/json")
    public Response endCall(@Context HttpServletRequest request, @HeaderParam("authorization") String authString, CallHistory callHistory) {
        Auth auth = new Auth(authString);
        int phoneAuth = phoneEjb.phoneAuth(auth.getUsr(), auth.getPwd());
        JSONObject obj = new JSONObject();
        if (phoneAuth == 200) {
            HashMap callEnd = phoneEjb.endCall(callHistory.getToken(), callHistory.getDuration());
            int status = (Integer) callEnd.get("status");
            switch (status) {
                case 200:
                    response = Response.status(401).entity((CallHistory) callEnd.get("msg")).build();
                    break;

                case 404:
                    obj.put("status", "error");
                    obj.put("msg", "Phone not found");
                    response = Response.status(404).entity(obj.toString()).build();
                    break;

                case 500:
                    obj.put("status", "error");
                    obj.put("msg", "Call already ended");
                    response = Response.status(500).entity(obj.toString()).build();
                    break;
            }
        } else {
            obj.put("status", "error");
            obj.put("msg", "Unauthorized phone");
            response = Response.status(401).entity(obj.toString()).build();
        }

        return response;
    }

}
