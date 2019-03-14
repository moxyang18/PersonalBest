package com.example.team10.personalbest.friend;

public class StringAsObject extends Object {
    private String string;
    public StringAsObject(){
        string="";
    }

    public StringAsObject(String s){
        string=s;
    }

    public String getString() {
        return string;
    }
    public void setString(String s) {
        string = s;
    }
}
