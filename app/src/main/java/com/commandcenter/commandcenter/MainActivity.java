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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
        //asin="0000000000";
        try {
            asin = task.execute("http://ec2-52-78-183-104.ap-northeast-2.compute.amazonaws.com/RDS.php").get();
            asin = asin.substring(0,10);
            recommand_name.setText(asin);

        }catch (Exception e){
            e.printStackTrace();
        }
        asin = "0000000000";
        review=RL.searchReview(asin);
        setReview(review);

    }//Oncreate

    public void setReview(Product review){

        review_name.setText(review.getAsin());
        review_content.setText(review.getReview1());
    }
}//Main
