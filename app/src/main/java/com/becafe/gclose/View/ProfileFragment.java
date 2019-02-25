package com.becafe.gclose.View;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.Model.ViewPageAdapter;
import com.becafe.gclose.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
    private FloatingActionButton btnFloating;
    private GalleryFragment galleryFragment;
    private DescriptionFragment descripFragment;
    private static final int EDIT_PROFILE=1354;
    private FirebaseAuth mAuth;
    private boolean flagProfilePic, flagCoverPic, profileOwner;
    private ProgressBar progressBarProfilePic, progressBarCoverPic;

    DatabaseReference myRef;
    StorageReference storageRef, storageRefAux;

    public ProfileFragment() {
    }


    @Override
    public void onResume() {
        try {
            if (EditProfileActivity.changeProfilePic == true) {
                cargarPerfil();
                EditProfileActivity.changeProfilePic = false;
            }
            if (EditProfileActivity.changePortaitPic == true) {
                cargarPortada();
                EditProfileActivity.changePortaitPic = false;
            }
        }catch (NullPointerException ex){

        }
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.wtf("ZAF ENTRA AGAIN", "ON CREATE");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvProfile = (TextView) v.findViewById(R.id.tvProfile);
        perfil = (ImageView) v.findViewById(R.id.profilePhoto);
        portada = (ImageView) v.findViewById(R.id.portadaPhoto);
        tabLayout= (TabLayout) v.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) v.findViewById(R.id.vpProfile);
        btnFloating = (FloatingActionButton) v.findViewById(R.id.btnFloating);
        progressBarProfilePic = v.findViewById(R.id.progressbarProfile);
        progressBarCoverPic = v.findViewById(R.id.progressbarCover);
        adapter = new ViewPageAdapter(getChildFragmentManager());

        galleryFragment = new GalleryFragment();
        descripFragment = new DescriptionFragment();

//        if (getArguments().getString("uid")!=null){
            //MOSTRAR PERFIL CON EL UID QUE LLEGA, PERMITIR PONER LIKE/UNLIKE Y TERMINAR EL ONCREATE CON return;
//            return v;
//        }
        if(getArguments() != null) {
            user_id = getArguments().getString("USER_ID");
//            Log.e("USERID", getArguments().getString("USER_ID"));
        }

        profileOwner = false;
        mAuth = FirebaseAuth.getInstance();

        if (user_id.equals(mAuth.getCurrentUser().getUid())){
            profileOwner = true;
        }else{
            profileOwner = false;
            btnFloating.setVisibility(View.GONE);
            ocultarNavigationBottomMenu();
        }

        this.cargarDatos();

//        storageRef = FirebaseStorage.getInstance().getReference().child("/"+user_id+"/images/foto_portada");
//        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                if (bytes!=null) {
//                    portada.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                //QUEDA LA POR DEFECTO
//            }
//        });

        //BIO


        adapter.addFragment(descripFragment, "");
        adapter.addFragment(galleryFragment, "");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_import_contacts_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_photo_library_white_24dp);

        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("usuarios").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        Intent i = new Intent (getActivity().getApplicationContext(), EditProfileActivity.class);
                        if (user.getEducacion()!=null && !user.getEducacion().isEmpty()) {
                            i.putExtra("educacion", user.getEducacion());
                        }
                        if (user.getTrabajo()!=null && !user.getTrabajo().isEmpty()) {
                            i.putExtra("trabajo", user.getTrabajo());
                        }
                        if(user.getLocalidad()!=null && !user.getLocalidad().isEmpty()) {
                            i.putExtra("localidad", user.getLocalidad());
                        }
                        if (user.getDescripcion()!=null && !user.getDescripcion().isEmpty()) {
                            i.putExtra("descripcion", user.getDescripcion());
                        }
                        getActivity().startActivityForResult(i, EDIT_PROFILE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        return v;
    }

    private void ocultarNavigationBottomMenu(){
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
    }

    private void cargarDatos(){
        myRef = FirebaseDatabase.getInstance().getReference("usuarios");
        myRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
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


        //FOTO DE PERFIL
        cargarPerfil();

        //FOTO DE PORTADA
        cargarPortada();
    }

    private void cargarPerfil(){
        new MyTaskProfile().execute();
        storageRef = FirebaseStorage.getInstance().getReference().child("/"+user_id+"/images/foto_perfil");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(ProfileFragment.this).load(imageURL).into(perfil);
                flagProfilePic = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void cargarPortada(){
        new MyTaskCover().execute();
        storageRef = FirebaseStorage.getInstance().getReference().child("/"+user_id+"/images/foto_portada");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(ProfileFragment.this).load(imageURL).into(portada);
                flagCoverPic = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    class MyTaskProfile extends AsyncTask<String, Integer, String> {

        public MyTaskProfile(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            flagProfilePic = false;

            perfil.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.default_profile_pic));

            progressBarProfilePic.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            while(!flagProfilePic) {
                for (int i = 0; i <= 15; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    if (flagProfilePic){
                        return "Fin";
                    }
                }
            }

            return "Fin";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBarProfilePic.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarProfilePic.setVisibility(View.GONE);
        }
    }

    class MyTaskCover extends AsyncTask<String, Integer, String> {

        public MyTaskCover(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            flagCoverPic = false;

            portada.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.portadabackground));

            progressBarCoverPic.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            while(!flagCoverPic) {
                for (int i = 0; i <= 15; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    if (flagCoverPic){
                        return "Fin";
                    }
                }
            }

            return "Fin";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBarCoverPic.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarCoverPic.setVisibility(View.GONE);
        }
    }

}
