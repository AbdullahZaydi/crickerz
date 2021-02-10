package com.zayditech.cricerzfantasy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.tastytoast.TastyToast;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Tab2Fragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference teamRef;
    private FirebaseUser mFireBaseUser;
    private String JSONStr;
    private boolean captainSelected = false;
    View _view;
    GeneralMethods gms;
    MaterialCardView btnCard;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        _view = view;
        gms= new GeneralMethods(getActivity().getApplicationContext());
        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            teamRef = database.getReference(gms.encodeIntoBase64(mFireBaseUser.getEmail()));
        }
        btnCard = view.findViewById(R.id.createTeamBtn);
    }

    protected void displayReceivedData(String message)
    {
        List<PlayerList> rowItems = new ArrayList<PlayerList>();
        JSONArray playersArray = new JSONArray();
        ListView listView = (ListView) _view.findViewById(R.id.list);
        TextView txtBudget = _view.findViewById(R.id.budget);
        try {
            JSONArray jsonArray = new JSONArray(message);
            JSONStr = message;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PlayerList item = new PlayerList(jsonObject.getString("imageURL"),
                        jsonObject.getString("name"),
                        jsonObject.getString("playingRole"), jsonObject.getInt("value"));
                rowItems.add(item);
            }
            btnCard.setOnClickListener(v -> {
                if(jsonArray.length() >= 10) {
                    teamRef.setValue(message);
                    TastyToast.success(getActivity().getApplicationContext(),"Your Team has been created!",
                            TastyToast.LENGTH_SHORT,
                            TastyToast.SHAPE_RECTANGLE,
                            false);
                    startActivity(new Intent(getContext(), HomeActivity.class));
                }
                else {
                    TastyToast.error(getActivity().getApplicationContext(),"Please select 11 players first!",
                            TastyToast.LENGTH_SHORT,
                            TastyToast.SHAPE_RECTANGLE,
                            false);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CreateTeamAdapter adapter = new CreateTeamAdapter(getActivity().getApplicationContext(),
                R.layout.list_item_4, rowItems, message);
        listView.setAdapter(adapter);
    }
}