package com.becafe.gclose.Model;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.becafe.gclose.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class FullScreenAdapter extends PagerAdapter {

    Context context;
    private ArrayList<String> urlList;
    LayoutInflater layoutInflater;

    public FullScreenAdapter(Context context, ArrayList<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.full_image_screen, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.img);

        Glide.with(context).load(urlList.get(position)).apply(new RequestOptions().centerInside()).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       // super.destroyItem(container, position, object);

        ViewPager vp= (ViewPager) container;
        View v = (View) object;
        vp.removeView(v);
    }
}
