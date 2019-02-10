package com.becafe.gclose.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.becafe.gclose.Controller.IRecyclerViewClickListener;
import com.becafe.gclose.Model.GalleryImageAdapter;
import com.becafe.gclose.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryFragment extends Fragment {

    private RecyclerView gallery;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> urlList = new ArrayList<>();

    public GalleryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v1 = inflater.inflate(R.layout.fragment_photos, container, false);
        View v2 = inflater.inflate(R.layout.fragment_no_photos, container, false);
        gallery = v1.findViewById(R.id.galleryImages);
        layoutManager = new GridLayoutManager(this.getContext(), 3);
        gallery.setHasFixedSize(true);
        gallery.setLayoutManager(layoutManager);


        final IRecyclerViewClickListener listener = new IRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(), FullScreenPhotoActivity.class);
                i.putStringArrayListExtra("IMAGES", urlList);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        };

        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this.getContext(), urlList, listener);
        gallery.setAdapter(galleryImageAdapter);







        if (!urlList.isEmpty()){
            return v1;
        }
        else{
            return v2;
        }
    }


}
