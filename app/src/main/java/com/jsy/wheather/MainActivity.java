package com.jsy.wheather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;

    ArrayList<String> list;
    TextView tvdeg,tvcom1,tvcloth,tvupdate,tvcloud;
    //Button /*btncome,*/btnvlist;
    SQLiteDatabase sqlDB,sqlDB2;
    myDBHelper myHelper,myHelper2;
    Date date,date1;
    String locnx;
    String locny;
    int fsky=0;
    private TextView tvloc;
    AdView adView;
    ImageView ivcloud;
    ImageButton btnvlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("weather");

        tvdeg = (TextView)findViewById(R.id.tvdeg);
        tvcom1 = (TextView)findViewById(R.id.tvcom1);
        tvcloth = (TextView)findViewById(R.id.tvcloth);
        tvupdate = (TextView)findViewById(R.id.tvupdate);
        tvcloud = (TextView)findViewById(R.id.tvcloud);


        //btncome = (Button)findViewById(R.id.btncome);
        //btnvlist = (Button)findViewById(R.id.btnvlist);
        btnvlist = (ImageButton) findViewById(R.id.btnvlist);

        list = new ArrayList<>();
        myHelper = new myDBHelper(this);
        myHelper2 = new myDBHelper(this);
        date = new Date(System.currentTimeMillis());
        date1 = new Date(System.currentTimeMillis());

        tvloc = (TextView)findViewById(R.id.tvloc);

        //지역선택 버튼 눌렀을 때 ↓
        btnvlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivityForResult(intent,REQUEST_CODE);

            }
        });
        //지역선택 버튼 눌렀을 때 ↑


        /*//새로고침 버튼 눌렀을 때↓
        btncome.setOnClickListener(new View.OnClickListener() {

            //인터넷 연결을 위한 쓰레드↓
            @Override
            public void onClick(View v) {
                tvupdate.setText("새로고침 중입니다...(약 3초 내외)");
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB,1,2);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            weather();
                            Log.i("loadingtest","weather함수 끝");
                            deg();
                            Log.i("loadingtest","deg함수 끝");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });


                t.start();



            }
            //인터넷 연결을 위한 쓰레드↑
        });
        //새로고침 버튼 눌렀을 때↑*/

        //온도 꺼내오기 버튼 눌렀을 때 ↓
        /*btndeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                SimpleDateFormat time = new SimpleDateFormat("HHmm");*/

                /*
                시간 알고리즘
                Base_time : 200, 500, 800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
                현재시간<200 -> where fcstDate-1 and 2300=fcstTime
                현재시간<500 -> where 200=fcstTime
                현재시간<800 -> where 500=fcstTime
                현재시간<1100 -> where 800=fcstTime
                현재시간<1400 -> where 1100=fcstTime
                현재시간<1700 -> where 1400=fcstTime
                현재시간<2000 -> where 1700=fcstTime
                현재시간<2300 -> where 2000=fcstTime
                 */

                //if(Integer.parseInt(time.format(date))<1700){
                    /*cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }



                    tvdeg.setText(fvale);
                    if(Integer.parseInt(fvale)<0) {
                        tvcom1.setText("영하에요!");
                    }else if(Integer.parseInt(fvale)<9){
                        tvcom1.setText("추워요!");
                    }else if(Integer.parseInt(fvale)<15){
                        tvcom1.setText("쌀쌀해요!");
                    }else if(Integer.parseInt(fvale)<20){
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                //}
                }


        });*/
        //온도 꺼내오기 버튼 눌렀을 때 ↑

        //광고↓
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //광고↑




    }

    //온도 꺼내오기 함수 ↓
            public void deg() {
                runOnUiThread(new Runnable() {
                                  public void run() {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                SimpleDateFormat time = new SimpleDateFormat("HHmm");

                /*
                시간 알고리즘
                Base_time : 200, 500, 800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
                현재시간<300 -> where fcstDate and 0000=fcstTime
                현재시간<600 -> where 300=fcstTime
                현재시간<900 -> where 600=fcstTime
                현재시간<1200 -> where 900=fcstTime
                현재시간<1500 -> where 1200=fcstTime
                현재시간<1800 -> where 1500=fcstTime
                현재시간<2100 -> where 1800=fcstTime
                현재시간<0000 -> where fcstDate-1 and 2100=fcstTime
                 */
            Log.i("timetest1",time.format(date));
            //시간 알고리즘 ↓
                if(Integer.parseInt(time.format(date))<300){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=0 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }

                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<600){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=300 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }

                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<900){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=600 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }

                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<1200){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=900 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑


                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }
                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<1500){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1200 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest fsky",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"º");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<1800){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);

                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑



                SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<2100){
                    Log.i("timetest1","2");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1800 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);

                    }

                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }else if(Integer.parseInt(time.format(date))<2400){
                    Log.i("timetest1","3");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=2100 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }

                    //구름 정도 select 해서 화면에 뿌려줌 ↓
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);

                    while (cursor.moveToNext()){
                        fsky += cursor.getInt(5);
                        Log.i("fvaletest",fsky+"");
                        switch (fsky){
                            case 1: tvcloud.setText("맑음");
                                ivcloud.setImageResource(R.drawable.sunny2);
                                break;
                            case 3: tvcloud.setText("구름 많음");
                                ivcloud.setImageResource(R.drawable.littlecloud);
                                break;
                            case 4: tvcloud.setText("흐림");
                                ivcloud.setImageResource(R.drawable.cloud);
                                break;
                        }
                    }
                    //구름 정도 select 해서 화면에 뿌려줌 ↑

                    SimpleDateFormat time1 = new SimpleDateFormat("MM.dd. HH:mm");
                    tvupdate.setText("최근 업데시트 시간 : "+(time1.format(date1)));
                    String upcolor = "#000000";
                    tvupdate.setTextColor(Color.parseColor(upcolor));
                    if(Integer.parseInt(fvale)<0) {
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("영하에요!");
                        tvcloth.setText("두꺼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<9){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("추워요!");
                        tvcloth.setText("따뜻한 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<15){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("쌀쌀해요!");
                        tvcloth.setText("가벼운 외투가 필요해요");
                    }else if(Integer.parseInt(fvale)<20){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("선선해요!");
                    }else if(Integer.parseInt(fvale)<25){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("따뜻해요!");
                    }else if(Integer.parseInt(fvale)<30){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("약간 더워요!");
                    }else if(30<=Integer.parseInt(fvale)){
                        tvdeg.setText(fvale+"℃");
                        tvcom1.setText("더워요!");
                    }
                    else{
                        tvcom1.setText("무언가 오류!");
                    }

                    cursor.close();
                    sqlDB.close();
                }
                //시간 알고리즘 ↑
                                  }
                });
                }



    //온도 꺼내오기 함수 ↑

    public void weather() throws IOException {
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMdd");
        int ftdate = Integer.parseInt(fourteen_format.format(date));
        Log.i("timetest",Integer.parseInt(fourteen_format.format(date))+"");
        Log.i("timetest",ftdate+"ftdate");
        SimpleDateFormat time = new SimpleDateFormat("HHmm");
        Log.i("timetest3",time.format(date));
        String btime=null;
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
        if(0000<=Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<900){
            btime = "1700";
            ftdate -= 1;
        }
        if(900<=Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<2100){
            btime = "0500";
        }else if(2100<=Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<2400){
            btime = "1700";
        }

        for(int i=1;i<25;i++) {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"); /*URL*/
            //urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(i+"", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(ftdate+"", "UTF-8")); /*15년 12월 1일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(btime+"", "UTF-8")); /*06시 발표(정시단위)*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(locnx+"", "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(locny+"", "UTF-8")); /*예보지점 Y 좌표*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            //Log.i("myfirst", sb.toString());
            jParsing(sb.toString());
            Log.i("mytag11",sb.toString());
        }
    }



    void jParsing(String data){

        try {
            //JSONObject jobj = new JSONObject(data);
            //JSONObject jobj1 = jobj.getJSONObject("getVilageFcst");


                    String data1 = data.substring(data.indexOf('['), data.indexOf(']') + 1);
                    String[] dataArray=null;
            Log.i("datatest",data1);
                    for(int jj=0;jj<10;jj++) {
                        data1 = data1.replaceFirst("baseDate","!"+jj);
                        data1 = data1.replaceFirst("baseTime","@"+jj);
                        data1 = data1.replaceFirst("category","#"+jj);
                        data1 = data1.replaceFirst("fcstDate","|"+jj);
                        data1 = data1.replaceFirst("fcstTime","?"+jj);
                        data1 = data1.replaceFirst("fcstValue","&"+jj);
                        data1 = data1.replaceFirst("nx","[*]"+jj);
                        data1 = data1.replaceFirst("ny","~"+jj);
                    }
                    Log.i("datatest1",data1);
                    data1 = data1.replace("!","baseDate");
                    data1 = data1.replace("@","baseTime");
                    data1 = data1.replace("#","category");
                    data1 = data1.replace("|","fcstDate");
                    data1 = data1.replace("?","fcstTime");
                    data1 = data1.replace("&","fcstValue");
                    data1 = data1.replace("[*]","nx");
                    data1 = data1.replace("~","ny");
                    Log.i("datatest2",data1);
                    /*for(int jjj=1;jjj<11;jjj++) {
                        dataArray[jjj] =data1.substring(data1.indexOf(jjj+"{"), data1.indexOf("}"+jjj) + 1);
                        Log.i("datatest1",dataArray[jjj]+"");
                    }*/

                        StringBuffer result = new StringBuffer();


                            JSONArray jArray = new JSONArray(data1);
                            Log.i("jtest", jArray.length() + "");
                            Log.i("mytag12", jArray + "");

            /*JSONObject root = (JSONObject)new JSONTokener(data).nextValue();
            JSONArray array = new JSONArray(root.getString("response"));
            String category = array.getJSONObject(0).getJSONObject("body").getString("baseDate");*/

            int[] bdate = new int[10];
            int[] btime = new int[10];
            int[] fdate = new int[10];
            int[] ftime = new int[10];
            int[] nx = new int[10];
            int[] ny = new int[10];
            String[] cat = new String[10];
            String[] fval = new String[10];
                    for (int i = 0; i < 10; i++) {
                        JSONObject jObject = jArray.getJSONObject(i);  // JSONObject 추출
                        bdate[i] = jObject.getInt("baseDate"+i);
                        btime[i] = jObject.getInt("baseTime"+i);
                        cat[i] = jObject.getString("category"+i);
                        fdate[i] = jObject.getInt("fcstDate"+i);
                        ftime[i] = jObject.getInt("fcstTime"+i);
                        fval[i] = jObject.getString("fcstValue"+i);
                        nx[i] = jObject.getInt("nx"+i);
                        ny[i] = jObject.getInt("ny"+i);

                        result.append(
                                "기준날짜 : " + bdate[i] +
                                        ", 기준시간 : " + btime[i] +
                                        ", 카테고리 : " + cat[i] +
                                        " , 목표날짜 : " + fdate[i] +
                                        " , 목표시간 : " + ftime[i] +
                                        " , 목푯값 : " + fval[i] +
                                        " , x축 : " + nx[i] +
                                        " , y축 : " + ny[i]
                        );
                        Log.i("mytag13", i + "번 " + result + "");
                    }
                        sqlDB = myHelper.getWritableDatabase();
                    for(int i = 0;i<10;i++) {
                        sqlDB.execSQL("insert into village(baseDate, baseTime, category, fcstDate, fcstTime, fcstValue, nx,ny) " +
                                "VALUES ('" + bdate[i] + "','" + btime[i] + "','" + cat[i] + "','" + fdate[i] + "','" + ftime[i] + "','" + fval[i] + "','" + nx[i] + "','" + ny[i] + "');");
                    }
                        sqlDB.close();
                        //Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();


            //Log.i("mytag12",result.toString());

        }catch (Exception e){ Log.i("mytagcatch",e.getLocalizedMessage());}

    }
    // 시간대별 날씨 메서드 ↓
    public void timeweather(){
        runOnUiThread(new Runnable() {
            public void run() {
        TextView onel1 = (TextView)findViewById(R.id.onel1);
        TextView onel2 = (TextView)findViewById(R.id.onel2);
        TextView onel3 = (TextView)findViewById(R.id.onel3);
        TextView onel4 = (TextView)findViewById(R.id.onel4);
        TextView onel5 = (TextView)findViewById(R.id.onel5);
        TextView onel6 = (TextView)findViewById(R.id.onel6);
        TextView onel7 = (TextView)findViewById(R.id.onel7);
        TextView onel8 = (TextView)findViewById(R.id.onel8);
        TextView onel9 = (TextView)findViewById(R.id.onel9);
        TextView onel10 = (TextView)findViewById(R.id.onel10);
        TextView onel11 = (TextView)findViewById(R.id.onel11);
        TextView onel12 = (TextView)findViewById(R.id.onel12);
        TextView onel13 = (TextView)findViewById(R.id.onel13);
        TextView onel14 = (TextView)findViewById(R.id.onel14);
        TextView onel15 = (TextView)findViewById(R.id.onel15);
        TextView onel16 = (TextView)findViewById(R.id.onel16);
        TextView onel17 = (TextView)findViewById(R.id.onel17);
        TextView onel18 = (TextView)findViewById(R.id.onel18);
        TextView onel19 = (TextView)findViewById(R.id.onel19);

        TextView timewea1 = (TextView)findViewById(R.id.timewea1);
        TextView timewea2 = (TextView)findViewById(R.id.timewea2);
        TextView timewea3 = (TextView)findViewById(R.id.timewea3);
        TextView timewea4 = (TextView)findViewById(R.id.timewea4);
        TextView timewea5 = (TextView)findViewById(R.id.timewea5);
        TextView timewea6 = (TextView)findViewById(R.id.timewea6);
        TextView timewea7 = (TextView)findViewById(R.id.timewea7);
        TextView timewea8 = (TextView)findViewById(R.id.timewea8);
        TextView timewea9 = (TextView)findViewById(R.id.timewea9);
        TextView timewea10 = (TextView)findViewById(R.id.timewea10);
        TextView timewea11 = (TextView)findViewById(R.id.timewea11);
        TextView timewea12 = (TextView)findViewById(R.id.timewea12);
        TextView timewea13 = (TextView)findViewById(R.id.timewea13);
        TextView timewea14 = (TextView)findViewById(R.id.timewea14);
        TextView timewea15 = (TextView)findViewById(R.id.timewea15);
        TextView timewea16 = (TextView)findViewById(R.id.timewea16);
        TextView timewea17 = (TextView)findViewById(R.id.timewea17);
        TextView timewea18 = (TextView)findViewById(R.id.timewea18);
        TextView timewea19 = (TextView)findViewById(R.id.timewea19);

        ImageView timeimg1 = (ImageView)findViewById(R.id.timeimg1);
        ImageView timeimg2 = (ImageView)findViewById(R.id.timeimg2);
        ImageView timeimg3 = (ImageView)findViewById(R.id.timeimg3);
        ImageView timeimg4 = (ImageView)findViewById(R.id.timeimg4);
        ImageView timeimg5 = (ImageView)findViewById(R.id.timeimg5);
        ImageView timeimg6 = (ImageView)findViewById(R.id.timeimg6);
        ImageView timeimg7 = (ImageView)findViewById(R.id.timeimg7);
        ImageView timeimg8 = (ImageView)findViewById(R.id.timeimg8);
        ImageView timeimg9 = (ImageView)findViewById(R.id.timeimg9);
        ImageView timeimg10 = (ImageView)findViewById(R.id.timeimg10);
        ImageView timeimg11 = (ImageView)findViewById(R.id.timeimg11);
        ImageView timeimg12 = (ImageView)findViewById(R.id.timeimg12);
        ImageView timeimg13 = (ImageView)findViewById(R.id.timeimg13);
        ImageView timeimg14 = (ImageView)findViewById(R.id.timeimg14);
        ImageView timeimg15 = (ImageView)findViewById(R.id.timeimg15);
        ImageView timeimg16 = (ImageView)findViewById(R.id.timeimg16);
        ImageView timeimg17 = (ImageView)findViewById(R.id.timeimg17);
        ImageView timeimg18 = (ImageView)findViewById(R.id.timeimg18);
        ImageView timeimg19 = (ImageView)findViewById(R.id.timeimg19);

        TextView hour1 = (TextView)findViewById(R.id.hour1);
        TextView hour2 = (TextView)findViewById(R.id.hour2);
        TextView hour3 = (TextView)findViewById(R.id.hour3);
        TextView hour4 = (TextView)findViewById(R.id.hour4);
        TextView hour5 = (TextView)findViewById(R.id.hour5);
        TextView hour6 = (TextView)findViewById(R.id.hour6);
        TextView hour7 = (TextView)findViewById(R.id.hour7);
        TextView hour8 = (TextView)findViewById(R.id.hour8);
        TextView hour9 = (TextView)findViewById(R.id.hour9);
        TextView hour10 = (TextView)findViewById(R.id.hour10);
        TextView hour11 = (TextView)findViewById(R.id.hour11);
        TextView hour12 = (TextView)findViewById(R.id.hour12);
        TextView hour13 = (TextView)findViewById(R.id.hour13);
        TextView hour14 = (TextView)findViewById(R.id.hour14);
        TextView hour15 = (TextView)findViewById(R.id.hour15);
        TextView hour16 = (TextView)findViewById(R.id.hour16);
        TextView hour17 = (TextView)findViewById(R.id.hour17);
        TextView hour18 = (TextView)findViewById(R.id.hour18);
        TextView hour19 = (TextView)findViewById(R.id.hour19);

        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout2);
        LinearLayout layout3 = (LinearLayout)findViewById(R.id.layout3);
        LinearLayout layout4 = (LinearLayout)findViewById(R.id.layout4);
        LinearLayout layout5 = (LinearLayout)findViewById(R.id.layout5);
        LinearLayout layout6 = (LinearLayout)findViewById(R.id.layout6);
        LinearLayout layout7 = (LinearLayout)findViewById(R.id.layout7);
        LinearLayout layout8 = (LinearLayout)findViewById(R.id.layout8);
        LinearLayout layout9 = (LinearLayout)findViewById(R.id.layout9);
        LinearLayout layout10 = (LinearLayout)findViewById(R.id.layout10);
        LinearLayout layout11 = (LinearLayout)findViewById(R.id.layout11);
        LinearLayout layout12 = (LinearLayout)findViewById(R.id.layout12);
        LinearLayout layout13 = (LinearLayout)findViewById(R.id.layout13);
        LinearLayout layout14 = (LinearLayout)findViewById(R.id.layout14);
        LinearLayout layout15 = (LinearLayout)findViewById(R.id.layout15);
        LinearLayout layout16 = (LinearLayout)findViewById(R.id.layout16);
        LinearLayout layout17 = (LinearLayout)findViewById(R.id.layout17);
        LinearLayout layout18 = (LinearLayout)findViewById(R.id.layout18);
        LinearLayout layout19 = (LinearLayout)findViewById(R.id.layout19);


                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout1.getHeight());
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout2.getHeight());
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout3.getHeight());
                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout4.getHeight());
                LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout5.getHeight());
                LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout6.getHeight());
                LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout7.getHeight());
                LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout8.getHeight());
                LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout9.getHeight());
                LinearLayout.LayoutParams params10 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout10.getHeight());
                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout11.getHeight());
                LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout12.getHeight());
                LinearLayout.LayoutParams params13 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout13.getHeight());
                LinearLayout.LayoutParams params14 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout14.getHeight());
                LinearLayout.LayoutParams params15 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout15.getHeight());
                LinearLayout.LayoutParams params16 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout16.getHeight());
                LinearLayout.LayoutParams params17 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout17.getHeight());
                LinearLayout.LayoutParams params18 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout18.getHeight());
                LinearLayout.LayoutParams params19 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, layout19.getHeight());
                params1.width = 200;
                params2.width = 200;
                params3.width = 200;
                params4.width = 200;
                params5.width = 200;
                params6.width = 200;
                params7.width = 200;
                params8.width = 200;
                params9.width = 200;
                params10.width = 200;
                params11 .width = 200;
                params12 .width = 200;
                params13 .width = 200;
                params14 .width = 200;
                params15 .width = 200;
                params16 .width = 200;
                params17 .width = 200;
                params18 .width = 200;
                params19 .width = 200;





                layout6.setLayoutParams(params6);
                layout7.setLayoutParams(params7);
                layout8.setLayoutParams(params8);
                layout9.setLayoutParams(params9);
                layout10.setLayoutParams(params10);
                layout11.setLayoutParams(params11);
                layout12.setLayoutParams(params11);
                layout13.setLayoutParams(params11);
                layout14.setLayoutParams(params11);
                layout15 .setLayoutParams(params11);
                layout16 .setLayoutParams(params11);
                layout17 .setLayoutParams(params11);
                layout18 .setLayoutParams(params11);
                layout19 .setLayoutParams(params11);
        //

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        //SimpleDateFormat time = new SimpleDateFormat("HHmm");
        SimpleDateFormat time = new SimpleDateFormat("HH");
        SimpleDateFormat time1 = new SimpleDateFormat("YYYYMMdd");
        Log.i("timetimetest",Integer.parseInt(time.format(date))+"");
        Log.i("timetimetest",Integer.parseInt(time1.format(date))+"");
        String fvale = "";
        String fvale1 = "";
        String t3hval = "";
        String skyval = "";
        String ftime = "";
        int onel=0;
        //시간, 온도 ↓
        cursor = sqlDB.rawQuery("Select * from village where category='T3H';", null);
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                Log.i("valtest",t3hval);
                Log.i("datetimetest",Integer.parseInt(time.format(date))+"");
                Log.i("datetimetest",Integer.parseInt(ftime)+"");
                //if((600<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<859)||(1800<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<2059)) {
                ftime = ftime.replace("00", "");
                switch (Integer.parseInt(time.format(date))) {
                    case 9:
                    case 10:
                    case 11:

                    /*layout1.setLayoutParams(params1);



                    if (onel == 0 && Integer.parseInt(ftime) < 22) {
                        onel1.setText("오늘");
                        onel++;
                    } else if (ftime == "0") {
                        if (onel == 0) {
                            onel2.setText("내일");
                            onel += 2;
                        } else if (onel == 2) {
                            onel2.setText("모레");
                        }
                    }
                    Log.i("oneltest", onel + "");
                    hour1.setText(ftime + "시1");
                    timewea1.setText(t3hval + "º");*/
                        //}
                        cursor.moveToNext();
                        ftime = cursor.getString(4);
                        t3hval = cursor.getString(5);
                        //if((900<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<1159)||(21<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<1159)) {
                        layout2.setLayoutParams(params2);
                        ftime = ftime.replace("00", "");

                        if (onel == 0 && Integer.parseInt(ftime) < 22) {
                            onel2.setText("오늘");
                            onel++;
                        } else if (ftime == "0") {
                            if (onel == 1) {
                                onel2.setText("내일");
                                onel++;
                            } else if (onel == 2) {
                                onel2.setText("모레");
                            }
                        }
                        hour2.setText(ftime + "시2");
                        timewea2.setText(t3hval + "º");
                        //}
                    case 12:
                    case 13:
                    case 14:
                        if(12<=Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<15){
                            cursor.moveToNext();
                        }
                        cursor.moveToNext();
                        ftime = cursor.getString(4);
                        t3hval = cursor.getString(5);

                        //if((0<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<259)||(1200<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<1459)) {
                        layout3.setLayoutParams(params3);
                        ftime = ftime.replace("00", "");
                        if (onel == 0 && Integer.parseInt(ftime) < 22) {
                            onel3.setText("오늘");
                            onel++;
                        } else if (ftime == "0") {
                            if (onel == 1) {
                                onel3.setText("내일");
                                onel++;
                            } else if (onel == 2) {
                                onel3.setText("모레");
                            }
                        }
                        hour3.setText(ftime + "시3");
                        timewea3.setText(t3hval + "º");
                        //}
                        cursor.moveToNext();
                        ftime = cursor.getString(4);
                        t3hval = cursor.getString(5);
                        //if((3<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<559)||(1500<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<1759)) {
                        ftime = ftime.replace("00", "");
                        layout4.setLayoutParams(params4);
                        if (onel == 0 && Integer.parseInt(ftime) < 22) {
                            onel4.setText("오늘");
                            onel++;
                        } else if (ftime == "0") {
                            if (onel == 1) {
                                onel4.setText("내일");
                                onel++;
                            } else if (onel == 2) {
                                onel4.setText("모레");
                            }
                        }
                        hour4.setText(ftime + "시4");
                        timewea4.setText(t3hval + "º");
                        //}
                }
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                //if((6<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<859)||(1800<Integer.parseInt(time.format(date))&&Integer.parseInt(time.format(date))<2059)) {
                    ftime = ftime.replace("00", "");
                    layout5.setLayoutParams(params5);
                    if (onel == 0 && Integer.parseInt(ftime) < 22) {
                        onel5.setText("오늘");
                        onel++;
                    } else if (ftime == "0") {
                        if (onel == 1) {
                            onel5.setText("내일");
                            onel++;
                        } else if (onel == 2) {
                            onel5.setText("모레");
                        }
                    }
                    hour5.setText(ftime + "시5");
                    timewea5.setText(t3hval + "º");
                //}
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel6.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel6.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel6.setText("모레");
                    }
                }
                hour6.setText(ftime + "시6");
                timewea6.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel7.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel7.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel7.setText("모레");
                    }
                }
                hour7.setText(ftime + "시7");
                timewea7.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel8.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel8.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel8.setText("모레");
                    }
                }
                hour8.setText(ftime + "시8");
                timewea8.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel9.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel9.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel9.setText("모레");
                    }
                }
                hour9.setText(ftime + "시9");
                timewea9.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel10.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel10.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel10.setText("모레");
                    }
                }
                hour10.setText(ftime + "시");
                timewea10.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel11.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel11.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel11.setText("모레");
                    }
                }
                hour11.setText(ftime + "시");
                timewea11.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel12.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel12.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel12.setText("모레");
                    }
                }
                hour12.setText(ftime + "시");
                timewea12.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel13.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel13.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel13.setText("모레");
                    }
                }
                hour13.setText(ftime + "시");
                timewea13.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel14.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel14.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel14.setText("모레");
                    }
                }
                hour14.setText(ftime + "시");
                timewea14.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel15.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel15.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel15.setText("모레");
                    }
                }
                hour15.setText(ftime + "시");
                timewea15.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel16.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel16.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel16.setText("모레");
                    }
                }
                hour16.setText(ftime + "시");
                timewea16.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel17.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel17.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel17.setText("모레");
                    }
                }
                hour17.setText(ftime + "시");
                timewea17.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel18.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel18.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel18.setText("모레");
                    }
                }
                hour18.setText(ftime + "시");
                timewea18.setText(t3hval + "º");
            cursor.moveToNext();
                ftime = cursor.getString(4);
                t3hval = cursor.getString(5);
                ftime = ftime.replace("00", "");
                Log.i("oneltest ftime",ftime);
                if(onel==0&&Integer.parseInt(ftime)<22){
                    onel19.setText("오늘");
                    onel++;
                }else if(Integer.parseInt(ftime)==0) {
                    if (onel==1) {
                        onel19.setText("내일");
                        onel++;
                    }
                    else if(onel==2){
                        onel19.setText("모레");
                    }
                }
                hour19.setText(ftime + "시");
                timewea19.setText(t3hval + "º");
        //시간, 온도 ↑

        //날씨 그림 ↓
            cursor = sqlDB.rawQuery("Select * from village where category='SKY';", null);
            cursor.moveToFirst();
            if(hour1.getText().toString().contains("시")) {

                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg1.setImageResource(R.drawable.sunny2);
                        break;
                    case 3:
                        timeimg1.setImageResource(R.drawable.littlecloud);
                        break;
                    case 4:
                        timeimg1.setImageResource(R.drawable.cloud);
                        break;
                }
            }else{}
            cursor.moveToNext();
                if(hour2.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg2.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg2.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg2.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                if(hour3.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg3.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg3.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg3.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                if(hour4.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg4.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg4.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg4.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                if(hour5.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg5.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg5.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg5.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                if(hour6.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg6.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg6.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg6.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                if(hour7.getText().toString().contains("시")) {
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg7.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg7.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg7.setImageResource(R.drawable.cloud);break;
                }
                }else{}
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg8.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg8.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg8.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg9.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg9.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg9.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg10.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg10.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg10.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg11.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg11.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg11.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg12.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg12.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg12.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg13.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg13.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg13.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg14.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg14.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg14.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg15.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg15.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg15.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg16.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg16.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg16.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg17.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg17.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg17.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg18.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg18.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg18.setImageResource(R.drawable.cloud);break;
                }
            cursor.moveToNext();
                skyval = cursor.getString(5);
                switch (Integer.parseInt(skyval)) {
                    case 1:
                        timeimg19.setImageResource(R.drawable.sunny2);break;
                    case 3:
                        timeimg19.setImageResource(R.drawable.littlecloud);break;
                    case 4:
                        timeimg19.setImageResource(R.drawable.cloud);break;
                }
        //날씨 그림 ↑
        if(Integer.parseInt(time.format(date))<17){//시간은 맞게 바꿔야함.


            /*// 시작1 ↓
            onel1.setText("오늘");
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1200 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale += cursor.getString(5);
                Log.i("fvaletest",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1200 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour1.setText("12시");
            timewea1.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                timeimg1.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg1.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg1.setImageResource(R.drawable.cloud);break;
            }
            // 시작1 ↑
            // 시작2 ↓
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale = cursor.getString(5);
                Log.i("fvaletest 15 deg",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour2.setText("15시");
            timewea2.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                    timeimg2.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg2.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg2.setImageResource(R.drawable.cloud);break;
            }
            // 시작2 ↑
            // 시작3 ↓
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1800 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale = cursor.getString(5);
                Log.i("fvaletest 18 deg",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1800 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour3.setText("18시");
            timewea3.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                    timeimg3.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg3.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg3.setImageResource(R.drawable.cloud);break;
            }
            // 시작3 ↑
            // 시작4 ↓
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=2100 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale = cursor.getString(5);
                Log.i("fvaletest 18 deg",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=2100 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour4.setText("21시");
            timewea4.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                    timeimg4.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg4.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg4.setImageResource(R.drawable.cloud);break;
            }
            // 시작4 ↑
            // 시작5 ↓
            onel5.setText("내일");
            cursor = sqlDB.rawQuery("Select * from village where baseDate=fcstDate+1 and fcstTime=0000 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale = cursor.getString(5);
                Log.i("fvaletest 18 deg",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where baseDate=fcstDate+1 and fcstTime=0000 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour5.setText("00시");
            timewea5.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                    timeimg5.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg5.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg5.setImageResource(R.drawable.cloud);break;
            }
            // 시작5 ↑
            // 시작6 ↓
            cursor = sqlDB.rawQuery("Select * from village where baseDate=fcstDate+1 and fcstTime=0300 and category='T3H';", null);
            while (cursor.moveToNext()){
                fvale = cursor.getString(5);
                Log.i("fvaletest 18 deg",fvale);
            }
            cursor = sqlDB.rawQuery("Select * from village where baseDate=fcstDate+1 and fcstTime=0300 and category='SKY';", null);
            while (cursor.moveToNext()){
                fvale1 = cursor.getString(5);
                Log.i("fvaletest fvalue",fvale1);
            }
            hour6.setText("03시");
            timewea6.setText(fvale+"º");
            switch (Integer.parseInt(fvale1)) {
                case 1:
                    timeimg6.setImageResource(R.drawable.sunny2);break;
                case 3:
                    timeimg6.setImageResource(R.drawable.littlecloud);break;
                case 4:
                    timeimg6.setImageResource(R.drawable.cloud);break;
            }
            // 시작6 ↑*/
        }

        }
        });
    }
    // 시간대별 날씨 메서드 ↑

    //DB 생성 클래스↓ (쓰레드 직전에 호출함)
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context){
            super(context, "weather", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table village(baseDate Integer , baseTime Integer, category char, fcstDate Integer, fcstTime Integer, fcstValue char, nx Integer, ny Integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table if exists village");
            onCreate(db);
        }
    }
    public class myDBHelper2 extends SQLiteOpenHelper {
        public myDBHelper2(Context context){
            super(context, "weather", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table vlist(baseDate Integer , baseTime Integer, category char, fcstDate Integer, fcstTime Integer, fcstValue char, nx Integer, ny Integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table if exists vlist");
            onCreate(db);
        }
    }
    //DB 생성 클래스↑

    //intent로 지역 들고옴 ↓
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
ivcloud = (ImageView)findViewById(R.id.ivcloud);
        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            String sendText = data.getExtras().getString("result");

            Log.i("resulttest",sendText);
            tvloc.setText(sendText+"");
            tvcom1.setText("");
            String[] locarray = new String[4];
            locarray[0]="";
            locarray[1]="";
            locarray[2]="";
            locarray[3]="";
            locarray = sendText.split("\\s");
            Log.i("splittest",locarray.length+"");
            /*
            Log.i("splittest",locarray[0]+"0");
            Log.i("splittest",locarray[1]+"1");
            Log.i("splittest",locarray[2]+"2");
            Log.i("splittest",locarray[3]+"3");*/




            if (locarray.length==4) {

                Log.i("leveltest", "1번");
                Cursor cursor2;
                sqlDB = myHelper.getWritableDatabase();
                cursor2 = sqlDB.rawQuery("Select nx,ny from vlist where level1='" + locarray[1] + "' and level2='" + locarray[2] + "' and level3='" + locarray[3] + "';", null);


            while(cursor2.moveToNext()) {
                locnx = cursor2.getString(0);
                locny = cursor2.getString(1);
                Log.i("nxtest", locnx);
                Log.i("nxtest",locny);
            }
            cursor2.close();
            sqlDB.close();
            //자동 새로고침 ↓
                tvupdate.setText("새로고침 중입니다...(약 3초 내외)");
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB,1,2);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            weather();
                            Log.i("loadingtest","weather함수 끝");
                            deg();
                            Log.i("loadingtest","deg함수 끝");
                            /*switch (fsky){
                                case 1: tvcloud.setText("맑음");
                                    break;
                                case 3: tvcloud.setText("구름 많음");
                                    break;
                                case 4: tvcloud.setText("흐림");
                                    break;
                            }*/
                            timeweather();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                    }

                });

                t.start();

                //자동 새로고침 ↑
            }else if (locarray.length==3) {

                Log.i("leveltest", "2번");
                Cursor cursor2;
                sqlDB = myHelper.getWritableDatabase();
                cursor2 = sqlDB.rawQuery("Select nx,ny from vlist where level1='" + locarray[1] + "' and level2='" + locarray[2] + "' and level3 is null;", null);


                while(cursor2.moveToNext()) {
                    locnx = cursor2.getString(0);
                    locny = cursor2.getString(1);
                    Log.i("nxtest", locnx);
                    Log.i("nxtest",locny);
                }
                cursor2.close();
                sqlDB.close();
                //자동 새로고침 ↓
                tvupdate.setText("새로고침 중입니다...(약 3초 내외)");
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB,1,2);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            weather();
                            Log.i("loadingtest","weather함수 끝");
                            deg();
                            Log.i("loadingtest","deg함수 끝");

                            switch (fsky){
                                case 1: tvcloud.setText("맑음");
                                    break;
                                case 3: tvcloud.setText("구름 많음");
                                    break;
                                case 4: tvcloud.setText("흐림");
                                    break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });


                t.start();

                //자동 새로고침 ↑
            }else if (locarray.length==2) {

                Log.i("leveltest", "3번");
                Cursor cursor2;
                sqlDB = myHelper.getWritableDatabase();
                cursor2 = sqlDB.rawQuery("Select nx,ny from vlist where level1='" + locarray[1] + "' and level2 is null and level3 is null;", null);


                while(cursor2.moveToNext()) {
                    locnx = cursor2.getString(0);
                    locny = cursor2.getString(1);
                    Log.i("nxtest", locnx);
                    Log.i("nxtest",locny);
                }
                cursor2.close();
                sqlDB.close();
                //자동 새로고침 ↓
                tvupdate.setText("새로고침 중입니다...(약 3초 내외)");
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB,1,2);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            weather();
                            Log.i("loadingtest","weather함수 끝");
                            deg();
                            Log.i("loadingtest","deg함수 끝");

                            switch (fsky){
                                case 1: tvcloud.setText("맑음");
                                    break;
                                case 3: tvcloud.setText("구름 많음");
                                    break;
                                case 4: tvcloud.setText("흐림");
                                    break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });


                t.start();

                //자동 새로고침 ↑
            }else{
                Log.i("leveltest","삐빅 오류~~");
            }



        }
    }
    //intent로 지역 들고옴 ↑
}

/*
        POP	강수확률	%
        PTY	강수형태	코드값
        R06	6시간 강수량	범주 (1 mm)
        REH	습도	%
        S06	6시간 신적설	범주(1 cm)
        SKY	하늘상태	코드값
        T3H	3시간 기온	℃
        TMN	아침 최저기온	℃
        TMX	낮 최고기온	℃
        UUU	풍속(동서성분)	m/s
        VVV	풍속(남북성분)	m/s
        WAV	파고	M
        VEC	풍향	m/s
        WSD	풍속	m/s
*/