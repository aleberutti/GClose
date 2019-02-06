package com.becafe.gclose.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.becafe.gclose.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;

import com.becafe.gclose.R;

public class NavigationActivity extends AppCompatActivity {


   /* private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    return true;
                case R.id.navigation_get_close:
                    return true;
                case R.id.navigation_crushes:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }*/

}
