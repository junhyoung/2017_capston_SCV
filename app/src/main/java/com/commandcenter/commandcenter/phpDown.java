package com.commandcenter.commandcenter;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 준형 on 2017-05-17.
 */
//Mysql connect
public class phpDown extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... urls) {
        // int count = 0;
        String value;
        StringBuilder jsonHtml = new StringBuilder();
        StringBuffer sb=null;
        try {
            // 연결 url 설정
            URL url = new URL(urls[0]);
            // 커넥션 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 연결되었으면.
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                // 연결되었음 코드가 리턴되면.
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    sb = new StringBuffer();
                    String line = "";
                    line=br.readLine(); // num 잘라내기
                    java.lang.System.out.println("LINE : "+line);
                    line = br.readLine(); // Json 내용
                    try {
                        JSONArray jarray = new JSONArray(line);   // JSONArray 생성
                        JSONObject jObject = jarray.getJSONObject(jarray.length()-1);  // JSONObject 추출
                        String con = jObject.getString("con");
                        sb.append(con);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    } //doInBackground
}//phpDown