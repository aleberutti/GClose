package com.becafe.gclose.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DescriptionFragment extends Fragment {

    private TextView educacion, trabajo, localidad, descripcion;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    public DescriptionFragment() {
    }

    @Override
    public void onResume(){
        myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);
                    if (user.getEducacion()!=null && !user.getEducacion().isEmpty()) {
                        educacion.setText(user.getEducacion());
                        educacion.setVisibility(View.VISIBLE);
                    }else {
                        educacion.setVisibility(View.GONE);
                    }
                    if (user.getTrabajo()!=null && !user.getTrabajo().isEmpty()) {
                        trabajo.setText(user.getTrabajo());
                        trabajo.setVisibility(View.VISIBLE);
                    }else {
                        trabajo.setVisibility(View.GONE);
                    }
                    if(user.getLocalidad()!=null && !user.getLocalidad().isEmpty()) {
                        localidad.setText(user.getLocalidad());
                        localidad.setVisibility(View.VISIBLE);
                    }else {
                        localidad.setVisibility(View.GONE);
                    }
                    if (user.getDescripcion()!=null && !user.getDescripcion().isEmpty()) {
                        descripcion.setText(user.getDescripcion());
                        descripcion.setVisibility(View.VISIBLE);
                    }else{
                        descripcion.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onResume();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_description, container, false);
        educacion = v.findViewById(R.id.tvStudies);
        trabajo = v.findViewById(R.id.tvWork);
        localidad = v.findViewById(R.id.tvCity);
        descripcion = v.findViewById(R.id.tvDescription);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);
                    if (user.getEducacion()!=null && !user.getEducacion().isEmpty()) {
                        educacion.setText(user.getEducacion());
                    }else {
                        educacion.setVisibility(View.GONE);
                    }
                    if (user.getTrabajo()!=null && !user.getTrabajo().isEmpty()) {
                        trabajo.setText(user.getTrabajo());
                    }else {
                        trabajo.setVisibility(View.GONE);
                    }
                    if(user.getLocalidad()!=null && !user.getLocalidad().isEmpty()) {
                        localidad.setText(user.getLocalidad());
                    }else {
                        localidad.setVisibility(View.GONE);
                    }
                    if (user.getDescripcion()!=null && !user.getDescripcion().isEmpty()) {
                        descripcion.setText(user.getDescripcion());
                    }else{
                        descripcion.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}
