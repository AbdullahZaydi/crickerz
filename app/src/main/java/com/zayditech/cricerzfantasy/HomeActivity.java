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

import androidx.annotation.NonNull;
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference TeamRef;
    int totalPoints = 0;
    JSONArray jsonArray;
    DatabaseReference userTeam;
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
            GeneralMethods gms = new GeneralMethods(this);
            userTeam = database.getReference(gms.encodeIntoBase64(mFirebaseUser.getEmail()));
            TextView nav_transfers = headerView.findViewById(R.id.nav_transfers);
            TextView nav_points = headerView.findViewById(R.id.nav_points);
            PlayersStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot playerSnap) {
                    userTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                jsonArray = new JSONArray(snapshot.getValue(String.class));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    if(json.has("transfers")) {
                                        nav_transfers.setText(String.valueOf(json.getInt("transfers")));
                                    }
                                    else {
                                        nav_transfers.setText("90");
                                    }

                                    if(json.has("name")) {
                                        try {
                                            JSONArray jsonArr = new JSONArray(playerSnap.getValue(String.class));
                                            int index = gms.findInJSONArray(jsonArr, json.getString("name"));
                                            JSONObject jsonAtIndex = jsonArr.getJSONObject(index);
                                            if(!json.has("totalPoints")) {
                                                json.put("totalPoints", jsonAtIndex.getInt("totalPoints"));
                                            }
                                            else {
                                                json.remove("totalPoints");
                                                json.put("totalPoints", jsonAtIndex.getInt("totalPoints"));
                                            }
                                            jsonArray = gms.updateJsonArray(jsonArray, json);
                                            totalPoints += jsonAtIndex.getInt("totalPoints");
                                            nav_points.setText(String.valueOf(totalPoints));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                userTeam.setValue(jsonArray.toString());
                            }
                            catch (Exception ex) {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            if(mFirebaseUser.getEmail().equals("crickerzpsl@gmail.com")) {
//                API_Data_Fetcher data_fetcher = new API_Data_Fetcher(getApplicationContext());
//                data_fetcher.execute();
//            }
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
}