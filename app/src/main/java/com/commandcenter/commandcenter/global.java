package com.commandcenter.commandcenter;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by kimdonghoon on 2017-05-17.
 */


public class global extends Application {
    static  String asin="";
    static int changemsg = 0; // 희망목록 작성하고 돌아 올 때만 잠시 1로 변하고 그 때 리스트를 갱신함
    static int changemsg2 = 0; // 바코드 찍을 때만 refresh 씀 그 때만 구매 목록 갱신함
    static ArrayList<String> item = new ArrayList<String>() ; // 구매희망목록
    static ArrayList<String> buy_item = new ArrayList<String>() ; // 구매목록
    static int countdb;
    static int total_num;
    static boolean dbfirst = false; // db 접근이 처음이라면 countdb 값을 total_num으로 초기화해줘야 한다.
    static Context c =null;

    public void setC(Context mContext){
        c=mContext;
    }
}
