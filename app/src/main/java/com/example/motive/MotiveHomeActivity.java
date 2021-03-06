package com.example.motive;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import android.location.Address;

import static com.example.motive.RegisterActivity.TAG1;


public class MotiveHomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView myProfileIcon;
    ImageView settingsIcon;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ImageView home, events, mental, messages;


    List<Fragment> FragmentList;

    HashMap<String, Object> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivehome);
        FindViewsById();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Bundle
        Bundle dbfields = this.getIntent().getExtras();
        if (dbfields != null) {
            user = (HashMap<String,Object>)dbfields.getSerializable("user");
        }

        myProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(),
                        ProfileActivity.class);
                startActivity(myIntent);
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(),
                        SettingsActivity.class);
                startActivity(myIntent);
            }
        });



        FragmentList = new ArrayList<Fragment>();

        SupportMapFragment map = new com.google.android.gms.maps.SupportMapFragment();
        map.getMapAsync(this);

        FragmentList.add(map);
        FragmentList.add(new EventsFragment());
        FragmentList.add(new MentalFragment());
        FragmentList.add(new MessagesFragment());

        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter = new com.example.motive.PagerAdapter(getSupportFragmentManager(), FragmentList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.home_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.events_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.mental_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.messages_icon);

    }

    private void FindViewsById() {
        myProfileIcon = findViewById(R.id.profileButton);
        settingsIcon = findViewById(R.id.settingsImageView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        home = findViewById(R.id.tabHome);
        events = findViewById(R.id.tabEvents);
        mental = findViewById(R.id.tabMental);
        messages = findViewById(R.id.tabMessages);
    }


   @Override
    protected void onStart() {
        super.onStart();
       if (mainMap != null) {
           mainMap.clear();
           onMapReady(mainMap);
       }
   }


    @Override
    protected void onResume() {
        super.onResume();
        if (mainMap != null) {
            mainMap.clear();
            onMapReady(mainMap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //Rotation of device
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Google Maps Method

    GoogleMap mainMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mainMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //ZOOM IN ON CURRENT USER
      //  userID = fAuth.getCurrentUser().getUid();
       // DocumentReference documentReference = fStore.collection("users").document(userID);
        // documentReference.get(mainMap.animateCamera(CameraUpdateFactory.zoomBy(15))

        // Markers and camera zoom
        final Context context = this;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

               // mainMap.animateCamera(CameraUpdateFactory.zoomBy(17));

                //Custom Alert Dialogue

                final View customAlertView = getLayoutInflater().inflate(R.layout.custompopup, null);
                final AlertDialog.Builder customAlert = new AlertDialog.Builder(context)
                        .setView(customAlertView);

                TextView alertUserName = customAlertView.findViewById(R.id.alertUserName);
                alertUserName.setText(marker.getTitle());
                TextView alertMotive = customAlertView.findViewById(R.id.alertMotive);
                alertMotive.setText(marker.getSnippet());

                Button alertButton = customAlertView.findViewById(R.id.alertBtnViewProfile);
                alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "View Profile", Toast.LENGTH_SHORT).show();

                        Intent myIntent = new Intent(getBaseContext(), ProfileActivity.class);
                        Bundle extras = new Bundle();
                        extras.putSerializable("userName", marker.getTitle());
                        myIntent.putExtras(extras);
                        startActivity(myIntent);

                    }


                });

                customAlert.show();

                return false;
            }
        });

        CollectionReference collectionReferenceUsers = fStore.collection("users");

        collectionReferenceUsers.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                List<DocumentSnapshot> documents = documentSnapshots.getDocuments();

                for (int i = 0; i < documents.size(); i++) {
                    double lat = documents.get(i).getDouble("lat");
                    double lng = documents.get(i).getDouble("lng");

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions marker = new MarkerOptions().position(latLng).title(documents.get(i).getString("username")).snippet(documents.get(i).getString("main motive")).icon(BitmapDescriptorFactory.fromResource(R.drawable.motive_marker));
//                    marker.
                    mainMap.addMarker(marker);
                    if (i == 0){
                        //Camera zooms in on start up
                        mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                    }
                }
            }
        });

    }

}


