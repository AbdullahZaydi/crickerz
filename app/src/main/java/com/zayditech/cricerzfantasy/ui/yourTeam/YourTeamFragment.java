package com.zayditech.cricerzfantasy.ui.yourTeam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zayditech.cricerzfantasy.CustomListViewAdapter;
import com.zayditech.cricerzfantasy.GeneralMethods;
import com.zayditech.cricerzfantasy.MainActivity;
import com.zayditech.cricerzfantasy.R;
import com.zayditech.cricerzfantasy.RowItem;
import com.zayditech.cricerzfantasy.ui.slideshow.SlideshowViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YourTeamFragment extends Fragment {

    private YourTeamViewModel YourTeamViewModel;
    private FirebaseDatabase database;
    private DatabaseReference teamRef;
    private FirebaseUser mFireBaseUser;
    ListView listView;
    List<RowItem> rowItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        YourTeamViewModel =
                new ViewModelProvider(this).get(YourTeamViewModel.class);
        View root = inflater.inflate(R.layout.fragment_yourteam, container, false);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getContext(), MainActivity.class));
            return root;
        }
        database = FirebaseDatabase.getInstance();
        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        GeneralMethods gms = new GeneralMethods(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            teamRef = database.getReference(gms.encodeIntoBase64(mFireBaseUser.getEmail()));
        }
        rowItems = new ArrayList<RowItem>();
        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        return root;
    }
}