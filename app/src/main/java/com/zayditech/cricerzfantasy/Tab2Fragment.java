package com.zayditech.cricerzfantasy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Tab2Fragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference teamRef;
    private FirebaseUser mFireBaseUser;
    private String JSONStr;
    private String jsonData = "";
    JSONArray jsonArr;
    boolean notFound = false;
    private boolean captainSelected = false;
    View _view;
    int transfer;
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
        teamRef = database.getReference(gms.encodeIntoBase64(mFireBaseUser.getEmail()));
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
                    if(!jsonData.equals("")) {
                        try {
                            jsonArr = new JSONArray(jsonData);
                            teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        JSONArray oldJsonArray = new JSONArray(snapshot.getValue(String.class));
                                        for (int i = 0; i < oldJsonArray.length(); i++) {
                                            JSONObject oldJsonObjects = oldJsonArray.getJSONObject(i);
                                            JSONObject transfers = new JSONObject();
                                            if(!oldJsonObjects.has("transfers")) {
                                                notFound= true;
                                            }
                                            else {
                                                transfer = oldJsonObjects.getInt("transfers");
                                                notFound = false;
                                            }
                                        }

                                        JSONObject transfers = new JSONObject();
                                        if(notFound) {
                                            transfers.put("transfers", 90);
                                            jsonArr.put(transfers);
                                            teamRef.setValue(jsonArr.toString());
                                            TastyToast.makeText(getActivity().getApplicationContext(),"Your Team has been created!",
                                                    TastyToast.LENGTH_SHORT,
                                                    TastyToast.SUCCESS);
                                            startActivity(new Intent(getContext(), HomeActivity.class));
                                            getActivity().finish();
                                        }
                                        else {
                                            if(transfer > 90) {
                                                TastyToast.makeText(getActivity().getApplicationContext(),
                                                        "You've reached your maximum transfer limit. You can not change team now!",
                                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                                startActivity(new Intent(getContext(), HomeActivity.class));
                                                getActivity().finish();
                                            }
                                            else {
                                                transfer--;
                                                transfers.put("transfers", transfer);
                                                jsonArr.put(transfers);
                                                teamRef.setValue(jsonArr.toString());
                                                TastyToast.makeText(getActivity().getApplicationContext(),"Your Team has been created!",
                                                        TastyToast.LENGTH_SHORT,
                                                        TastyToast.SUCCESS);
                                                startActivity(new Intent(getContext(), HomeActivity.class));
                                                getActivity().finish();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        TastyToast.makeText(getActivity().getApplicationContext(),"Please select captain!",
                                TastyToast.LENGTH_SHORT,
                                TastyToast.ERROR);
                    }
                }
                else {
                    TastyToast.makeText(getActivity().getApplicationContext(),"Please select 11 players first!",
                            TastyToast.LENGTH_SHORT,
                            TastyToast.INFO);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CreateTeamAdapter adapter = new CreateTeamAdapter(getActivity().getApplicationContext(),
                R.layout.list_item_4, rowItems, message, this);
        listView.setAdapter(adapter);
    }
    public void setTeamJSON(String JSONData){
        this.jsonData = JSONData;
    }
}