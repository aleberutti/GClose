package com.becafe.gclose.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.Model.ViewPageAdapter;
import com.becafe.gclose.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class ProfileFragment extends Fragment {

    private ImageView perfil, portada;
    private TextView tvProfile;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private String user_id;
    private Usuario user;

    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    StorageReference storageRef;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvProfile = (TextView) v.findViewById(R.id.tvProfile);
        perfil = (ImageView) v.findViewById(R.id.profilePhoto);
        portada = (ImageView) v.findViewById(R.id.portadaPhoto);
        tabLayout= (TabLayout) v.findViewById(R.id.tabLayout);
        viewPager =(ViewPager) v.findViewById(R.id.vpProfile);
        adapter = new ViewPageAdapter(getActivity().getSupportFragmentManager());


        Fragment galleryFragment = new GalleryFragment();
        Fragment descripFragmnet = new DescriptionFragment();

//        Bundle argumentos = getArguments();
//        if(argumentos != null)  user_id = argumentos.getString("USER_ID");
        myRef = FirebaseDatabase.getInstance().getReference("usuarios");
        mAuth = FirebaseAuth.getInstance();
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(Usuario.class);
                String nombre = user.getNombre()+ " " + user.getApellido();
                tvProfile.setText(nombre);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("asd", "onCancelled", databaseError.toException());
            }
        });



        adapter.addFragment(descripFragmnet, "");
        adapter.addFragment(galleryFragment, "");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_import_contacts_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_photo_library_white_24dp);



        return v;
    }
}
