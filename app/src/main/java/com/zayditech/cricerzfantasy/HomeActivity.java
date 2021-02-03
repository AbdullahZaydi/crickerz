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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        TeamRef = database.getReference("Teams");
        PlayersRef = database.getReference("Players");
        PlayersStatsRef = database.getReference("PlayersStats");
        View headerView = navigationView.getHeaderView(0);
        TextView nav_username = headerView.findViewById(R.id.nav_username);
        TextView nav_email = headerView.findViewById(R.id.nav_email);
        nav_username.setText(mFirebaseUser.getDisplayName().equals(null)
                || mFirebaseUser.getDisplayName().equals("") ? mFirebaseUser.getEmail().split("@")[0]
                : mFirebaseUser.getDisplayName());
        nav_email.setText(mFirebaseUser.getEmail());
        if(mFirebaseUser.getEmail().equals("crickerzpsl@gmail.com")) {
            API_Data_Fetcher data_fetcher = new API_Data_Fetcher();
            data_fetcher.execute();
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
                String newJson = "";
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
                        newJson += jsonObject.toString()+ ",";
                    }
                }
                TeamRef.setValue(newJson);
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
                    for (String name: gms.playerNames) {
                        if(jsonObject.getString("name").equals(name)) {
                            newJson.put(jsonObject);
                        }
                    }
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
                            json.put("name", response.getString("fullName"));
                            json.put("playingRole", response.getString("playingRole"));
                            json.put("age", response.getString("currentAge"));
                            json.put("imageURL", response.getString("imageURL"));
                            json.put("profile", response.getString("profile"));
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