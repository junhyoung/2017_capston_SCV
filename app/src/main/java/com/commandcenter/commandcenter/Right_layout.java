package com.commandcenter.commandcenter;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by 준형 on 2017-05-17.
 */

public class Right_layout{
    private Context mContext = null;
    private searchDDB s=new searchDDB();
    private Product p= new Product();


    public Right_layout(Context context) {
        this.mContext = context;
        System.out.print(mContext);
    }
    Product searchReview(String asin){
        try {
            p = s.execute(asin).get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return p;
    }
    private class searchDDB extends AsyncTask<String, Integer, Product> {

        @Override
        protected Product doInBackground(String... asin) {
            Product p=new Product();
            // TODO Auto-generated method stub
            try {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        mContext,
                        "ap-northeast-2:bdb4b618-47d6-4f17-ba74-e8e5009b6ba2", // Identity Pool ID
                        Regions.AP_NORTHEAST_2 // Region
                );

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                ddbClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
                final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                CognitoSyncManager syncClient = new CognitoSyncManager(
                        mContext,
                        Regions.AP_NORTHEAST_2, // Region
                        credentialsProvider);

                String queryString = "Great";
                p = mapper.load(Product.class, asin[0].toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }

        protected void onPostExecute(Product p) {
            //txt1.setText("A");
            //txt2.setText(p.getAsin());
        }
    }
}
