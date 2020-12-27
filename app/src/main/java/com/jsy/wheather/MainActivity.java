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
import android.widget.Button;
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
    TextView tvdeg,tvcom1,tvcloth,tvupdate;
    Button /*btncome,*/btnvlist;
    SQLiteDatabase sqlDB,sqlDB2;
    myDBHelper myHelper,myHelper2;
    Date date,date1;
    String locnx;
    String locny;

    private TextView tvloc;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("weather");

        tvdeg = (TextView)findViewById(R.id.tvdeg);
        tvcom1 = (TextView)findViewById(R.id.tvcom1);
        tvcloth = (TextView)findViewById(R.id.tvcloth);
        tvupdate = (TextView)findViewById(R.id.tvupdate);

        //btncome = (Button)findViewById(R.id.btncome);
        btnvlist = (Button)findViewById(R.id.btnvlist);

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
                }else if(Integer.parseInt(time.format(date))<600){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=300 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }


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
                }else if(Integer.parseInt(time.format(date))<900){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=600 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }


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
                }else if(Integer.parseInt(time.format(date))<1200){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=900 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }


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
                }else if(Integer.parseInt(time.format(date))<1500){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1200 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }


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
                }else if(Integer.parseInt(time.format(date))<1800){
                    Log.i("timetest1","1");
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='T3H';", null);
                    String fvale = "";

                    while (cursor.moveToNext()){
                        fvale += cursor.getString(5);
                        Log.i("fvaletest",fvale);
                    }


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



    //온도 꺼내오기 함수 ↑

    public void weather() throws IOException {
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMdd");
        Log.i("timetest",Integer.parseInt(fourteen_format.format(date))+"");
        for(int i=1;i<10;i++) {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"); /*URL*/
            //urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(i+"", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(fourteen_format.format(date)+"", "UTF-8")); /*15년 12월 1일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0200", "UTF-8")); /*06시 발표(정시단위)*/
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