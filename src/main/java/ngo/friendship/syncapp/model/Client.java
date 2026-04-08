/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Imtiaz
 */
public class Client {
    @JsonProperty("ID")
    private long id;
    @JsonProperty("NAME")
    private String name;
    @JsonProperty("CODE")
    private String code;
    @JsonProperty("STATE")
    private long state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Client(long id, String name, String code, long state) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.state = state;
    }
}
