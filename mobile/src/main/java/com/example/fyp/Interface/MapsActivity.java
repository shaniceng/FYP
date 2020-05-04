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

import com.example.fyp.Common;
import com.example.fyp.Model.IGoogleAPIService;
import com.example.fyp.Model.Results;
import com.example.fyp.ParkAdapter;
import com.example.fyp.ParkName;
import com.example.fyp.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    private static final String TAG=MapsActivity.class.getSimpleName();

    private double latitude,longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MapView mapView;
    private GoogleMap googleMap;

    IGoogleAPIService mService;

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


        mRecyclerView=v.findViewById(R.id.recyclerView);



        //Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        Button park = v.findViewById(R.id.parkbutton);
        Button gym = v.findViewById(R.id.gymbutton);
        Button stadium =v.findViewById(R.id.stadiumbutton);
        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                SingaporeParks();
                //nearByPlace("park");

            }
        });

        gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                //code later
                //nearByPlace("gym");

            }
        });

        stadium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code later
                //nearByPlace("stadium");

            }
        });



        return v;

    }

    private void SingaporeParks(){

        //Woodlands
        LatLng Admiralty_Park = new LatLng(1.4484, 103.7790);
        mMap.addMarker(new MarkerOptions().position(Admiralty_Park).title("Admiralty Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Woodlands_Waterfront_Park = new LatLng (1.4530, 103.7803);
        mMap.addMarker(new MarkerOptions().position(Woodlands_Waterfront_Park).title("Woodlands Waterfront Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Marsiling_Park = new LatLng (1.4381, 103.7706);
        mMap.addMarker(new MarkerOptions().position(Marsiling_Park).title("Marsiling Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Woodlands_Town_Part_East = new LatLng (1.4366, 103.7792);
        mMap.addMarker(new MarkerOptions().position(Woodlands_Town_Part_East).title("Woodlands Town Part East").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Vista_Park = new LatLng(1.4297, 103.7961);
        mMap.addMarker(new MarkerOptions().position(Vista_Park).title("Vista Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Sembawang
        LatLng Sembawang_Park = new LatLng (1.4604, 103.8363);
        mMap.addMarker(new MarkerOptions().position(Sembawang_Park).title("Sembawang Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Janggus_Garden = new LatLng (1.4576, 103.8371);
        //mMap.addMarker(new MarkerOptions().position(Janggus_Garden).title("Janggus Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Kandis_Garden = new LatLng (1.4561, 103.8359);
        //mMap.addMarker(new MarkerOptions().position(Kandis_Garden).title("Kandis Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Pebble_Garden = new LatLng (1.4506, 103.8294);
        //mMap.addMarker(new MarkerOptions().position(Pebble_Garden).title("Pebble Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Mandai
        LatLng Jalan_Kemuning_Park = new LatLng (1.4395, 103.8269);
        mMap.addMarker(new MarkerOptions().position(Jalan_Kemuning_Park).title("Kemuning Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Platina_Road_Playground = new LatLng (1.4277, 103.8261);
        //mMap.addMarker(new MarkerOptions().position(Platina_Road_Playground).title("Platina Road Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Goodlink_Park_Playground = new LatLng (1.4273, 103.8255);
        //mMap.addMarker(new MarkerOptions().position(Goodlink_Park_Playground).title("Goodlink Park Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Yishun
        LatLng Yishun_Neighbourhood_Park = new LatLng (1.437835, 103.835343);
        mMap.addMarker(new MarkerOptions().position(Yishun_Neighbourhood_Park).title("Yishun Neighbourhood Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Yishun_Pond_Park = new LatLng (1.4279,103.8399);
        mMap.addMarker(new MarkerOptions().position(Yishun_Pond_Park).title("Yishun Pond Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Yishun_Park = new LatLng (1.4250, 103.8447);
        mMap.addMarker(new MarkerOptions().position(Yishun_Park).title("Yishun Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Lim Chu Kang
        LatLng Sungei_Buloh_Wetland_Reserve = new LatLng (1.4480, 103.7245);
        mMap.addMarker(new MarkerOptions().position(Sungei_Buloh_Wetland_Reserve).title("Sungei Buloh Wetland Reserve").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kranji_Reservoir_Park = new LatLng (1.439694, 103.737806);
        mMap.addMarker(new MarkerOptions().position(Kranji_Reservoir_Park).title("Kranji Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kranji_Nature_Trail = new LatLng (1.4446,103.7356);
        mMap.addMarker(new MarkerOptions().position(Kranji_Nature_Trail).title("Kranji Nature Trail").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Sungei Kadut
        LatLng Jalan_Rasok_Park = new LatLng (1.4165, 103.7589);
        mMap.addMarker(new MarkerOptions().position(Jalan_Rasok_Park).title("Jalan Rasok Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Punggol
        LatLng Coney_Island_Park = new LatLng (1.409472, 103.92167);
        mMap.addMarker(new MarkerOptions().position(Coney_Island_Park).title("Coney Island Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Punggol_Park = new LatLng(1.3769802, 103.8986585);
        mMap.addMarker(new MarkerOptions().position(Punggol_Park).title("Punggol Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Punggol_Waterway_Park = new LatLng(1.4111243, 103.9046565);
        mMap.addMarker(new MarkerOptions().position(Punggol_Waterway_Park).title("Punggol Waterway Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //SengKang
        //LatLng St Annes Wood Playground = new LatLng();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Sengkang_Riverside_Park = new LatLng (1.3993, 103.8849);
        mMap.addMarker(new MarkerOptions().position(Sengkang_Riverside_Park).title("Sengkang Riverside Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Dew_Garden = new LatLng (1.3993, 103.8711);
        //mMap.addMarker(new MarkerOptions().position(Dew_Garden).title("Dew Garden").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Lorong_Tanggam_Park = new LatLng (1.3989,103.8736);
        mMap.addMarker(new MarkerOptions().position(Lorong_Tanggam_Park).title("Lorong Tanggam Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Changi
        //LatLng Changi West Boardwalk = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Turnhouse_Park = new LatLng (1.3902192, 103.9790907);
        mMap.addMarker(new MarkerOptions().position(Turnhouse_Park).title("Turnhouse Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Changi_Point_Waterfront_Park = new LatLng (1.3833, 104.0010);
        mMap.addMarker(new MarkerOptions().position(Changi_Point_Waterfront_Park).title("Changi Point Waterfront Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Changi_Beach_Park = new LatLng (1.3909, 103.9921);
        mMap.addMarker(new MarkerOptions().position(Changi_Beach_Park).title("Changi Beach Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
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
        mMap.addMarker(new MarkerOptions().position(Pasir_Ris_Park).title("Pasir Ris Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Sungei_Api_Api_Park = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position(Sungei_Api_Api_Park).title("Sungei Api Api Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Pasir_Ris_Town_Park = new LatLng (1.3725, 103.9517);
        mMap.addMarker(new MarkerOptions().position(Pasir_Ris_Town_Park).title("Pasir Ris Town Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Elias Terrace Playground = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Tampines
        LatLng Tampines_Eco_Green = new LatLng (1.3638, 103.9482);
        mMap.addMarker(new MarkerOptions().position(Tampines_Eco_Green).title("Tampines Eco Green").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Sun_Plaza_Park = new LatLng (1.3591, 103.9446);
        mMap.addMarker(new MarkerOptions().position(Sun_Plaza_Park).title("Sun Plaza Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

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
        mMap.addMarker(new MarkerOptions().position(Elite_Terrace_Park).title("Elite Terrace Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Telok_Kurau_Park = new LatLng (1.3156, 103.9143);
        mMap.addMarker(new MarkerOptions().position(Telok_Kurau_Park).title("Telok Kurau Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //ALOT OF PLAYGROUNDS?

        //East Coast
        LatLng East_Coast_Terrace_Park = new LatLng (1.3111, 103.9236);
        mMap.addMarker(new MarkerOptions().position(East_Coast_Terrace_Park).title("East Coast Terrace Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng East_Coast_Park = new LatLng (1.3008, 103.9122);
        mMap.addMarker(new MarkerOptions().position(East_Coast_Park).title("East Coast Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Marine Parade
        LatLng Marine_Parade_Road_Park = new LatLng (1.3003243, 103.9089574);
        mMap.addMarker(new MarkerOptions().position(Marine_Parade_Road_Park).title("Marine Parade Road Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Joo_Chiat_Terrace_Interim_Park = new LatLng (1.3145,103.9017);
        mMap.addMarker(new MarkerOptions().position(Joo_Chiat_Terrace_Interim_Park).title("Joo Chiat Terrace Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Ceylon_Road_Interim_Park = new LatLng (1.309867, 103.899647);
        mMap.addMarker(new MarkerOptions().position(Ceylon_Road_Interim_Park).title("Ceylon Road Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Katong
        LatLng Katong_Park = new LatLng (1.2966,103.8867);
        mMap.addMarker(new MarkerOptions().position(Katong_Park).title("Katong Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Wikinson_Interim_Park = new LatLng (1.3017,103.887);
        mMap.addMarker(new MarkerOptions().position(Wikinson_Interim_Park).title("Wikinson Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Arthur_Park = new LatLng (1.3041, 103.8882);
        mMap.addMarker(new MarkerOptions().position(Arthur_Park).title("Arthur Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Farrer Park
        LatLng Cambridge_Park = new LatLng (1.3136681, 103.847015);
        mMap.addMarker(new MarkerOptions().position(Cambridge_Park).title("Cambridge Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Lavaender
        LatLng Kallang_Riverside_Park = new LatLng (1.306978,103.86801);
        mMap.addMarker(new MarkerOptions().position(Kallang_Riverside_Park).title("Kallang Riverside Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kampong_Glam_Park = new LatLng (1.30166546, 103.855996576);
        mMap.addMarker(new MarkerOptions().position(Kampong_Glam_Park).title("Kampong Glam Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Dhoby Ghaut
        LatLng Istana_Park = new LatLng (1.2992, 103.8438);
        mMap.addMarker(new MarkerOptions().position(Istana_Park).title("Istana Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Fort Canning
        LatLng Fort_Canning_Park = new LatLng(1.2945126, 103.845801);
        mMap.addMarker(new MarkerOptions().position(Fort_Canning_Park).title("Fort Canning Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Esplanade
        LatLng War_Memorial_Park = new LatLng(1.2928957, 103.8546778);
        mMap.addMarker(new MarkerOptions().position(War_Memorial_Park).title("War Memorial Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Esplanade_Park = new LatLng (1.2899, 103.8539);
        mMap.addMarker(new MarkerOptions().position(Esplanade_Park).title("Esplanade Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Youth_Olympic_Park = new LatLng (1.2890, 103.8604);
        mMap.addMarker(new MarkerOptions().position(Youth_Olympic_Park).title("Youth Olympic Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Marina_Promenade = new LatLng (1.2899, 103.8648);
        mMap.addMarker(new MarkerOptions().position(Marina_Promenade).title("Marina Promenade").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Raffles place
        LatLng Raffles_Place_Park = new LatLng (1.2838806, 103.8515333);
        mMap.addMarker(new MarkerOptions().position(Raffles_Place_Park).title("Raffles Place Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Telok Ayer
        LatLng Telok_Ayer_Green = new LatLng (1.2814,103.8477);
        mMap.addMarker(new MarkerOptions().position(Telok_Ayer_Green).title("Telok Ayer Green").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Ann_Siang_Hill_Park = new LatLng (1.2809,103.8464);
        mMap.addMarker(new MarkerOptions().position(Ann_Siang_Hill_Park).title("Ann Siang Hill Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Telok_Ayer_Park = new LatLng (1.2778, 103.8468);
        mMap.addMarker(new MarkerOptions().position(Telok_Ayer_Park).title("Telok Ayer Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Outram Park
        LatLng Duxton_Plain_Park = new LatLng (1.2804,103.8413);
        mMap.addMarker(new MarkerOptions().position(Duxton_Plain_Park).title("Duxton Plain Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Pearls_Hill_City_Park = new LatLng (1.284389, 103.839194);
        mMap.addMarker(new MarkerOptions().position(Pearls_Hill_City_Park).title("Pearl's Hill City Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Pasir Panjang
        LatLng Kent_Ridge_Park = new LatLng (1.2839, 103.7913);
        mMap.addMarker(new MarkerOptions().position(Kent_Ridge_Park).title("Kent Ridge Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Jambol_Place_Park = new LatLng (1.31413, 103.81733);
        mMap.addMarker(new MarkerOptions().position(Jambol_Place_Park).title("Jambol Place Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Pasir_Panjang_Park = new LatLng (1.2820, 103.7812);
        mMap.addMarker(new MarkerOptions().position(Pasir_Panjang_Park).title("Pasir Panjang Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng HortPark = new LatLng (1.2787, 103.8018);
        mMap.addMarker(new MarkerOptions().position(HortPark).title("HortPark").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        LatLng Southern_Ridges = new LatLng (1.280102,103.802995);
        mMap.addMarker(new MarkerOptions().position(Southern_Ridges).title("Southern Ridges").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Telok_Blangah_Hill_Park = new LatLng (1.2788, 103.8107);
        mMap.addMarker(new MarkerOptions().position(Telok_Blangah_Hill_Park).title("Telok Blangah Hill Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Mount_Faber_Park = new LatLng (1.2738, 103.8175);
        mMap.addMarker(new MarkerOptions().position(Mount_Faber_Park).title("Mount Faber Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        LatLng West_Coast_Park = new LatLng (1.2914, 103.7667);
        mMap.addMarker(new MarkerOptions().position(West_Coast_Park).title("West Coast Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Clementi_Woods_Park = new LatLng (1.3017, 103.7667);
        mMap.addMarker(new MarkerOptions().position(Clementi_Woods_Park).title("Clementi Woods Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        LatLng Pandan_Reservoir_Fitness_Corner = new LatLng (1.3176122, 103.7486499);
        mMap.addMarker(new MarkerOptions().position(Pandan_Reservoir_Fitness_Corner).title("Pandan Reservoir Fitness Corner").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Pioneer
        LatLng Yunnan_Park = new LatLng (1.3374, 103.6930);
        mMap.addMarker(new MarkerOptions().position(Yunnan_Park).title("Yunnan Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Jurong_Central_Park = new LatLng (1.3381, 103.7078);
        mMap.addMarker(new MarkerOptions().position(Jurong_Central_Park).title("Jurong Central Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Lakeside_Grove_Fitness_Corner = new LatLng (1.3398,103.7171);
        mMap.addMarker(new MarkerOptions().position(Lakeside_Grove_Fitness_Corner).title("Lakeside Grove Fitness Corner").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Choa Chu Kang
        LatLng Choa_Chu_Kang_Park = new LatLng (1.3869, 103.7473);
        mMap.addMarker(new MarkerOptions().position(Choa_Chu_Kang_Park).title("Choa Chu Kang Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Villa_Verde_Park = new LatLng (1.3895,103.7529);
        mMap.addMarker(new MarkerOptions().position(Villa_Verde_Park).title("Villa Verde Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Hillview
        LatLng Chestnut_Nature_Park = new LatLng (1.31636, 103.86321);
        mMap.addMarker(new MarkerOptions().position(Chestnut_Nature_Park).title("Chestnut Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Dairy_Farm_Nature_Park = new LatLng(1.3618794, 103.7753145);
        mMap.addMarker(new MarkerOptions().position(Dairy_Farm_Nature_Park).title("Dairy Farm Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Fuyong_Interim_Park = new LatLng (1.3574719, 103.768588);
        mMap.addMarker(new MarkerOptions().position(Fuyong_Interim_Park).title("Fuyong Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Bukit_Timah_Nature_Reserve = new LatLng(1.3540562, 103.7769454);
        mMap.addMarker(new MarkerOptions().position(Bukit_Timah_Nature_Reserve).title("Bukit Timah Nature Reserve").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Hindhede_Nature_Park = new LatLng (1.34846664486, 103.775428404);
        mMap.addMarker(new MarkerOptions().position(Hindhede_Nature_Park).title("Hindhede Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Beauty World
        LatLng Bukit_Batok_Nature_Park = new LatLng (1.3485, 103.7642);
        mMap.addMarker(new MarkerOptions().position(Bukit_Batok_Nature_Park).title("Bukit Batok Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Hoover_Park = new LatLng (1.33271, 103.69756);
        mMap.addMarker(new MarkerOptions().position(Hoover_Park).title("Hoover Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Eng_Kong_Place_Fitness_Corner = new LatLng (1.3348,103.7673);
        mMap.addMarker(new MarkerOptions().position(Eng_Kong_Place_Fitness_Corner).title("Eng Kong Place Fitness Corner").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Sunset_Way_Park = new LatLng (1.327456, 103.7647549);
        mMap.addMarker(new MarkerOptions().position(Sunset_Way_Park).title("Sunset Way Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Dover
        LatLng Holland_Grove_View_Fitness_Corner = new LatLng (1.3125,103.7927);
        mMap.addMarker(new MarkerOptions().position(Holland_Grove_View_Fitness_Corner).title("Holland Grove View Fitness Corner").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Bukit Gombak
        LatLng Bukit_Batok_Town_Park = new LatLng (1.3559, 103.7549);
        mMap.addMarker(new MarkerOptions().position(Bukit_Batok_Town_Park).title("Bukit Batok Town Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Jalan_Remaja_Interim_Park = new LatLng (1.3576,103.7609);
        mMap.addMarker(new MarkerOptions().position(Jalan_Remaja_Interim_Park).title("Jalan Remaja Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //one-north
        LatLng One_north_Park = new LatLng (1.3028391, 103.7905769);
        mMap.addMarker(new MarkerOptions().position(One_north_Park).title("One-north Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));


        //Holland Village
        LatLng Holland_Village_Park = new LatLng (1.3118, 103.7955);
        mMap.addMarker(new MarkerOptions().position(Holland_Village_Park).title("Holland Village Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Tiong Bahru
        LatLng Tiong_Bahru_Park = new LatLng (1.2874, 103.8247);
        mMap.addMarker(new MarkerOptions().position(Tiong_Bahru_Park).title("Tiong Bahru Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kim_Pong_Park = new LatLng (1.2835,103.8297);
        mMap.addMarker(new MarkerOptions().position(Kim_Pong_Park).title("Kim Pong Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kim_Seng_Park = new LatLng (1.2929,103.8348);
        mMap.addMarker(new MarkerOptions().position(Kim_Seng_Park).title("Kim Seng Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Botanic Gardents
        LatLng Singapore_Botanic_Gardens = new LatLng(1.3138397,103.8159136);
        mMap.addMarker(new MarkerOptions().position(Singapore_Botanic_Gardens).title("Singapore Botanic Gardens").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng University_Road_Park = new LatLng (1.3255,103.8175);
        mMap.addMarker(new MarkerOptions().position(University_Road_Park).title("University Road Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Kheam_Hock_Park = new LatLng (1.3303,103.8195);
        mMap.addMarker(new MarkerOptions().position(Kheam_Hock_Park).title("Kheam Hock Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Novena
        LatLng Novena_Park = new LatLng (1.3222, 103.8428);
        mMap.addMarker(new MarkerOptions().position(Novena_Park).title("Novena Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Novena_Rise_Park = new LatLng (1.3252,103.8429);
        mMap.addMarker(new MarkerOptions().position(Novena_Rise_Park).title("Novena Rise Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Stevens
        LatLng Malcolm_Park = new LatLng (1.324725, 103.8301232);
        mMap.addMarker(new MarkerOptions().position(Malcolm_Park).title("Malcolm Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Bukit Timah
        LatLng Holland_Green_Linear_Park = new LatLng (1.3266, 103.7855);
        mMap.addMarker(new MarkerOptions().position(Holland_Green_Linear_Park).title("Holland Green Linear Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Namly_Park = new LatLng (1.3234, 103.7965);
        mMap.addMarker(new MarkerOptions().position(Namly_Park).title("Namly Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Duchess_Park = new LatLng (1.3241,103.8054);
        mMap.addMarker(new MarkerOptions().position(Duchess_Park).title("Duchess Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Bougainvillea_Park = new LatLng (1.3270, 103.8071);
        mMap.addMarker(new MarkerOptions().position(Bougainvillea_Park).title("Bougainvillea Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Sian_Tuan_Interim_Park = new LatLng (1.3386,103.786);
        mMap.addMarker(new MarkerOptions().position(Sian_Tuan_Interim_Park).title("Sian Tuan Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Mayfair_Park = new LatLng (1.3413,103.7808);
        mMap.addMarker(new MarkerOptions().position(Mayfair_Park).title("Mayfair Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Paya Lebar
        LatLng Bedok_Town_Park = new LatLng (1.3354, 103.9256);
        mMap.addMarker(new MarkerOptions().position(Bedok_Town_Park).title("Bedok Town Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Bedok_Reservoir_Park = new LatLng (1.3413, 103.9245);
        mMap.addMarker(new MarkerOptions().position(Bedok_Reservoir_Park).title("Bedok Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Hougang
        LatLng Luxus_Hill_Park = new LatLng (1.3797,103.8755);
        mMap.addMarker(new MarkerOptions().position(Luxus_Hill_Park).title("Luxus Hill Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Realty_Park = new LatLng (1.368,103.8903);
        mMap.addMarker(new MarkerOptions().position(Realty_Park).title("Realty Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));


        //Serangoon
        LatLng Tavistock_Avenue_Park = new LatLng (1.3705024, 103.8651618);
        mMap.addMarker(new MarkerOptions().position(Tavistock_Avenue_Park).title("Tavistock Avenue Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Richards_Avenue_Park = new LatLng (1.3870084, 103.8371533);
        mMap.addMarker(new MarkerOptions().position(Richards_Avenue_Park).title("Richards Avenue Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Surin_Avenue_Neighbourhood_Park = new LatLng (1.3556,103.8841);
        mMap.addMarker(new MarkerOptions().position(Surin_Avenue_Neighbourhood_Park).title("Surin Avenue Neighbourhood Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Parry_Avenue_Interim_Park = new LatLng (1.3637493, 103.8809803);
        mMap.addMarker(new MarkerOptions().position(Parry_Avenue_Interim_Park).title("Parry Avenue Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Chartwell_Drive_Park = new LatLng (1.28222411319, 103.864259691);
        mMap.addMarker(new MarkerOptions().position(Chartwell_Drive_Park).title("Chartwell Drive Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Haus_Linear_Park = new LatLng (1.3652,103.8603);
        mMap.addMarker(new MarkerOptions().position(Haus_Linear_Park).title("Haus Linear Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Jalan_Geneng_Interim_Park = new LatLng (1.3532981, 103.8808952);
        mMap.addMarker(new MarkerOptions().position(Jalan_Geneng_Interim_Park).title("Jalan Geneng Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Bunga_Rampai_Park = new LatLng (1.340499, 103.8838455);
        mMap.addMarker(new MarkerOptions().position(Bunga_Rampai_Park).title("Bunga Rampai Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Lorong Chuan
        LatLng Chuan_Lane_Park = new LatLng (1.3518,103.8582);
        mMap.addMarker(new MarkerOptions().position(Chuan_Lane_Park).title("Chuan Lane Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Chiltern_Drive_Interim_Park = new LatLng (1.3486,103.8621);
        mMap.addMarker(new MarkerOptions().position(Chiltern_Drive_Interim_Park).title("Chiltern Drive Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));


        LatLng Siang_Kuang_Avenue_Interim_Park = new LatLng (1.3299,103.8731);
        mMap.addMarker(new MarkerOptions().position(Siang_Kuang_Avenue_Interim_Park).title("Siang Kuang Avenue Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Oasis_Mulberry_Park = new LatLng (1.3320596, 103.8785996);
        mMap.addMarker(new MarkerOptions().position(Oasis_Mulberry_Park).title("Oasis@Mulberry Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng MacPherson_Linear_Park = new LatLng (1.3333,103.8818);
        mMap.addMarker(new MarkerOptions().position(MacPherson_Linear_Park).title("MacPherson Linear Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Aljunied_Park = new LatLng (1.3297, 103.8799);
        mMap.addMarker(new MarkerOptions().position(Aljunied_Park).title("Aljunied Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));

        //Ang Mo Kio
        //LatLng Ang_Mo_Kio_Town_Garden_West = new LatLng(1.3720142, 103.8257891);
        //mMap.addMarker(new MarkerOptions().position(Ang_Mo_Kio_Town_Garden_West).title("Ang Mo Kio Town Garden West").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Bishan_Ang_Mo_Kio_Park = new LatLng(1.3634088, 103.8435614);
        mMap.addMarker(new MarkerOptions().position(Bishan_Ang_Mo_Kio_Park).title("Bishan-Ang Mo Kio Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Gelenggang_Park = new LatLng (1.3734,103.8296);
        mMap.addMarker(new MarkerOptions().position(Gelenggang_Park).title("Gelenggang Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Leban_Park = new LatLng (1.3728,103.829);
        mMap.addMarker(new MarkerOptions().position(Leban_Park).title("Leban Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Lower_Peirce_Reservoir_Park = new LatLng(1.3743688, 103.8434332);
        mMap.addMarker(new MarkerOptions().position(Lower_Peirce_Reservoir_Park).title("Lower Peirce Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Thomson_Nature_Park = new LatLng (1.3858,103.8215);
        mMap.addMarker(new MarkerOptions().position(Thomson_Nature_Park).title("Thomson Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Central_Catchment_Nature_Reserve = new LatLng (1.3533,103.8185);
        mMap.addMarker(new MarkerOptions().position(Central_Catchment_Nature_Reserve).title("Central Catchment Nature Reserve").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Upper_Peirce_Reservoir_Park = new LatLng (1.3747, 103.8112);
        mMap.addMarker(new MarkerOptions().position(Upper_Peirce_Reservoir_Park).title("Upper Peirce Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));


        //Upper Thomson
        LatLng Windsor_Nature_Park = new LatLng (1.3599, 103.8273);
        mMap.addMarker(new MarkerOptions().position(Windsor_Nature_Park).title("Windsor Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Venus_Green = new LatLng (1.3586,103.8274);
        mMap.addMarker(new MarkerOptions().position(Venus_Green).title("Venus Green").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        //LatLng Onan Road Green Space = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Thomson_Park = new LatLng (1.3854, 103.8210);
        mMap.addMarker(new MarkerOptions().position(Thomson_Park).title("Thomson Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Taman_Permata_Park = new LatLng (1.3499,103.8348);
        mMap.addMarker(new MarkerOptions().position(Taman_Permata_Park).title("Taman Permata Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Thomson_Park_Estate_Interim_Park = new LatLng (1.3566,103.8341);
        mMap.addMarker(new MarkerOptions().position(Thomson_Park_Estate_Interim_Park).title("Thomson Park Estate Interim Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng MacRitchie_Reservoir_Park = new LatLng(1.3413779, 103.8348204);
        mMap.addMarker(new MarkerOptions().position(MacRitchie_Reservoir_Park).title("MacRitchie Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));
        LatLng Upper_Seletar_Reservoir_Park = new LatLng (1.397,103.8033);
        mMap.addMarker(new MarkerOptions().position(Upper_Seletar_Reservoir_Park).title("Upper Seletar Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));


        //LatLng ark = new LatLng ();
        //mMap.addMarker(new MarkerOptions().position().title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park_marker)));



        //LatLng ark = new LatLng ();


        LatLng Fountain_of_Wealth = new LatLng(1.2947425, 103.8589026);
        LatLng Springleaf_Nature_Park = new LatLng(1.4014737, 103.8174668);
        LatLng Kebun_Baru_Bird_Corner = new LatLng(1.3743012, 103.8402669);


        //LatLng Bishan_Park_1_Near_Grub = new LatLng(1.3643363, 103.8351006);
        //LatLng Bishan_Ang_Mo_Kio_Park_Inclusive_Playground = new LatLng(1.3632348, 103.8442614);
        LatLng TreeTop_Walk = new LatLng(1.3607287, 103.8125215);
        LatLng Neram_Park = new LatLng(1.3856951, 103.8658256);
        LatLng MacRitchie_Trail = new LatLng(1.3419705, 103.8348653);



    }

    private void SingaporeGym(){

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

        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker =mMap.addMarker(markerOptions);

        //Move Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        if(mGoogleApiClient!=null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

    }
}

