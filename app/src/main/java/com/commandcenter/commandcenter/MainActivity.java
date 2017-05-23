package com.commandcenter.commandcenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.commandcenter.commandcenter.global.asin;
import static com.commandcenter.commandcenter.global.buy_item;
import static com.commandcenter.commandcenter.global.buy_item_name;
import static com.commandcenter.commandcenter.global.c;
import static com.commandcenter.commandcenter.global.changemsg;
import static com.commandcenter.commandcenter.global.changemsg2;
import static com.commandcenter.commandcenter.global.item;
import static com.commandcenter.commandcenter.global.total_num2;


public class MainActivity extends AppCompatActivity {
    // Initialize the Amazon Cognito credentials provider
    // 구매희망목록용
    ArrayAdapter adapter;
    ListView listview;
    // 실제구매목록용
    ArrayAdapter adapter2;
    ArrayAdapter adapter3;
    ListView listview2;
    static SaleStandSet saleStandSet;
    MapPath_Floyd map;
    String urltmp;
    Image image;

    private Context mContext = this;
    ImageView recommand_img;
    TextView review_name;
    TextView review_content;
    TextView recommand_name;
    phpDown task; // 바코드 php
    phpDown_list task2; // 물품목록 php
    static final int MEMBER_NUM = 10000;
    static Member[] member = new Member[MEMBER_NUM]; // 객체타입의 배열 갯수
    static Member_list[] member2;// 객체타입의 배열 갯수
    Product review=new Product();
    recommand recom = new recommand();
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//        saleStandSet = new SaleStandSet(this);
//
//        PathFinder pathFinder = new PathFinder();
//        pathFinder.findPathWithFloyd(map, saleStandSet);
//
//        PathDraw pathDraw = new PathDraw(MainActivity.this, pathFinder.standArray, saleStandSet);
//        setContentView(pathDraw);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c=this;
        setContentView(R.layout.activity_main);
        task = new phpDown();
        task2 = new phpDown_list();
        task2.execute("http://ec2-52-78-183-104.ap-northeast-2.compute.amazonaws.com/list.php");


        Right_layout RL=new Right_layout(this);
        review_name = (TextView) findViewById(R.id.review_name);
        review_content = (TextView) findViewById(R.id.review_content);
        recommand_img=(ImageView)findViewById(R.id.recommand_image);
        recommand_name=(TextView)findViewById(R.id.recommand_Name);
        // 구매희망목록용
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, item) ;
        listview = (ListView) findViewById(R.id.list_view) ;
        // 실제구매목록용
        adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, buy_item) ;
        adapter3 = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, buy_item_name) ;
        listview2 = (ListView) findViewById(R.id.list_view2) ;


        map = new MapPath_Floyd();
        map.floyd();

        // 희망 구매 목록 thread 설정
        hope_thread runnable = new hope_thread();
        Thread thread = new Thread(runnable);
        thread.setDaemon(true); // main 종료시 같이 종료
        thread.start();
        // 실제 구매 목록 thread 설정
        buy_thread runnable2 = new buy_thread();
        Thread thread2 = new Thread(runnable2);
        thread2.setDaemon(true); // main 종료시 같이 종료

        thread2.start();
        for (int i = 0; i < MEMBER_NUM; i++) { //해당 객체 배열마다 생성하는 과정
            member[i] = new Member();
        }

        //asin="0000000000";
        asin = "0000000000"; // DynamoDB에 데이터가 없어서 임시로 강제 지정
        review=RL.searchReview(asin);
        recom=RL.searchrecommand(review.getCategory());
        //recommand_name.setText(recom.getCategory());
        //Log.e("REcom : ",recom.getAsin());
        setRecommnad(recom);

        setReview(review);
        // 검색 선택 버튼 클릭
        Button search = (Button)findViewById(R.id.search_product);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), hope_list_view.class);
                startActivity(intent);
            }
        });

        // 희망목록에서 물품 클릭 했을 때
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int check_position = listview.getCheckedItemPosition();
                String name = (String)adapterView.getAdapter().getItem(position);
                for(int i=0; i<total_num2; i++){
                    if(member2[i].getName_list().equals(name)){
                        // Toast.makeText(MainActivity.this, member2[i].getAsin(), Toast.LENGTH_SHORT).show();
                        Right_layout r= new Right_layout(com.commandcenter.commandcenter.MainActivity.this); //review 생성
                        Product pTmp=new Product();
                        pTmp=r.searchReview(member2[i].getAsin());
                        setReview(pTmp);
                        recommand re=new recommand();
                        re=r.searchrecommand(pTmp.getCategory());
                        setRecommnad(re);
                    }
                }
                //Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });


        //구매목록에서 구매취소 버튼 누를 때
        Button deleteButton = (Button)findViewById(R.id.del_list);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                SparseBooleanArray checkedItems = listview2.getCheckedItemPositions();
                int count = adapter3.getCount();
                for(int i=count-1; i>=0; i--){
                    if(checkedItems.get(i)){
                        buy_item.remove(i);
                    }
                }
                // 모든 선택 상태 초기화
                listview2.clearChoices();
                adapter3.notifyDataSetChanged();
                listview2.setAdapter(adapter3);
            }
        });

        //구매목록에서 리뷰 버튼 누를 때
        Button reviewButton = (Button)findViewById(R.id.f5);
        reviewButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                SparseBooleanArray checkedItems = listview2.getCheckedItemPositions();
                int count = adapter3.getCount();
                int check_count = 0;
                for(int i=0; i<count; i++){
                    if(checkedItems.get(i)){
                        check_count++;
                    }
                }
                for(int i=0; i<count; i++){
                    if(check_count == 1){
                        if(checkedItems.get(i)){
                            Right_layout r= new Right_layout(com.commandcenter.commandcenter.MainActivity.this); //review 생성
                            Product pTmp=new Product();
                            pTmp=r.searchReview(buy_item.get(buy_item.size()-1-i));
                            //pTmp=r.searchReview(buy_item.get(buy_item.size()-1));
                            setReview(pTmp);
                            recommand re=new recommand();
                            re=r.searchrecommand(pTmp.getCategory());
                            setRecommnad(re);
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Select only one!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 리스트 총 갯수만큼 Member_list 객체 할당
        //Member_list[] member2 = new Member_list[total_num2];
        member2 = new Member_list[total_num2];
        for (int i = 0; i < total_num2; i++) { //해당 객체 배열마다 생성하는 과정
            member2[i] = new Member_list();
        }
        task2 = new phpDown_list();
        task2.execute("http://ec2-52-78-183-104.ap-northeast-2.compute.amazonaws.com/list.php");

    }//Oncreate

    public void setReview(Product review){
        review_name.setText(review.getName());
        review_content.setText(review.getReview1());
    }
    public void setRecommnad(recommand r){
        Right_layout rl= new Right_layout(this);
        Product pp=rl.searchReview(r.getAsin());
        System.out.println("AA : "+pp.getImage());
        urltmp = pp.getImage();
       final Handler handler = new Handler();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    // 걍 외우는게 좋다 -_-;

                    URL url = new URL(urltmp);
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            recommand_img.setImageBitmap(bm);
                        }
                    });
                    recommand_img.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){

                }

            }
        });

        t.start();


//        try {
//            //image = new Image(pp.getImage());
//            System.out.println("GER RElate : "+pp.getImage());
//            //Bitmap bitmap= image.execute().get();
//            //System.out.println("Image : " + bitmap);
//            //recommand_img.setImageBitmap(bitmap);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
        recommand_name.setText(pp.getName());

        System.out.println("BB : "+ pp.getName());
    }

    // 희망목록 보여주는 무한루프 쓰레드
    public class hope_thread implements Runnable {
        public void run() {
            while (true) {
                mHandler.sendEmptyMessage(changemsg);
                try {
                    Thread.sleep(500); // 0.5초 단위로 갱신
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1){
                listview.setAdapter(adapter) ; // 희망 구매 목록 보여주기
                changemsg = 0;
            }
        }
    };
    // 구매목록 보여주는 무한루프 쓰레드
    public class buy_thread implements Runnable {
        public void run() {
            while (true) {
                try {
                    task = new phpDown();
                    asin =task.execute("http://ec2-52-78-183-104.ap-northeast-2.compute.amazonaws.com/RDS.php").get();
                    //for(int i=0; i<MEMBER_NUM; i++){
                    //if(member[i].getBacord()==null)
                    //break;
                    //여기에다 member[i].getBacord()를 이용해 member[i].setName()에 맵핑하여 구하면 된다.
                    //System.out.println("NAME1 :  "+ rr.searchReview(member[i].getBacord()).getName());
                    //buy_item.add(0, rr.searchReview(member[i].getBacord()).getName()); // 사용자가 보기 쉽게 맨 앞에 저장
                    //}
                    mHandler2.sendEmptyMessage(changemsg2);

                    Thread.sleep(500); // 0.5초 단위로 갱신
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.print(asin);
            }
        }
    }
    Handler mHandler2 = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1){
                listview2.setAdapter(adapter3) ; // 구매 목록 보여주기
                changemsg2 = 0;
                Right_layout rr=new Right_layout(com.commandcenter.commandcenter.MainActivity.this);
                buy_item_name.add(0, rr.searchReview(buy_item.get(buy_item.size()-1)).getName()); // 사용자가 보기 쉽게 맨 앞에 저장
                Right_layout r= new Right_layout(com.commandcenter.commandcenter.MainActivity.this); //review 생성
                Product pTmp=new Product();
                pTmp=r.searchReview(buy_item.get(buy_item.size()-1));
                setReview(pTmp);
                recommand re=new recommand();
                re=r.searchrecommand(pTmp.getCategory());
                setRecommnad(re);
                //System.out.println("ASIN" + asin);
                //recommand_name.setText(asin); <- 이거 넣으면 물품란에 asin 나옴
            }
        }
    };

    public class Image extends AsyncTask<String, Integer,Bitmap> {

        private  String url;
        Bitmap bmImg;

        Image(String url) {
            this.url=url;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(url);
                System.out.println(" URL : "+url);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            recommand_img.setImageBitmap(bmImg);
        }


    }

}//Main
