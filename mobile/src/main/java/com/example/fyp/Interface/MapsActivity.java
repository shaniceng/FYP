package com.example.fyp.Interface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fyp.Common;
import com.example.fyp.Model.IGoogleAPIService;
import com.example.fyp.ParkAdapter;
import com.example.fyp.ParkList;
import com.example.fyp.ParkName;
import com.example.fyp.R;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class MapsActivity extends Fragment implements ParkAdapter.OnParkListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap,gMap;
    private GoogleApiClient mGoogleApiClient;

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    private static final String TAG=MapsActivity.class.getSimpleName();

    private double latitude,longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    private CameraPosition cameraPosition;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ParkAdapter mAdapter;
    private ArrayList<ParkName> ParkDetails= new ArrayList<>();
    private ArrayList<String> parkname;
    private ArrayList<Double> parkdistance;

    LinkedHashMap<String, Double> mParkList;

    private ImageButton park,gym,stadium;

    private MapView mapView;
    private GoogleMap googleMap;

    IGoogleAPIService mService;
    LatLng latLng;
    Double distance;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_map, container, false);


        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        //Init Service
        mService = Common.getGoogleAPIServices();
        //mRecyclerView=v.findViewById(R.id.recyclerView);


        //Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        park = v.findViewById(R.id.parkbutton);
        gym = v.findViewById(R.id.gymbutton);
        stadium =v.findViewById(R.id.stadiumbutton);

        park.setEnabled(false);
        gym.setEnabled(false);
        stadium.setEnabled(false);

        mRecyclerView = v.findViewById(R.id.ParkDetails);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();

                latitude=mLastLocation.getLatitude();
                longitude=mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude,longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                SingaporeParks();
                //nearByPlace("park");

            }
        });

        gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                latitude=mLastLocation.getLatitude();
                longitude=mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude,longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                SingaporeGym();

                //nearByPlace("gym");

            }
        });

        stadium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                latitude=mLastLocation.getLatitude();
                longitude=mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude,longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                SingaporeStadium();
                //nearByPlace("stadium");

            }
        });



        return v;

    }



    private void SingaporeParks(){
        ParkDetails.clear();
        ParkDetails = new ArrayList<>();
        mParkList = new LinkedHashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(mParkList);
        TreeMap<String, Double> mParkList_sorted = new TreeMap<String, Double>(bvc);

        //Woodlands
        LatLng Admiralty_Park = new LatLng(1.4484, 103.7790);
        mMap.addMarker(new MarkerOptions().position(Admiralty_Park).title(getResources().getString(R.string.Admiralty_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Admiralty_Park),SphericalUtil.computeDistanceBetween(Admiralty_Park,latLng));
        LatLng Woodlands_Waterfront_Park = new LatLng (1.4530, 103.7803);
        mMap.addMarker(new MarkerOptions().position(Woodlands_Waterfront_Park).title(getResources().getString(R.string.Woodlands_Waterfront_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Woodlands_Waterfront_Park),SphericalUtil.computeDistanceBetween(Woodlands_Waterfront_Park,latLng));
        LatLng Marsiling_Park = new LatLng (1.4381, 103.7706);
        mMap.addMarker(new MarkerOptions().position(Marsiling_Park).title(getResources().getString(R.string.Marsiling_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Marsiling_Park),SphericalUtil.computeDistanceBetween(Marsiling_Park,latLng));
        LatLng Woodlands_Town_Part_East = new LatLng (1.4366, 103.7792);
        mMap.addMarker(new MarkerOptions().position(Woodlands_Town_Part_East).title(getResources().getString(R.string.Woodlands_Town_Part_East)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Woodlands_Town_Part_East),SphericalUtil.computeDistanceBetween(Woodlands_Town_Part_East,latLng));
        LatLng Vista_Park = new LatLng(1.4297, 103.7961);
        mMap.addMarker(new MarkerOptions().position(Vista_Park).title(getResources().getString(R.string.Vista_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Vista_Park),SphericalUtil.computeDistanceBetween(Vista_Park,latLng));
        LatLng Mandai_Tekong_Park = new LatLng(1.4349, 103.7941);
        mMap.addMarker(new MarkerOptions().position(Mandai_Tekong_Park).title(getResources().getString(R.string.Mandai_Tekong_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Mandai_Tekong_Park),SphericalUtil.computeDistanceBetween(Mandai_Tekong_Park,latLng));

        //Toast.makeText(this,distance/1000+"km",Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, Integer.valueOf(distance/1000)+"km",Toast.LENGTH_SHORT).show();

        //Sembawang
        LatLng Sembawang_Park = new LatLng (1.4604, 103.8363);
        mMap.addMarker(new MarkerOptions().position(Sembawang_Park).title(getResources().getString(R.string.Sembawang_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sembawang_Park),SphericalUtil.computeDistanceBetween(Sembawang_Park,latLng));
        //LatLng Janggus_Garden = new LatLng (1.4576, 103.8371);
        //mMap.addMarker(new MarkerOptions().position(Janggus_Garden).title("Janggus Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Kandis_Garden = new LatLng (1.4561, 103.8359);
        //mMap.addMarker(new MarkerOptions().position(Kandis_Garden).title("Kandis Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Pebble_Garden = new LatLng (1.4506, 103.8294);
        //mMap.addMarker(new MarkerOptions().position(Pebble_Garden).title("Pebble Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Mandai
        LatLng Jalan_Kemuning_Park = new LatLng (1.4395, 103.8269);
        mMap.addMarker(new MarkerOptions().position(Jalan_Kemuning_Park).title(getResources().getString(R.string.Jalan_Kemuning_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jalan_Kemuning_Park),SphericalUtil.computeDistanceBetween(Jalan_Kemuning_Park,latLng));
        //LatLng Platina_Road_Playground = new LatLng (1.4277, 103.8261);
        //mMap.addMarker(new MarkerOptions().position(Platina_Road_Playground).title("Platina Road Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Goodlink_Park_Playground = new LatLng (1.4273, 103.8255);
        //mMap.addMarker(new MarkerOptions().position(Goodlink_Park_Playground).title("Goodlink Park Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Yishun
        LatLng Yishun_Neighbourhood_Park = new LatLng (1.437835, 103.835343);
        mMap.addMarker(new MarkerOptions().position(Yishun_Neighbourhood_Park).title(getResources().getString(R.string.Yishun_Neighbourhood_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Yishun_Neighbourhood_Park),SphericalUtil.computeDistanceBetween(Yishun_Neighbourhood_Park,latLng));
        LatLng Yishun_Pond_Park = new LatLng (1.4279,103.8399);
        mMap.addMarker(new MarkerOptions().position(Yishun_Pond_Park).title(getResources().getString(R.string.Yishun_Pond_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Yishun_Pond_Park),SphericalUtil.computeDistanceBetween(Yishun_Pond_Park,latLng));
        LatLng Yishun_Park = new LatLng (1.4250, 103.8447);
        mMap.addMarker(new MarkerOptions().position(Yishun_Park).title(getResources().getString(R.string.Yishun_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Yishun_Park),SphericalUtil.computeDistanceBetween(Yishun_Park,latLng));

        //Lim Chu Kang
        LatLng Sungei_Buloh_Wetland_Reserve = new LatLng (1.4480, 103.7245);
        mMap.addMarker(new MarkerOptions().position(Sungei_Buloh_Wetland_Reserve).title(getResources().getString(R.string.Sungei_Buloh_Wetland_Reserve)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sungei_Buloh_Wetland_Reserve),SphericalUtil.computeDistanceBetween(Sungei_Buloh_Wetland_Reserve,latLng));
        LatLng Kranji_Reservoir_Park = new LatLng (1.439694, 103.737806);
        mMap.addMarker(new MarkerOptions().position(Kranji_Reservoir_Park).title(getResources().getString(R.string.Kranji_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kranji_Reservoir_Park),SphericalUtil.computeDistanceBetween(Kranji_Reservoir_Park,latLng));
        LatLng Kranji_Nature_Trail = new LatLng (1.4446,103.7356);
        mMap.addMarker(new MarkerOptions().position(Kranji_Nature_Trail).title(getResources().getString(R.string.Kranji_Nature_Trail)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kranji_Nature_Trail),SphericalUtil.computeDistanceBetween(Kranji_Nature_Trail,latLng));

        //Sungei Kadut
        LatLng Jalan_Rasok_Park = new LatLng (1.4165, 103.7589);
        mMap.addMarker(new MarkerOptions().position(Jalan_Rasok_Park).title(getResources().getString(R.string.Jalan_Rasok_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jalan_Rasok_Park),SphericalUtil.computeDistanceBetween(Jalan_Rasok_Park,latLng));

        //Punggol
        LatLng Coney_Island_Park = new LatLng (1.409472, 103.92167);
        mMap.addMarker(new MarkerOptions().position(Coney_Island_Park).title(getResources().getString(R.string.Coney_Island_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Coney_Island_Park),SphericalUtil.computeDistanceBetween(Coney_Island_Park,latLng));
        LatLng Punggol_Park = new LatLng(1.3769802, 103.8986585);
        mMap.addMarker(new MarkerOptions().position(Punggol_Park).title(getResources().getString(R.string.Punggol_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Punggol_Park),SphericalUtil.computeDistanceBetween(Punggol_Park,latLng));
        LatLng Punggol_Waterway_Park = new LatLng(1.4111243, 103.9046565);
        mMap.addMarker(new MarkerOptions().position(Punggol_Waterway_Park).title(getResources().getString(R.string.Punggol_Waterway_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Punggol_Waterway_Park),SphericalUtil.computeDistanceBetween(Punggol_Waterway_Park,latLng));

        //SengKang
        //LatLng St Annes Wood Playground = new LatLng();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Sengkang_Riverside_Park = new LatLng (1.3993, 103.8849);
        mMap.addMarker(new MarkerOptions().position(Sengkang_Riverside_Park).title(getResources().getString(R.string.Sengkang_Riverside_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sengkang_Riverside_Park),SphericalUtil.computeDistanceBetween(Sengkang_Riverside_Park,latLng));
        //LatLng Dew_Garden = new LatLng (1.3993, 103.8711);
        //mMap.addMarker(new MarkerOptions().position(Dew_Garden).title("Dew Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Lorong_Tanggam_Park = new LatLng (1.3989,103.8736);
        mMap.addMarker(new MarkerOptions().position(Lorong_Tanggam_Park).title(getResources().getString(R.string.Lorong_Tanggam_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Lorong_Tanggam_Park),SphericalUtil.computeDistanceBetween(Lorong_Tanggam_Park,latLng));

        //Changi
        //LatLng Changi West Boardwalk = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Turnhouse_Park = new LatLng (1.3902192, 103.9790907);
        mMap.addMarker(new MarkerOptions().position(Turnhouse_Park).title(getResources().getString(R.string.Turnhouse_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Turnhouse_Park),SphericalUtil.computeDistanceBetween(Turnhouse_Park,latLng));
        LatLng Changi_Point_Waterfront_Park = new LatLng (1.3833, 104.0010);
        mMap.addMarker(new MarkerOptions().position(Changi_Point_Waterfront_Park).title(getResources().getString(R.string.Changi_Point_Waterfront_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Changi_Point_Waterfront_Park),SphericalUtil.computeDistanceBetween(Changi_Point_Waterfront_Park,latLng));
        LatLng Changi_Beach_Park = new LatLng (1.3909, 103.9921);
        mMap.addMarker(new MarkerOptions().position(Changi_Beach_Park).title(getResources().getString(R.string.Changi_Beach_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Changi_Beach_Park),SphericalUtil.computeDistanceBetween(Changi_Beach_Park,latLng));
        //LatLng Loyang View Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Mariam Way Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Mariam Walk Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Changi Heights Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Pasir Ris
        LatLng Pasir_Ris_Park = new LatLng (1.3725, 103.9517);
        mMap.addMarker(new MarkerOptions().position(Pasir_Ris_Park).title(getResources().getString(R.string.Pasir_Ris_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Pasir_Ris_Park),SphericalUtil.computeDistanceBetween(Pasir_Ris_Park,latLng));
        //LatLng Sungei_Api_Api_Park = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position(Sungei_Api_Api_Park).title("Sungei Api Api Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Pasir_Ris_Town_Park = new LatLng (1.3725, 103.9517);
        mMap.addMarker(new MarkerOptions().position(Pasir_Ris_Town_Park).title(getResources().getString(R.string.Pasir_Ris_Town_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Pasir_Ris_Town_Park),SphericalUtil.computeDistanceBetween(Pasir_Ris_Town_Park,latLng));
        //LatLng Elias Terrace Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Tampines
        LatLng Tampines_Eco_Green = new LatLng (1.3638, 103.9482);
        mMap.addMarker(new MarkerOptions().position(Tampines_Eco_Green).title(getResources().getString(R.string.Tampines_Eco_Green)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Tampines_Eco_Green),SphericalUtil.computeDistanceBetween(Tampines_Eco_Green,latLng));
        LatLng Sun_Plaza_Park = new LatLng (1.3591, 103.9446);
        mMap.addMarker(new MarkerOptions().position(Sun_Plaza_Park).title(getResources().getString(R.string.Sun_Plaza_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sun_Plaza_Park),SphericalUtil.computeDistanceBetween(Sun_Plaza_Park,latLng));

        //Simei
        //LatLng Mera Terrace Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Jalan Pelatok Open Space = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Jalan Pelatok Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Meragi Road Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Sunbird Circle Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Harbey Crescent Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Jalan Angin Laut Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Kembangan
        LatLng Elite_Terrace_Park = new LatLng (1.3159059, 103.9244601);
        mMap.addMarker(new MarkerOptions().position(Elite_Terrace_Park).title(getResources().getString(R.string.Elite_Terrace_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Elite_Terrace_Park),SphericalUtil.computeDistanceBetween(Elite_Terrace_Park,latLng));
        LatLng Telok_Kurau_Park = new LatLng (1.3156, 103.9143);
        mMap.addMarker(new MarkerOptions().position(Telok_Kurau_Park).title(getResources().getString(R.string.Telok_Kurau_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Telok_Kurau_Park),SphericalUtil.computeDistanceBetween(Telok_Kurau_Park,latLng));
        //ALOT OF PLAYGROUNDS?

        //East Coast
        LatLng East_Coast_Terrace_Park = new LatLng (1.3111, 103.9236);
        mMap.addMarker(new MarkerOptions().position(East_Coast_Terrace_Park).title(getResources().getString(R.string.East_Coast_Terrace_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.East_Coast_Terrace_Park),SphericalUtil.computeDistanceBetween(East_Coast_Terrace_Park,latLng));
        LatLng East_Coast_Park = new LatLng (1.3008, 103.9122);
        mMap.addMarker(new MarkerOptions().position(East_Coast_Park).title(getResources().getString(R.string.East_Coast_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.East_Coast_Park),SphericalUtil.computeDistanceBetween(East_Coast_Park,latLng));

        //Marine Parade
        LatLng Marine_Parade_Road_Park = new LatLng (1.3003243, 103.9089574);
        mMap.addMarker(new MarkerOptions().position(Marine_Parade_Road_Park).title(getResources().getString(R.string.Marine_Parade_Road_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Marine_Parade_Road_Park),SphericalUtil.computeDistanceBetween(Marine_Parade_Road_Park,latLng));
        LatLng Joo_Chiat_Terrace_Interim_Park = new LatLng (1.3145,103.9017);
        mMap.addMarker(new MarkerOptions().position(Joo_Chiat_Terrace_Interim_Park).title(getResources().getString(R.string.Joo_Chiat_Terrace_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Joo_Chiat_Terrace_Interim_Park),SphericalUtil.computeDistanceBetween(Joo_Chiat_Terrace_Interim_Park,latLng));
        LatLng Ceylon_Road_Interim_Park = new LatLng (1.309867, 103.899647);
        mMap.addMarker(new MarkerOptions().position(Ceylon_Road_Interim_Park).title(getResources().getString(R.string.Ceylon_Road_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Ceylon_Road_Interim_Park),SphericalUtil.computeDistanceBetween(Ceylon_Road_Interim_Park,latLng));

        //Katong
        LatLng Katong_Park = new LatLng (1.2966,103.8867);
        mMap.addMarker(new MarkerOptions().position(Katong_Park).title(getResources().getString(R.string.Katong_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Katong_Park),SphericalUtil.computeDistanceBetween(Katong_Park,latLng));
        LatLng Wikinson_Interim_Park = new LatLng (1.3017,103.887);
        mMap.addMarker(new MarkerOptions().position(Wikinson_Interim_Park).title(getResources().getString(R.string.Wikinson_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Wikinson_Interim_Park),SphericalUtil.computeDistanceBetween(Wikinson_Interim_Park,latLng));
        LatLng Arthur_Park = new LatLng (1.3041, 103.8882);
        mMap.addMarker(new MarkerOptions().position(Arthur_Park).title(getResources().getString(R.string.Arthur_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Arthur_Park),SphericalUtil.computeDistanceBetween(Arthur_Park,latLng));

        //Farrer Park
        LatLng Cambridge_Park = new LatLng (1.3136681, 103.847015);
        mMap.addMarker(new MarkerOptions().position(Cambridge_Park).title(getResources().getString(R.string.Cambridge_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Cambridge_Park),SphericalUtil.computeDistanceBetween(Cambridge_Park,latLng));

        //Lavaender
        LatLng Kallang_Riverside_Park = new LatLng (1.306978,103.86801);
        mMap.addMarker(new MarkerOptions().position(Kallang_Riverside_Park).title(getResources().getString(R.string.Kallang_Riverside_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kallang_Riverside_Park),SphericalUtil.computeDistanceBetween(Kallang_Riverside_Park,latLng));
        LatLng Kampong_Glam_Park = new LatLng (1.30166546, 103.855996576);
        mMap.addMarker(new MarkerOptions().position(Kampong_Glam_Park).title(getResources().getString(R.string.Kampong_Glam_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kampong_Glam_Park),SphericalUtil.computeDistanceBetween(Kampong_Glam_Park,latLng));

        //Dhoby Ghaut
        LatLng Istana_Park = new LatLng (1.2992, 103.8438);
        mMap.addMarker(new MarkerOptions().position(Istana_Park).title(getResources().getString(R.string.Istana_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Istana_Park),SphericalUtil.computeDistanceBetween(Istana_Park,latLng));

        //Fort Canning
        LatLng Fort_Canning_Park = new LatLng(1.2945126, 103.845801);
        mMap.addMarker(new MarkerOptions().position(Fort_Canning_Park).title(getResources().getString(R.string.Fort_Canning_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Fort_Canning_Park),SphericalUtil.computeDistanceBetween(Fort_Canning_Park,latLng));

        //Esplanade
        LatLng War_Memorial_Park = new LatLng(1.2928957, 103.8546778);
        mMap.addMarker(new MarkerOptions().position(War_Memorial_Park).title(getResources().getString(R.string.War_Memorial_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.War_Memorial_Park),SphericalUtil.computeDistanceBetween(War_Memorial_Park,latLng));
        LatLng Esplanade_Park = new LatLng (1.2899, 103.8539);
        mMap.addMarker(new MarkerOptions().position(Esplanade_Park).title(getResources().getString(R.string.Esplanade_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Esplanade_Park),SphericalUtil.computeDistanceBetween(Esplanade_Park,latLng));
        LatLng Youth_Olympic_Park = new LatLng (1.2890, 103.8604);
        mMap.addMarker(new MarkerOptions().position(Youth_Olympic_Park).title(getResources().getString(R.string.Youth_Olympic_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Youth_Olympic_Park),SphericalUtil.computeDistanceBetween(Youth_Olympic_Park,latLng));
        LatLng Marina_Promenade = new LatLng (1.2899, 103.8648);
        mMap.addMarker(new MarkerOptions().position(Marina_Promenade).title(getResources().getString(R.string.Marina_Promenade)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Marina_Promenade),SphericalUtil.computeDistanceBetween(Marina_Promenade,latLng));

        //Raffles place
        LatLng Raffles_Place_Park = new LatLng (1.2838806, 103.8515333);
        mMap.addMarker(new MarkerOptions().position(Raffles_Place_Park).title(getResources().getString(R.string.Raffles_Place_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Raffles_Place_Park),SphericalUtil.computeDistanceBetween(Raffles_Place_Park,latLng));

        //Telok Ayer
        LatLng Telok_Ayer_Green = new LatLng (1.2814,103.8477);
        mMap.addMarker(new MarkerOptions().position(Telok_Ayer_Green).title(getResources().getString(R.string.Telok_Ayer_Green)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Telok_Ayer_Green),SphericalUtil.computeDistanceBetween(Telok_Ayer_Green,latLng));
        LatLng Ann_Siang_Hill_Park = new LatLng (1.2809,103.8464);
        mMap.addMarker(new MarkerOptions().position(Ann_Siang_Hill_Park).title(getResources().getString(R.string.Ann_Siang_Hill_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Ann_Siang_Hill_Park),SphericalUtil.computeDistanceBetween(Ann_Siang_Hill_Park,latLng));
        LatLng Telok_Ayer_Park = new LatLng (1.2778, 103.8468);
        mMap.addMarker(new MarkerOptions().position(Telok_Ayer_Park).title(getResources().getString(R.string.Telok_Ayer_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Telok_Ayer_Park),SphericalUtil.computeDistanceBetween(Telok_Ayer_Park,latLng));

        //Outram Park
        LatLng Duxton_Plain_Park = new LatLng (1.2804,103.8413);
        mMap.addMarker(new MarkerOptions().position(Duxton_Plain_Park).title(getResources().getString(R.string.Duxton_Plain_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Duxton_Plain_Park),SphericalUtil.computeDistanceBetween(Duxton_Plain_Park,latLng));
        LatLng Pearls_Hill_City_Park = new LatLng (1.284389, 103.839194);
        mMap.addMarker(new MarkerOptions().position(Pearls_Hill_City_Park).title(getResources().getString(R.string.Pearls_Hill_City_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Pearls_Hill_City_Park),SphericalUtil.computeDistanceBetween(Pearls_Hill_City_Park,latLng));

        //Pasir Panjang
        LatLng Kent_Ridge_Park = new LatLng (1.2839, 103.7913);
        mMap.addMarker(new MarkerOptions().position(Kent_Ridge_Park).title(getResources().getString(R.string.Kent_Ridge_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kent_Ridge_Park),SphericalUtil.computeDistanceBetween(Kent_Ridge_Park,latLng));
        LatLng Jambol_Place_Park = new LatLng (1.31413, 103.81733);
        mMap.addMarker(new MarkerOptions().position(Jambol_Place_Park).title(getResources().getString(R.string.Jambol_Place_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jambol_Place_Park),SphericalUtil.computeDistanceBetween(Jambol_Place_Park,latLng));
        LatLng Pasir_Panjang_Park = new LatLng (1.2820, 103.7812);
        mMap.addMarker(new MarkerOptions().position(Pasir_Panjang_Park).title(getResources().getString(R.string.Pasir_Panjang_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Pasir_Panjang_Park),SphericalUtil.computeDistanceBetween(Pasir_Panjang_Park,latLng));
        LatLng HortPark = new LatLng (1.2787, 103.8018);
        mMap.addMarker(new MarkerOptions().position(HortPark).title(getResources().getString(R.string.HortPark)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.HortPark),SphericalUtil.computeDistanceBetween(HortPark,latLng));

        LatLng Southern_Ridges = new LatLng (1.280102,103.802995);
        mMap.addMarker(new MarkerOptions().position(Southern_Ridges).title(getResources().getString(R.string.Southern_Ridges)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Southern_Ridges),SphericalUtil.computeDistanceBetween(Southern_Ridges,latLng));
        LatLng Telok_Blangah_Hill_Park = new LatLng (1.2788, 103.8107);
        mMap.addMarker(new MarkerOptions().position(Telok_Blangah_Hill_Park).title(getResources().getString(R.string.Telok_Blangah_Hill_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Telok_Blangah_Hill_Park),SphericalUtil.computeDistanceBetween(Telok_Blangah_Hill_Park,latLng));
        LatLng Mount_Faber_Park = new LatLng (1.2738, 103.8175);
        mMap.addMarker(new MarkerOptions().position(Mount_Faber_Park).title(getResources().getString(R.string.Mount_Faber_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Mount_Faber_Park),SphericalUtil.computeDistanceBetween(Mount_Faber_Park,latLng));

        LatLng West_Coast_Park = new LatLng (1.2914, 103.7667);
        mMap.addMarker(new MarkerOptions().position(West_Coast_Park).title(getResources().getString(R.string.West_Coast_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.West_Coast_Park),SphericalUtil.computeDistanceBetween(West_Coast_Park,latLng));
        LatLng Clementi_Woods_Park = new LatLng (1.3017, 103.7667);
        mMap.addMarker(new MarkerOptions().position(Clementi_Woods_Park).title(getResources().getString(R.string.Clementi_Woods_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Clementi_Woods_Park),SphericalUtil.computeDistanceBetween(Clementi_Woods_Park,latLng));

        LatLng Pandan_Reservoir_Fitness_Corner = new LatLng (1.3176122, 103.7486499);
        mMap.addMarker(new MarkerOptions().position(Pandan_Reservoir_Fitness_Corner).title(getResources().getString(R.string.Pandan_Reservoir_Fitness_Corner)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Pandan_Reservoir_Fitness_Corner),SphericalUtil.computeDistanceBetween(Pandan_Reservoir_Fitness_Corner,latLng));

        //Pioneer
        LatLng Yunnan_Park = new LatLng (1.3374, 103.6930);
        mMap.addMarker(new MarkerOptions().position(Yunnan_Park).title(getResources().getString(R.string.Yunnan_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Yunnan_Park),SphericalUtil.computeDistanceBetween(Yunnan_Park,latLng));
        LatLng Jurong_Central_Park = new LatLng (1.3381, 103.7078);
        mMap.addMarker(new MarkerOptions().position(Jurong_Central_Park).title(getResources().getString(R.string.Jurong_Central_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jurong_Central_Park),SphericalUtil.computeDistanceBetween(Jurong_Central_Park,latLng));
        LatLng Lakeside_Grove_Fitness_Corner = new LatLng (1.3398,103.7171);
        mMap.addMarker(new MarkerOptions().position(Lakeside_Grove_Fitness_Corner).title(getResources().getString(R.string.Lakeside_Grove_Fitness_Corner)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Lakeside_Grove_Fitness_Corner),SphericalUtil.computeDistanceBetween(Lakeside_Grove_Fitness_Corner,latLng));

        //Choa Chu Kang
        LatLng Choa_Chu_Kang_Park = new LatLng (1.3869, 103.7473);
        mMap.addMarker(new MarkerOptions().position(Choa_Chu_Kang_Park).title(getResources().getString(R.string.Choa_Chu_Kang_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Choa_Chu_Kang_Park),SphericalUtil.computeDistanceBetween(Choa_Chu_Kang_Park,latLng));
        LatLng Villa_Verde_Park = new LatLng (1.3895,103.7529);
        mMap.addMarker(new MarkerOptions().position(Villa_Verde_Park).title(getResources().getString(R.string.Villa_Verde_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Villa_Verde_Park),SphericalUtil.computeDistanceBetween(Villa_Verde_Park,latLng));

        //Hillview
        LatLng Chestnut_Nature_Park = new LatLng (1.31636, 103.86321);
        mMap.addMarker(new MarkerOptions().position(Chestnut_Nature_Park).title(getResources().getString(R.string.Chestnut_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Chestnut_Nature_Park),SphericalUtil.computeDistanceBetween(Chestnut_Nature_Park,latLng));
        LatLng Dairy_Farm_Nature_Park = new LatLng(1.3618794, 103.7753145);
        mMap.addMarker(new MarkerOptions().position(Dairy_Farm_Nature_Park).title(getResources().getString(R.string.Dairy_Farm_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Dairy_Farm_Nature_Park),SphericalUtil.computeDistanceBetween(Dairy_Farm_Nature_Park,latLng));
        LatLng Fuyong_Interim_Park = new LatLng (1.3574719, 103.768588);
        mMap.addMarker(new MarkerOptions().position(Fuyong_Interim_Park).title(getResources().getString(R.string.Fuyong_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Fuyong_Interim_Park),SphericalUtil.computeDistanceBetween(Fuyong_Interim_Park,latLng));
        LatLng Bukit_Timah_Nature_Reserve = new LatLng(1.3540562, 103.7769454);
        mMap.addMarker(new MarkerOptions().position(Bukit_Timah_Nature_Reserve).title(getResources().getString(R.string.Bukit_Timah_Nature_Reserve)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bukit_Timah_Nature_Reserve),SphericalUtil.computeDistanceBetween(Bukit_Timah_Nature_Reserve,latLng));
        LatLng Hindhede_Nature_Park = new LatLng (1.34846664486, 103.775428404);
        mMap.addMarker(new MarkerOptions().position(Hindhede_Nature_Park).title(getResources().getString(R.string.Hindhede_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Hindhede_Nature_Park),SphericalUtil.computeDistanceBetween(Hindhede_Nature_Park,latLng));

        //Beauty World
        LatLng Bukit_Batok_Nature_Park = new LatLng (1.3485, 103.7642);
        mMap.addMarker(new MarkerOptions().position(Bukit_Batok_Nature_Park).title(getResources().getString(R.string.Bukit_Batok_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bukit_Batok_Nature_Park),SphericalUtil.computeDistanceBetween(Bukit_Batok_Nature_Park,latLng));
        LatLng Hoover_Park = new LatLng (1.33271, 103.69756);
        mMap.addMarker(new MarkerOptions().position(Hoover_Park).title(getResources().getString(R.string.Hoover_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Hoover_Park),SphericalUtil.computeDistanceBetween(Hoover_Park,latLng));
        LatLng Eng_Kong_Place_Fitness_Corner = new LatLng (1.3348,103.7673);
        mMap.addMarker(new MarkerOptions().position(Eng_Kong_Place_Fitness_Corner).title(getResources().getString(R.string.Eng_Kong_Place_Fitness_Corner)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Eng_Kong_Place_Fitness_Corner),SphericalUtil.computeDistanceBetween(Eng_Kong_Place_Fitness_Corner,latLng));
        LatLng Sunset_Way_Park = new LatLng (1.327456, 103.7647549);
        mMap.addMarker(new MarkerOptions().position(Sunset_Way_Park).title(getResources().getString(R.string.Sunset_Way_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sunset_Way_Park),SphericalUtil.computeDistanceBetween(Sunset_Way_Park,latLng));

        //Dover
        LatLng Holland_Grove_View_Fitness_Corner = new LatLng (1.3125,103.7927);
        mMap.addMarker(new MarkerOptions().position(Holland_Grove_View_Fitness_Corner).title(getResources().getString(R.string.Holland_Grove_View_Fitness_Corner)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Holland_Grove_View_Fitness_Corner),SphericalUtil.computeDistanceBetween(Holland_Grove_View_Fitness_Corner,latLng));

        //Bukit Gombak
        LatLng Bukit_Batok_Town_Park = new LatLng (1.3559, 103.7549);
        mMap.addMarker(new MarkerOptions().position(Bukit_Batok_Town_Park).title(getResources().getString(R.string.Bukit_Batok_Town_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bukit_Batok_Town_Park),SphericalUtil.computeDistanceBetween(Bukit_Batok_Town_Park,latLng));
        LatLng Jalan_Remaja_Interim_Park = new LatLng (1.3576,103.7609);
        mMap.addMarker(new MarkerOptions().position(Jalan_Remaja_Interim_Park).title(getResources().getString(R.string.Jalan_Remaja_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jalan_Remaja_Interim_Park),SphericalUtil.computeDistanceBetween(Jalan_Remaja_Interim_Park,latLng));

        //one-north
        LatLng One_north_Park = new LatLng (1.3028391, 103.7905769);
        mMap.addMarker(new MarkerOptions().position(One_north_Park).title(getResources().getString(R.string.One_north_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.One_north_Park),SphericalUtil.computeDistanceBetween(One_north_Park,latLng));


        //Holland Village
        LatLng Holland_Village_Park = new LatLng (1.3118, 103.7955);
        mMap.addMarker(new MarkerOptions().position(Holland_Village_Park).title(getResources().getString(R.string.Holland_Village_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Holland_Village_Park),SphericalUtil.computeDistanceBetween(Holland_Village_Park,latLng));

        //Tiong Bahru
        LatLng Tiong_Bahru_Park = new LatLng (1.2874, 103.8247);
        mMap.addMarker(new MarkerOptions().position(Tiong_Bahru_Park).title(getResources().getString(R.string.Tiong_Bahru_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Tiong_Bahru_Park),SphericalUtil.computeDistanceBetween(Tiong_Bahru_Park,latLng));
        LatLng Kim_Pong_Park = new LatLng (1.2835,103.8297);
        mMap.addMarker(new MarkerOptions().position(Kim_Pong_Park).title(getResources().getString(R.string.Kim_Pong_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kim_Pong_Park),SphericalUtil.computeDistanceBetween(Kim_Pong_Park,latLng));
        LatLng Kim_Seng_Park = new LatLng (1.2929,103.8348);
        mMap.addMarker(new MarkerOptions().position(Kim_Seng_Park).title(getResources().getString(R.string.Kim_Seng_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kim_Seng_Park),SphericalUtil.computeDistanceBetween(Kim_Seng_Park,latLng));

        //Botanic Gardens
        LatLng Singapore_Botanic_Gardens = new LatLng(1.3138397,103.8159136);
        mMap.addMarker(new MarkerOptions().position(Singapore_Botanic_Gardens).title(getResources().getString(R.string.Singapore_Botanic_Gardens)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Singapore_Botanic_Gardens),SphericalUtil.computeDistanceBetween(Singapore_Botanic_Gardens,latLng));
        LatLng University_Road_Park = new LatLng (1.3255,103.8175);
        mMap.addMarker(new MarkerOptions().position(University_Road_Park).title(getResources().getString(R.string.University_Road_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.University_Road_Park),SphericalUtil.computeDistanceBetween(University_Road_Park,latLng));
        LatLng Kheam_Hock_Park = new LatLng (1.3303,103.8195);
        mMap.addMarker(new MarkerOptions().position(Kheam_Hock_Park).title(getResources().getString(R.string.Kheam_Hock_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Kheam_Hock_Park),SphericalUtil.computeDistanceBetween(Kheam_Hock_Park,latLng));

        //Novena
        LatLng Novena_Park = new LatLng (1.3222, 103.8428);
        mMap.addMarker(new MarkerOptions().position(Novena_Park).title(getResources().getString(R.string.Novena_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Novena_Park),SphericalUtil.computeDistanceBetween(Novena_Park,latLng));
        LatLng Novena_Rise_Park = new LatLng (1.3252,103.8429);
        mMap.addMarker(new MarkerOptions().position(Novena_Rise_Park).title(getResources().getString(R.string.Novena_Rise_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Novena_Rise_Park),SphericalUtil.computeDistanceBetween(Novena_Rise_Park,latLng));

        //Stevens
        LatLng Malcolm_Park = new LatLng (1.324725, 103.8301232);
        mMap.addMarker(new MarkerOptions().position(Malcolm_Park).title(getResources().getString(R.string.Malcolm_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Malcolm_Park),SphericalUtil.computeDistanceBetween(Malcolm_Park,latLng));

        //Bukit Timah
        LatLng Holland_Green_Linear_Park = new LatLng (1.3266, 103.7855);
        mMap.addMarker(new MarkerOptions().position(Holland_Green_Linear_Park).title(getResources().getString(R.string.Holland_Green_Linear_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Holland_Green_Linear_Park),SphericalUtil.computeDistanceBetween(Holland_Green_Linear_Park,latLng));
        LatLng Namly_Park = new LatLng (1.3234, 103.7965);
        mMap.addMarker(new MarkerOptions().position(Namly_Park).title(getResources().getString(R.string.Namly_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Namly_Park),SphericalUtil.computeDistanceBetween(Namly_Park,latLng));
        LatLng Duchess_Park = new LatLng (1.3241,103.8054);
        mMap.addMarker(new MarkerOptions().position(Duchess_Park).title(getResources().getString(R.string.Duchess_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Duchess_Park),SphericalUtil.computeDistanceBetween(Duchess_Park,latLng));
        LatLng Bougainvillea_Park = new LatLng (1.3270, 103.8071);
        mMap.addMarker(new MarkerOptions().position(Bougainvillea_Park).title(getResources().getString(R.string.Bougainvillea_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bougainvillea_Park),SphericalUtil.computeDistanceBetween(Bougainvillea_Park,latLng));
        LatLng Sian_Tuan_Interim_Park = new LatLng (1.3386,103.786);
        mMap.addMarker(new MarkerOptions().position(Sian_Tuan_Interim_Park).title(getResources().getString(R.string.Sian_Tuan_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sian_Tuan_Interim_Park),SphericalUtil.computeDistanceBetween(Sian_Tuan_Interim_Park,latLng));
        LatLng Mayfair_Park = new LatLng (1.3413,103.7808);
        mMap.addMarker(new MarkerOptions().position(Mayfair_Park).title(getResources().getString(R.string.Mayfair_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Mayfair_Park),SphericalUtil.computeDistanceBetween(Mayfair_Park,latLng));

        //Paya Lebar
        LatLng Bedok_Town_Park = new LatLng (1.3354, 103.9256);
        mMap.addMarker(new MarkerOptions().position(Bedok_Town_Park).title(getResources().getString(R.string.Bedok_Town_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bedok_Town_Park),SphericalUtil.computeDistanceBetween(Bedok_Town_Park,latLng));
        LatLng Bedok_Reservoir_Park = new LatLng (1.3413, 103.9245);
        mMap.addMarker(new MarkerOptions().position(Bedok_Reservoir_Park).title(getResources().getString(R.string.Bedok_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bedok_Reservoir_Park),SphericalUtil.computeDistanceBetween(Bedok_Reservoir_Park,latLng));

        //Hougang
        LatLng Luxus_Hill_Park = new LatLng (1.3797,103.8755);
        mMap.addMarker(new MarkerOptions().position(Luxus_Hill_Park).title(getResources().getString(R.string.Luxus_Hill_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Luxus_Hill_Park),SphericalUtil.computeDistanceBetween(Luxus_Hill_Park,latLng));
        LatLng Realty_Park = new LatLng (1.368,103.8903);
        mMap.addMarker(new MarkerOptions().position(Realty_Park).title(getResources().getString(R.string.Realty_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Realty_Park),SphericalUtil.computeDistanceBetween(Realty_Park,latLng));

        //Serangoon
        LatLng Tavistock_Avenue_Park = new LatLng (1.3705024, 103.8651618);
        mMap.addMarker(new MarkerOptions().position(Tavistock_Avenue_Park).title(getResources().getString(R.string.Tavistock_Avenue_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Tavistock_Avenue_Park),SphericalUtil.computeDistanceBetween(Tavistock_Avenue_Park,latLng));
        LatLng Richards_Avenue_Park = new LatLng (1.3870084, 103.8371533);
        mMap.addMarker(new MarkerOptions().position(Richards_Avenue_Park).title(getResources().getString(R.string.Richards_Avenue_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Richards_Avenue_Park),SphericalUtil.computeDistanceBetween(Richards_Avenue_Park,latLng));
        LatLng Surin_Avenue_Neighbourhood_Park = new LatLng (1.3556,103.8841);
        mMap.addMarker(new MarkerOptions().position(Surin_Avenue_Neighbourhood_Park).title(getResources().getString(R.string.Surin_Avenue_Neighbourhood_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Surin_Avenue_Neighbourhood_Park),SphericalUtil.computeDistanceBetween(Surin_Avenue_Neighbourhood_Park,latLng));
        LatLng Parry_Avenue_Interim_Park = new LatLng (1.3637493, 103.8809803);
        mMap.addMarker(new MarkerOptions().position(Parry_Avenue_Interim_Park).title(getResources().getString(R.string.Parry_Avenue_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Parry_Avenue_Interim_Park),SphericalUtil.computeDistanceBetween(Parry_Avenue_Interim_Park,latLng));
        LatLng Chartwell_Drive_Park = new LatLng (1.28222411319, 103.864259691);
        mMap.addMarker(new MarkerOptions().position(Chartwell_Drive_Park).title(getResources().getString(R.string.Chartwell_Drive_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Chartwell_Drive_Park),SphericalUtil.computeDistanceBetween(Chartwell_Drive_Park,latLng));
        LatLng Haus_Linear_Park = new LatLng (1.3652,103.8603);
        mMap.addMarker(new MarkerOptions().position(Haus_Linear_Park).title(getResources().getString(R.string.Haus_Linear_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Haus_Linear_Park),SphericalUtil.computeDistanceBetween(Haus_Linear_Park,latLng));
        LatLng Jalan_Geneng_Interim_Park = new LatLng (1.3532981, 103.8808952);
        mMap.addMarker(new MarkerOptions().position(Jalan_Geneng_Interim_Park).title(getResources().getString(R.string.Jalan_Geneng_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Jalan_Geneng_Interim_Park),SphericalUtil.computeDistanceBetween(Jalan_Geneng_Interim_Park,latLng));
        LatLng Bunga_Rampai_Park = new LatLng (1.340499, 103.8838455);
        mMap.addMarker(new MarkerOptions().position(Bunga_Rampai_Park).title(getResources().getString(R.string.Bunga_Rampai_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bunga_Rampai_Park),SphericalUtil.computeDistanceBetween(Bunga_Rampai_Park,latLng));

        //Lorong Chuan
        LatLng Chuan_Lane_Park = new LatLng (1.3518,103.8582);
        mMap.addMarker(new MarkerOptions().position(Chuan_Lane_Park).title(getResources().getString(R.string.Chuan_Lane_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Chuan_Lane_Park),SphericalUtil.computeDistanceBetween(Chuan_Lane_Park,latLng));
        LatLng Chiltern_Drive_Interim_Park = new LatLng (1.3486,103.8621);
        mMap.addMarker(new MarkerOptions().position(Chiltern_Drive_Interim_Park).title(getResources().getString(R.string.Chiltern_Drive_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Chiltern_Drive_Interim_Park),SphericalUtil.computeDistanceBetween(Chiltern_Drive_Interim_Park,latLng));


        LatLng Siang_Kuang_Avenue_Interim_Park = new LatLng (1.3299,103.8731);
        mMap.addMarker(new MarkerOptions().position(Siang_Kuang_Avenue_Interim_Park).title(getResources().getString(R.string.Siang_Kuang_Avenue_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Siang_Kuang_Avenue_Interim_Park),SphericalUtil.computeDistanceBetween(Siang_Kuang_Avenue_Interim_Park,latLng));
        LatLng Oasis_Mulberry_Park = new LatLng (1.3320596, 103.8785996);
        mMap.addMarker(new MarkerOptions().position(Oasis_Mulberry_Park).title(getResources().getString(R.string.Oasis_Mulberry_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Oasis_Mulberry_Park),SphericalUtil.computeDistanceBetween(Oasis_Mulberry_Park,latLng));
        LatLng MacPherson_Linear_Park = new LatLng (1.3333,103.8818);
        mMap.addMarker(new MarkerOptions().position(MacPherson_Linear_Park).title(getResources().getString(R.string.MacPherson_Linear_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.MacPherson_Linear_Park),SphericalUtil.computeDistanceBetween(MacPherson_Linear_Park,latLng));
        LatLng Aljunied_Park = new LatLng (1.3297, 103.8799);
        mMap.addMarker(new MarkerOptions().position(Aljunied_Park).title(getResources().getString(R.string.Aljunied_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Aljunied_Park),SphericalUtil.computeDistanceBetween(Aljunied_Park,latLng));

        //Ang Mo Kio
        LatLng Ang_Mo_Kio_Town_Garden_West = new LatLng(1.3743688, 103.8434332);
        mMap.addMarker(new MarkerOptions().position(Ang_Mo_Kio_Town_Garden_West).title(getResources().getString(R.string.Ang_Mo_Kio_Town_Garden_West)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Ang_Mo_Kio_Town_Garden_West),SphericalUtil.computeDistanceBetween(Ang_Mo_Kio_Town_Garden_West,latLng));
        LatLng Bishan_Ang_Mo_Kio_Park = new LatLng(1.3634088, 103.8435614);
        mMap.addMarker(new MarkerOptions().position(Bishan_Ang_Mo_Kio_Park).title(getResources().getString(R.string.Bishan_Ang_Mo_Kio_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Bishan_Ang_Mo_Kio_Park),SphericalUtil.computeDistanceBetween(Bishan_Ang_Mo_Kio_Park,latLng));
        LatLng Gelenggang_Park = new LatLng (1.3734,103.8296);
        mMap.addMarker(new MarkerOptions().position(Gelenggang_Park).title(getResources().getString(R.string.Chestnut_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Gelenggang_Park),SphericalUtil.computeDistanceBetween(Gelenggang_Park,latLng));
        LatLng Leban_Park = new LatLng (1.3728,103.829);
        mMap.addMarker(new MarkerOptions().position(Leban_Park).title(getResources().getString(R.string.Leban_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Leban_Park),SphericalUtil.computeDistanceBetween(Leban_Park,latLng));
        LatLng Lower_Peirce_Reservoir_Park = new LatLng(1.3720142, 103.8257891);
        mMap.addMarker(new MarkerOptions().position(Lower_Peirce_Reservoir_Park).title(getResources().getString(R.string.Lower_Peirce_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Lower_Peirce_Reservoir_Park),SphericalUtil.computeDistanceBetween(Lower_Peirce_Reservoir_Park,latLng));
        LatLng Thomson_Nature_Park = new LatLng (1.3858,103.8215);
        mMap.addMarker(new MarkerOptions().position(Thomson_Nature_Park).title(getResources().getString(R.string.Thomson_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Thomson_Nature_Park),SphericalUtil.computeDistanceBetween(Thomson_Nature_Park,latLng));
        LatLng Central_Catchment_Nature_Reserve = new LatLng (1.3533,103.8185);
        mMap.addMarker(new MarkerOptions().position(Central_Catchment_Nature_Reserve).title(getResources().getString(R.string.Central_Catchment_Nature_Reserve)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Central_Catchment_Nature_Reserve),SphericalUtil.computeDistanceBetween(Central_Catchment_Nature_Reserve,latLng));
        LatLng Upper_Peirce_Reservoir_Park = new LatLng (1.3747, 103.8112);
        mMap.addMarker(new MarkerOptions().position(Upper_Peirce_Reservoir_Park).title(getResources().getString(R.string.Upper_Peirce_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Upper_Peirce_Reservoir_Park),SphericalUtil.computeDistanceBetween(Upper_Peirce_Reservoir_Park,latLng));


        //Upper Thomson
        LatLng Windsor_Nature_Park = new LatLng (1.3599, 103.8273);
        mMap.addMarker(new MarkerOptions().position(Windsor_Nature_Park).title(getResources().getString(R.string.Windsor_Nature_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Windsor_Nature_Park),SphericalUtil.computeDistanceBetween(Windsor_Nature_Park,latLng));
        LatLng Venus_Green = new LatLng (1.3586,103.8274);
        mMap.addMarker(new MarkerOptions().position(Venus_Green).title(getResources().getString(R.string.Venus_Green)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Venus_Green),SphericalUtil.computeDistanceBetween(Venus_Green,latLng));
        //LatLng Onan Road Green Space = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Thomson_Park = new LatLng (1.3854, 103.8210);
        mMap.addMarker(new MarkerOptions().position(Thomson_Park).title(getResources().getString(R.string.Thomson_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Thomson_Park),SphericalUtil.computeDistanceBetween(Thomson_Park,latLng));
        LatLng Taman_Permata_Park = new LatLng (1.3499,103.8348);
        mMap.addMarker(new MarkerOptions().position(Taman_Permata_Park).title(getResources().getString(R.string.Taman_Permata_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Taman_Permata_Park),SphericalUtil.computeDistanceBetween(Taman_Permata_Park,latLng));
        LatLng Thomson_Park_Estate_Interim_Park = new LatLng (1.3566,103.8341);
        mMap.addMarker(new MarkerOptions().position(Thomson_Park_Estate_Interim_Park).title(getResources().getString(R.string.Thomson_Park_Estate_Interim_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Thomson_Park_Estate_Interim_Park),SphericalUtil.computeDistanceBetween(Thomson_Park_Estate_Interim_Park,latLng));
        LatLng MacRitchie_Reservoir_Park = new LatLng(1.3413779, 103.8348204);
        mMap.addMarker(new MarkerOptions().position(MacRitchie_Reservoir_Park).title(getResources().getString(R.string.MacRitchie_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.MacRitchie_Reservoir_Park),SphericalUtil.computeDistanceBetween(MacRitchie_Reservoir_Park,latLng));
        LatLng Upper_Seletar_Reservoir_Park = new LatLng (1.397,103.8033);
        mMap.addMarker(new MarkerOptions().position(Upper_Seletar_Reservoir_Park).title(getResources().getString(R.string.Upper_Seletar_Reservoir_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        InsertCheckDistance(getResources().getString(R.string.Upper_Seletar_Reservoir_Park),SphericalUtil.computeDistanceBetween(Upper_Seletar_Reservoir_Park,latLng));



        mParkList_sorted.putAll(mParkList);
        showParkList(mParkList_sorted);


        LatLng Fountain_of_Wealth = new LatLng(1.2947425, 103.8589026);
        LatLng Springleaf_Nature_Park = new LatLng(1.4014737, 103.8174668);
        LatLng Kebun_Baru_Bird_Corner = new LatLng(1.3743012, 103.8402669);


        //LatLng Bishan_Park_1_Near_Grub = new LatLng(1.3643363, 103.8351006);
        //LatLng Bishan_Ang_Mo_Kio_Park_Inclusive_Playground = new LatLng(1.3632348, 103.8442614);
        LatLng TreeTop_Walk = new LatLng(1.3607287, 103.8125215);
        LatLng Neram_Park = new LatLng(1.3856951, 103.8658256);
        LatLng MacRitchie_Trail = new LatLng(1.3419705, 103.8348653);



        mAdapter = new ParkAdapter(ParkDetails,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

    private void SingaporeGym(){

        ParkDetails.clear();
        ParkDetails = new ArrayList<>();
        mParkList = new LinkedHashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(mParkList);
        TreeMap<String, Double> mParkList_sorted = new TreeMap<String, Double>(bvc);

        //Amore Fitness
        LatLng AF_Jurong= new LatLng(1.339312,103.705409);
        mMap.addMarker(new MarkerOptions().position(AF_Jurong).title(getResources().getString(R.string.AF_Jurong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Jurong),SphericalUtil.computeDistanceBetween(AF_Jurong,latLng));
        LatLng AF_Hillion = new LatLng(1.378237,103.763838);
        mMap.addMarker(new MarkerOptions().position(AF_Hillion).title(getResources().getString(R.string.AF_Hillion)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Hillion),SphericalUtil.computeDistanceBetween(AF_Hillion,latLng));
        LatLng AF_Orchard= new LatLng(1.300009,103.845348);
        mMap.addMarker(new MarkerOptions().position(AF_Orchard).title(getResources().getString(R.string.AF_Orchard)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Orchard),SphericalUtil.computeDistanceBetween(AF_Orchard,latLng));
        LatLng AF_Bugis= new LatLng(1.298936,103.855062);
        mMap.addMarker(new MarkerOptions().position(AF_Bugis).title(getResources().getString(R.string.AF_Bugis)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Bugis),SphericalUtil.computeDistanceBetween(AF_Bugis,latLng));
        LatLng AF_Woodlands= new LatLng(1.435164,103.787190);
        mMap.addMarker(new MarkerOptions().position(AF_Woodlands).title(getResources().getString(R.string.AF_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Woodlands),SphericalUtil.computeDistanceBetween(AF_Woodlands,latLng));
        LatLng AF_Heartland_Mall= new LatLng(1.359489,103.885019);
        mMap.addMarker(new MarkerOptions().position(AF_Heartland_Mall).title(getResources().getString(R.string.AF_Heartland_Mall)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Heartland_Mall),SphericalUtil.computeDistanceBetween(AF_Heartland_Mall,latLng));
        LatLng AF_Tampines= new LatLng(1.354310,103.945015);
        mMap.addMarker(new MarkerOptions().position(AF_Tampines).title(getResources().getString(R.string.AF_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Tampines),SphericalUtil.computeDistanceBetween(AF_Tampines,latLng));
        LatLng AF_Kovan= new LatLng(1.359498,103.885019);
        mMap.addMarker(new MarkerOptions().position(AF_Kovan).title(getResources().getString(R.string.AF_Kovan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.AF_Kovan),SphericalUtil.computeDistanceBetween(AF_Kovan,latLng));
        //amorefitness.com,6266 6822

        //Anytime Fitness
        LatLng ATF_Sembawang= new LatLng(1.441785,103.825146);
        mMap.addMarker(new MarkerOptions().position(ATF_Sembawang).title(getResources().getString(R.string.ATF_Sembawang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Sembawang),SphericalUtil.computeDistanceBetween(ATF_Sembawang,latLng));
        LatLng ATF_Woods_Square= new LatLng(1.441785,103.825146);
        mMap.addMarker(new MarkerOptions().position(ATF_Woods_Square).title(getResources().getString(R.string.ATF_Woods_Square)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Woods_Square),SphericalUtil.computeDistanceBetween(ATF_Woods_Square,latLng));
        LatLng ATF_Admiralty= new LatLng(1.439115, 103.802638);
        mMap.addMarker(new MarkerOptions().position(ATF_Admiralty).title(getResources().getString(R.string.ATF_Admiralty)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Admiralty),SphericalUtil.computeDistanceBetween(ATF_Admiralty,latLng));
        LatLng ATF_Woodgrove= new LatLng(1.429252,103.780894);
        mMap.addMarker(new MarkerOptions().position(ATF_Woodgrove).title(getResources().getString(R.string.ATF_Woodgrove)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Woodgrove),SphericalUtil.computeDistanceBetween(ATF_Woodgrove,latLng));
        LatLng ATF_Woodlands= new LatLng(1.427608,103.792182);
        mMap.addMarker(new MarkerOptions().position(ATF_Woodlands).title(getResources().getString(R.string.ATF_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Woodlands),SphericalUtil.computeDistanceBetween(ATF_Woodlands,latLng));
        LatLng ATF_Junction9= new LatLng(1.433212,103.841639);
        mMap.addMarker(new MarkerOptions().position(ATF_Junction9).title(getResources().getString(R.string.ATF_Junction9)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Junction9),SphericalUtil.computeDistanceBetween(ATF_Junction9,latLng));
        LatLng ATF_NorthPoint_City= new LatLng(1.428545,103.836557);
        mMap.addMarker(new MarkerOptions().position(ATF_NorthPoint_City).title(getResources().getString(R.string.ATF_NorthPoint_City)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_NorthPoint_City),SphericalUtil.computeDistanceBetween(ATF_NorthPoint_City,latLng));
        LatLng ATF_Wisteria= new LatLng(1.418198,103.841366);
        mMap.addMarker(new MarkerOptions().position(ATF_Wisteria).title(getResources().getString(R.string.ATF_Wisteria)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Wisteria),SphericalUtil.computeDistanceBetween(ATF_Wisteria,latLng));
        LatLng ATF_AMK= new LatLng(1.371968,103.845946);
        mMap.addMarker(new MarkerOptions().position(ATF_AMK).title(getResources().getString(R.string.ATF_AMK)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_AMK),SphericalUtil.computeDistanceBetween(ATF_AMK,latLng));
        LatLng ATF_Greenwich= new LatLng(1.387700,103.869542);
        mMap.addMarker(new MarkerOptions().position(ATF_Greenwich).title(getResources().getString(R.string.ATF_Greenwich)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Greenwich),SphericalUtil.computeDistanceBetween(ATF_Greenwich,latLng));
        LatLng ATF_SengKang_Rivervale= new LatLng(1.392345,103.904515);
        mMap.addMarker(new MarkerOptions().position(ATF_SengKang_Rivervale).title(getResources().getString(R.string.ATF_SengKang_Rivervale)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_SengKang_Rivervale),SphericalUtil.computeDistanceBetween(ATF_SengKang_Rivervale,latLng));
        LatLng ATF_Buangkok= new LatLng(1.379298,103.87821);
        mMap.addMarker(new MarkerOptions().position(ATF_Buangkok).title(getResources().getString(R.string.ATF_Buangkok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Buangkok),SphericalUtil.computeDistanceBetween(ATF_Buangkok,latLng));
        LatLng ATF_Hougang_Central= new LatLng(1.375016,103.882749);
        mMap.addMarker(new MarkerOptions().position(ATF_Hougang_Central).title(getResources().getString(R.string.ATF_Hougang_Central)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Hougang_Central),SphericalUtil.computeDistanceBetween(ATF_Hougang_Central,latLng));
        LatLng ATF_Serangoon_Garden= new LatLng(1.363265,103.865831);
        mMap.addMarker(new MarkerOptions().position(ATF_Serangoon_Garden).title(getResources().getString(R.string.ATF_Serangoon_Garden)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Serangoon_Garden),SphericalUtil.computeDistanceBetween(ATF_Serangoon_Garden,latLng));
        LatLng ATF_Upper_Thomson= new LatLng(1.354012,103.834230);
        mMap.addMarker(new MarkerOptions().position(ATF_Upper_Thomson).title(getResources().getString(R.string.ATF_Upper_Thomson)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Upper_Thomson),SphericalUtil.computeDistanceBetween(ATF_Upper_Thomson,latLng));
        LatLng ATF_Punggol_Oasis= new LatLng(1.402847, 103.913231);
        mMap.addMarker(new MarkerOptions().position(ATF_Punggol_Oasis).title(getResources().getString(R.string.ATF_Punggol_Oasis)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Punggol_Oasis),SphericalUtil.computeDistanceBetween(ATF_Punggol_Oasis,latLng));
        LatLng ATF_SKH_Campus= new LatLng(1.396426, 103.891121);
        mMap.addMarker(new MarkerOptions().position(ATF_SKH_Campus).title(getResources().getString(R.string.ATF_SKH_Campus)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_SKH_Campus),SphericalUtil.computeDistanceBetween(ATF_SKH_Campus,latLng));
        LatLng ATF_Bishan_North= new LatLng(1.361371, 103.841574);
        mMap.addMarker(new MarkerOptions().position(ATF_Bishan_North).title(getResources().getString(R.string.ATF_Bishan_North)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Bishan_North),SphericalUtil.computeDistanceBetween(ATF_Bishan_North,latLng));
        LatLng ATF_Kovan= new LatLng(1.360367, 103.888094);
        mMap.addMarker(new MarkerOptions().position(ATF_Kovan).title(getResources().getString(R.string.ATF_Kovan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Kovan),SphericalUtil.computeDistanceBetween(ATF_Kovan,latLng));
        LatLng ATF_Elias_CC= new LatLng(1.378329, 103.942766);
        mMap.addMarker(new MarkerOptions().position(ATF_Elias_CC).title(getResources().getString(R.string.ATF_Elias_CC)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Elias_CC),SphericalUtil.computeDistanceBetween(ATF_Elias_CC,latLng));
        LatLng ATF_Pasir_Ris= new LatLng(1.375738, 103.955730);
        mMap.addMarker(new MarkerOptions().position(ATF_Pasir_Ris).title(getResources().getString(R.string.ATF_Pasir_Ris)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Pasir_Ris),SphericalUtil.computeDistanceBetween(ATF_Pasir_Ris,latLng));
        LatLng ATF_Tampines_North= new LatLng(1.357473, 103.946582);
        mMap.addMarker(new MarkerOptions().position(ATF_Tampines_North).title(getResources().getString(R.string.ATF_Tampines_North)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tampines_North),SphericalUtil.computeDistanceBetween(ATF_Tampines_North,latLng));
        LatLng ATF_Tampines_1= new LatLng(1.348848, 103.935738);
        mMap.addMarker(new MarkerOptions().position(ATF_Tampines_1).title(getResources().getString(R.string.ATF_Tampines_1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tampines_1),SphericalUtil.computeDistanceBetween(ATF_Tampines_1,latLng));
        LatLng ATF_Tampines_2= new LatLng(1.354301, 103.960276);
        mMap.addMarker(new MarkerOptions().position(ATF_Tampines_2).title(getResources().getString(R.string.ATF_Tampines_2)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tampines_2),SphericalUtil.computeDistanceBetween(ATF_Tampines_2,latLng));
        LatLng ATF_Loyang_Point= new LatLng(1.367024, 103.964746);
        mMap.addMarker(new MarkerOptions().position(ATF_Loyang_Point).title(getResources().getString(R.string.ATF_Loyang_Point)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Loyang_Point),SphericalUtil.computeDistanceBetween(ATF_Loyang_Point,latLng));
        LatLng ATF_Eastpoint_Mall= new LatLng(1.342658, 103.953158);
        mMap.addMarker(new MarkerOptions().position(ATF_Eastpoint_Mall).title(getResources().getString(R.string.ATF_Eastpoint_Mall)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Eastpoint_Mall),SphericalUtil.computeDistanceBetween(ATF_Eastpoint_Mall,latLng));
        LatLng ATF_Changi_Business_Park= new LatLng(1.334696, 103.966575);
        mMap.addMarker(new MarkerOptions().position(ATF_Changi_Business_Park).title(getResources().getString(R.string.ATF_Changi_Business_Park)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Changi_Business_Park),SphericalUtil.computeDistanceBetween(ATF_Changi_Business_Park,latLng));
        LatLng ATF_Bedok_South_Bedok_CC= new LatLng(1.324712, 103.936021);
        mMap.addMarker(new MarkerOptions().position(ATF_Bedok_South_Bedok_CC).title(getResources().getString(R.string.ATF_Bedok_South_Bedok_CC)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Bedok_South_Bedok_CC),SphericalUtil.computeDistanceBetween(ATF_Bedok_South_Bedok_CC,latLng));
        LatLng ATF_Kembangan= new LatLng(1.318825, 103.911415);
        mMap.addMarker(new MarkerOptions().position(ATF_Kembangan).title(getResources().getString(R.string.ATF_Kembangan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Kembangan),SphericalUtil.computeDistanceBetween(ATF_Kembangan,latLng));
        LatLng ATF_Paya_Lebar= new LatLng(1.315137, 103.894644);
        mMap.addMarker(new MarkerOptions().position(ATF_Paya_Lebar).title(getResources().getString(R.string.ATF_Paya_Lebar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Paya_Lebar),SphericalUtil.computeDistanceBetween(ATF_Paya_Lebar,latLng));
        LatLng ATF_East_Coast= new LatLng(1.301708, 103.906695);
        mMap.addMarker(new MarkerOptions().position(ATF_East_Coast).title(getResources().getString(R.string.ATF_East_Coast)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_East_Coast),SphericalUtil.computeDistanceBetween(ATF_East_Coast,latLng));
        LatLng ATF_Kallang_Wave= new LatLng(1.303030, 103.873546);
        mMap.addMarker(new MarkerOptions().position(ATF_Kallang_Wave).title(getResources().getString(R.string.ATF_Kallang_Wave)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Kallang_Wave),SphericalUtil.computeDistanceBetween(ATF_Kallang_Wave,latLng));
        LatLng ATF_Upper_Cross_Street= new LatLng(1.284414, 103.845793);
        mMap.addMarker(new MarkerOptions().position(ATF_Upper_Cross_Street).title(getResources().getString(R.string.ATF_Upper_Cross_Street)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Upper_Cross_Street),SphericalUtil.computeDistanceBetween(ATF_Upper_Cross_Street,latLng));
        LatLng ATF_Cecil_Street= new LatLng(1.283031, 103.850493);
        mMap.addMarker(new MarkerOptions().position(ATF_Cecil_Street).title(getResources().getString(R.string.ATF_Cecil_Street)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Cecil_Street),SphericalUtil.computeDistanceBetween(ATF_Cecil_Street,latLng));
        LatLng ATF_Cantonment_Road= new LatLng(1.279191, 103.840169);
        mMap.addMarker(new MarkerOptions().position(ATF_Cantonment_Road).title(getResources().getString(R.string.ATF_Cantonment_Road)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Cantonment_Road),SphericalUtil.computeDistanceBetween(ATF_Cantonment_Road,latLng));
        LatLng ATF_Tanjong_Pagar= new LatLng(1.276082, 103.845915);
        mMap.addMarker(new MarkerOptions().position(ATF_Tanjong_Pagar).title(getResources().getString(R.string.ATF_Tanjong_Pagar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tanjong_Pagar),SphericalUtil.computeDistanceBetween(ATF_Tanjong_Pagar,latLng));
        LatLng ATF_HarbourFront= new LatLng(1.264541, 103.818660);
        mMap.addMarker(new MarkerOptions().position(ATF_HarbourFront).title(getResources().getString(R.string.ATF_HarbourFront)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_HarbourFront),SphericalUtil.computeDistanceBetween(ATF_HarbourFront,latLng));
        LatLng ATF_Queensway= new LatLng(1.287946, 103.803675);
        mMap.addMarker(new MarkerOptions().position(ATF_Queensway).title(getResources().getString(R.string.ATF_Queensway)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Queensway),SphericalUtil.computeDistanceBetween(ATF_Queensway,latLng));
        LatLng ATF_Telok_Blangah= new LatLng(1.273702, 103.808377);
        mMap.addMarker(new MarkerOptions().position(ATF_Telok_Blangah).title(getResources().getString(R.string.ATF_Telok_Blangah)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Telok_Blangah),SphericalUtil.computeDistanceBetween(ATF_Telok_Blangah,latLng));
        LatLng ATF_Pasir_Panjang= new LatLng(1.274558, 103.794223);
        mMap.addMarker(new MarkerOptions().position(ATF_Pasir_Panjang).title(getResources().getString(R.string.ATF_Pasir_Panjang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Pasir_Panjang),SphericalUtil.computeDistanceBetween(ATF_Pasir_Panjang,latLng));
        LatLng ATF_Clementi_City= new LatLng(1.314244, 103.765172);
        mMap.addMarker(new MarkerOptions().position(ATF_Clementi_City).title(getResources().getString(R.string.ATF_Clementi_City)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Clementi_City),SphericalUtil.computeDistanceBetween(ATF_Clementi_City,latLng));
        LatLng ATF_West_Coast= new LatLng(1.303729, 103.766028);
        mMap.addMarker(new MarkerOptions().position(ATF_West_Coast).title(getResources().getString(R.string.ATF_West_Coast)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_West_Coast),SphericalUtil.computeDistanceBetween(ATF_West_Coast,latLng));
        LatLng ATF_Taman_Jurong= new LatLng(1.335522, 103.721574);
        mMap.addMarker(new MarkerOptions().position(ATF_Taman_Jurong).title(getResources().getString(R.string.ATF_Taman_Jurong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Taman_Jurong),SphericalUtil.computeDistanceBetween(ATF_Taman_Jurong,latLng));
        LatLng ATF_Boon_Lay= new LatLng(1.346288, 103.712581);
        mMap.addMarker(new MarkerOptions().position(ATF_Boon_Lay).title(getResources().getString(R.string.ATF_Boon_Lay)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Boon_Lay),SphericalUtil.computeDistanceBetween(ATF_Boon_Lay,latLng));
        LatLng ATF_Jurong_West= new LatLng(1.342407, 103.692496);
        mMap.addMarker(new MarkerOptions().position(ATF_Jurong_West).title(getResources().getString(R.string.ATF_Jurong_West)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Jurong_West),SphericalUtil.computeDistanceBetween(ATF_Jurong_West,latLng));
        LatLng ATF_Bukit_Timah= new LatLng(1.339029, 103.776989);
        mMap.addMarker(new MarkerOptions().position(ATF_Bukit_Timah).title(getResources().getString(R.string.ATF_Bukit_Timah)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Bukit_Timah),SphericalUtil.computeDistanceBetween(ATF_Bukit_Timah,latLng));
        LatLng ATF_CSC_Bukit_Batok= new LatLng(1.352706, 103.749808);
        mMap.addMarker(new MarkerOptions().position(ATF_CSC_Bukit_Batok).title(getResources().getString(R.string.ATF_CSC_Bukit_Batok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_CSC_Bukit_Batok),SphericalUtil.computeDistanceBetween(ATF_CSC_Bukit_Batok,latLng));
        LatLng ATF_Fitness_hillV2= new LatLng(1.363136, 103.764190);
        mMap.addMarker(new MarkerOptions().position(ATF_Fitness_hillV2).title(getResources().getString(R.string.ATF_Fitness_hillV2)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Fitness_hillV2),SphericalUtil.computeDistanceBetween(ATF_Fitness_hillV2,latLng));
        LatLng ATF_Choa_Chu_Kang= new LatLng(1.376907, 103.745243);
        mMap.addMarker(new MarkerOptions().position(ATF_Choa_Chu_Kang).title(getResources().getString(R.string.ATF_Choa_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Choa_Chu_Kang),SphericalUtil.computeDistanceBetween(ATF_Choa_Chu_Kang,latLng));
        LatLng ATF_Yew_Tee= new LatLng(1.394958, 103.744725);
        mMap.addMarker(new MarkerOptions().position(ATF_Yew_Tee).title(getResources().getString(R.string.ATF_Yew_Tee)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Yew_Tee),SphericalUtil.computeDistanceBetween(ATF_Yew_Tee,latLng));
        LatLng ATF_Tiong_Bahru_Plaza= new LatLng(1.286415, 103.826478);
        mMap.addMarker(new MarkerOptions().position(ATF_Tiong_Bahru_Plaza).title(getResources().getString(R.string.ATF_Tiong_Bahru_Plaza)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tiong_Bahru_Plaza),SphericalUtil.computeDistanceBetween(ATF_Tiong_Bahru_Plaza,latLng));
        LatLng ATF_River_Valley= new LatLng(1.292185, 103.826451);
        mMap.addMarker(new MarkerOptions().position(ATF_River_Valley).title(getResources().getString(R.string.ATF_River_Valley)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_River_Valley),SphericalUtil.computeDistanceBetween(ATF_River_Valley,latLng));
        LatLng ATF_Valley_Point= new LatLng(1.293406, 103.827208);
        mMap.addMarker(new MarkerOptions().position(ATF_Valley_Point).title(getResources().getString(R.string.ATF_Valley_Point)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Valley_Point),SphericalUtil.computeDistanceBetween(ATF_Valley_Point,latLng));
        LatLng ATF_PoMo_Mall= new LatLng(1.300162, 103.849072);
        mMap.addMarker(new MarkerOptions().position(ATF_PoMo_Mall).title(getResources().getString(R.string.ATF_PoMo_Mall)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_PoMo_Mall),SphericalUtil.computeDistanceBetween(ATF_PoMo_Mall,latLng));
        LatLng ATF_Tanglin= new LatLng(1.305707, 103.822954);
        mMap.addMarker(new MarkerOptions().position(ATF_Tanglin).title(getResources().getString(R.string.ATF_Tanglin)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Tanglin),SphericalUtil.computeDistanceBetween(ATF_Tanglin,latLng));
        LatLng ATF_Jalan_Besar= new LatLng(1.307420, 103.858449);
        mMap.addMarker(new MarkerOptions().position(ATF_Jalan_Besar).title(getResources().getString(R.string.ATF_Jalan_Besar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Jalan_Besar),SphericalUtil.computeDistanceBetween(ATF_Jalan_Besar,latLng));
        LatLng ATF_Stevens= new LatLng(1.319916, 103.827401);
        mMap.addMarker(new MarkerOptions().position(ATF_Stevens).title(getResources().getString(R.string.ATF_Stevens)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Stevens),SphericalUtil.computeDistanceBetween(ATF_Stevens,latLng));
        LatLng ATF_Novena= new LatLng(1.320991, 103.841810);
        mMap.addMarker(new MarkerOptions().position(ATF_Novena).title(getResources().getString(R.string.ATF_Novena)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Novena),SphericalUtil.computeDistanceBetween(ATF_Novena,latLng));
        LatLng ATF_Toa_Payoh= new LatLng(1.333202, 103.848635);
        mMap.addMarker(new MarkerOptions().position(ATF_Toa_Payoh).title(getResources().getString(R.string.ATF_Toa_Payoh)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Toa_Payoh),SphericalUtil.computeDistanceBetween(ATF_Toa_Payoh,latLng));
        LatLng ATF_Potong_Pasir= new LatLng(1.329381, 103.869477);
        mMap.addMarker(new MarkerOptions().position(ATF_Potong_Pasir).title(getResources().getString(R.string.ATF_Potong_Pasir)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Potong_Pasir),SphericalUtil.computeDistanceBetween(ATF_Potong_Pasir,latLng));
        LatLng ATF_Geylang_Bahru= new LatLng(1.324311, 103.869705);
        mMap.addMarker(new MarkerOptions().position(ATF_Geylang_Bahru).title(getResources().getString(R.string.ATF_Geylang_Bahru)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Geylang_Bahru),SphericalUtil.computeDistanceBetween(ATF_Geylang_Bahru,latLng));
        LatLng ATF_Balestier= new LatLng(1.322989, 103.852886);
        mMap.addMarker(new MarkerOptions().position(ATF_Balestier).title(getResources().getString(R.string.ATF_Balestier)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Balestier),SphericalUtil.computeDistanceBetween(ATF_Balestier,latLng));
        LatLng ATF_Boon_Keng= new LatLng(1.314844, 103.859958);
        mMap.addMarker(new MarkerOptions().position(ATF_Boon_Keng).title(getResources().getString(R.string.ATF_Boon_Keng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Boon_Keng),SphericalUtil.computeDistanceBetween(ATF_Boon_Keng,latLng));
        LatLng ATF_MacPherson= new LatLng(1.323432, 103.884716);
        mMap.addMarker(new MarkerOptions().position(ATF_MacPherson).title(getResources().getString(R.string.ATF_MacPherson)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_MacPherson),SphericalUtil.computeDistanceBetween(ATF_MacPherson,latLng));
        LatLng ATF_Hwi_Yoh= new LatLng(1.374776, 103.873642);
        mMap.addMarker(new MarkerOptions().position(ATF_Hwi_Yoh).title(getResources().getString(R.string.ATF_Hwi_Yoh)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_Hwi_Yoh),SphericalUtil.computeDistanceBetween(ATF_Hwi_Yoh,latLng));
        LatLng ATF_nex= new LatLng(1.350963, 103.872407);
        mMap.addMarker(new MarkerOptions().position(ATF_nex).title(getResources().getString(R.string.ATF_nex)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ATF_nex),SphericalUtil.computeDistanceBetween(ATF_nex,latLng));

        //Contours Express Fitness Boutique
        LatLng CE_Bedok= new LatLng(1.324729, 103.931670);
        mMap.addMarker(new MarkerOptions().position(CE_Bedok).title(getResources().getString(R.string.CE_Bedok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Bedok),SphericalUtil.computeDistanceBetween(CE_Bedok,latLng));
        LatLng CE_Bishan= new LatLng(1.359656, 103.842076);
        mMap.addMarker(new MarkerOptions().position(CE_Bishan).title(getResources().getString(R.string.CE_Bishan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Bishan),SphericalUtil.computeDistanceBetween(CE_Bishan,latLng));
        LatLng CE_Choa_Chu_Kang= new LatLng(1.381907, 103.749400);
        mMap.addMarker(new MarkerOptions().position(CE_Choa_Chu_Kang).title(getResources().getString(R.string.CE_Choa_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Choa_Chu_Kang),SphericalUtil.computeDistanceBetween(CE_Choa_Chu_Kang,latLng));
        LatLng CE_Jurong_East= new LatLng(1.333796, 103.739798);
        mMap.addMarker(new MarkerOptions().position(CE_Jurong_East).title(getResources().getString(R.string.CE_Jurong_East)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Jurong_East),SphericalUtil.computeDistanceBetween(CE_Jurong_East,latLng));
        LatLng CE_Pasir_Ris= new LatLng(1.376096, 103.946410);
        mMap.addMarker(new MarkerOptions().position(CE_Pasir_Ris).title(getResources().getString(R.string.CE_Pasir_Ris)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Pasir_Ris),SphericalUtil.computeDistanceBetween(CE_Pasir_Ris,latLng));
        LatLng CE_Serangoon= new LatLng(1.354098, 103.870964);
        mMap.addMarker(new MarkerOptions().position(CE_Serangoon).title(getResources().getString(R.string.CE_Serangoon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Serangoon),SphericalUtil.computeDistanceBetween(CE_Serangoon,latLng));
        LatLng CE_Tampines= new LatLng(1.353163, 103.954220);
        mMap.addMarker(new MarkerOptions().position(CE_Tampines).title(getResources().getString(R.string.CE_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Tampines),SphericalUtil.computeDistanceBetween(CE_Tampines,latLng));
        LatLng CE_Woodlands= new LatLng(1.443708, 103.789132);
        mMap.addMarker(new MarkerOptions().position(CE_Woodlands).title(getResources().getString(R.string.CE_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Woodlands),SphericalUtil.computeDistanceBetween(CE_Woodlands,latLng));
        LatLng CE_Yishun= new LatLng(1.426946, 103.837330);
        mMap.addMarker(new MarkerOptions().position(CE_Yishun).title(getResources().getString(R.string.CE_Yishun)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.CE_Yishun),SphericalUtil.computeDistanceBetween(CE_Yishun,latLng));

        //ActiveSG
        LatLng ASG_AMK= new LatLng(1.366962, 103.840415);
        mMap.addMarker(new MarkerOptions().position(ASG_AMK).title(getResources().getString(R.string.ASG_AMK)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_AMK),SphericalUtil.computeDistanceBetween(ASG_AMK,latLng));
        LatLng ASG_Fernvale_Square= new LatLng(1.391261, 103.872987);
        mMap.addMarker(new MarkerOptions().position(ASG_Fernvale_Square).title(getResources().getString(R.string.ASG_Fernvale_Square)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Fernvale_Square),SphericalUtil.computeDistanceBetween(ASG_Fernvale_Square,latLng));
        //LatLng ASG_Toa_Payoh= new LatLng(1.330523, 103.850778);
        //mMap.addMarker(new MarkerOptions().position(ASG_Toa_Payoh).title("Toa Payoh ActiveSG Gym").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        LatLng ASG_Boon_Lay= new LatLng(1.347790, 103.711543);
        mMap.addMarker(new MarkerOptions().position(ASG_Boon_Lay).title(getResources().getString(R.string.ASG_Boon_Lay)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Boon_Lay),SphericalUtil.computeDistanceBetween(ASG_Boon_Lay,latLng));
        LatLng ASG_Bishan= new LatLng(1.355539, 103.851013);
        mMap.addMarker(new MarkerOptions().position(ASG_Bishan).title(getResources().getString(R.string.ASG_Bishan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Bishan),SphericalUtil.computeDistanceBetween(ASG_Bishan,latLng));
        LatLng ASG_Bukit_Gombak= new LatLng(1.359633, 103.752367);
        mMap.addMarker(new MarkerOptions().position(ASG_Bukit_Gombak).title(getResources().getString(R.string.ASG_Bukit_Gombak)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Bukit_Gombak),SphericalUtil.computeDistanceBetween(ASG_Bukit_Gombak,latLng));
        LatLng ASG_Bukit_Batok= new LatLng(1.344538, 103.747918);
        mMap.addMarker(new MarkerOptions().position(ASG_Bukit_Batok).title(getResources().getString(R.string.ASG_Bukit_Batok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Bukit_Batok),SphericalUtil.computeDistanceBetween(ASG_Bukit_Batok,latLng));
        LatLng ASG_Choa_Chu_Kang= new LatLng(1.391056, 103.748152);
        mMap.addMarker(new MarkerOptions().position(ASG_Choa_Chu_Kang).title(getResources().getString(R.string.ASG_Choa_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Choa_Chu_Kang),SphericalUtil.computeDistanceBetween(ASG_Choa_Chu_Kang,latLng));
        LatLng ASG_Clementi= new LatLng(1.310981, 103.765001);
        mMap.addMarker(new MarkerOptions().position(ASG_Clementi).title(getResources().getString(R.string.ASG_Clementi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Clementi),SphericalUtil.computeDistanceBetween(ASG_Clementi,latLng));
        LatLng ASG_Delta= new LatLng(1.290488, 103.820511);
        mMap.addMarker(new MarkerOptions().position(ASG_Delta).title(getResources().getString(R.string.ASG_Delta)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Delta),SphericalUtil.computeDistanceBetween(ASG_Delta,latLng));
        LatLng ASG_Enabling_Village= new LatLng(1.287258, 103.814820);
        mMap.addMarker(new MarkerOptions().position(ASG_Enabling_Village).title(getResources().getString(R.string.ASG_Enabling_Village)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Enabling_Village),SphericalUtil.computeDistanceBetween(ASG_Enabling_Village,latLng));
        LatLng ASG_Bedok= new LatLng(1.326986, 103.932151);
        mMap.addMarker(new MarkerOptions().position(ASG_Bedok).title(getResources().getString(R.string.ASG_Bedok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Bedok),SphericalUtil.computeDistanceBetween(ASG_Bedok,latLng));
        LatLng ASG_Hougang= new LatLng(1.370795, 103.888158);
        mMap.addMarker(new MarkerOptions().position(ASG_Hougang).title(getResources().getString(R.string.ASG_Hougang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Hougang),SphericalUtil.computeDistanceBetween(ASG_Hougang,latLng));
        LatLng ASG_Jalan_Besar= new LatLng(1.310555, 103.859658);
        mMap.addMarker(new MarkerOptions().position(ASG_Jalan_Besar).title(getResources().getString(R.string.ASG_Jalan_Besar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Jalan_Besar),SphericalUtil.computeDistanceBetween(ASG_Jalan_Besar,latLng));
        LatLng ASG_Jurong_East= new LatLng(1.346900, 103.729192);
        mMap.addMarker(new MarkerOptions().position(ASG_Jurong_East).title(getResources().getString(R.string.ASG_Jurong_East)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Jurong_East),SphericalUtil.computeDistanceBetween(ASG_Jurong_East,latLng));
        LatLng ASG_Jurong_Lake= new LatLng(1.330536, 103.725610);
        mMap.addMarker(new MarkerOptions().position(ASG_Jurong_Lake).title(getResources().getString(R.string.ASG_Jurong_Lake)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Jurong_Lake),SphericalUtil.computeDistanceBetween(ASG_Jurong_Lake,latLng));
        LatLng ASG_Jurong_West= new LatLng(1.337832, 103.694191);
        mMap.addMarker(new MarkerOptions().position(ASG_Jurong_West).title(getResources().getString(R.string.ASG_Jurong_West)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Jurong_West),SphericalUtil.computeDistanceBetween(ASG_Jurong_West,latLng));
        LatLng ASG_Pasir_Ris= new LatLng(1.374015, 103.951895);
        mMap.addMarker(new MarkerOptions().position(ASG_Pasir_Ris).title(getResources().getString(R.string.ASG_Pasir_Ris)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Pasir_Ris),SphericalUtil.computeDistanceBetween(ASG_Pasir_Ris,latLng));
        LatLng ASG_Sengkang= new LatLng(1.396483, 103.886833);
        mMap.addMarker(new MarkerOptions().position(ASG_Sengkang).title(getResources().getString(R.string.ASG_Sengkang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Sengkang),SphericalUtil.computeDistanceBetween(ASG_Sengkang,latLng));
        LatLng ASG_Tampines= new LatLng(1.353711, 103.940769);
        mMap.addMarker(new MarkerOptions().position(ASG_Tampines).title(getResources().getString(R.string.ASG_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Tampines),SphericalUtil.computeDistanceBetween(ASG_Tampines,latLng));
        LatLng ASG_Toa_Payoh= new LatLng(1.330495, 103.850787);
        mMap.addMarker(new MarkerOptions().position(ASG_Toa_Payoh).title(getResources().getString(R.string.ASG_Toa_Payoh)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Toa_Payoh),SphericalUtil.computeDistanceBetween(ASG_Toa_Payoh,latLng));
        LatLng ASG_Woodlands= new LatLng(1.434116, 103.779787);
        mMap.addMarker(new MarkerOptions().position(ASG_Woodlands).title(getResources().getString(R.string.ASG_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Woodlands),SphericalUtil.computeDistanceBetween(ASG_Woodlands,latLng));
        LatLng ASG_Yio_Chu_Kang= new LatLng(1.382243, 103.845928);
        mMap.addMarker(new MarkerOptions().position(ASG_Yio_Chu_Kang).title(getResources().getString(R.string.ASG_Yio_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Yio_Chu_Kang),SphericalUtil.computeDistanceBetween(ASG_Yio_Chu_Kang,latLng));
        LatLng ASG_Yishun= new LatLng(1.411999, 103.831112);
        mMap.addMarker(new MarkerOptions().position(ASG_Yishun).title(getResources().getString(R.string.ASG_Yishun)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.ASG_Yishun),SphericalUtil.computeDistanceBetween(ASG_Yishun,latLng));

        //GYMMBOXX
        LatLng GB_Kebun_Baru= new LatLng(1.373239, 103.837463);
        mMap.addMarker(new MarkerOptions().position(GB_Kebun_Baru).title(getResources().getString(R.string.GB_Kebun_Baru)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_Kebun_Baru),SphericalUtil.computeDistanceBetween(GB_Kebun_Baru,latLng));
        LatLng GB_Keat_Hong= new LatLng(1.384148, 103.745171);
        mMap.addMarker(new MarkerOptions().position(GB_Keat_Hong).title(getResources().getString(R.string.GB_Keat_Hong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_Keat_Hong),SphericalUtil.computeDistanceBetween(GB_Keat_Hong,latLng));
        LatLng GB_Bishan= new LatLng(1.349938, 103.850881);
        mMap.addMarker(new MarkerOptions().position(GB_Bishan).title(getResources().getString(R.string.GB_Bishan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_Bishan),SphericalUtil.computeDistanceBetween(GB_Bishan,latLng));
        LatLng GB_JCube= new LatLng(1.333444, 103.740326);
        mMap.addMarker(new MarkerOptions().position(GB_JCube).title(getResources().getString(R.string.GB_JCube)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_JCube),SphericalUtil.computeDistanceBetween(GB_JCube,latLng));
        LatLng GB_Tampines= new LatLng(1.352164, 103.943606);
        mMap.addMarker(new MarkerOptions().position(GB_Tampines).title(getResources().getString(R.string.GB_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_Tampines),SphericalUtil.computeDistanceBetween(GB_Tampines,latLng));
        LatLng GB_Bedok_Point= new LatLng(1.325166, 103.932259);
        mMap.addMarker(new MarkerOptions().position(GB_Bedok_Point).title(getResources().getString(R.string.GB_Bedok_Point)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.GB_Bedok_Point),SphericalUtil.computeDistanceBetween(GB_Bedok_Point,latLng));

        //True Fitness
        LatLng TF_AMK= new LatLng(1.372891, 103.847665);
        mMap.addMarker(new MarkerOptions().position(TF_AMK).title(getResources().getString(R.string.TF_AMK)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_AMK),SphericalUtil.computeDistanceBetween(TF_AMK,latLng));
        LatLng TF_Tampines= new LatLng(1.352130, 103.941966);
        mMap.addMarker(new MarkerOptions().position(TF_Tampines).title(getResources().getString(R.string.TF_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_Tampines),SphericalUtil.computeDistanceBetween(TF_Tampines,latLng));
        LatLng TF_Novena= new LatLng(1.319684, 103.843993);
        mMap.addMarker(new MarkerOptions().position(TF_Novena).title(getResources().getString(R.string.TF_Novena)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_Novena),SphericalUtil.computeDistanceBetween(TF_Novena,latLng));
        LatLng TF_Upper_Changi= new LatLng(1.323157, 103.921411);
        mMap.addMarker(new MarkerOptions().position(TF_Upper_Changi).title(getResources().getString(R.string.TF_Upper_Changi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_Upper_Changi),SphericalUtil.computeDistanceBetween(TF_Upper_Changi,latLng));
        LatLng TF_Zion= new LatLng(1.292934, 103.832157);
        mMap.addMarker(new MarkerOptions().position(TF_Zion).title(getResources().getString(R.string.TF_Zion)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_Zion),SphericalUtil.computeDistanceBetween(TF_Zion,latLng));
        LatLng TF_Promenade= new LatLng(1.292888, 103.859718);
        mMap.addMarker(new MarkerOptions().position(TF_Promenade).title(getResources().getString(R.string.TF_Promenade)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_Promenade),SphericalUtil.computeDistanceBetween(TF_Promenade,latLng));
        LatLng TF_HarbourFront= new LatLng(1.264232, 103.820368);
        mMap.addMarker(new MarkerOptions().position(TF_HarbourFront).title(getResources().getString(R.string.TF_HarbourFront)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.TF_HarbourFront),SphericalUtil.computeDistanceBetween(TF_HarbourFront,latLng));


        //Fitness First
        LatLng FF_AMK= new LatLng(1.369752, 103.848707);
        mMap.addMarker(new MarkerOptions().position(FF_AMK).title(getResources().getString(R.string.FF_AMK)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_AMK),SphericalUtil.computeDistanceBetween(FF_AMK,latLng));
        LatLng FF_Junction10= new LatLng(1.380832, 103.760158);
        mMap.addMarker(new MarkerOptions().position(FF_Junction10).title(getResources().getString(R.string.FF_Junction10)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Junction10),SphericalUtil.computeDistanceBetween(FF_Junction10,latLng));
        LatLng FF_Tanjong_Pagar= new LatLng(1.275026, 103.843655);
        mMap.addMarker(new MarkerOptions().position(FF_Tanjong_Pagar).title(getResources().getString(R.string.FF_Tanjong_Pagar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Tanjong_Pagar),SphericalUtil.computeDistanceBetween(FF_Tanjong_Pagar,latLng));
        LatLng FF_Clementi= new LatLng(1.312046, 103.764891);
        mMap.addMarker(new MarkerOptions().position(FF_Clementi).title(getResources().getString(R.string.FF_Clementi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Clementi),SphericalUtil.computeDistanceBetween(FF_Clementi,latLng));
        LatLng FF_Alexandra= new LatLng(1.274880, 103.799745);
        mMap.addMarker(new MarkerOptions().position(FF_Alexandra).title(getResources().getString(R.string.FF_Alexandra)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Alexandra),SphericalUtil.computeDistanceBetween(FF_Alexandra,latLng));
        LatLng FF_Bugis_Junction= new LatLng(1.299701, 103.855730);
        mMap.addMarker(new MarkerOptions().position(FF_Bugis_Junction).title(getResources().getString(R.string.FF_Bugis_Junction)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Bugis_Junction),SphericalUtil.computeDistanceBetween(FF_Bugis_Junction,latLng));
        LatLng FF_Capital_Tower= new LatLng(1.277795, 103.847544);
        mMap.addMarker(new MarkerOptions().position(FF_Capital_Tower).title(getResources().getString(R.string.FF_Capital_Tower)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Capital_Tower),SphericalUtil.computeDistanceBetween(FF_Capital_Tower,latLng));
        LatLng FF_Changi= new LatLng(1.335244, 103.963867);
        mMap.addMarker(new MarkerOptions().position(FF_Changi).title(getResources().getString(R.string.FF_Changi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Changi),SphericalUtil.computeDistanceBetween(FF_Changi,latLng));
        LatLng FF_Fusionopolis= new LatLng(1.299763, 103.787741);
        mMap.addMarker(new MarkerOptions().position(FF_Fusionopolis).title(getResources().getString(R.string.FF_Fusionopolis)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Fusionopolis),SphericalUtil.computeDistanceBetween(FF_Fusionopolis,latLng));
        LatLng FF_Marina_Bay= new LatLng(1.280054, 103.855070);
        mMap.addMarker(new MarkerOptions().position(FF_Marina_Bay).title(getResources().getString(R.string.FF_Marina_Bay)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Marina_Bay),SphericalUtil.computeDistanceBetween(FF_Marina_Bay,latLng));
        LatLng FF_Market_Street= new LatLng(1.284390, 103.850517);
        mMap.addMarker(new MarkerOptions().position(FF_Market_Street).title(getResources().getString(R.string.FF_Market_Street)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Market_Street),SphericalUtil.computeDistanceBetween(FF_Market_Street,latLng));
        LatLng FF_One_George_Street= new LatLng(1.285675, 103.847863);
        mMap.addMarker(new MarkerOptions().position(FF_One_George_Street).title(getResources().getString(R.string.FF_One_George_Street)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_One_George_Street),SphericalUtil.computeDistanceBetween(FF_One_George_Street,latLng));
        LatLng FF_One_Raffles_Quay= new LatLng(1.281152, 103.851928);
        mMap.addMarker(new MarkerOptions().position(FF_One_Raffles_Quay).title(getResources().getString(R.string.FF_One_Raffles_Quay)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_One_Raffles_Quay),SphericalUtil.computeDistanceBetween(FF_One_Raffles_Quay,latLng));
        LatLng FF_Paragon= new LatLng(1.304343, 103.836177);
        mMap.addMarker(new MarkerOptions().position(FF_Paragon).title(getResources().getString(R.string.FF_Paragon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Paragon),SphericalUtil.computeDistanceBetween(FF_Paragon,latLng));
        LatLng FF_Paya_Lebar= new LatLng(1.318687, 103.893856);
        mMap.addMarker(new MarkerOptions().position(FF_Paya_Lebar).title(getResources().getString(R.string.FF_Paya_Lebar)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Paya_Lebar),SphericalUtil.computeDistanceBetween(FF_Paya_Lebar,latLng));
        LatLng FF_Tampines= new LatLng(1.353155, 103.943672);
        mMap.addMarker(new MarkerOptions().position(FF_Tampines).title(getResources().getString(R.string.FF_Tampines)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Tampines),SphericalUtil.computeDistanceBetween(FF_Tampines,latLng));
        LatLng FF_Dhoby_Ghaut= new LatLng(1.299424, 103.847637);
        mMap.addMarker(new MarkerOptions().position(FF_Dhoby_Ghaut).title(getResources().getString(R.string.FF_Dhoby_Ghaut)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Dhoby_Ghaut),SphericalUtil.computeDistanceBetween(FF_Dhoby_Ghaut,latLng));
        LatLng FF_Metropolis= new LatLng(1.305533, 103.791977);
        mMap.addMarker(new MarkerOptions().position(FF_Metropolis).title(getResources().getString(R.string.FF_Metropolis)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Metropolis),SphericalUtil.computeDistanceBetween(FF_Metropolis,latLng));
        LatLng FF_Westgate= new LatLng(1.334228, 103.743315);
        mMap.addMarker(new MarkerOptions().position(FF_Westgate).title(getResources().getString(R.string.FF_Westgate)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FF_Westgate),SphericalUtil.computeDistanceBetween(FF_Westgate,latLng));

        //Fitness Workz Gym
        LatLng FW_Bukit_Batok= new LatLng(1.365851, 103.750302);
        mMap.addMarker(new MarkerOptions().position(FW_Bukit_Batok).title(getResources().getString(R.string.FW_Bukit_Batok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FW_Bukit_Batok),SphericalUtil.computeDistanceBetween(FW_Bukit_Batok,latLng));
        LatLng FW_Balestier= new LatLng(1.329011, 103.848763);
        mMap.addMarker(new MarkerOptions().position(FW_Balestier).title(getResources().getString(R.string.FW_Balestier)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        InsertCheckDistance(getResources().getString(R.string.FW_Balestier),SphericalUtil.computeDistanceBetween(FW_Balestier,latLng));
        //LatLng FW_Khatib= new LatLng(1.365835, 103.750318);
        //mMap.addMarker(new MarkerOptions().position().title("Fitness Workz Gym").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));
        //LatLng FW_Sembawang= new LatLng();
        //mMap.addMarker(new MarkerOptions().position().title("Fitness Workz Gym").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));

        mParkList_sorted.putAll(mParkList);
        showParkList(mParkList_sorted);
        mAdapter = new ParkAdapter(ParkDetails,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

    private void SingaporeStadium(){

        ParkDetails.clear();
        ParkDetails = new ArrayList<>();
        mParkList = new LinkedHashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(mParkList);
        TreeMap<String, Double> mParkList_sorted = new TreeMap<String, Double>(bvc);

        LatLng Stadium_Woodlands= new LatLng(1.434573, 103.780822);
        mMap.addMarker(new MarkerOptions().position(Stadium_Woodlands).title(getResources().getString(R.string.Stadium_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Woodlands),SphericalUtil.computeDistanceBetween(Stadium_Woodlands,latLng));
        LatLng Sports_Centre_Woodlands= new LatLng(1.434118, 103.779801);
        mMap.addMarker(new MarkerOptions().position(Sports_Centre_Woodlands).title(getResources().getString(R.string.Sports_Centre_Woodlands)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Centre_Woodlands),SphericalUtil.computeDistanceBetween(Sports_Centre_Woodlands,latLng));
        LatLng Stadium_Yio_Chu_Kang= new LatLng(1.382958, 103.846586);
        mMap.addMarker(new MarkerOptions().position(Stadium_Yio_Chu_Kang).title(getResources().getString(R.string.Stadium_Yio_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Yio_Chu_Kang),SphericalUtil.computeDistanceBetween(Stadium_Yio_Chu_Kang,latLng));
        LatLng Sports_Centre_Yio_Chu_Kang= new LatLng(1.381832, 103.845008);
        mMap.addMarker(new MarkerOptions().position(Sports_Centre_Yio_Chu_Kang).title(getResources().getString(R.string.Sports_Centre_Yio_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Centre_Yio_Chu_Kang),SphericalUtil.computeDistanceBetween(Sports_Centre_Yio_Chu_Kang,latLng));
        LatLng Sports_Centre_Sengkang= new LatLng(1.396528, 103.886842);
        mMap.addMarker(new MarkerOptions().position(Sports_Centre_Sengkang).title(getResources().getString(R.string.Sports_Centre_Sengkang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Centre_Sengkang),SphericalUtil.computeDistanceBetween(Sports_Centre_Sengkang,latLng));
        LatLng Stadium_Bishan= new LatLng(1.354501, 103.851566);
        mMap.addMarker(new MarkerOptions().position(Stadium_Bishan).title(getResources().getString(R.string.Stadium_Bishan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Bishan),SphericalUtil.computeDistanceBetween(Stadium_Bishan,latLng));
        LatLng Sports_Hall_Bishan= new LatLng(1.355330, 103.850899);
        mMap.addMarker(new MarkerOptions().position(Sports_Hall_Bishan).title(getResources().getString(R.string.Sports_Hall_Bishan)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Hall_Bishan),SphericalUtil.computeDistanceBetween(Sports_Hall_Bishan,latLng));
        LatLng Stadium_Serangoon= new LatLng(1.355977, 103.874897);
        mMap.addMarker(new MarkerOptions().position(Stadium_Serangoon).title(getResources().getString(R.string.Stadium_Serangoon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Serangoon),SphericalUtil.computeDistanceBetween(Stadium_Serangoon,latLng));
        LatLng Sports_Centre_Hougang= new LatLng(1.370874, 103.888147);
        mMap.addMarker(new MarkerOptions().position(Sports_Centre_Hougang).title(getResources().getString(R.string.Sports_Centre_Hougang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Centre_Hougang),SphericalUtil.computeDistanceBetween(Sports_Centre_Hougang,latLng));
        LatLng Stadium_Hougang= new LatLng(1.369689, 103.887101);
        mMap.addMarker(new MarkerOptions().position(Stadium_Hougang).title(getResources().getString(R.string.Stadium_Hougang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Hougang),SphericalUtil.computeDistanceBetween(Stadium_Hougang,latLng));
        LatLng Stadium_Bedok= new LatLng(1.326547, 103.939376);
        mMap.addMarker(new MarkerOptions().position(Stadium_Bedok).title(getResources().getString(R.string.Stadium_Bedok)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Bedok),SphericalUtil.computeDistanceBetween(Stadium_Bedok,latLng));
        LatLng Field_Kallang= new LatLng(1.304224, 103.880911);
        mMap.addMarker(new MarkerOptions().position(Field_Kallang).title(getResources().getString(R.string.Field_Kallang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Field_Kallang),SphericalUtil.computeDistanceBetween(Field_Kallang,latLng));
        LatLng Stadium_Queenstown= new LatLng(1.295858, 103.802460);
        mMap.addMarker(new MarkerOptions().position(Stadium_Queenstown).title(getResources().getString(R.string.Stadium_Queenstown)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Queenstown),SphericalUtil.computeDistanceBetween(Stadium_Queenstown,latLng));
        LatLng Stadium_Clementi= new LatLng(1.310137, 103.762699);
        mMap.addMarker(new MarkerOptions().position(Stadium_Clementi).title(getResources().getString(R.string.Stadium_Clementi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Clementi),SphericalUtil.computeDistanceBetween(Stadium_Clementi,latLng));
        LatLng Stadium_MOE_PESEB= new LatLng(1.320489, 103.819856);
        mMap.addMarker(new MarkerOptions().position(Stadium_MOE_PESEB).title(getResources().getString(R.string.Stadium_MOE_PESEB)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_MOE_PESEB),SphericalUtil.computeDistanceBetween(Stadium_MOE_PESEB,latLng));
        LatLng Stadium_Toa_Payoh= new LatLng(1.330365, 103.852983);
        mMap.addMarker(new MarkerOptions().position(Stadium_Toa_Payoh).title(getResources().getString(R.string.Stadium_Toa_Payoh)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Toa_Payoh),SphericalUtil.computeDistanceBetween(Stadium_Toa_Payoh,latLng));
        LatLng Stadium_Jurong_East= new LatLng(1.346743, 103.729946);
        mMap.addMarker(new MarkerOptions().position(Stadium_Jurong_East).title(getResources().getString(R.string.Stadium_Jurong_East)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Jurong_East),SphericalUtil.computeDistanceBetween(Stadium_Jurong_East,latLng));
        LatLng Stadium_Jurong_West= new LatLng(1.338414, 103.694975);
        mMap.addMarker(new MarkerOptions().position(Stadium_Jurong_West).title(getResources().getString(R.string.Stadium_Jurong_West)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Jurong_West),SphericalUtil.computeDistanceBetween(Stadium_Jurong_West,latLng));
        LatLng Stadium_Bukit_Gombak= new LatLng(1.358339, 103.753617);
        mMap.addMarker(new MarkerOptions().position(Stadium_Bukit_Gombak).title(getResources().getString(R.string.Stadium_Bukit_Gombak)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Stadium_Bukit_Gombak),SphericalUtil.computeDistanceBetween(Stadium_Bukit_Gombak,latLng));
        LatLng Sports_Centre_Choa_Chu_Kang= new LatLng(1.391084, 103.748157);
        mMap.addMarker(new MarkerOptions().position(Sports_Centre_Choa_Chu_Kang).title(getResources().getString(R.string.Sports_Centre_Choa_Chu_Kang)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stadium_marker)));
        InsertCheckDistance(getResources().getString(R.string.Sports_Centre_Choa_Chu_Kang),SphericalUtil.computeDistanceBetween(Sports_Centre_Choa_Chu_Kang,latLng));
        //LatLng gym= new LatLng();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gym_marker_2)));

        mParkList_sorted.putAll(mParkList);
        showParkList(mParkList_sorted);
        mAdapter = new ParkAdapter(ParkDetails,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

   /* private void nearByPlace(final String placeType) {

        mMap.clear();
        String url =getUrl(latitude,longitude,placeType);
        mService.getNearByPlaces(url)
                .enqueue(new Callback<com.example.fyp.Model.MyPlaces>() {
                    @Override
                    public void onResponse(Call<com.example.fyp.Model.MyPlaces> call, Response<com.example.fyp.Model.MyPlaces> response) {
                        if(response.isSuccessful())
                        {
                            for(int i=0;i<response.body().getResults().length;i++){
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName= googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();

                                ArrayList<ParkName> parkName = new ArrayList<>();
                                parkName.add(new ParkName(R.drawable.ic_battery_charging_full_black_24dp,placeName,"Test2"));



                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                mAdapter=new ParkAdapter(parkName);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mRecyclerView.setAdapter(mAdapter);




                                LatLng latLng = new LatLng(lat,lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if(placeType.equals("park"))
                                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    else if(placeType.equals("gym"))
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                else if(placeType.equals("stadium"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                LatLng marker_2 = new LatLng(1.4297, 103.7961);
                                mMap.addMarker(new MarkerOptions().position(marker_2).title("Vista Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));

                                //Add to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<com.example.fyp.Model.MyPlaces> call, Throwable t) {

                    }
                });
    } */

   /* private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+1000000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl",googlePlacesUrl.toString());
        return googlePlacesUrl.toString();


    } */

    public void InsertCheckDistance(String name, Double distance){
        distance = distance/1000;
        if (distance < 2){
            mParkList.put(name,distance);
        }
    }


    @Override
    public void onParkClick(int position) {
        String name = ParkDetails.get(position).getmName();
        name = name.replace(" ", "_");
        Log.d(TAG, "CLICKED"+name);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        //cameraPosition = new CameraPosition.Builder().target(name).zoom(12).build();

    }



    class ValueComparator implements Comparator<String> {
        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) <= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    private void showParkList( Map<String,Double> mParkList) {



        for (Map.Entry<String, Double> entry : mParkList.entrySet()) {

            String mName = entry.getKey();
            Double mduration = entry.getValue();
            ParkDetails.add(new ParkName(mName,mduration));

        }



    }

    private boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity(),new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity(),new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE);
            return false;
        }
        else
            return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_CODE:
             {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient==null)
                            buildGoogleApiClien();
                        mMap.setMyLocationEnabled(true);
                    }

                }
                //else
                    //Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gMap = googleMap;
        //googleMap = map;

        //Init Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClien();
                mMap.setMyLocationEnabled(true);
            }

        }
            else
            {
                buildGoogleApiClien();
                mMap.setMyLocationEnabled(true);
            }
        }

    private synchronized void buildGoogleApiClien() {
        mGoogleApiClient= new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mMarker !=null)
            mMarker.remove();


        latitude=location.getLatitude();
        longitude=location.getLongitude();

        latLng = new LatLng(latitude,longitude);

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        //cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker =mMap.addMarker(markerOptions);

        //Move Camera
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        if(mGoogleApiClient!=null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


        park.setEnabled(true);
        gym.setEnabled(true);
        stadium.setEnabled(true);
    }


}

