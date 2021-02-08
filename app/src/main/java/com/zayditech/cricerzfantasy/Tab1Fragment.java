package com.zayditech.cricerzfantasy;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class Tab1Fragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference PlayersStatsRef;
    SendMessage SM;
    onTabChangeClickListener TabChangedListener;
    ListView listView;
    List<PlayerList> rowItems;
    JSONArray playersArray;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        PlayersStatsRef = database.getReference("PlayersStats");
        rowItems = new ArrayList<PlayerList>();
        playersArray = new JSONArray();
        PlayersStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PlayerList item = new PlayerList(jsonObject.getString("imageURL"),
                                jsonObject.getString("name"),
                                jsonObject.getString("playingRole"), jsonObject.getInt("value"));
                        rowItems.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listView = (ListView) container.findViewById(R.id.list);
                TeamListAdapter adapter = new TeamListAdapter(getActivity().getApplicationContext(),
                        R.layout.list_item_2, rowItems, value, SM);
                listView.setAdapter(adapter);
                adapter.setOnBudgetChangeListener(budget -> {
                    TextView txtBudget = container.findViewById(R.id.budget);
                    txtBudget.setText(String.valueOf(budget));
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> TabChangedListener.changeTab(true));
    }

    interface SendMessage {
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
            TabChangedListener = (onTabChangeClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    interface onTabChangeClickListener {
        void changeTab(boolean shouldTabChange);
    }

}
