package com.commandcenter.commandcenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {
    // Initialize the Amazon Cognito credentials provider

    private Context mContext = this;
    ImageView recommand_img;
    TextView review_name;
    TextView review_content;
    TextView recommand_name;
    phpDown task;
    static final int MEMBER_NUM = 10000;
    static final int OPTION_NUM = 3;
    int total_num;
    String asin;
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
        asin = "0000000000"; // DynamoDB에 데이터가 없어서 임시로 강제 지정
        review=RL.searchReview(asin);
        setReview(review);

    }//Oncreate

    public void setReview(Product review){

        review_name.setText(review.getName());
        review_content.setText(review.getReview1());
    }
}//Main
