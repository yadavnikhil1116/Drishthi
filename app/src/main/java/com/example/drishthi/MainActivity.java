package com.example.drishthi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_CODE = 100;
    private static final int PERMISSION_CODE_MESSAGE = 101;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private DatabaseReference databaseReference;
    private FloatingActionButton floatingActionButton, floatingActionButtonMessage;
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButtonMessage = findViewById(R.id.floatingActionButtonMessage);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ContactsActivity.class);
                startActivity(i);
            }
        });

        floatingActionButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSendPermission();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        CheckLocationPermission();
    }

    private void checkSendPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendMessage();
        } else {
            RequestSendPermission();
        }
    }

    private void RequestSendPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_CODE_MESSAGE);
    }

    private void sendMessage() {
        smsManager = SmsManager.getDefault();
        String message = "My location: https://www.google.com/maps/search/?api=1&query=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        String phoneNumber = "+91 8950186507";
        smsManager.sendTextMessage (phoneNumber, null, message, null, null);
        Toast.makeText(this, "Message Sended...", Toast.LENGTH_SHORT).show();
    }

    private void CheckLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            RequestPermission();
        }
    }

    private void RequestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("location", String.valueOf(location));
                    currentLocation = location;
                    HashMap l = new HashMap();
                    l.put("Latitude", currentLocation.getLatitude());
                    l.put("Longitude", currentLocation.getLongitude());
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(currentUser.getUid()).updateChildren(l).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(MainActivity.this, "Location updated...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation();
                Toast.makeText(this, "Permissions Accepted...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permissions Denied...", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == PERMISSION_CODE_MESSAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendMessage();
                Toast.makeText(this, "Permissions Accepted...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permissions Denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Geocoder geocoder = new Geocoder(this);
        try {
            ArrayList<Address> addressArrayList = (ArrayList<Address>) geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Toast.makeText(this, addressArrayList.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.addMarker(markerOptions);
        googleMap.addCircle(
                new CircleOptions()
                        .center(latLng)
                        .radius(300)
                        .fillColor(ContextCompat.getColor(getBaseContext(), R.color.blue_lighter))
                        .strokeColor(ContextCompat.getColor(getBaseContext(), R.color.blue_lighter))
        );
    }
}