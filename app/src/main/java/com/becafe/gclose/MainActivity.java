package com.becafe.gclose;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.becafe.gclose.R;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private Button btn_reg;
    private Button btn_init;
    private Bitmap reg1, init1, reg2, init2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_init= (Button) findViewById(R.id.btn_init);
        btn_reg= (Button) findViewById(R.id.btn_reg);
        reg1 = decodeSampledBitmapFromResource(getResources(), R.drawable.btn_reg1, 5, 25);
        reg2 = decodeSampledBitmapFromResource(getResources(), R.drawable.btn_reg2, 5, 25);
        init1 = decodeSampledBitmapFromResource(getResources(), R.drawable.btn_init1, 5, 25);
        init2 = decodeSampledBitmapFromResource(getResources(), R.drawable.btn_init2, 5, 25);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();


        btn_init.setBackground(new BitmapDrawable(getResources(), init1));
        btn_reg.setBackground(new BitmapDrawable(getResources(), reg1));

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_reg.setBackground(new BitmapDrawable(getResources(), reg2));

            }
        });

        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_init.setBackground(new BitmapDrawable(getResources(), init2));

            }
        });


    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 3;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
