package com.jsy.wether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        list = new ArrayList<>();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            whether();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();


            }
        });
    }

    public void whether() throws IOException {
        /*for(int i=1;i<25;i++) {*/
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=j3VbMNWCQKFxaQ4nRw2%2BX4%2BGMdddZQerxp6RIpyU78DGfBiVwnHli4vXdpIn9ldST%2FXHZ6ahHUw16ieG7ynh7g%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("3", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode("20201102", "UTF-8")); /*15년 12월 1일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0800", "UTF-8")); /*06시 발표(정시단위)*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("97", "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("74", "UTF-8")); /*예보지점 Y 좌표*/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
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
            //System.out.println(sb.toString());
            Log.i("myfirst", sb.toString());
            jParsing(sb.toString());
            //Log.i("mytag",sb.toString());
        }
    /*}*/
    void jParsing(String data){

        try {
            //JSONObject jobj = new JSONObject(data);
            //JSONObject jobj1 = jobj.getJSONObject("getVilageFcst");
            String data1 = data.substring(data.indexOf('['),data.indexOf(']')+1);
            StringBuffer result = new StringBuffer();

            JSONArray jArray = new JSONArray(data1);
            //Log.i("mytag",jArray.length()+"");
            for(int i=0;i<jArray.length();i++){
                JSONObject jobj2 = jArray.getJSONObject(i);

               /*if(jobj2.isNull("POP")){*//*Log.i("mytag","POP없다");*//*}
                else{
                    String baseDate = jobj2.getString("POP");
                    result.append(baseDate+" @ ");
                    Log.i("mycheck",baseDate);
                }
                if(jobj2.isNull("PTY")){Log.i("mytag","PTY없다");}
                else{
                    String baseDate = jobj2.getString("PTY");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("R06")){*//*Log.i("mytag","R06없다");*//*}
                else{
                    String baseDate = jobj2.getString("R06");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("REH")){*//*Log.i("mytag","REH없다");*//*}
                else{
                    String baseDate = jobj2.getString("REH");

                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("S06")){*//*Log.i("mytag","S06없다");*//*}
                else{
                    String baseDate = jobj2.getString("S06");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("SKY")){*//*Log.i("mytag","SKY없다");*//*}
                else{
                    String baseDate = jobj2.getString("SKY");
                    result.append(baseDate+" @ ");
                }*/
                if(jobj2.isNull("T3H")){Log.i("mytag","T3H없다");}
                else{
                    String baseDate = jobj2.getString("T3H");
                    result.append(baseDate+" @ ");
                }
                /*if(jobj2.isNull("TMX")){*//*Log.i("mytag","TMX없다");*//*}
                else{
                    String baseDate = jobj2.getString("TMX");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("UUU")){*//*Log.i("mytag","UUU없다");*//*}
                else{
                    String baseDate = jobj2.getString("UUU");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("VVV")){*//*Log.i("mytag","VVV없다");*//*}
                else{
                    String baseDate = jobj2.getString("VVV");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("WAV")){*//*Log.i("mytag","WAV없다");*//*}
                else{
                    String baseDate = jobj2.getString("WAV");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("VEC")){*//*Log.i("mytag","VEC없다");*//*}
                else{
                    String baseDate = jobj2.getString("VEC");
                    result.append(baseDate+" @ ");
                }
                if(jobj2.isNull("WSD")){*//*Log.i("mytag","WSD없다");*//*}
                else{
                    String baseDate = jobj2.getString("WSD");
                    result.append(baseDate+" @ ");
                }*/
                /* String rainPer = jobj2.getString("POP");
                String rainCo = jobj2.getString("PTY");
                String rainSix  = jobj2.getString("R06");
                String humid = jobj2.getString("REH");
                String snowSix = jobj2.getString("S06");
                String sky = jobj2.getString("SKY");
                String morTem = jobj2.getString("T3H");
                String dayTem = jobj2.getString("TMX");
                String ewWind = jobj2.getString("UUU");
                String snWind = jobj2.getString("VVV");
                String wav  = jobj2.getString("WAV");
                String winDir = jobj2.getString("VEC");
                String winPo = jobj2.getString("WSD");*/
                /*Log.i("mytag",rainPer+" "+rainCo+" "+rainSix+" "+humid+" "+snowSix+" "+sky+" "+morTem+" "+dayTem+" "+ewWind+" "+snWind+" "+wav+" "+winDir+" "+winPo );
                list.add(rainPer+" @ "+rainCo+" @ "+rainSix+" @ "+humid+" @ "+snowSix+" @ "+sky+" @ "+morTem+" @ "+dayTem+" @ "+ewWind+" @ "+snWind+" @ "+wav+" @ "+winDir+" @ "+winPo);*/
                list.add(result.toString());
                Log.i("myresult",list.toString());
            }

        }catch (Exception e){ Log.i("myexception",e.getLocalizedMessage());}

    }


}