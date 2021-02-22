package com.zayditech.cricerzfantasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class PlayersActivity extends AppCompatActivity {

    private DatabaseReference PlayerStats;
    private FirebaseDatabase database;
    List<MatchList> matchList;
    ListView listView;
    JSONArray jsonArray;
    GeneralMethods gms;
    private AlertDialog.Builder alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        database = FirebaseDatabase.getInstance();
        PlayerStats = database.getReference("PlayersStats");
        matchList = new ArrayList<>();
        gms = new GeneralMethods(this);
        alert = new AlertDialog.Builder(this);
        int uid = getIntent().getIntExtra("uid", 0);
        String[] teamsArr = getIntent().getStringExtra("team").split(",");
        PlayerStats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    jsonArray = new JSONArray(snapshot.getValue(String.class));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        if(json.getString("team").equals(teamsArr[0]) || json.getString("team").equals(teamsArr[1])) {
                            MatchList matchListItem = new MatchList(
                                    json.getString("name"),
                                    "",
                                    json.getString("playingRole"),
                                    json.getString("totalPoints"),
                                    uid);
                            matchList.add(matchListItem);
                        }
                    }

                    listView = (ListView) findViewById(R.id.list);
                    PlayersAdapter matchListAdapter = new PlayersAdapter(getApplicationContext(),
                            R.layout.list_team_1, matchList, snapshot.getValue(String.class));
                    listView.setAdapter(matchListAdapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        final EditText edittext = new EditText( PlayersActivity.this);
                        alert.setTitle("Set Player's Point");
                        alert.setView(edittext);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        edittext.setLayoutParams(lp);
                        edittext.setGravity(Gravity.TOP| Gravity.LEFT);
                        lp.setMargins(10, 10, 10, 10);
                        edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_CLASS_NUMBER);
                        edittext.setLines(1);
                        edittext.setMaxLines(1);
                        alert.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(!String.valueOf(edittext.getText().toString()).equals("")) {
                                    int pointsToAdd = parseInt(edittext.getText().toString());
                                    edittext.requestFocus();
                                    matchList.get(position).setTime(String.valueOf(parseInt(matchList.get(position).getTime()) + pointsToAdd));
                                    matchListAdapter.notifyDataSetChanged();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            JSONObject json = jsonArray.getJSONObject(i);
                                            if(json.getString("name").equals(matchList.get(position).getFirstTeam())) {
                                                int totalPoints = json.getInt("totalPoints");
                                                json.remove("totalPoints");
                                                json.put("totalPoints", (totalPoints + pointsToAdd));
                                                jsonArray = gms.updateJsonArray(jsonArray, json);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    edittext.setError("A number is required here");
                                    edittext.requestFocus();
                                }
                            }
                        });

                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        alert.show();
                    });

                    MaterialCardView btnSave = findViewById(R.id.savePoints);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlayerStats.setValue(jsonArray.toString());
                            TastyToast.makeText(getApplicationContext(), "Points Have Been Added successfully",
                                    TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}