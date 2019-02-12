package com.becafe.gclose.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.becafe.gclose.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText work, studies, location, desc;
    private MaterialButton btnPerfil, btnPortada, btnGaleria, btnGuardarCambios;
    private static final int GALLERY_INTENT=1;
    private static final int GALLERY_INTENT_PERFIL=2;
    private static final int CROP_IMAGE=3;

    private FirebaseAuth mAuth;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        work = findViewById(R.id.EditWork);
        studies = findViewById(R.id.EditStudies);
        location = findViewById(R.id.EditLocation);
        desc = findViewById(R.id.EditDesc);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        btnPerfil = findViewById(R.id.btnCambiarPerfil);
        btnPortada = findViewById(R.id.btnCambiarPortada);
        btnGaleria = findViewById(R.id.btnGaleria);

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("localidad").setValue(location.getText().toString());
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("trabajo").setValue(work.getText().toString());
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("educacion").setValue(studies.getText().toString());
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("descripcion").setValue(desc.getText().toString());
            }
        });

        btnPortada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_INTENT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){

        }
    }
}
