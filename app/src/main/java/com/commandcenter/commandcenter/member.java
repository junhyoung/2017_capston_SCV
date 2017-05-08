package com.commandcenter.commandcenter;

public class Member {
    private String date;
    private String bacord_number;

    Member() {}
    void print(){
        System.out.println("date: " + date+", bacord_number : "+bacord_number);
    }
    void setDate(String date){
        this.date = date;
    }
    String getDate(){
        return this.date;
    }

    void setBacord(String num){
        this.bacord_number = num;
    }

    String getBacord(){
        return this.bacord_number;
    }
}
