package com.zayditech.cricerzfantasy.ui.prizes;

import android.content.Intent;
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
import com.sdsmdg.tastytoast.TastyToast;
import com.zayditech.cricerzfantasy.CreateTeamActivity;
import com.zayditech.cricerzfantasy.GeneralMethods;
import com.zayditech.cricerzfantasy.MainActivity;
import com.zayditech.cricerzfantasy.R;
import com.zayditech.cricerzfantasy.RowItem;
import com.zayditech.cricerzfantasy.YourTeamListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrizesFragment extends Fragment {

    private PrizesViewModel YourTeamViewModel;
    private FirebaseDatabase database;
    private DatabaseReference teamRef;
    private FirebaseUser mFireBaseUser;
    ListView listView;
    List<RowItem> rowItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        YourTeamViewModel =
                new ViewModelProvider(this).get(PrizesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prizes, container, false);
        return root;
    }
}