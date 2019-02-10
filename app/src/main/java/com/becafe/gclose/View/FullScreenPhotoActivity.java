package com.becafe.gclose.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.becafe.gclose.Model.FullScreenAdapter;
import com.becafe.gclose.R;

import java.util.ArrayList;

public class FullScreenPhotoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<String> urlList;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);

        if(savedInstanceState==null){
            Intent i = getIntent();
            urlList =  (ArrayList<String>) i.getStringArrayListExtra("IMAGES");
            position = i.getIntExtra("POSITION", -1);

        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);

        FullScreenAdapter fullScreenAdapter = new FullScreenAdapter(this, urlList);
        viewPager.setAdapter(fullScreenAdapter);
        viewPager.setCurrentItem(position, true);


    }
}
