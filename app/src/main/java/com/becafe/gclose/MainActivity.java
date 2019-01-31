package com.becafe.gclose;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.becafe.gclose.R;
import com.becafe.gclose.View.LoginActivity;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private Button btn_reg;
    private Button btn_init;
    private Bitmap reg1, init1, reg2, init2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Llamo a la actividad login
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);


    }

}
