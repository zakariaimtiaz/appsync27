/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import static ngo.friendship.syncapp.util.Constant.CLIENT;
import static ngo.friendship.syncapp.util.Constant.SERVER;

/**
 *
 * @author Imtiaz
 */
public class AppSync {
    @JsonIgnore
    private Long id=1L;
    @JsonProperty("NAME")
    private String name;
    @JsonProperty("CODE")
    private String code;
    @JsonProperty("TYPE")
    private String type;
    
    @JsonProperty("STATE")
    private long state;
    
    @JsonProperty("TYPE_LIST")
    private List<String> typeList;
    
    public Long getId() {
        return id;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getState() {
        return state;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public AppSync() {
    }

    public AppSync(Long id, String name, String code, String type,long state) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.state=state;
    }
    
    public boolean isClient(){
       return  CLIENT.equals(type);
    }
    public boolean isServer(){
       return  SERVER.equals(type);
    }
    public boolean isActive(){
       return  state==1?true:false;
    }
    
}
