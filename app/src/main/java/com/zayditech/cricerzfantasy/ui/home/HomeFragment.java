package com.zayditech.cricerzfantasy.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.zayditech.cricerzfantasy.CustomListViewAdapter;
import com.zayditech.cricerzfantasy.R;
import com.zayditech.cricerzfantasy.RowItem;
import com.zayditech.cricerzfantasy.SliderAdapter;
import com.zayditech.cricerzfantasy.SliderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static final String[] titles = new String[] { "Strawberry",
            "Banana", "Orange", "Mixed", "Sample Data", "Sample Data 2" };
    private FirebaseDatabase database;
    private DatabaseReference PlayersStatsRef;
    public static final String[] descriptions = new String[] {
            "It is an aggregate accessory fruit",
            "It is the largest herbaceous flowering plant", "Citrus Fruit",
            "Mixed Fruits", "Sample" , "Sample" };

    public static final Integer[] images = { R.drawable.home_icon,
            R.drawable.mobile_icon, R.drawable.arrow_down, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery };

    ListView listView;
    List<RowItem> rowItems;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        PlayersStatsRef = database.getReference("PlayersStats");
        rowItems = new ArrayList<RowItem>();
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
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                "Item " + (position + 1) + ": " + rowItems.get(position),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
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

        SliderView sliderView = root.findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(getActivity().getApplicationContext());
        SliderItem newItem1 = new SliderItem("https://img2.pngio.com/index-of-assets-front-psl-assets-images-karachi-kings-png-374_287.png", "Karachi Kings");
        SliderItem newItem2 = new SliderItem("https://www.brandsynario.com/wp-content/uploads/lead-10.jpg", "Lahore Qalandars");
        SliderItem newItem3 = new SliderItem("https://www.brandsynario.com/wp-content/uploads/ISLAMABAD-UNITED-LOGO.jpg", "Islamabad United");
        SliderItem newItem4 = new SliderItem("https://i.pinimg.com/originals/b7/4e/81/b74e8108e7ab69bd05793156c2158a5b.jpg", "Multan Sultans");
        SliderItem newItem5 = new SliderItem("https://www.brandsynario.com/wp-content/uploads/lead-quetta-gladiators.jpg", "Quetta Gladiators");
        SliderItem newItem6 = new SliderItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzrK8dV9wixvirUWxMtDnoQaJfWYD46d2ZSw&usqp=CAU", "Pehsawar Zalmi");
        adapter.addItem(newItem1);
        adapter.addItem(newItem2);
        adapter.addItem(newItem3);
        adapter.addItem(newItem4);
        adapter.addItem(newItem5);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        return root;
    }
}
