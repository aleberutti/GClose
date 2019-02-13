package com.becafe.gclose.View;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.becafe.gclose.Controller.RecyclerViewAdapter;
import com.becafe.gclose.MainActivity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GetCloseFragment extends Fragment {

    private StorageReference storageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private List<String> placesUids;

    private List<Usuario> userList;

    private RecyclerView recyclerViewCantante;
    private RecyclerViewAdapter adapterCantante;

    public GetCloseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.get_close_card, container, false);

        recyclerViewCantante = v.findViewById(R.id.recycler);
        recyclerViewCantante.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        adapterCantante = new RecyclerViewAdapter(obtenerUsuarios());
        recyclerViewCantante.setAdapter(adapterCantante);

        //Indicate a layoutManager
        //It shows items in a vertical or horizontal scrolling list.
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        //Create an adapter and set it
//        adapter = new RecyclerViewAdapter(getActivity().getBaseContext(), userList);
//        recyclerView.setAdapter(adapter);

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
//                                            Log.wtf("ZAFUSER", aMostrar.getApellido());
                                            /*
                                            if (le_da_like){
                                                mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("likes").push().setValue(placesUids.get(i));
                                                mDatabase.child("usuarios").child(placesUids.get(i)).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                                                            if (child.getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                                                // ITS A FUCKING MATCH
                                                                Intent destino = new Intent(getContext(), ChatFragment.class);
                                                                destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                createNotificationChannel();
                                                                PendingIntent pendingIntent =
                                                                        PendingIntent.getActivity(getContext(), 0, destino, 0);
                                                                NotificationCompat.Builder mBuilder = new
                                                                        NotificationCompat.Builder(getContext(), "2")
                                                                        .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                                                                        .setContentTitle("♥ ♥ ♥ Parece que tienes una cita ! ! !")
                                                                        .setContentText("Presiona aquí para comenzar una conversación")
                                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                        .setContentIntent(pendingIntent)
                                                                        .setAutoCancel(true);
                                                                NotificationManagerCompat notificationManager =
                                                                        NotificationManagerCompat.from(getContext());
                                                                notificationManager.notify(99, mBuilder.build());
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User request";
            String description = "New user in place";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    this.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public List<Usuario> obtenerUsuarios(){
        List<Usuario> cantante = new ArrayList<>();
        cantante.add(new Usuario("Juan", "Cabrera", "Masculino", "Femenino", "01/09/1995"));
        cantante.add(new Usuario("Juan1", "Cabrera2", "Masculino", "Femenino", "01/09/1995"));
        cantante.add(new Usuario("Juan2", "Cabrera3", "Masculino", "Femenino", "01/09/1995"));
        cantante.add(new Usuario("Juan3", "Cabrera4", "Masculino", "Femenino", "01/09/1995"));
        cantante.add(new Usuario("Juan4", "Cabrera5", "Masculino", "Femenino", "01/09/1995"));

        return cantante;
    }
}