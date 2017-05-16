package com.commandcenter.commandcenter;

public class Member {
    private String bacord_number;

    Member() {}
    void print(){
        System.out.println("bacord_number : "+bacord_number);
    }

    void setBacord(String num){
        this.bacord_number = num;
    }

    String getBacord(){
        return this.bacord_number;
    }
}
