package mitigate.transportation.user;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, RatingDialogListener {

    private GoogleMap googleMap;
    GPSTracker gpsTracker;
    Location location;
    double latitude;
    double longitude;
    LatLng latLng;
    String s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;
    ArrayList<MarkerData> markersArray = new ArrayList<>();

    AutoCompleteTextView pickup_location, dropoff_location;
    TextView logout, get_driver_text, car_model, car_color, number_plate, started_text;
    LinearLayout get_driver;
    ProgressBar progressBar;
    LinearLayout non_assigned_layout, assigned_layout;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(AppConstants.DATABASENAME);

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String prefs = "user_credentials";
    String driverUid;
    AssignModel assignModel;
    String pickup_selected_title, dropoff_selected_title;
    double pickup_selected_lat, pickup_selected_lng, dropoff_selected_lat, dropoff_selected_lng;

    String item[] = {
            "12th Residential College , University of Malaya", "5th Residential College , University of Malaya",
            "11th Residential College , University of Malaya", "API , University of Malaya",
            "Faculty of Computer Science , University of Malaya", "Main Bus stop , University of Malaya",
            "Main Library , University of Malaya", "First Residential College , University of Malaya",
            "Faculty of Engineering, University of Malaya", "Faculty of Economics, University of Malaya",
            "7th Residential College , University of Malaya"
    };

    double mine_longi;
    double mine_lati;
    UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        setContentView(R.layout.activity_main);

        pickup_location = findViewById(R.id.pickup_location);
        dropoff_location = findViewById(R.id.dropoff_location);
        logout = findViewById(R.id.logout);
        get_driver = findViewById(R.id.get_driver);
        get_driver_text = findViewById(R.id.get_driver_text);
        assigned_layout = findViewById(R.id.assigned_layout);
        non_assigned_layout = findViewById(R.id.non_assigned_layout);
        car_color = findViewById(R.id.car_color);
        car_model = findViewById(R.id.car_model);
        number_plate = findViewById(R.id.number_plate);
        progressBar = findViewById(R.id.progress_bar);
        started_text = findViewById(R.id.started_text);

        currentUserModel = new Gson().fromJson(sharedPreferences.getString("User", ""), UserModel.class);

        s0 = "12th Residential College , University of Malaya";
        s1 = "5th Residential College , University of Malaya";
        s2 = "11th Residential College , University of Malaya";
        s3 = "API , University of Malaya";
        s4 = "Faculty of Computer Science , University of Malaya";
        s5 = "Main Bus stop , University of Malaya";
        s6 = "Main Library , University of Malaya";
        s7 = "First Residential College , University of Malaya";
        s8 = "Faculty of Engineering, University of Malaya";
        s9 = "Faculty of Economics, University of Malaya";
        s10 = "7th Residential College , University of Malaya";

        markersArray.add(new MarkerData(s0, 0, 0));
        markersArray.add(new MarkerData(s1, 0, 0));
        markersArray.add(new MarkerData(s2, 0, 0));
        markersArray.add(new MarkerData(s3, 0, 0));
        markersArray.add(new MarkerData(s4, 0, 0));
        markersArray.add(new MarkerData(s5, 0, 0));
        markersArray.add(new MarkerData(s6, 0, 0));
        markersArray.add(new MarkerData(s7, 0, 0));
        markersArray.add(new MarkerData(s8, 0, 0));
        markersArray.add(new MarkerData(s9, 0, 0));
        markersArray.add(new MarkerData(s10, 0, 0));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pickup_location.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item));
        dropoff_location.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item));

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickup_location.showDropDown();
            }
        });
        dropoff_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropoff_location.showDropDown();
            }
        });

        pickup_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < markersArray.size(); j++) {
                    if (item[i].equals(markersArray.get(j).getTitle())) {
                        pickup_selected_title = item[i];
                        pickup_selected_lat = markersArray.get(j).getLatitude();
                        pickup_selected_lng = markersArray.get(j).getLongitude();
                    }

                }
            }
        });

        dropoff_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < markersArray.size(); j++) {
                    if (item[i].equals(markersArray.get(j).getTitle())) {
                        dropoff_selected_title = item[i];
                        dropoff_selected_lat = markersArray.get(j).getLatitude();
                        dropoff_selected_lng = markersArray.get(j).getLongitude();
                    }

                }
            }
        });

        get_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pickup_location.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter your pickup Location First!", Toast.LENGTH_SHORT).show();
                } else if (dropoff_location.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter your dropoff Location First!", Toast.LENGTH_SHORT).show();
                } else {
                    get_driver_text.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    AssignModel assignModel = new AssignModel(null, currentUserModel.getUserID(), myRef.push().getKey(), pickup_location.getText().toString()
                            , dropoff_location.getText().toString(), null, null, null, currentUserModel.name, currentUserModel.getEmail(), null
                            , pickup_selected_lat, pickup_selected_lng, dropoff_selected_lat, dropoff_selected_lng);

                    FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.REQUESTS).child(assignModel.getId()).setValue(assignModel);

                    get_driver.setEnabled(false);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
                mEditor.clear().apply();
            }
        });

        getdriverwihcar();

    }

    @Override
    public void onMapReady(GoogleMap mMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap = mMap;
//        googleMap.setMyLocationEnabled(true);

//        location = gpsTracker.getLocation();
//
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();

        // Creating a marker
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.title("Me");
//        mine_lati = markerOptions.getPosition().latitude;
//        mine_longi = markerOptions.getPosition().longitude;
//
////         Setting the position for the marker
//        markerOptions.position(latLng);

//        googleMap.addMarker(markerOptions);

        for (int i = 0; i < markersArray.size(); i++) {
            getLatlong(markersArray.get(i).getTitle(), i);

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(markersArray.get(i).getTitle()));
        }

        latLng = new LatLng(markersArray.get(1).getLatitude(), markersArray.get(1).getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)        // Sets the center of the map to Mountain View
                .zoom(15)              // Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(0)               // Sets the tilt of the camera to 30 degrees
                .build();              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void getLatlong(String name, int i) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(name, 1);
            if (!addresses.isEmpty()) {
                Address obj = addresses.get(0);
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                markersArray.remove(i);
                markersArray.add(i, new MarkerData(name, lat, lng));

            } else {

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void getdriverwihcar() {
        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.CURRENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    assignModel = snapshot.getValue(AssignModel.class);
                    if (assignModel.getUser_uid().equals(currentUserModel.getUserID())) {
                        driverUid = assignModel.getDriver_uid();
                        if (assignModel.getStatus().equals("1")) {

                            get_driver_text.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            get_driver.setEnabled(true);
                            get_driver.setVisibility(View.GONE);
                            assigned_layout.setVisibility(View.VISIBLE);
                            non_assigned_layout.setVisibility(View.GONE);
                            pickup_location.setText("");
                            dropoff_location.setText("");

                            car_color.setText(assignModel.getCar_color());
                            car_model.setText(assignModel.getCar_model());
                            number_plate.setText(assignModel.getCar_plate());

                        } else if (assignModel.getStatus().equals("2")) {
                            started_text.setText("Ride Started");
                            assigned_layout.setVisibility(View.VISIBLE);
                            non_assigned_layout.setVisibility(View.GONE);
                            get_driver.setVisibility(View.GONE);
                        } else if (assignModel.getStatus().equals("3")) {
                            showDialog();
                        }
                    } else {
                        non_assigned_layout.setVisibility(View.VISIBLE);
                        assigned_layout.setVisibility(View.GONE);
                        started_text.setText("Ride Assigned");
                        get_driver.setVisibility(View.VISIBLE);
                        get_driver_text.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        get_driver.setEnabled(true);
                    }
                }
                if (dataSnapshot.getValue() == null) {
                    non_assigned_layout.setVisibility(View.VISIBLE);
                    assigned_layout.setVisibility(View.GONE);
                    started_text.setText("Ride Assigned");
                    get_driver.setVisibility(View.VISIBLE);
                    get_driver_text.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    get_driver.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void insertPast(AssignModel assign) {
        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.CURRENT).child(assign.getId()).removeValue();
        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.PAST).child(assign.getId()).setValue(assign);

    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this Driver")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.colorPrimary)
                .setNoteDescriptionTextColor(R.color.colorPrimary)
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.colorPrimary)
                .setCommentTextColor(R.color.colorPrimary)
                .setCommentBackgroundColor(R.color.white)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(Objects.requireNonNull(MainActivity.this))
//                .setTargetFragment(this, 0) // only if listener is implemented by fragment
                .show();


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(final int i, final String s) {
        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.DRIVERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DriverModel driver = snapshot.getValue(DriverModel.class);
                    if (Objects.requireNonNull(driver).getUid().equals(driverUid)) {
                        driver.setComment(s);
                        driver.setRating(i);
                        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.DRIVERS).child(driverUid).setValue(driver);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        insertPast(assignModel);
    }
}
