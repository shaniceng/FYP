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
                nearByPlace("park");

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
        LatLng Vista_Park = new LatLng(1.4297, 103.7961);
        LatLng Singapore_Botanic_Gardens = new LatLng(1.3138397,103.8159136);
        LatLng War_Memorial_Park = new LatLng(1.2928957, 103.8546778);
        LatLng Bishan_Ang_Mo_Kio_Park = new LatLng(1.3634088, 103.8435614);
        LatLng Fort_Canning_Park = new LatLng(1.2945126, 103.845801);
        LatLng Punggol_Park = new LatLng(1.3769802, 103.8986585);
        LatLng Punggol_Waterway_Park = new LatLng(1.4111243, 103.9046565);
        LatLng Bukit_Timah_Nature_Reserve = new LatLng(1.3540562, 103.7769454);
        LatLng Fountain_of_Wealth = new LatLng(1.2947425, 103.8589026);
        LatLng Springleaf_Nature_Park = new LatLng(1.4014737, 103.8174668);
        LatLng Dairy_Farm_Nature_Park = new LatLng(1.3618794, 103.7753145);
        LatLng Kebun_Baru_Bird_Corner = new LatLng(1.3743012, 103.8402669);
        LatLng Ang_Mo_Kio_Town_Garden_West = new LatLng(1.3720142, 103.8257891);
        LatLng Lower_Peirce_Reservoir_Park = new LatLng(1.3743688, 103.8434332);
        LatLng Bishan_Park_1_Near_Grub = new LatLng(1.3643363, 103.8351006);
        LatLng Bishan_Ang_Mo_Kio_Park_Inclusive_Playground = new LatLng(1.3632348, 103.8442614);
        LatLng TreeTop_Walk = new LatLng(1.3607287, 103.8125215);
        LatLng Neram_Park = new LatLng(1.3856951, 103.8658256);
        LatLng MacRitchie_Trail = new LatLng(1.3419705, 103.8348653);
        LatLng MacRitchie_Reservoir_Park = new LatLng(1.3413779, 103.8348204);


        mMap.addMarker(new MarkerOptions().position(Vista_Park).title("Vista Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Singapore_Botanic_Gardens).title("Singapore Botanic Gardens").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(War_Memorial_Park).title("War Memorial Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));

        mMap.addMarker(new MarkerOptions().position(Bishan_Ang_Mo_Kio_Park).title("Bishan Ang Mo Kio Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Fort_Canning_Park).title("Fort Canning Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Punggol_Park).title("Punggol Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Punggol_Waterway_Park).title("Punggol_Waterway_Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Bukit_Timah_Nature_Reserve).title("Bukit Timah Nature Reserve").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Fountain_of_Wealth).title("Fountain of Wealth").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Springleaf_Nature_Park).title("Springleaf Nature").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Dairy_Farm_Nature_Park).title("Dairy Farm Nature Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Kebun_Baru_Bird_Corner).title("Kebun Baru Bird Corner").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Ang_Mo_Kio_Town_Garden_West).title("Ang Mo Kio Town Garden West").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Lower_Peirce_Reservoir_Park).title("Lower Peirce Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Bishan_Park_1_Near_Grub).title("Bishan Park 1 Near Grub").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Bishan_Ang_Mo_Kio_Park_Inclusive_Playground).title("Bishan-Ang Mo Kio Park Inclusive Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        //mMap.addMarker(new MarkerOptions().position(TreeTop_Walk).title("Bishan-Ang Mo Kio Park Inclusive Playground").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(TreeTop_Walk).title("TreeTop Walk").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(Neram_Park).title("Neram Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(MacRitchie_Trail).title("MacRitchie Trail").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));
        mMap.addMarker(new MarkerOptions().position(MacRitchie_Reservoir_Park).title("MacRitchie Reservoir Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));


    }

    private void SingaporeGym(){

    }

    private void nearByPlace(final String placeType) {

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
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+1000000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl",googlePlacesUrl.toString());
        return googlePlacesUrl.toString();


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

