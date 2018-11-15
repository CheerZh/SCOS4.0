package es.source.code.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;
    public String userName;
    public String password;
    public Boolean oldUser;
    ArrayList users = new ArrayList();

    public User(){ }
    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
    public User(String userName, String password,Boolean oldUser) {
        this.userName = userName;
        this.password = password;
        this.oldUser = oldUser;
    }

    public void setter(String userName , String password,Boolean oldUser){
        this.userName = userName;
        this.password = password;
        this.oldUser = oldUser;
        users.add(new User(userName,password,oldUser));
    }

    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public Boolean getOldUser(){
        return oldUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
