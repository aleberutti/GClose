package com.becafe.gclose.View;

import android.os.Bundle;

import com.becafe.gclose.Controller.RecyclerViewAdapter;
import com.becafe.gclose.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCantante;
    private RecyclerViewAdapter adapterCantante;
    private FloatingActionButton match, not;

    private List<String> placesUids;
    private StorageReference storageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerViewCantante = findViewById(R.id.recyclerView);
        recyclerViewCantante.setLayoutManager(new LinearLayoutManager(this));

//        adapterCantante = new RecyclerViewAdapter(obtenerUsuarios());
        recyclerViewCantante.setAdapter(adapterCantante);

        match = (FloatingActionButton) findViewById(R.id.btnFloatingMatch);
        not = findViewById(R.id.btnFloatingNot);

//        match.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(RecyclerViewActivity.this, LoginActivity.class);
//                startActivity(i);
//            }
//        });
//
//        not.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(RecyclerViewActivity.this, ProfileFragment.class);
//                startActivity(i);
//            }
//        });

    }

//    public List<Usuario> obtenerUsuarios(){
//        final List<Usuario> usuarios = new ArrayList<>();
////        usuarios.add(new Usuario("Juan", "Cabrera", "Masculino", "Femenino", "01/09/1995"));
////        usuarios.add(new Usuario("Juan1", "Cabrera2", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan", "Cabrera", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan1", "Cabrera2", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan", "Cabrera", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan1", "Cabrera2", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan2", "Cabrera3", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan3", "Cabrera4", "Masculino", "Femenino", "01/09/1995"));
////        cantante.add(new Usuario("Juan4", "Cabrera5", "Masculino", "Femenino", "01/09/1995"));
//
//
//        placesUids = new ArrayList<String>();
//
//        //MANEJAR CUANDO LLEGUE UNA NOTIFICACION DE ALGUIEN QUE ACABA DE REGISTRARSE EN EL LUGAR QUE ESTAS BUSCANDO
////        getArguments().getString("uid");
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mAuth = FirebaseAuth.getInstance();
//
//        mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() < 1) {
//                    Toast.makeText(getBaseContext(), "DEBE REGISTRARSE EN UN LUGAR PARA\nOBTENER ALTERNATIVAS", Toast.LENGTH_SHORT).show();
//                } else {
//                    for (int j = 0; j < dataSnapshot.getChildrenCount(); j++) {
//                        List<HashMap<String, String>> listaLugares = new ArrayList<HashMap<String, String>>();
//                        listaLugares.addAll((List<HashMap<String, String>>) dataSnapshot.getValue());
//                        final String placeId = listaLugares.get(j).get("id");
//                        mDatabase.child("places").child(placeId).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                    placesUids.add(child.getValue().toString());
//                                }
//
//                                for (int i = 0; i < placesUids.size(); i++) {
//                                    mDatabase.child("usuarios").child(placesUids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                                usuarios.add(child.getValue(Usuario.class));
//                                            }
//                                            //Usuario aMostrar = dataSnapshot.getValue(Usuario.class);
//                                            // ------------------ MOSTRAR PERFIL DEL USUARIO EN LUGAR DEL Log
////                                            Log.wtf("ZAFUSER", aMostrar.getApellido());
//                                            /*
//                                            if (le_da_like){
//                                                mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("likes").push().setValue(placesUids.get(i));
//                                                mDatabase.child("usuarios").child(placesUids.get(i)).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
//                                                            if (child.getValue().toString().equals(mAuth.getCurrentUser().getUid())){
//                                                                // ITS A FUCKING MATCH
//                                                                Intent destino = new Intent(getContext(), ChatFragment.class);
//                                                                destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                                createNotificationChannel();
//                                                                PendingIntent pendingIntent =
//                                                                        PendingIntent.getActivity(getContext(), 0, destino, 0);
//                                                                NotificationCompat.Builder mBuilder = new
//                                                                        NotificationCompat.Builder(getContext(), "2")
//                                                                        .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
//                                                                        .setContentTitle("♥ ♥ ♥ Parece que tienes una cita ! ! !")
//                                                                        .setContentText("Presiona aquí para comenzar una conversación")
//                                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                                                        .setContentIntent(pendingIntent)
//                                                                        .setAutoCancel(true);
//                                                                NotificationManagerCompat notificationManager =
//                                                                        NotificationManagerCompat.from(getContext());
//                                                                notificationManager.notify(99, mBuilder.build());
//                                                            }
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }else{
//                                                mDatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("unlikes").push().setValue(placesUids.get(i));
//                                            }
//                                            */
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        return usuarios;
//    }

}
