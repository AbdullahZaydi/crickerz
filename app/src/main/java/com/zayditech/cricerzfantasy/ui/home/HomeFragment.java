package com.zayditech.cricerzfantasy.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.zayditech.cricerzfantasy.CreateTeamActivity;
import com.zayditech.cricerzfantasy.CustomListViewAdapter;
import com.zayditech.cricerzfantasy.GeneralMethods;
import com.zayditech.cricerzfantasy.MainActivity;
import com.zayditech.cricerzfantasy.R;
import com.zayditech.cricerzfantasy.RowItem;
import com.zayditech.cricerzfantasy.SliderAdapter;
import com.zayditech.cricerzfantasy.SliderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseDatabase database;
    private DatabaseReference PlayersStatsRef;
    private DatabaseReference Teams;
    DatabaseReference CreatedTeamRef;
    ListView listView;
    List<RowItem> rowItems;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        PlayersStatsRef = database.getReference("PlayersStats");
        Teams = database.getReference("Teams");
        rowItems = new ArrayList<RowItem>();
        GeneralMethods gms = new GeneralMethods(getContext());
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            CreatedTeamRef = database.getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        }
        MaterialCardView card = root.findViewById(R.id.createTeamCard);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(getActivity().getApplicationContext(), CreateTeamActivity.class));
                }
                else {
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    Toast.makeText(getContext(), "Please Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PlayersStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        RowItem item = new RowItem(jsonObject.getString("imageURL"),
                                jsonObject.getString("name"),
                                jsonObject.getString("playingRole"));
                        rowItems.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listView = (ListView) root.findViewById(R.id.list);
                CustomListViewAdapter adapter = new CustomListViewAdapter(getActivity().getApplicationContext(),
                        R.layout.list_item, rowItems);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
//                                "Item " + (position + 1) + ": " + rowItems.get(position),
//                                Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//                        toast.show();
                    }
                });
//                for (int i = 0; i < titles.length; i++) {
//                    RowItem item = new RowItem(images[i], titles[i], descriptions[i]);
//                    rowItems.add(item);
//                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        Teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                SliderView sliderView = root.findViewById(R.id.imageSlider);
                SliderAdapter adapter = new SliderAdapter(getActivity().getApplicationContext());
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String dateStr = json.getString("dateTimeGMT").split("T")[0].replace("-", "/");
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
                        String day = (String) android.text.format.DateFormat.format("EEEE", date);
                        SliderItem sliderItem1 = new SliderItem(
                                json.getString("team-1"),
                                json.getString("team-2"),
                                dateStr,
                                day,
                                "No Text");
                        adapter.addItem(sliderItem1);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                sliderView.setSliderAdapter(adapter);
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//        sliderView.setIndicatorSelectedColor(Color.WHITE);
//        sliderView.setIndicatorUnselectedColor(Color.GRAY);
                sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                sliderView.startAutoCycle();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            CreatedTeamRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String encoded = gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    String value = dataSnapshot.getValue(String.class);
                    if(value != null) {
                        TextView createText = container.findViewById(R.id.createText);
                        if(createText != null) {
                            createText.setText("Change Team");
                            TextView supportingText = container.findViewById(R.id.supportingText);
                            supportingText.setText("Modify Your Team\nYou can modify your team\nAs much as you can\nUntil new psl has started");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
        CountdownView mCvCountdownView = root.findViewById(R.id.mycountdown);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String countDate = "20-02-2021 14:00:00";
        Date now = new Date();


        try {
            //Formatting from String to Date
            Date date = sdf.parse(countDate);
            long currentTime = now.getTime();
            long newYearDate = date.getTime();
            long countDownToNewYear = newYearDate - currentTime;
            mCvCountdownView.start(countDownToNewYear);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return root;
    }
}