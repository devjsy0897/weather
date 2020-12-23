package com.jsy.wheather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.InterruptedByTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;

    ArrayList<String> list;
    TextView tvdeg,tvcom1;
    Button btndeg,btncome,btnvlist;
    SQLiteDatabase sqlDB;
    myDBHelper myHelper;
    Date date;
    String locnx;
    String locny;

    private TextView tvloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("weather");

        tvdeg = (TextView)findViewById(R.id.tvdeg);
        tvcom1 = (TextView)findViewById(R.id.tvcom1);

        btndeg = (Button)findViewById(R.id.btndeg);
        btncome = (Button)findViewById(R.id.btncome);
        btnvlist = (Button)findViewById(R.id.btnvlist);
        list = new ArrayList<>();
        myHelper = new myDBHelper(this);
        date = new Date(System.currentTimeMillis());

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


        //새로고침 버튼 눌렀을 때↓
        btncome.setOnClickListener(new View.OnClickListener() {

            //인터넷 연결을 위한 쓰레드↓
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB,1,2);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            weather();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();


            }
            //인터넷 연결을 위한 쓰레드↑
        });
        //새로고침 버튼 눌렀을 때↑

        btndeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                SimpleDateFormat time = new SimpleDateFormat("HHmm");

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
                    cursor = sqlDB.rawQuery("Select * from village where fcstDate=baseDate and fcstTime=1500 and category='T3H';", null);
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

                }

            //}
        });


        //광고↓
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        //광고↑




    }
    public void weather() throws IOException {
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMdd");
        Log.i("timetest",Integer.parseInt(fourteen_format.format(date))+"");
        for(int i=1;i<150;i++) {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=j3VbMNWCQKFxaQ4nRw2%2BX4%2BGMdddZQerxp6RIpyU78DGfBiVwnHli4vXdpIn9ldST%2FXHZ6ahHUw16ieG7ynh7g%3D%3D"); /*Service Key*/
            //urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(i+"", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
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
                    //data1 = data1.replace("{/","}#");
                    //data1+="#";
                    StringBuffer result = new StringBuffer();
                    String[] dataArray=null;

                    JSONArray jArray = new JSONArray(data1);
                    Log.i("jtest",jArray.length()+"");
                    //Log.i("mytag11",jArray+"");

            /*JSONObject root = (JSONObject)new JSONTokener(data).nextValue();
            JSONArray array = new JSONArray(root.getString("response"));
            String category = array.getJSONObject(0).getJSONObject("body").getString("baseDate");*/

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);  // JSONObject 추출
                        int bdate = jObject.getInt("baseDate");
                        int btime = jObject.getInt("baseTime");
                        String cat = jObject.getString("category");
                        int fdate = jObject.getInt("fcstDate");
                        int ftime = jObject.getInt("fcstTime");
                        String fval = jObject.getString("fcstValue");
                        int nx = jObject.getInt("nx");
                        int ny = jObject.getInt("ny");

                        result.append(
                                "기준날짜 : " + bdate +
                                        ", 기준시간 : " + btime +
                                        ", 카테고리 : " + cat +
                                        " , 목표날짜 : " + fdate +
                                        " , 목표시간 : " + ftime +
                                        " , 목푯값 : " + fval +
                                        " , x축 : " + nx +
                                        " , y축 : " + ny

                        );
                        Log.i("mytag13", result + "");
                        sqlDB = myHelper.getWritableDatabase();
                        sqlDB.execSQL("insert into village(baseDate, baseTime, category, fcstDate, fcstTime, fcstValue, nx,ny) " +
                                "VALUES ('" + bdate + "','" + btime + "','" + cat + "','" + fdate + "','" + ftime + "','" + fval + "','" + nx + "','" + ny + "');");
                        sqlDB.close();
                        Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();
                    }
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
            String[] locarray = sendText.split("\\s");
            Log.i("splittest",locarray[1]+"");
            String level1 = locarray[1];
            String level2 = locarray[2];
            String level3 = locarray[3];
            Cursor cursor2;
            sqlDB = myHelper.getWritableDatabase();
            cursor2 = sqlDB.rawQuery("Select nx,ny from vlist where level1='"+level1+"' and level2='"+level2+"' and level3='"+level3+"';", null);

            while(cursor2.moveToNext()) {
                locnx = cursor2.getString(0);
                locny = cursor2.getString(1);
                Log.i("nxtest", locnx);
                Log.i("nxtest",locny);
            }
            cursor2.close();
            sqlDB.close();
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