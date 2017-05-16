package com.commandcenter.commandcenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.system.System;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.services.dynamodbv2.model.*;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


public class MainActivity extends AppCompatActivity {
    // Initialize the Amazon Cognito credentials provider

    private Context mContext = this;
    ImageView recommand_img;
    TextView review_name;
    TextView review_content;
    TextView recommand_name;

    Bitmap bmImg;
    phpDown task;
    static final int MEMBER_NUM = 10000;
    static final int OPTION_NUM = 3;
    int total_num;
    String asin;
    //Member[] m1, m2, m3 ; //참조 변수 선언
    Member[] member = new Member[MEMBER_NUM]; //객체타입의 배열 갯수
    int[] countArray = new int[MEMBER_NUM];
    Product review=new Product();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new phpDown();
        Right_layout RL=new Right_layout(this);
        review_name = (TextView) findViewById(R.id.review_name);
        review_content = (TextView) findViewById(R.id.review_content);
        recommand_img=(ImageView)findViewById(R.id.recommand_image);
        recommand_name=(TextView)findViewById(R.id.recommand_Name);
        for (int i = 0; i < MEMBER_NUM; i++) { //해당 객체 배열마다 생성하는 과정
            //System.out.println(i+"번째 객체 생성"); // 테스트 출력
            member[i] = new Member();
            countArray[i] = 0; //배열초기화도 같이
        }
        asin="0000000000";
        review=RL.searchReview(asin);
        setReview(review);
        try {
            recommand_name.setText(task.execute("http://ec2-52-78-183-104.ap-northeast-2.compute.amazonaws.com/RDS.php").get());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setReview(Product review){

        review_name.setText(review.getAsin());
        review_content.setText(review.getReview1());
    }


    //Mysql connect
    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            // int count = 0;
            String value;
            StringBuilder jsonHtml = new StringBuilder();
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
                        for (int i=0;;i++ ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            //if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            //name
                            if (i==0){
                                int index = line.indexOf(":");
                                int index2 = line.indexOf(" ");
                                total_num = Integer.parseInt(line.substring(index+1, index2)); // 바코드 총 갯수 세기 (string -> int형)
                            }
                            if(MEMBER_NUM == i-1) break; // 데이터 갯수만큼만 반복
                            int index = line.indexOf("cnt"); // cnt의 c의 인덱스값 찾기
                            index += 10; // +10 해주면 날짜의 첫번째 수가 나옴
                            int index2 = line.indexOf(","); // ,의 인덱스 찾기
                            value = line.substring(index, index+17); // 날짜를 value에 넣기
                            member[i].setDate(value);
                            //line = line.substring(index2+1); // , 다음부터 한줄 끝까지
                            // bacord number
                            line = br.readLine();
                            value = line.substring(0, 10);
                            member[i].setBacord(value);
                            jsonHtml.append("date : " + member[i].getDate() + "bacord_number : " + member[i].getBacord() + "\n");
                            //count++; // line 하나 끝냈는지 확인하기 위해 ++
                            // System.out.println("count : "+count);
                            member[i].print();
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        } //doInBackground
    }//phpDown
}//Main
