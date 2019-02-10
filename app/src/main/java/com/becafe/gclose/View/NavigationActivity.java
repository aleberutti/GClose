package com.becafe.gclose.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.becafe.gclose.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.becafe.gclose.R;

public class NavigationActivity extends AppCompatActivity {

    private String tag="";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment selectedFragment = null;


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    tag="profileFragment";
                    selectedFragment=getSupportFragmentManager().findFragmentByTag(tag);
                    if(selectedFragment==null)selectedFragment = new ProfileFragment();
                    return true;
                case R.id.navigation_get_close:
                    return true;
                case R.id.navigation_crushes:
                    return true;
            }

            getSupportFragmentManager()
                    .beginTransaction().
                    replace(R.id.fragmentContainer, selectedFragment, tag)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment fragmentInicio = new ProfileFragment();

        Intent i = getIntent();
        String id = i.getStringExtra("USER_ID");

        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", id);
        fragmentInicio.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.fragmentContainer, fragmentInicio)
                .commit();

    }

}
