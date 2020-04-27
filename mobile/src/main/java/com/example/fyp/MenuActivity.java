package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MenuActivity extends AppCompatActivity {
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    private static final String TAG=MenuActivity.class.getSimpleName();
   // private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

       // firebaseAuth=FirebaseAuth.getInstance();

        bottomNav=findViewById(R.id.bottom_nav);
        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.nav_home,true);
            fragmentManager =getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,homeFragment)
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.nav_home:
                        fragment=new HomeFragment();
                        break;
                    case R.id.nav_map:
                        startActivity(new Intent(MenuActivity.this, MapsActivity.class));
                        break;
                    case R.id.nav_settings:
                        fragment=new SettingsFragment();
                        break;

                }
                if(fragment!=null)
                {
                    fragmentManager=getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                }
                else {
                    Log.e(TAG, "Error in creating fragment");
                }
            }
        });
    }
}
