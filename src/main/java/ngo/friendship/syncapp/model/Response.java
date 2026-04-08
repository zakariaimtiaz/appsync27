/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.model;

/**
 * created: 22 Oct 2017
 *
 * @author Shahadat
 */
public class Response {

    String code;
    String message;
    Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Response OK(Object data) {
        Response response = new Response();
        response.setCode("01");
        response.setData(data);
        return response;
    }

    public static Response ERROR(String message) {
        Response response = new Response();
        response.setCode("00");
        response.setMessage(message);
        return response;
    }
    
    public boolean isOK(){
       return "01".equals(code);
    }

    public String toStringError() {
        return String.format("%s-%s", code,message);
    }
}
