package com.zayditech.cricerzfantasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class GeneralMethods {
    private Context appContext = null;

    public GeneralMethods(Context _context) {
        appContext = _context;
    }
    public String[] playerNames = {"Babar Azam","Colin Ingram","Sharjeel Khan","Zeeshan Malik","Qasim Akram","Imad Wasim","Mohammad Nabi",
            "Daniel Christian","Danish Aziz","ChadWick Walton","Joe Clarke","Mohammad Amir","Aamer Yamin","Waqas Maqsood","Arshad Iqbal",
            "Mohammad Ilyas","Noor Ahmad","Chris Gayle","Usman Khan","Saim Ayub","Cameron Delport","Ben Cutting","Mohammad Nawaz",
            "Sarfaraz Ahmad","Azam Khan","Tom Banton","Mohammad Hasnain","Naseem Shah","Zahid Mahmood","Anwar Ali","Usman Shinwari",
            "Qais Ahmad","Dale Steyn","Abdul Nasir","Arish Ali Khan","Fakhar Zaman","Muhammad Zaid Alam","Sohail Akhtar","Mohammad Hafeez",
            "Joe Denly","David Wiese","Tom Abell","Samit Patel","Agha Salman","Ben Dunk","Zeeshan Ashraf","Muhammad Faizan","Rashid Khan",
            "Shaheen Afridi","Haris Rauf","Dibar Hussain","Maaz Khan","David Miller","Haider Ali","Liam Livingstone","Imam-ul-Haq",
            "Shoaib Malik","Sherfane Rutherford","Ravi Bopara","Amad Butt","Umaid Asif","Mohammad Imran","Kamran Akmal","Wahab Riaz",
            "Mujeeb Ur Rahman","Saqib Mahmood","Mohammad Irfan","Mohammad Aamir Khan","Abrar Ahmed","Alex Hales","Philip Salt","Asif Ali",
            "Colin Munro","Hussain Talat","Iftikhar Ahmad","Shadab Khan","Lewis Gregory","Faheem Ashraf","Akif Javed","Mohammad Wasim Jr",
            "Rohail Nazir","Hasan Ali","Muhammad Musa","Zafar Gohar","Reece Topley","Ahmad Safi Abdullah","Chris Jordan","Rilee Rossouw",
            "Khushdil Shah","James Vince","Chris Lynn","Sohaib Maqsood","Adam Lyth","Shan Masood","Usman Qadir","Shahid Afridi",
            "Carlos Brathwaite","Mohammad Rizwan","Sohail Tanvir","Imran Tahir","Sohail Khan","Sohaibullah","Shahnawaz Dhani","Imran Khan"};
    String hitAPI(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output= "";
            while (output.isEmpty()) {
                output += br.readLine();
            }
            conn.disconnect();
            return output;
        } catch (Exception e) {
            return "Exception in NetClientGet:- " + e;
        }
    }
    JSONArray removeDuplicatesFromJSON(JSONArray yourJSONArray) throws JSONException {
        Set<String> stationCodes =new HashSet<String>();
        JSONArray tempArray=new JSONArray();
        try {
            for(int i=0;i<yourJSONArray.length();i++){
                String  stationCode=yourJSONArray.getJSONObject(i).getString("name");
                if(stationCodes.contains(stationCode)){
                    continue;
                }
                else {
                    stationCodes.add(stationCode);
                    tempArray.put(yourJSONArray.getJSONObject(i));
                }
            }
        }
        catch (Exception ex) {
            return new JSONArray("[{\"ex\":"+ex.getCause() + "\n" + ex.getMessage()+"]");
        }
        return tempArray;
    }
    public String encodeIntoBase64(String dataToEncode) {
        final byte[] authBytes = dataToEncode.getBytes(StandardCharsets.UTF_8);
        final String  encoded = android.util.Base64.encodeToString(authBytes, Base64.NO_WRAP);
        return  encoded;
    }

    public String decodeData(String dataToDecode) throws UnsupportedEncodingException {
        byte[] dStr = android.util.Base64.decode(dataToDecode, Base64.NO_WRAP);
        String dataString = new String(dStr, "UTF-8");
        return dataString;
    }

    JSONArray updateJsonArray(JSONArray jsonArray, JSONObject updatedObject) throws JSONException {
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(jsonObject.equals(updatedObject)) {
                tempArray.put(updatedObject);
            }
            else {
                tempArray.put(jsonObject);
            }
        }
        return tempArray;
    }

    public int findInJSONArray(JSONArray jsonArray, String Title) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(jsonObject.getString("name").equals(Title)) {
                return i;
            }
        }
        return -1;
    }

    public JSONArray compareAndAddToArray(JSONArray jsonArray, JSONArray arr) throws JSONException {
        JSONArray tempArr = new JSONArray();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject comparingObj = arr.getJSONObject(i);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objToCompareWith = jsonArray.getJSONObject(j);
                if(objToCompareWith.getString("pid").equals(comparingObj.getString("pid"))) {
                    JSONObject tempObj = objToCompareWith;
                    tempObj.put("isSelected", true);
                    tempArr.put(tempObj);
                }
                else {
                    tempArr.put(objToCompareWith);
                }
            }
        }
        return tempArr;
    }
}
