package com.zayditech.cricerzfantasy.ui.leaderboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zayditech.cricerzfantasy.GeneralMethods;
import com.zayditech.cricerzfantasy.Leaderboard;
import com.zayditech.cricerzfantasy.LeaderboardList;
import com.zayditech.cricerzfantasy.MatchList;
import com.zayditech.cricerzfantasy.MatchListAdapter;
import com.zayditech.cricerzfantasy.R;
import com.zayditech.cricerzfantasy.leaderboardListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private LeaderboardViewModel YourTeamViewModel;
    private FirebaseDatabase database;
    private DatabaseReference teamRef;
    private FirebaseUser mFireBaseUser;
    DatabaseReference dataRef;
    ListView listView;
    List<MatchList> matchLists;
    GeneralMethods gms;
    leaderboardListAdapter adapter;
    List<LeaderboardList> LeaderboardList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        YourTeamViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_yourteam, container, false);
        listView = root.findViewById(R.id.list);
        LeaderboardList = new ArrayList<>();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        gms = new GeneralMethods(getActivity().getApplicationContext());
        dataRef = FirebaseDatabase.getInstance().getReference();
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                for (DataSnapshot snapshot : snap.getChildren()) {
                    if(snapshot.getKey().equals("Players") || snapshot.getKey().equals("PlayersStats") || snapshot.getKey().equals("Teams")) {
                        // DO NOTHING
                    }
                    else {
                        try {
                            JSONArray jsonArray = new JSONArray(snapshot.getValue(String.class));
                            String email = gms.decodeData(snapshot.getKey());
                            int totalPoints = 0;
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                totalPoints += json.has("totalPoints") ? json.getInt("totalPoints") : 0;
                            }
                            LeaderboardList listItem = new LeaderboardList(email, totalPoints);
                            LeaderboardList.add(listItem);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                adapter= new leaderboardListAdapter(getActivity().getApplicationContext(), R.layout.list_leaderboard, LeaderboardList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
}

