package com.ra.sowmya.findfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sowmya on 4/5/17.
 */

public class ServerDB {

    private final static String REG_URL = "http://78.46.121.79/API/reguser.php";
    private final static String LOG_URL = "http://78.46.121.79/API/loguser.php";
    private final static String GET_USERS_URL = "http://78.46.121.79/API/getfriends.php";
    private final static String UPDATE_LOC_URL = "http://78.46.121.79/API/updateGEO.php";
    private final static String GET_LOC_URL = "http://78.46.121.79/API/getlocation.php";
    static final String TAG="Myproject";

    public static void REG_USER(String Username, String Password, String Email, String Phone, Context ctx) {

        class RegisterUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s.equalsIgnoreCase("1")){
                    Intent intent = new Intent(ctx.getApplicationContext(),Login.class);
                    ctx.startActivity(intent);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String response = "";
                String sql="";
                try {
                    url = new URL(REG_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

//            Preparing sql query
                    sql="username="+Username+"&password="+Password+"&phone="+Phone+"&email="+Email;

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(sql);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                        Toast.makeText(ctx.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        response = "1";
                    }
                    else {
                        response="Error Registering";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        }
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }




    public static void LOG_USER(String Password, String Phone, Context ctx) {

        class LoginUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s.equalsIgnoreCase("1")){

                    //Shared Preference
                    SharedPreferences sharedPreferences = ctx.getSharedPreferences("MyData", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login",Phone);
                    editor.commit();

                    Intent i= new Intent(ctx.getApplicationContext(),Addfriends.class);
                    i.putExtra("Phone",Phone);
                    ctx.startActivity(i);
                }else{
                    Toast.makeText(ctx.getApplicationContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String response = "";
                String sql="";
                try {
                    url = new URL(LOG_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

//            Preparing sql query
                    sql="password="+params[0]+"&phone="+params[1];

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(sql);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                    }
                    else {
                        response="Error Registering";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        }
        LoginUser ru = new LoginUser();
        ru.execute(Password,Phone);
    }


    public static String SantString(String Phonenumber){
        String temp=Phonenumber.replaceAll("[-+.^: ,()]","");
        if(temp.length()==10){
            return temp;
        }else{
            String x="";
            for (int i = 2; i <temp.length() ; i++) {
                x+=temp.charAt(i);
            }
            return x;
        }
    }


    public static void GET_ALL_USERS(Context ctx, Hashtable<String,String> mmp, ListView list) {

        Vector<String> vt= new Vector<String>();


        class GET_ALL extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                // Toast.makeText(ctx,s,Toast.LENGTH_LONG).show();
                try {
                    JSONArray arr= new JSONArray(s);
                    for(int i=0;i<arr.length();i++){
                        JSONObject obj=arr.getJSONObject(i);
                        String data=SantString(obj.getString("phone"));
                        // Toast.makeText(ctx,data,Toast.LENGTH_LONG).show();
                        if(mmp.containsKey(data)){
                            vt.add(mmp.get(data)+"\n"+data);
                        }
                    }
                } catch (JSONException e) {e.printStackTrace();}
                ListAdapter lta = new CustomList_contacts(ctx,vt);
                list.setAdapter(lta);

            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String response = "";
                String sql="";
                try {
                    url = new URL(GET_USERS_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    // conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

//            Preparing sql query
                    //sql="password="+params[0]+"&phone="+params[1];

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(sql);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                    }
                    else {
                        response="Error Registering";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        }
        GET_ALL ru = new GET_ALL();
        ru.execute();
    }



    public static void UPDATE_COORDINATES(String Phone, String Longitude, String Latitude, Context ctx) {

        class AddLocation extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String response = "";
                String sql="";
                try {
                    url = new URL(UPDATE_LOC_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

//            Preparing sql query
                    sql="phone="+Phone+"&lat="+Latitude+"&lon="+Longitude;

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(sql);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                    }
                    else {
                        response="Error updating location";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        }
        AddLocation ad = new AddLocation();
        ad.execute();
    }

    public static void GET_LOC(String Phone, Context ctx) {

        class GetLocation extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG,"In post execute of get location method");
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String x = jsonObject.getString("LAT")+" "+jsonObject.getString("LON");
                    Log.d(TAG,"***latlog**"+x);

                    Intent i= new Intent(ctx.getApplicationContext(),MapsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("coordinates",x);
                    ctx.startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                Log.d(TAG,"IN background service of gtelocation");
                URL url;
                String response = "";
                String sql="";
                try {
                    url = new URL(GET_LOC_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

//            Preparing sql query
                    sql="phone="+params[0];

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(sql);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                        Log.d(TAG,"response string "+response);
                    }
                    else {
                        response="Error Registering";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        }
        GetLocation ru = new GetLocation();
        ru.execute(Phone);
    }
}
