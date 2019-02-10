package com.becafe.gclose.View;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private TextView TextNombre, TextApellido, TextFechaNac, TextSexo, TextInteres, TextUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        TextNombre = findViewById(R.id.TextNombre);
        TextApellido = findViewById(R.id.TextApellido);
        TextFechaNac = findViewById(R.id.TextFechaNac);
        TextSexo = findViewById(R.id.TextSexo);
        TextInteres = findViewById(R.id.TextInteres);
        TextUsuario = findViewById(R.id.TextUsuario);



        myRef = FirebaseDatabase.getInstance().getReference("usuarios").child(myRef.getKey());


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario user = dataSnapshot.getValue(Usuario.class);

                TextNombre.setText(user.getNombre());
                TextApellido.setText(user.getApellido());
                TextFechaNac.setText(user.getFecha_nac());
                TextSexo.setText(user.getSexo());
                TextInteres.setText(user.getInteres());
                TextUsuario.setText(user.getIdUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
