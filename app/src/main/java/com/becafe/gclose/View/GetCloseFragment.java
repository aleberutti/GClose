package com.becafe.gclose.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GetCloseFragment extends Fragment {

    private StorageReference storageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private List<String> placesUids;

    public GetCloseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_get_close, container, false);

        placesUids = new ArrayList<String>();

        //MANEJAR CUANDO LLEGUE UNA NOTIFICACION DE ALGUIEN QUE ACABA DE REGISTRARSE EN EL LUGAR QUE ESTAS BUSCANDO
//        getArguments().getString("uid");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()<1){
                    Toast.makeText(container.getContext(), "DEBE REGISTRARSE EN UN LUGAR PARA\nOBTENER ALTERNATIVAS", Toast.LENGTH_SHORT).show();
                }else{
                    for (int j = 0; j<dataSnapshot.getChildrenCount(); j++){
                        List <HashMap<String, String>> listaLugares = new ArrayList<HashMap<String, String>>();
                        listaLugares.addAll((List<HashMap<String,String>>)dataSnapshot.getValue());
                        final String placeId = listaLugares.get(j).get("id");
                        mDatabase.child("places").child(placeId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    placesUids.add(child.getValue().toString());
                                }

                                for (int i = 0; i<placesUids.size(); i++) {
                                    mDatabase.child("usuarios").child(placesUids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Usuario aMostrar = dataSnapshot.getValue(Usuario.class);
                                            // ------------------ MOSTRAR PERFIL DEL USUARIO EN LUGAR DEL Log
                                            Log.wtf("ZAFUSER", aMostrar.getApellido());
                                            /*
                                            if (le_da_like){
                                                mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("likes").push().setValue(placesUids.get(i));
                                                mDatabase.child("usuarios").child(placesUids.get(i)).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                                                            if (child.getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                                                // ITS A FUCKING MATCH
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }else{
                                                mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("unlikes").push().setValue(placesUids.get(i));
                                            }
                                            */
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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