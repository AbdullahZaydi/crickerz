package com.zayditech.cricerzfantasy;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zayditech.cricerzfantasy.ui.home.HomeViewModel;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference TeamRef;
    DatabaseReference PlayersRef;
    DatabaseReference PlayersStatsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_yourTeam)
                    .setDrawerLayout(drawer)
                    .build();
        }
        else {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                    .setDrawerLayout(drawer)
                    .build();
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        TeamRef = database.getReference("Teams");
        PlayersRef = database.getReference("Players");
        PlayersStatsRef = database.getReference("PlayersStats");
        if(mFirebaseUser != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView nav_username = headerView.findViewById(R.id.nav_username);
            TextView nav_email = headerView.findViewById(R.id.nav_email);
            nav_username.setText(mFirebaseUser.getDisplayName() == null
                    || mFirebaseUser.getDisplayName().equals("") ? mFirebaseUser.getEmail().split("@")[0]
                    : mFirebaseUser.getDisplayName());
            nav_email.setText(mFirebaseUser.getEmail());
            if(mFirebaseUser.getEmail().equals("crickerzpsl@gmail.com")) {
                API_Data_Fetcher data_fetcher = new API_Data_Fetcher();
                data_fetcher.execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        menu.findItem(R.id.logoutBtn).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    class API_Data_Fetcher extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Looper.prepare();
            GeneralMethods gms = new GeneralMethods(getApplicationContext());
            try {
                String data = gms.hitAPI("https://cricapi.com/api/matches?apikey=JJ8gDRXXaTSizhtuz39mlUnmjJ93");
                JSONObject json = new JSONObject(data);
                JSONArray jsonArray = json.getJSONArray("matches");
                JSONArray newJson = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(
                            (jsonObject.getString("team-1").equals("Karachi Kings")
                                    || jsonObject.getString("team-1").equals("Quetta Gladiators")
                                    || jsonObject.getString("team-1").equals("Lahore Qalandars")
                                    || jsonObject.getString("team-1").equals("Peshawar Zalmi")
                                    || jsonObject.getString("team-1").equals("Islamabad United")
                                    || jsonObject.getString("team-1").equals("Multan Sultans"))
                                    &&
                                    (jsonObject.getString("team-2").equals("Karachi Kings")
                                            || jsonObject.getString("team-2").equals("Quetta Gladiators")
                                            || jsonObject.getString("team-2").equals("Lahore Qalandars")
                                            || jsonObject.getString("team-2").equals("Peshawar Zalmi")
                                            || jsonObject.getString("team-2").equals("Islamabad United")
                                            || jsonObject.getString("team-2").equals("Multan Sultans"))
                    ) {
                        newJson.put(jsonObject);
                    }
                }
                System.out.println(newJson);
                TeamRef.setValue(newJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String playerData = gms.hitAPI("https://cricapi.com/api/allPlayers?apikey=JJ8gDRXXaTSizhtuz39mlUnmjJ93");
                JSONObject json = new JSONObject(playerData);
                JSONArray jsonArray = json.getJSONArray("data");
                JSONArray newJson = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    switch(jsonObject.getInt("pid")) {
                        case 227760:
                        case 230371:
                        case 1158100:
                        case 348148:
                        case 1137262:
                        case 1158088:
                        case 433614:
                        case 232491:
                        case 51880:
                        case 877051:
                        case 697279:
                        case 322810:
                        case 914171:
                        case 1216919:
                        case 1161031:
                        case 1076387:
                        case 47492:
                        case 919519:
                        case 348144:
                        case 290948:
                        case 45705:
                        case 227758:
                        case 434429:
                        case 227762:
                        case 533561:
                        case 1130463:
                        case 25913:
                        case 4864:
                        case 315586:
                        case 571911:
                        case 793411:
                        case 1159371:
                        case 793413:
                        case 1076457:
                        case 41434:
                        case 1072470:
                        case 512191:
                        case 221140:
                        case 1161606:
                        case 1206623:
                        case 532424:
                        case 793463:
                        case 18632:
                        case 462727:
                        case 520183:
                        case 623977:
                        case 1218226:
                        case 1161605:
                        case 1072466:
                        case 12454:
                        case 589663:
                        case 43590:
                        case 42657:
                        case 41028:
                        case 403902:
                        case 681077:
                        case 321777:
                        case 974109:
                        case 914541:
                        case 717373:
                        case 429122:
                        case 643885:
                        case 568276:
                        case 1076393:
                        case 734459:
                        case 322233:
                        case 10582:
                        case 552799:
                        case 42639:
                        case 318845:
                        case 43265:
                        case 40618:
                        case 716733:
                        case 296597:
                        case 348152:
                        case 326637:
                        case 317252:
                        case 323389:
                        case 43266:
                        case 1062813:
                        case 251721:
                        case 1203995:
                        case 1201886:
                        case 316363:
                        case 457249:
                        case 249866:
                        case 922943:
                        case 232359:
                        case 681117:
                        case 628240:
                        case 494230:
                        case 1072472:
                        case 348154:
                        case 681305:
                        case 362201:
                        case 669365:
                        case 1092313:
                        case 461632:
                        case 480603:
                        case 1185538:
                        case 964519:
                        case 288992:
                        case 1203669:
                            newJson.put(jsonObject);
                            break;
                    }

//                    for (String name: gms.playerNames) {
//                        if(jsonObject.getString("name").equals(name)) {
//                            newJson.put(jsonObject);
//                        }
//                    }
                }
                PlayersRef.setValue(newJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PlayersRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    try {
                        JSONArray jsonArray = new JSONArray(value);
                        JSONArray newJson = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String playerData = gms.hitAPI("https://cricapi.com/api/playerStats?apikey=JJ8gDRXXaTSizhtuz39mlUnmjJ93&pid="+jsonObject.getInt("pid"));
                            JSONObject response = new JSONObject(playerData);
                            JSONObject json = new JSONObject();
                            json.put("pid", response.getString("pid"));
                            json.put("name", jsonObject.getString("name"));
                            json.put("age", response.getString("currentAge"));
                            json.put("profile", response.getString("profile"));
                            switch(jsonObject.getInt("pid")) {
                                //Quetta Gladiators - start
                                case 227760:
                                    json.put("value", 8.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://cricketaddictor.gumlet.io/wp-content/uploads/2019/10/sarfaraz-1024.jpg?compress=true&quality=80&w=1024&dpr=2.6");
                                    break;
                                case 230371:
                                    json.put("value", 9.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://www.cricket.com.au/-/media/Players/Men/Domestic/Brisbane%20Heat/BBL09/Ben%20Cutting%20BBL09.ashx?h=548");
                                    break;
                                case 1158100:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://tnimage.s3.hicloud.net.tw/photos/2020/AP/20200306/1ee897a6cae34c9dace2404bf2b46bc5.jpg");
                                    break;
                                case 348148:
                                    json.put("value", 9.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQfV_hjMjFt3LDg7cHY59wGwHDcCxoYi3h3KQ&usqp=CAU");
                                    break;
                                case 1137262:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://storage.cricingif.com/cig-live-images/player-images/13015.png");
                                    break;
                                case 1158088:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://storage.googleapis.com/afs-prod/media/4fb72f605da649f78fc1fd53a9a9fb5e/800.jpeg");
                                    break;
                                case 433614:
                                    json.put("value", 8.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGvJWJ7x9kJvsyRodx_2OAG1C_aSR0_oUyTA&usqp=CAU");
                                    break;
                                case 232491:
                                    json.put("value", 8.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "http://quettagladiators.com/v1/assets/images/players/16.png");
                                    break;
                                case 51880:
                                    json.put("value", 11.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://www.samaa.tv/wp-content/uploads/2020/06/Chris-Gayle.jpg");
                                    break;
                                case 877051:
                                    json.put("value", 10.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://i.pinimg.com/originals/18/96/07/18960750a97f32ef30eee2cf22a5c98b.png");
                                    break;
                                case 697279:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "http://t1.gstatic.com/images?q=tbn:ANd9GcRmEivLbFDaQMYmo7d-y3vxxnbwFsQWEU5WUP65X-tbeZP4z0gHv4-0qwfgcHnA");
                                    break;
                                case 322810:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Cameron-Delport.png");
                                    break;
                                case 914171:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQIsJy7pHi23fqqB1GGdoYr0a98wKeWI24gQ&usqp=CAU");
                                    break;
                                case 1216919:
                                    json.put("value", 7);
                                    json.put("team", "QG");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGV6TuZsr9dTw8EtaCYdLqeV5HSdwbBKiKaw&usqp=CAU");
                                    break;
                                case 1161031:
                                    json.put("value", 6.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://www.natcour.com/wp-content/uploads/2019/04/31351293_1096826487125850_306895174443728896_n-720x430.jpg");
                                    break;
                                case 1076387:
                                    json.put("value", 6.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.mjunoon.tv/images/psl/quetta/arish.png");
                                    break;
                                case 47492:
                                    json.put("value", 9);
                                    json.put("team", "QG");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrj4IBNilZC7syAyAuHXmW3HDaNuZNVigh5g&usqp=CAU");
                                    break;
                                case 919519:
                                    json.put("value", 6.5);
                                    json.put("team", "QG");
                                    json.put("playingRole", "No Data Available");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                //Quetta Gladiators - end
                                //Karachi Kings - start
                                case 348144:
                                    json.put("value", 13);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://i2.wp.com/battingwithbimal.com/wp-content/uploads/2019/10/291306.jpg?ssl=1");
                                    break;
                                case 290948:
                                    json.put("value", 10.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://imagevars.gulfnews.com/2019/06/11/Pakistan-s-Mohammad-Amir-_16b45bda5ca_large.jpg");
                                    break;
                                case 45705:
                                    json.put("value", 9.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c171078/colin-ingram.jpg");
                                    break;
                                case 227758:
                                    json.put("value", 10);
                                    json.put("team", "KK");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://sportzcraazy.com/wp-content/uploads/2019/09/Imad-Wasim.jpg");
                                    break;
                                case 434429:
                                    json.put("value", 9);
                                    json.put("team", "KK");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://ttensports.com/wp-content/uploads/2018/11/Amir-Yamin-233x300.png");
                                    break;
                                case 227762:
                                    json.put("value", 9);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c155558/sharjeel-khan.jpg");
                                    break;
                                case 533561:
                                    json.put("value", 8.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://p.imgci.com/db/PICTURES/CMS/195900/195917.jpg");
                                    break;
                                case 1130463:
                                    json.put("value", 8);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c156798/arshad-iqbal.jpg");
                                    break;
                                case 25913:
                                    json.put("value", 11);
                                    json.put("team", "KK");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://resources.platform.pulselive.com/ecb/photo/2018/03/07/6b90f8ef-973a-41dd-be61-e4797457065b/GettyImages-514651748.jpg");
                                    break;
                                case 4864:
                                    json.put("value", 9.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://st3.cricketcountry.com/wp-content/uploads/cricket/20140522031516.jpeg");
                                    break;
                                case 315586:
                                    json.put("value", 8.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Chadwick-walton.png");
                                    break;
                                case 571911:
                                    json.put("value", 8);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSchP8fdbTDCN2SAqGmfcKaEcKnrSygwCRb3w&usqp=CAU");
                                    break;
                                case 793411:
                                    json.put("value", 7.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfz4L0kVBlL_3HmSOi6EBV_g5QxM3lg7oJIA&usqp=CAU");
                                    break;
                                case 1159371:
                                    json.put("value", 8.5);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Mohammad-Ilyas.png");
                                    break;
                                case 793413:
                                    json.put("value", 7);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://storage.cricingif.com/cig-live-images/player-images/15932.png");
                                    break;
                                case 1076457:
                                    json.put("value", 8);
                                    json.put("team", "KK");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRKgDVm7ntUQ1yknPejSkdEDvCriiX8N2vnMQ&usqp=CAU");
                                    break;
                                //Karachi Kings - end
                                //Lahore Qalandars - start
                                case 41434:
                                    json.put("value", 12);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c170776/mohammad-hafeez.jpg");
                                    break;
                                case 1072470:
                                    json.put("value", 11.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.crictracker.com/wp-content/uploads/2019/09/CT_358508.jpg");
                                    break;
                                case 512191:
                                    json.put("value", 10);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://i.pinimg.com/originals/29/38/f2/2938f2853d00264b2c85b089b1bf4787.jpg");
                                    break;
                                case 221140:
                                    json.put("value", 9);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://storage.cricingif.com/cig-live-images/player-images/13540.png");
                                    break;
                                case 1161606:
                                    json.put("value", 10);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcShzV4MzHh0VbkRCeB2mQdNijpFv-UX2q0vaw&usqp=CAU");
                                    break;
                                case 1206623:
                                    json.put("value", 8.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Dilbar-Hussain-573x480.png");
                                    break;
                                case 532424:
                                    json.put("value", 8.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Sohail-Akhtar.png");
                                    break;
                                case 793463:
                                    json.put("value", 12.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2018/08/rashid-khan-in-action.jpg");
                                    break;
                                case 18632:
                                    json.put("value", 9.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Samit-Patel-age.jpg");
                                    break;
                                case 462727:
                                    json.put("value", 8);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c158007/tom-abell.jpg");
                                    break;
                                case 520183:
                                    json.put("value", 8);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3e4tIlzQcWhrjpEyVK5KRr02DXaWVcGeUSg&usqp=CAU");
                                    break;
                                case 623977:
                                    json.put("value", 8);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://p.imgci.com/db/PICTURES/CMS/192500/192557.1.jpg");
                                    break;
                                case 1218226:
                                    json.put("value", 7.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://www.thenews.com.pk//assets/uploads/updates/2019-12-08/580498_2365276_260831_7117222_updates1_updates.jpg");
                                    break;
                                case 1161605:
                                    json.put("value", 6.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2021/01/Maaz-Khan-height.png");
                                    break;
                                case 1072466:
                                    json.put("value", 6.5);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c155534/muhammad-zaid-alam.jpg");
                                    break;
                                case 12454:
                                    json.put("value", 8);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c171053/joe-denly.jpg");
                                    break;
                                case 589663:
                                    json.put("value", 7);
                                    json.put("team", "LQ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://p.imgci.com/db/PICTURES/CMS/173000/173073.1.jpg");
                                    break;
                                //Lahore Qalandars - end
                                //Peshawar Zalmi - start
                                case 43590:
                                    json.put("value", 11);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://resources.pulse.icc-cricket.com/players/champions-trophy-2017/210/988.png");
                                    break;
                                case 42657:
                                    json.put("value", 11);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://www.cricket.com.au/-/media/Players/Men/International/Pakistan/ODIWC19/Shoaib-Malik-CWC19.ashx");
                                    break;
                                case 41028:
                                    json.put("value", 10.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjzMs9VFM22550hbXXMAogZoZrsMgwXvF6gQ&usqp=CAU");
                                    break;
                                case 403902:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRk_lc4qaaTAcJXz0mflfuVQ0u6PIDJllwyIg&usqp=CAU");
                                    break;
                                case 681077:
                                    json.put("value", 10);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQjKbbz3JiFzIKx4kaV1zA59WBswqC2rPsexg&usqp=CAU");
                                    break;
                                case 321777:
                                    json.put("value", 10);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://st3.cricketcountry.com/wp-content/uploads/cricket/20140521030922.jpeg");
                                    break;
                                case 974109:
                                    json.put("value", 10.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.cricwaves.com/cricket/pics/players/Mujeeb-Ur-Rahman.jpg");
                                    break;
                                case 914541:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://tnimage.s3.hicloud.net.tw/photos/2020/AP/20200306/1ee897a6cae34c9dace2404bf2b46bc5.jpg");
                                    break;
                                case 717373:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Amad-Butt-height.jpg");
                                    break;
                                case 429122:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://i.ytimg.com/vi/sRAVmTkevT0/maxresdefault.jpg");
                                    break;
                                case 643885:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.indiafantasy.com/wp-content/uploads/skysports-saqib-mahmood-england_4837245.jpg");
                                    break;
                                case 568276:
                                    json.put("value", 9);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcShmrtI92MfhEEsM3vBJKKGF2iXofdoXiEz0A&usqp=CAU");
                                    break;
                                // ambiguity
                                case 1076393:
                                    json.put("value", 6.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://pbs.twimg.com/profile_images/1313977511672532993/M2z8xdtE_400x400.jpg");
                                    break;
                                case 734459:
                                    json.put("value", 6.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://i.ytimg.com/vi/7AG6K9ALHk8/maxresdefault.jpg");
                                    break;
                                case 322233:
                                    json.put("value", 6.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxASEhAQEhASFRUVEBUVEBAPEg8PDxAQFRIWFhUSFRUYHSggGBolGxUVITEhJSktLi4uFx8zODMsNygtLisBCgoKDg0OFxAQFysdHR0tLS0tLS0rLS0rLS0tLSstKy0tLS0tLS0tLSs3Ky03Ky0tLSsrNy03NystLSsrLS0rK//AABEIAQMAwgMBIgACEQEDEQH/xAAbAAABBQEBAAAAAAAAAAAAAAADAAECBAUGB//EAD4QAAEDAwMCBAQEBQIDCQAAAAEAAhEDBCEFMUESUQYTYXEigZGxFDKhwUJScuHwYtEzwvEjJENTY3OCkrL/xAAZAQADAQEBAAAAAAAAAAAAAAAAAQIDBAX/xAAlEQACAgMBAAIBBAMAAAAAAAAAAQIRAxIhMQRREyJBQmEjMnH/2gAMAwEAAhEDEQA/APUaNBp4CjcWbSNlfpUgEQsCS6S3Ri2tANxHKK+ieFG4MVAES7vBTbJWbnctS65Zk3Dy057ozbhrcrN1G/DyqbHE8py4rQLrNu4uZgo9vdThUabRCgblrSlDIqthKLsu3tYAbK1psOZKwri8DsStnQ/+GsM8r8NMa4V7upkidlWBlZ+s3XTWcJ/yEClqrdpC1UpUjOjX0vFVaGvtPQI7rI0a5a6qMha3iSpFPHdYfzNv4nOurkGEwcXbqv1I1rJK6JGaR1eij4Amu64bKLpYhiyNSJ6ne654esuRnVr49R90a0rFzmz3WXcNiVZ0+uAQSrmnQKjt31CGSOyxK+pnlSvdZYKcA5jAXL17ySUsSdEzkka9xqk4hH0eoOsErlH1jKt0L1zcjhVODZEclenoXnBJefHV63dJZ/hZf5UemdYCia7e657xHdVGdHQdysZ9esRl5XVjlcURKNPp0F/XHWCDyg6pcs6PiiFk2ZJcJP1RNboS3flZbf5C64Z767YxCVrUkhVm0wN1Zs4BC1yJ6kRaujauqUUyfRclWuyV193Wb5ZE8LiLiN1nhj9lyLFpVJcu70J4FPK8mr6z5ZJBbI4csi98ZXZBa15aDw37p5MWwRmkdn4yvG+ZUex7eADOJGCF55X1eqXFwMGeNsKlWu31IDnE+k/VI1A0QtoxpUQ3ZoW2t3DH+aKhDsf5C9PtvFVK6t6ZLh1wOsbfFzAXi73SSrNpXcw4Me3KUoJjUj2AVWkSPkremCSSvLrbXqzIgyBiD2XZeGPE9MkB+DPxdkpLgJnfWtV35QFaZps5cjWlSmAHSMjB7+yDf63TY052XK7vhp/0ytdtqbGnZcu+rGyPf6g6q4knE4HoqUjJW8YUunPKd+CfUJSHE9lHrBUXO+6uiLJOOyZs5TPqjhLqkH2ToVhBCSCGpI1DY7HVNQY8t5hZd3dTACqKLltDEoqiZZHJhqdcjKlc3znDKHQZ1GDsmvaYYMKWop+dNFs0VKlYlSp3CAHEo1K2O6dWNcGuK7yNzELjNY1SoC4CQO2F1+o3NOk09RO3C8112+8x5jae2SlVFFW7uS4gkoLnThQhRDlIyxTMT7bpnNJzPuhB3wpOfACAJvMZ/wAPqo+bBTebKQaEAWxUnP8AhUqdcj+x3/2VVjoTVGtOQY7+6YHWaP4srsd8T3GMDq4EbLcoa15uZmd915i0EGZWxYXLhHZLVCZ6IHk7RskDG6z9Jug5o3mFemU6MJcF1bKVNkxhTp0wUVrwMJtCsgKQ57pw3dSuagAVOncQmkO0GNIpIf4xJOhbI2HlNUo7GVOjbu5RHWrjucIcm3w0jFIr1LoAY3Q/Kc/JKv07BoVhtFoQkirMunQARCr5YxQeWAFMDzTxleHzOiTA39yuNqEkkrc8T1+u4qn/AFwsjp2H1UMoAJUmsO8I9KjPCtUrbvsobKUWzOcoPBhbTrEYIUDa8DPuJCWxWjMVSAK2aWmjeFbZYHgfojdDWNnOiRwpg9wt+ppp7KrXsY4QpCcGjKqkcBXNPq8H2KHUAGPqoB4DpHeCrRB0ml3JY4Dg4hdRTq4XC2jzIz/0Xa2wHSPb9lfpz5UX7R0yg1KsOj1ULet0z7odR8klBnsooPWcCFVe5IuUYTMnNsfpSSToFZ1jaxCZ1yUAlNKfD0dAhrHunFQ90GVOk0lOxShSE5xUXsMGOytst4ygVXxI9FLmQl9njupuPmOJ/mM+8qsxv3VnUQfNqSP/ABDj5oNFuzeZWbZqkaGm288LUFtHCtWNn0sGOEc0Vi2dMVSM2o0Rsh02Zw35wt23te4Vp1sO3HClstIxaNt6QrTKfH2V3yxt9VKmA3ieyEUVvw2Fn6ha4W51k/w47qb7SRKpEM80vacOhCFMEroNf03pcDBzyufcYlaRZzTVMtaeCXb8x8l3VAQ1o9FwmmuMtPd2V3VM4HstYqzkztonKaEQ7ILlev2ctElIFDIU2BPVDH6gkhFJFIDqHtIUWtJVis9pRKRaFnZ6WwKnb91M1A1Bu70DZZda7lKmzGeVI07i+wsurcEobnoXWmomEsjZweutitUHrP1UvDtPrrs+ZWtr2neZULgYcW4HB+aD4Vty25cCMtYfuFlJrp2406TZ0d/UDG9zwOVhVbuuZhh+y3r4fULAuKdckmAG8ZBd6fCcLKLs3aKj9RummcD6FadjrVQ4fHyWTTs7gkGofhzMdHrHAR6FItiWxnuIPrhOa4KF2dJ+JhvUsq+1zp9/RaVIF1MiOFydVhLyIJzsN1MUazbXhYZ4iqyIYY+ZWjQ8RuB+IH5grLfaVcGmJ+E9Xwj4TwJduq7fPEdTJ7jErWjC2dNXv2V2FuJ4hcdqVuWOgrZtGwQQCM7QoeK6EeW7uT9ko+jkuGZo1MF7B/qXbNwua8P2QkOc6DuxuZPr/ZdJK6IM4M/H0m5xQy4p3FQLgtGc9j9RTlyG4qZpkhPVlJMGXlJDNMpkaMdHQ1bkynddujdVHJdSzE5tjOqEndRcUkzimTZLqQwVIFDCQAawBq+zRj6paaxv4io7nyxt36krgQ8u7s+yHZVgKlJw/iDgf6pBE/Jcb/2Z7ca/FE1KrMoVW2ackIr35hS3x6KLNEuGbUtmcN+uUI2kkD9OwWx5QiUGhRyXR7eydjoPaUoCw73T4f1DGd10tvjKhc02OmSAT3TB9MKlZdwR6icq1R0qnvk+5VizuMEEg9Jgz90SvcSCixKIB1Cn22WJ4jZ1Bg7PEK++pCx9dq9XS0e/0VQM8iQa1tCxzSeYP67fqtNxyqdCmYpDnpBP1lXwJlb/AB4t3Ry/NXIgnuCdlMFOGCVLpXpQ+PsrZ59pEHUgFLqhM4eqrPJWycYKg62TL0kHpKSndfQ9TXcUpUXFIrziSIdlNUcnhRIKZSiOHYUG7qcKAQKwppdTYnLchU72gB0ObgtdOOSd0ckgyE13W6mxHIK5543do7cHyFrpIj53PO3oiiseFnF3+ZVi3qSN1zSXTvhIsPrE4lV36g9rvzDpjY7/AFQnuk4Q61q3+J4HpMlNL7By+jQfrA6cHccZKxxqdR7sYE7bq3TpURPxiIzjKreXSBJa7PYiFVIG5Flji0yTuco1S4WbVrkEA5UzWkfdLUNx6tZSsrAViS4HAhsEATPKoPqSVuaEHw5zQI2PutYwb4jFzSdsMy2LSXO3iAOB2CdxR63U7JGyCLdxzBXt/FwrFjp+s8z5GV5J3+wADMp3Ij6RCbyCeCui0uGPbKhJJ3Q6LSXbq6bUjhBZSIdELFwiWpMkW+qSIbd/ZJPSJNsJ5sojXI1SwhFbZYXlmrgZ9WtCj+IVirZElRfYwgerIMeh1akK5StMINxamYTon8bK/npjkFTNkVYo2hhKh607Maq/GFOi8QeEPVqJpPjHSRj0PIVRtTbK43E9KMuFlrS6ckDggqbbFm7nEnmSh2jzPorlW2DhM49FPUzWNURGn0OXn1+M/ZAuLK3Gxd/9pVG4sD1Yc5XKOnbEuPzVuw2vlFZls0SQXHsSU73gDBT3lbpHSO6pOJxlCRnJpeBZB/3Xonhe0a22aTu6XH2nC88tKRe4N+vsuuZqjmgNAIAEAdgt8afpz5ZG0+k0uhXG2zQFzlC+cTKtHUnBdFyOdUWb61bIjurdCzaAueudSfMqdLxCRuEOUvsFRs3VJoVKhbA1JjlV6+stdCJR1JoyltJIaSs1fwbUlnnWQkp3kOolu0ohwlwVkUmbSlYhsQUapaNOywTbRtwE62pbyFSum043RLnSnnZxCyK+jVQfzOPzTVj4WmXdJoMkKrT1GiXHITUtDc78zXKxT0KizPTBTv8AsVGPe62xriAtHSG3Nf4mUiGc1KnwM+U7/JbOj+HqMPuXMBDcUw4S3q5dHMLRv7qIE8fJaIh/0cX4qsulwaXB3wZIGJzsuPqMc324Xa+I5Lmzy391zVxTXLJ/qZ1JXFFGldcFWaV+eduFWq24KrVLR4mCjjJ6jTGo5/2UTfnusk0an+FQ6Hp8C2Wq1zPqmYXHA5/RQpW55Wla0AE20hJNmp4et2te0umP4ojq2XXanohpEGJY4AtfjOJg9iuVoAhriNw0lex2DGVral1CWvpNP1aMhaYn6RmR53ToAGFKrQBwtDXNOdbvzlpPwP4PofVVqMHK6P2Oco3lqAFR/BhbF8MKjOwSYUZtSgrVtb4Vl1MQoUeAj0KB+Ukrnkp06QqLFOq5quUr7uimxPMKD7EDlecs0Tq1L1vdDujeczmFztZ3TsVWqXrjjK22FR0lXVabZELIYXXNZtNmJOT2HJ+izerv+q7HwZpvQw1nD4n4bO4Z3+Z+ycI7MG6RpalSbTtyxogNpkD5Bee3+tNaA4kOeRimMx6u7L0fWGTTeO7HD9P7rwqgzHy/VaZnrQYY7Gmbt9UlzzJ+w7BDqNUKOFZK45Pp1xjyjPdT27JNHH7K46mhOpI2BxA+SI4/VUntyrlQOGAUIUe6pMlxGo05V+jSULenCtNCTlbKjEuWQyvVfDDgbW3j/wAoD6Y/ZeUW7oXqXg8zZ25/0H/9uW+F9OfOuIvX9kysx1N4kH6tPBHquNvPC13Tk0+mq0bdJ6Xx6tP7LvGogXRI5Tx7UHVG/C9rmns4EFV6NRey3NvTqDpqU2PHZ7WuH6rmdU8D0Hgmi80jwDL2H07hS7A4SpXEKNpVzlXNT8LXlCSafW0fxUvjEff9FjscQfWfZCk0grpu+cEllecnRsx0dK69cUJ9Vx5V3yqY7K1aaean5WY/mOG/VeVF2/0o3b+2YLqUq1Y6LUrH4RA5e7DR/uustdFpty74j22atHYQMDiNl348En2RlLJ9GRpvh2hSgkeY7+Zw+EH0atdKUpXWopeEW2Bu2y1eJVrboqVGR+Wo4fR2P0XuNUSCvL/FFj0XLzGKgDx77O+w+q5/kr9KZ0fHfaOdDYRWFTq00MLhs7aCQoKQUoSKK7ygOk8K49iVOmmmKgVNkIoRC1JrUxC2B9l6v4NEWNr/AO3/AMxXlVUYIXrXhJv/AHO1H/pD7ldHx31nN8lcRqtUinhMV1WcZElIOTvAQWuTXQDhyp6hpNvX/wCLSa4/zRDx/wDII3UpB6HEDnz4Es+9X26/7JLoetJLQdmTZ6JRp5IL3d37fILSn/Ao9SYlVGCj4ibHKiSlKSsRJqRSBSLh3H1SsYjsuN8d2n/ZsrAZY+D/AEO/vC7AVm91R1i1bWo1acj4mED33CidSi0XjeskzynqBUSyUX8L2+Y7JxQIXmM9NFYshTa0qbmnZEZTwlQyApykacKy0fdN05SGVvKJVqjZqzb0wtG0s31CGsbJ/QepPCaV8QnzrMO8oxgD2XpnhuqKdrbsdIcKQkEGR7hVtO0VlMgwH1P5zkN/pH7rZbbgCPqV24sTj1nBmyqXENU1BvDXH9FXN7UOzQPfJVltEDjdSDAF0UYA6HVu4yjuCUKBMJoCMpEpFQqGFQiXWkqnWkgA4enLlXlGbwmInKkXR+ybAElQa6cpARfSByd1A2wRwknSCyFKkAjhg7IaNTUuKQ0zktZ8Lx1VKAkGS6nue8tnf2XN+TiIz2OCD7L1KFm6potKtJ/K/wDnHP8AUOVy5MF9idOPPXGecmllCO7sbfdbepaXWomHNkE4cPyn58Kh+GxJ39FyOLXp1qV9RQnup0OSUZ9sfnurmk6XUrv6GjG7nHZg7oUW3wbkl1hNIsH1nBrB/U7+Fo7ld3Y6eyk3oYP6ncuKJp1hTos6GDHJ/ice5Kttau3FiUOv04cuZz4vBqVMBJx4UiYQpWy6YEikolOCmA6i5SUXIAGTH7qtXdjP6KzUxlCqsk/ZUBU6fVOpFqSBDu2R6SrjIRqGyQxVjJA4U/RDZuT8kVqYDp0k8JiGhO0wnTIAMCkQhNdCLKhoaZF7AQQQCDuCJB+S5/UvDjTLqMA/yOPwn2PHsuiJTKJQUvS4zcfDhLbS6tSp5fTBH5pwGjuV2mn2TKLAxg9zy49yjgCdhMb8pyohiUC55XIdOSogqL3rWjIZzpTKKeVVCJSnUQnlADymTSlKAGcgcEcg/orCr1sEFMB+pvZJVi33ToAVM4KnRfiOyHT3ITsH5lIBKeyM1Ap8I4KoGSTyopJiJSmJTJJDodSa9QToALKXt9ELqhTGVIEg5Lq/zumI9Eo5P/RAx3FBJT1HKITQhwpAqKSAJSlKikgB5TyoylKBjyh1cgpEodR/PdAgPnJ0EpIAM786K4b+ySSQCpfsiNSSVASTpJIASSSSAEUmpJIBkHolM5SSQwDpnpklIysd04TJJiJhJJJADJ0kkDGKZySSAIv491Vq/lPukkgRWlJJJAH/2Q==");
                                    break;
                                case 10582:
                                    json.put("value", 8.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://st3.cricketcountry.com/wp-content/uploads/cricket/20150301120957.jpeg");
                                    break;
                                case 552799:
                                    json.put("value", 6.5);
                                    json.put("team", "PZ");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Amir-Khan-Pakistani-Cricketer.png");
                                    break;
                                //Peshawar Zalmi - end
                                //Multan Sultans - start
                                case 42639:
                                    json.put("value", 11.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c146389/shahid-afridi.jpg");
                                    break;
                                case 318845:
                                    json.put("value", 9);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c157336/rilee-rossouw.jpg");
                                    break;
                                case 43265:
                                    json.put("value", 8.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c155525/sohail-tanvir.jpg");
                                    break;
                                case 40618:
                                    json.put("value", 10.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c170764/imran-tahir.jpg");
                                    break;
                                case 716733:
                                    json.put("value", 8.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 296597:
                                    json.put("value", 10.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://staticg.sportskeeda.com/editor/2018/02/eaf95-1518170665-800.jpg");
                                    break;
                                case 348152:
                                    json.put("value", 9);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Usman-Qadir-1.png");
                                    break;
                                case 326637:
                                    json.put("value", 11.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c154633/chris-lynn.jpg");
                                    break;
                                case 317252:
                                    json.put("value", 8);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://p.imgci.com/db/PICTURES/CMS/205200/205275.1.jpg");
                                    break;
                                case 323389:
                                    json.put("value", 9);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Wicket Keeper");
                                    json.put("imageURL", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxISEhUQEhIVFRIQExUVEBAQFRUPDw8QFRIWFhUVFRUYHSggGBolHRUVITEhJSkrLi4uFx81ODMsNygtLisBCgoKDg0OGxAQGyslHSUrLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAOEA4QMBIgACEQEDEQH/xAAbAAACAgMBAAAAAAAAAAAAAAAEBQMGAAIHAf/EAEAQAAEEAQIDBgIHBgUDBQAAAAEAAgMRBBIhBTFBBhMiUXGBMmEHFFKRobHBIzNCcrLRFSRiguFjksI0Q1Oi8P/EABoBAAIDAQEAAAAAAAAAAAAAAAMEAQIFAAb/xAAqEQACAgIDAAEEAQMFAAAAAAAAAQIRAyEEEjETFCIyQVEFI2EzcZHR8P/aAAwDAQACEQMRAD8AOwmJpAxAYDNk3x2oDCIzIj8KSui8SsOS3wpPIzxKqZLCIY0dHFsh4AmEY2XM5Fd4rADaHwuHtrkmmfGtMVmwV1JpENEmPgN8kPxfCGk7JxjtUHFWeEqFN2d1VHPMGGskD5rrvCW+Aei5VjtrKHqur8J+Aeia5FUgWL1lX7axWwrl2S2l1btmfAVyvK3KYwfgCyfkQsKIY5QsjRUcSMrKaIZHLVjFJLEVrGrRW9lX/gnjiUkkWy8heppXbIlKgVuxVMKK8Y9e5HNeMYg/7B/1sJZIopDalZGvDEV22Roh7pROipNIcQkKOfGcELM6Wg2FX6aYMduCuPC4qVWwmkOCt3DXJXNtBIaYdpWKS1iUCmuC4UmmOQqQOKaAbKnxu0Q80f4ZPwp3SLzLVJXNVpLJ2hBFWvWZxNFV+GSJ7plghpMIhsqkeLgGrRsHG21zV/pZsr8sUMsxoQ8JCXZPE7tK5eMaeq58eaJWSLLrjkLXiVafZVPG7RDzWZ3HwW7FVWCV+E90LXn/ADQ9V1DhDvAPRcm4ZIZMgFda4SzweyLni0kUxtWys9tDbCuaPC6j2sitpXNsiKkzxovrYHNL7qI4mhFxNS9r6RWO8kgDcnkE4pJeiziyWeMJZLzT2TQz4gXu8r0sH6lDucx4vu2Nro0frzQ55FejouhVGSibNKf6u3+Hn5Hf7j+ikbBspinJaLd0hW9qmjat8mOlA16la9Oe/A6NiyQUoopVvO7ZTOaoiMHYZw2dp2TaXFa4WqQzJ0PTePjW1Ws6UtjsVoOna1qJwM4XzVYzOIl3VQ4uYQobUtE+HQvrgXqpn+JnzWKvxI7seym7C9hx1uIiNyERjPF0taMIoQlOTPO5pSOy668kVkwnTdJHNGSULNBBMUn+zs/A+wmPHUk37aTY+L90D8m9fdSduOEMOM5zGAOip40ivC3Zwry0k/cE27L5fe4kEnV0LL/mDad+IKOzYg5hadw4EEfIiisiOWSn2b8GukWqOHOk2SjLFlH8QYYnvjdzje5h9Wkj9EK2ibW4oqexBycNEcOOpZoKCOx2hZmAUryxpIiOV2Qdnv37V2Lhg8A9Fxzs9/6gLsnDPgHosrl+j+Er/ar4Xei5tli10jtfsx3ouaTOTPEdwoByF91gXdWjsCo7cedUP1WsbQmGXwSSSBs0I11YkYCNTfFQ9q8/ZE5NY8dspiUssqiBZUrXCwQSem9jbe+nPyQfe1t16JfLKWnS4FrhzDhRHsUPJkpBSCOGzo3DexMs8DJHE48rXO7zvWkh8XxNeG3YcLIo1dX61/FymuBLTYBIDqrUL2NdLG6C4N2vnxseSKJzu8kkYWud42RMaDqAa6xZNdOVqDDyHOc97gAZXFxawaWBxNmh0F9FfjZJxm78IywhSr0MyzaCEaIf81swgLQpSBX18NYoSi+42UmMQVNkCgonhuOjoZt7K1xLH6oBsZTjJNlaxxBJxw/yMvJ/AtbASiGY5TNsAWpAC74d6JU9bAO4KxMdliJ8DK/IXLiHDAG8kj4LjapS2uRVt4xOA32SDgEoEpPmVm/NP+RtQj18LLJw0adwkk3CBvsrW7KaRSAkIoqflk0V6KyxfR5J/lBH/wDFI9vsTrH9Sss3wlUr6PMq35Ef8jx76mn8mq8EbIMdtkyVM4z9JmLoyC8f+4Gu+8UfxBVRilXQfpchruX+epv3EEf1LnDCtbizfQUzRXYaQTrMia1BAF69NuTA9Uhh2bj/AGwK6/w0eAei5J2ZP7ULrfDvg9kjzV4H4z9K/wBr2+B3ouV5dgrrHaseA+i5VxHmjcdVjKZXcyJkiYYDnFwLJHse3cOjJbY5Frq5j5EFKWIyEe17bbbK+VfJDqThl8c+xB2h7ySRpkaDTaMjaBcL/iA6j0670gfqLOd2PMmk7yZC4AP5jk4dR5nr933KFuPX8TfUEEH1BS+HjpqvC3Iyty7eisMjb/E2/UH8ETixukdojb4qB8Xg2JqwDufXkj452M+IBu9B1ANcf5jsPdWPhXAXSVkuj2NNDQCZXxtcdVHYAdfnXNXyY4443dgINzlVGj+xbn45e157+tUcRFMkZVhod9ojkbrp81SiHcqII5g7EHrYXdOF5zJG022loFxSNc2hW3hdu3lzBIXNe2uK2PNk0ihIGyUehcPF+IJ90tPNOI3ixxkyvYriOaNllsISeQBQsybTPHzOS2D5GFReiTu7KIix15AbTDHatCONNWIyyNERg2SzI2KsUrPCkeRAXO2Qc0VHYTDJy0CWvUd/hxWJf5V/Iz8bGPGuJEnSEsxZnMOoFDPmvcr2OXdZeqGyz4PFC47ozJzvAVWYJ6KmlybFKhaqLR9HGYRnUTtLE9vuCHD+krrgXCex+XozYHf9VrT6P8H/AJLuoUeMpLZzz6XoLx2O+xOL9HMcPzAXKWMPku2fSTBrw5B1BjcPaVoP4ErnXDuF2OSc4+VRjQLJG9iKK/Ir0hx6K4xcGHkpZ+DgDkj/AFKKfE6EHZn96F1zh/wD0XK8CPRkALqPDXeAeiHy59kicEasQdr5KYfRcqzX2V0/tkfA70XMXRG0XBL7KK5Y/dZCxEskpbRwIhmInY4m0LPKkDNm+VjyIsLYPb5V50eX/CJfiUEBO6tlHTo7I7d0MIH3tQN9D4r+VK9cC4M+GeSJl92GNbKKccf6x5Qi6YAL1AeY2XNMay4NbdlwA32snbf1XeuH5DBGwHRG8tBdFYGl5FuDftb3uOfNI8+UpUMcWKjYmh4aY3tNtGq/C0E6SCNufJUD6TJR9cb5nHZY8v2ktLpPFckGSNg2LfE4nw9fn6FcV7T5RycyWRm7Q7Sw87a3a/c2fdJY05DN9XYFO6wgbIKYOw3gboRzeiZxx6lMkuwZiTp3iShVYGkZDl0tDDn1TEsuGyzzZAqlmLECbVcOYVYeDuJAQObluOg/Ex09jbuAvVJusWPbNHQjzuFt0rOAcA1Gz5rbI4kHbJjwPirW81Z9kQqcbGuX2dYG8gl2PwAXVJtNxxrqFhMuFOB3UpNIo3YqxOzga5rwN2uDgfmCCPyV+yuIEPa1gaeZeCd+WwFWb68qQULQo8nABku6L2FxLRRJbVCx6koU3RaEVJ0zbtE0ywUW6S+N1tsOogaqsbHdqrvD+HUFYZshvdMGmtjrJpga3R4pPmL22vcqPFZsESLdFGl4DQ4HyW2bieFNomqLPHhXKTs6jnLoKyQug8NHg9lSZCDkhXzAb4QneRH7EBxP7mV3tRFbSqFNiUuj9ogNJVDziKTfHx/27F80/voXDYouByALTaKhYU9GbSFJY02FzcklyseymrwQhyBaq/vZaK6LQDBjG7GxG4PUFXHj2bJNw+R8Tnh7WwNcxjiJGvbMwO0bXRZq5ECjRB5pFC0J/wACA1OZ0c09a3G/90tz8FYXNerYXjZpPJ0X70V7iTpI4g+J7269TSHkl7g5tl1Gy0iy279Nkm4XEArVn4Yc57PtC2Xv4huB78vdViNpHJIcWs0b/Y1kjLC6k7G2SwaVXsjG8WybsDivJoKCa+mop8yEDsYrdmGUzawWj4YWpnDx1WxfLnpiRmAVZuDwUB8lJDjtU4cGhV5XFThonj8j79h1heJV9cCxZP0sjR+ZCmbAfVhpQZxpr+ArsEXBWkDZTxcCZ9kIbnsmq8OT4uNK0glpVz4POaCsObwZvkoMDhwBK6U7OjGvQ7DlKJdFTmyk1qfpJbz0aDQPS7vf5reDGpbS4Je5pvZlFosgE6hdt5HYCvUoU/C8GlIhM7WxOYWlzmOIDALc63kANvmas15BaQD/APDkjCGtDw8jenAc3WBzDRuoIW+XLp02XY/CJu2ExobiZ8J9EXG1RZ0dtKsvSDm0b/8ANro+B8I9FRJMbTlA+f8AdXvBHhTvIT6xF8XrEfaZ1NPoufTPtdF7Rstp9FQcmKlocOL6CvJku1EMUQR0EISkz0ioMtPdooUcZMg47xRkR0CnPqyLqv8AlVl/HnncMb73SuXZrsY3NiflSSuaZJHaSAHEAEjqlXG/o7mjJMczHjmAQWEj7yLWBl5zc3To18fEqHli/C441xAO19b2Vt4BlVM2+RsfeFyybDljPiaRvR6b+qvvZd7nQskNlwJ3PM6XED8k1hyy5EJY3/DAThHDNTr9j7jbtLw4dCk+TigPJA2d4m+h/wCbHsj+0YPP39ku+tXG0+Vj9Vnf0ifXN1f7Hf6lDtj7IIgx1vmY+yhgyVPNNYXp31owUp2V2U05FQzLWeK3KaDGQop/oNKq2FQzqOae0TFirWTDVnGT9KKUV4ALEV3CxV+En5TskTUSwKCNyIa5eZaNhEGW1CQR0SjMh6hjXRRLJ42qObL0Ot72sYOQO7j93JTMKGyWww6p3izZIcRrcL6NHT1U9dkN6FTs2HG1PjjozOt027i91XuSS7zroio83WNVUeZBI5Ulv1j6213eNtj9TWjUGyMbuOnmN9vNCMD9A03YZvq3f96uovtQNy1ZZYsoLXLyRpSGJzqvdeyOcW9Vovhxqxdclt1Qny8j/MA+StGHnjTzVPkgcZDsUQRIKAtWnjU0kTGfVtjfjGXqBVVnjLgnToXEb2oI8Y1yTOF9I9QGWPaVlZPDyTSMw+E+Iavh3J6bAWU8igF8kS8Bg1V8IJ+4IuXcHXtA8bqSvyzSfLmwoYGQwh0YY0OJAFnTZsh1gnfobJQHaTjrYWAyxkPlFtY2nODT+W6ez9oYGsic4g6wSGgF7jpG4oDbfZVLiHHYMzINA0Ghoa8Udt7H4/cvH+u2emTrSKn2kyRLCS2/A8FzHCnDpW23VW3snw4sjha4bUHOHrbiP0SDNxu9kbBCzU5x1U0WTXU+QutyrnLLDhtx8Nzh3xaNh0ABJJ8rOwHVP8bO8UJNL9MR5GOM5x7P9oS9rptzVlt8hzHySTh8ReDV0XWBd1smHGZrLhz+am7JxAtN9Cg/0v8A143/AO0G57/tOiFnD3BFtwXUrF9WFKf6sNK9TSo8/wBnZSHQG0TAOlJpNijf1UAgpwCrHL1dFpY+ys3iFKTIGymdBQtbTRGlf59A/g2JvZYiu5Xqj50d8Jffr9KaLiFqrkk72psImuaQ+hVDj5e6LCcuytjlAJEHkEi0Bn5bhQB6qn0aTov9RaLczOHmkvaJ7nua7cxNb05Nd1sBKmZLq5lT42S4t5qJ8KMo+nLO0x92Xxm6Xmw6iNNbADTy+e4Kr3HOJtjc+Ky1xc4B4aHtZpdzcDzG3LrR5cw77IZFySs6aWu2FciQfzVR7aQHHy5ZY9xPpc5jhqjEmkDvRfJ1ADy2Wdki8MmmxrHWT1D3svjCNj+Qc5/7RjSHNa8CiQetik5NEKgcB4k5hJs0/wCK/PzVqZO4tBDk7xmssaT2BzR+KW1oOZGLU/dN+Srjst4cd17JxF46+vomlga9YGWZUWbu2rQxNrkqnL2n07A2f9VtI9v7qt9puNSvLWd4QPi0tJA8hy90CU4rSYD6qPakjo5aLOyT9ouKmOB74GNmcG2B8THN61pO+18lzV0s7gWtc9xdzbZdbeuxTrKmlfjDFY7uz3bWvl50AN2srz5E7dUKWV16VychaoAmn7h51BrxpJgMhNaHHVsRuPI+iQ8Q4kNYk0gOabaGvc8cvNxtSZ2NPHG2OTS9t+A9PYpQ7HB/hNXVbk35BILHvZq/WQcNMc9nu0r8TXMxgfNK0Na9/iEbAbcdPUk6fu5JRlZ73v7xz3OkJ1OefiJ87RmVwOeFrXkPb3hNgigKq6sfNLsmPa0wqa0JufaWx9H2jaWFsoOs/wAbRYd6joVf/o8xQ6AzkbSuqMHnoZYLj6m/uC5DwfAdkTsgbsZHUT9lo3c72AK7jiTCJrIY2gMjaGtA6AInF4v394hsvIfXrLwdOhbXJSNhbXJLnZbq5KeLJNcloPFkr0X+THZ6cJh6BaHhzLBpRnOO+yGPFTfJVWLImWeSDQzdgMrksfw5tckGeKmuSlPEtuSlwnRHaNmv+Gs8lig/xX5LxV6TJ7RAhJt7KbAk8IXjYdvZTYMPhT2+os+tkT5PGfRQTx3XqEY+HxFDZD6r1UL8tnPzRO2Db2U2LB4UG/NACI4flhzVXtGtE9ZWMey405Lv9TCPuIP6Ibt3ADI2/wCJm3qCVFi5zopS9oBNEDVy3HWvZacQy5Z9Pe6SWnbQ0tAB6bk+SQzcZ5ctVpoaxZvjjf7spn7s0j8HjDmmhuD0Kh7VcMewd8z4D8Xm0/2SCDLA8/y3WNKOTBOn6jVjKGWNrxnQIsmM+Ktzzs2qn2j4uYXlllwcLYOhaeh9OSjZxD5/3SntO9r4g+92H8DsR+R9leHJySn9zsW5XExyx6Xgtn41I486F+pHoV7jZO/iNk9Uqx8Oab91FI/egWNc5t+RcBQ9yisdjmkte0te3ZzXAhzSOhHRNO6szvijFaOldhsBheJJHDS5pNU0gAEUDd8yrFxfCxRCXMIMgaNLgBrJ1C3EitqtVLguSWsAa5zCWAB8Tix4Gx2d0uq90fJO43qklftX7WV8oAqtmk0DW1gXufNWUlWxaObFHFKMlct0AZ+JrjLbaeRF7EUd9wN9r6JfgcOjZMwybtFucB9keXvQ90fkZLxYYAL5udyHoEqlcYrbZfK8gvJ5gfwt+XU+6pJWqE8c5RehnxzPEnd6GEPjB5v1Np2/XkVSuK4wa4t6EX96ssUL6c47kbuNgACwKF89yNgkvGhuD8iPyUQgoKl4NRzTnl7S9Y4+jDhFyy5BGzGhjD/qfu78AB7q9PFPSn6OItOHfV8shJ9DoH9KcT7vWzxY9YINNuXpJLJsiIpNkFNHsiYY9kZ+FaVmo3BQckPiCZQxbFDS7PC6O2S9R0RyQ7IkQ+FayvFIpjvCua0V7OxV9XWIxYppfwT2kRiTZTYUmyDEZpTYUZ0hc9xO6qwh7/EfRL8yOyPVGFh1H0QmTOAQPmopfslf4A8vDNKbg0BDRaOJDm+ygjlDQsaGT++1ejWnj/tWePFOPspO85eqBZlankImRh29Vrwa7aMyStbGezm04AgjcEAg+xQUvBMVwt0EfqGhv5IhgNLcA6VWcIv1HRtPTELuB42ogQt/H+69fwKCx+xZz6tDvzTNjfEiH1spWHGnfVESyzqrNIoKbQ2HkNh9yp/b3houOYDdwLH/ADqi3/yCvrQKSDtxEDjavsPafY2D+aDnSeNohSdlf4ViF7IjdeEajdkNaLdt6Ap9wLh4ypC3SGRtYXOeC4vZt4bs0d66DkeSXcH0iNjvJp1fZoggg+WxPoiWcce1roMICnXqkALm7itUsh+J1HZjABfUjY43WTkndJBuHm4mPBlWWNzb1f6X/ZkGAyM99klvdtGuNrXtL5Rq2dpBsggbDpzUAng/aSRyNZIXMuVsYfqaXS+ENLW71os15Ak7lLOLB0bRACXSPA7yR25DNROkeQsk181HiQ1z6IqZmfIorSHWVxOItLNDqdbQ0ExtjhsnSBqLSdmb6QfCedqk8abbbHmfuop5OdifPYJHxl1R35H9CpJxzcpqy+9iIS3BivqHu9nSOI/BMHHxhEcDx9GLCzq2FgProFoXI+MLYxKkhy7QZJVImHkl8rjSKgJpEfhXpsKZySrL/eeyYMJpByjxhRD05rqiCUGkbDdLWRmyLjbsulpHKewCisROhYus7uQ2KU2HVJa+Xb2U2BN4Qpk1R3xuwucgE+ipvHMp3eNA+0FaZn2SkWdjDWD80hzMvSKoc4uG2GQZFM38kFJkagAFpxGTSw+iB4I4uAJ8lkRW+xoydVEP4XCe8cfRWA1t6pXi0HOr5KSbJoj1WxwMnZbM/m468HrapSRgaUqGTsiMbJtqcfgl1dnsg3KgmebC3bJbit3s3CIjm6WyVshpL+0bS7FkH+m/uIKatj2UHFYgYJB/03f0lAybi0T2VlJ4JHK+IET6WsJaGBjdOx/iHU7owZU1hjae483NY5jR/wBx/JQ8Edobqrwucddbhr/tD5EV6UnrX6xQOx8jzCxWZ2VrsxDmWTqIsg6dQ+EuHkeq1ZHaN420+Brdq5AbBDBukbnkN1ZA2xZxOUAho6c0qzI+9McQ5yyxs/7nAFbZEtku8zt6Jn2QxO8zoQdxGHyn/Ywgf/ZzVaKtpDWGNSR0mI7UOmwQMsfitMYGc1DOzdbC9G+yojezZEws2Qc8lBTwS7K8vCu7CWs2S+ceMI6J+yEl3eqw0y1OiOZ9BFQv2UU8eymibsufh1xsjterfSsUWRcRDIdlNw0+EKJ1V7LfAkGkId6GK2EPO5STiuXTm/zJhLk+IhVjjk1vb/MkuVUhjC3EIzZNQ9kZwSMBg9Etl+H2RPDZyIx6JJwdaGIzV7GDZac4ei8yDu31SrGnJld7JhK/dvqn+JSQvnfZjE/D7KbDd4VGXDT7L2B4pNKVoE40TQO8RRbnckBiu8RRzq2TEPBTKGNdsh+MyhuNITt4CPc7D81LqFJH2yzS3G0Nbq70hpNWGtG55dTW3ug5dRbKL0T8DxC5jiw0dZokkt5C9uRUjuItaSy26wL1RjWCfKuh+SRcFB0utrmjY64XglvMbgbn0pyaSR2AHvY5vR7iGSD/AHDr7BY4plX3uxflcVcXXqLj8xpr2WfXtQPz5qbKZFXjkYTdBwI1n1rn6oGXDbzjkaT5EhcQqAstulxHTmD5g8lYvo6f/nD1vGk9v2kf9kvixw9ul3xDmAQa+YPkU67CxCLLIraSB7Wf6Xag9w+5pRMf5IPiku1MvmN/dRZQ3W+NIPzQ2ZNutVJ9g0VoXZrtlPA7ZC5AsFEQ1Ss3aDpUwzGOyhe7xqTFcKKgkPjChP7iZL7QnId4VLCdlBkfCpYjsrtaFX6bWsWmsLFWitFSkyzS0xcohoCX69qXrHUsrvI2OqCDMdTvZLs9mpzfk5Eg72tXiz6Krt7JqiWvDXyWY+zQPktC7aljDWymnVEVs0xdnuKMml3b8ihWNo35qR266Ka8JasOdlHT7LbGydggSTVLaM0KV7kRSGmPk04lTy8QFhJWuNrV5JKJHLOIOWKMh+/ieyW8Zye8i0/MHbatihHONIzg0bnSADmOd9B5/PevvUZM03CmAzwjCDkJeGC2PbqFahsHMu65h2oEfiFk/C43u1SPJvb9rK38NLv0Vmzy0HRr7t1nVG8tey+dtLt+fRewkBgBdEQ12rlQcee4B35BJmO8lu0ipSdmYdJe0EgAm2hzxXy8/ZCO4FERYDgPPQ9qtM+YT4WDS3p1oE2hJs1rGkAkmjvzOquanRZZJlXZjMidqbOA5vQvaD6EEKycGzHa2yaWiiDYJIPSwfQ/ikuHOZQQaMkR8RPi1Acn78/XnyUrM4ucLqh9mwD86K66CO21/JeouJc/UrWTNtyruJk6mh45OAPv1/G1J9YNpx8mcjXhijSY5ysgUthlgBJJJyVuZ9lT5ZhOiHWHmbe6l+sjWFXoJ6XpyjqtSss7s541VFjy80UsHEBSrc05K8M+yMuTKqBPjxsef4iFirneFYrfVSI+miRBerFiRGj1YsWKyOMK2CxYpIPQtl6sUo49WwWLFYg9WNWLFJx7ImnZn99/sd+YWLFTL+ArzP8ARYt7e8pP9n9K56zmF6sSn6MqHiLjwb4ULm/E70KxYpRy/IR4P75/q7+ko6D4vZYsXMNL8v8Agb8K/cx/y/qUSV6sR4+GvD8UeLFixWRcxYsWK6IMKwrFikgxYsWKTj//2Q==");
                                    break;
                                case 43266:
                                    json.put("value", 9);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 1062813:
                                    json.put("value", 6.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://i.ytimg.com/vi/kIBQWfm0m-c/maxresdefault.jpg");
                                    break;
                                case 251721:
                                    json.put("value", 7.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://storage.cricingif.com/cig-live-images/player-images/11249.png");
                                    break;
                                case 1203995:
                                    json.put("value", 7);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://fantasysure.com/wp-content/uploads/2021/01/ErMOO67W8AAov-9.jpg");
                                    break;
                                case 1201886:
                                    json.put("value", 6.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 316363:
                                    json.put("value", 7.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 457249:
                                    json.put("value", 8.5);
                                    json.put("team", "MS");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                //Multan Sultans - end
                                //Islamabad United - start
                                case 249866:
                                    json.put("value", 10.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c156171/alex-hales.jpg");
                                    break;
                                case 922943:
                                    json.put("value", 11);
                                    json.put("team", "IU");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 232359:
                                    json.put("value", 9);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c170737/colin-munro.jpg");
                                    break;
                                case 681117:
                                    json.put("value", 10.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c148313/faheem-ashraf.jpg");
                                    break;
                                case 628240:
                                    json.put("value", 8.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Hussain-Talat-1-453x480.png");
                                    break;
                                case 494230:
                                    json.put("value", 9);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://www.indiafantasy.com/wp-content/uploads/Asif-Ali-Noor-Fatima-daughter.jpg");
                                    break;
                                case 1072472:
                                    json.put("value", 8.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.crictracker.com/wp-content/uploads/2020/02/Musa-Khan-1.jpg");
                                    break;
                                case 348154:
                                    json.put("value", 8);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c157137/zafar-gohar.jpg");
                                    break;
                                case 681305:
                                    json.put("value", 11);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c170804/hasan-ali.jpg");
                                    break;
                                case 362201:
                                    json.put("value", 8.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c180929/lewis-gregory.jpg");
                                    break;
                                case 669365:
                                    json.put("value", 9);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://bucketimg.datanethosting.us/wp-content/uploads/20210113144657/GettyImages-939199358-e1523011433956.jpg");
                                    break;
                                case 1092313:
                                    json.put("value", 7.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Batsman");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c155978/rohail-nazir.jpg");
                                    break;
                                case 461632:
                                    json.put("value", 7.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://storage.googleapis.com/cricketimages/Players/1I6.png");
                                    break;
                                case 480603:
                                    json.put("value", 9.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "All Rounder");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Iftikhar-Ahmed-1.png");
                                    break;
                                case 1185538:
                                    json.put("value", 6.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://www.pcb.com.pk/images/profile/51754.jpg");
                                    break;
                                case 964519:
                                    json.put("value", 6.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "data:image/jpeg;base64,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");
                                    break;
                                case 288992:
                                    json.put("value", 9.5);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://m.cricbuzz.com/a/img/v1/192x192/i1/c148144/chris-jordan.jpg");
                                    break;
                                case 1203669:
                                    json.put("value", 7);
                                    json.put("team", "IU");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://superstarsbio.com/wp-content/uploads/2020/02/Akif-Javed.png");
                                    break;
                                //Islamabad United - end
                                // Def Values - start
                                default:
                                    json.put("value", 0);
                                    json.put("team", "NT");
                                    json.put("playingRole", "Bowler");
                                    json.put("imageURL", "https://tnimage.s3.hicloud.net.tw/photos/2020/AP/20200306/1ee897a6cae34c9dace2404bf2b46bc5.jpg");
                                    break;
                                // Def Values - end
                            }
                            newJson.put(json);

                            //                            if(response.isNull("playingRole") == false || Arrays.stream(gms.playerNames).anyMatch(jsonObject.getString("name")::equals)) {
//                            }
                        }
                        newJson = gms.removeDuplicatesFromJSON(newJson);
                        PlayersStatsRef.setValue(newJson.toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}