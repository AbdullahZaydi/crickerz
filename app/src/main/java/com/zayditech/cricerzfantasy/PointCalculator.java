package com.zayditech.cricerzfantasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PointCalculator extends AppCompatActivity {

    DatabaseReference Teams;
    FirebaseDatabase database;
    List<MatchList> matchList;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_calculator);
        database = FirebaseDatabase.getInstance();
        Teams = database.getReference("Teams");
        matchList = new ArrayList<>();
        Teams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    JSONArray jsonArray = new JSONArray(snapshot.getValue(String.class));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String date = json.getString("dateTimeGMT").split("T")[0].replace("-", "/");
                        String time = json.getString("dateTimeGMT").split("T")[1];
                        MatchList matchListItem = new MatchList(
                            json.getString("team-1"),
                            json.getString("team-2"),
                            date,
                            time,
                            json.getInt("unique_id"));
                        matchList.add(matchListItem);
                    }

                    listView = (ListView) findViewById(R.id.list);
                    MatchListAdapter matchListAdapter = new MatchListAdapter(getApplicationContext(),
                            R.layout.list_team, matchList, snapshot.getValue(String.class));
                    listView.setAdapter(matchListAdapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Intent gotoPlayers = new Intent(getApplicationContext(), PlayersActivity.class);
                        gotoPlayers.putExtra("uid", matchList.get(position).getUnique_id());
                        String team1 = "";
                        String team2 = "";
                        if(matchList.get(position).getFirstTeam().toLowerCase().equals("lahore qalandars")) {
                            team1 = "LQ";
                        }
                        else if (matchList.get(position).getFirstTeam().toLowerCase().equals("islamabad united")) {
                            team1= "IU";
                        }
                        else if (matchList.get(position).getFirstTeam().toLowerCase().equals("peshawar zalmi")) {
                            team1= "PZ";
                        }
                        else if (matchList.get(position).getFirstTeam().toLowerCase().equals("quetta gladiators")) {
                            team1= "QG";
                        }
                        else if (matchList.get(position).getFirstTeam().toLowerCase().equals("karachi kings")) {
                            team1= "KK";
                        }
                        else if (matchList.get(position).getFirstTeam().toLowerCase().equals("multan sultans")) {
                            team1= "MS";
                        }

                        if(matchList.get(position).getSecondTeam().toLowerCase().equals("lahore qalandars")) {
                            team2 = "LQ";
                        }
                        else if (matchList.get(position).getSecondTeam().toLowerCase().equals("islamabad united")) {
                            team2= "IU";
                        }
                        else if (matchList.get(position).getSecondTeam().toLowerCase().equals("peshawar zalmi")) {
                            team2= "PZ";
                        }
                        else if (matchList.get(position).getSecondTeam().toLowerCase().equals("quetta gladiators")) {
                            team2= "QG";
                        }
                        else if (matchList.get(position).getSecondTeam().toLowerCase().equals("karachi kings")) {
                            team2= "KK";
                        }
                        else if (matchList.get(position).getSecondTeam().toLowerCase().equals("multan sultans")) {
                            team2= "MS";
                        }
                        String teamsArr = team1 + "," + team2;
                        gotoPlayers.putExtra("team", teamsArr);
                        startActivity(gotoPlayers);
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