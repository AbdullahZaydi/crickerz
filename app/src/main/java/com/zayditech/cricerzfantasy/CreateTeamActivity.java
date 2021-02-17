package com.zayditech.cricerzfantasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateTeamActivity extends AppCompatActivity  implements Tab1Fragment.SendMessage, Tab1Fragment.onTabChangeClickListener {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseReference teamRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_team);
        this.getSupportActionBar().hide();
        String teamToShow = getIntent().getStringExtra("TeamName") == null ?
                "All" : getIntent().getStringExtra("TeamName");
        GeneralMethods gms = new GeneralMethods(getApplicationContext());
        teamRef = FirebaseDatabase.getInstance().getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager(), teamToShow);
        adapter.addFragment(new Tab1Fragment(), "Select Players");
        adapter.addFragment(new Tab2Fragment(), "Create Team");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        CircleImageView kk_image = findViewById(R.id.ic_kk);
        CircleImageView lq_image = findViewById(R.id.ic_lq);
        CircleImageView pz_image = findViewById(R.id.ic_pz);
        CircleImageView ms_image = findViewById(R.id.ic_ms);
        CircleImageView iu_image = findViewById(R.id.ic_iu);
        CircleImageView qg_image = findViewById(R.id.ic_qg);

//        kk_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "KK"));
//            finish();
//        });
//
//        lq_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "LQ"));
//            finish();
//        });
//
//        pz_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "PZ"));
//            finish();
//        });
//
//        ms_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "MS"));
//            finish();
//        });
//
//        iu_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "IU"));
//            finish();
//        });
//
//        qg_image.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("TeamName", "QG"));
//            finish();
//        });

        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value != null) {
                    TextView titleTxt = findViewById(R.id.toolbar_title);
                    titleTxt.setText("Modify Team");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void sendData(String message) {
        Tab2Fragment f = (Tab2Fragment) getSupportFragmentManager().getFragments().toArray()[1];
        f.displayReceivedData(message);
    }

    @Override
    public void changeTab(boolean shouldTabChange) {
        viewPager.setCurrentItem(1, true);
    }
}