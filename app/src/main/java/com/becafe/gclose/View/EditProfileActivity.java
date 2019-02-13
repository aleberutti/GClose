package com.becafe.gclose.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.becafe.gclose.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText work, studies, location, desc;
    private MaterialButton btnPerfil, btnPortada, btnGaleria, btnGuardarCambios;
    private static final int GALLERY_INTENT=1;
    private static final int GALLERY_INTENT_PERFIL=2;
    private static final int CROP_IMAGE=3;
    private static final int CAMERA=4;

    private FirebaseAuth mAuth;

    private DatabaseReference myRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

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

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i1.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i1, CAMERA);
                }
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
            Uri uri = data.getData();
            storageRef.child(mAuth.getCurrentUser().getUid()).child("images").child("foto_portada").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfileActivity.this, "Se ha actualizado su foto de portada", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            storageRef.child(mAuth.getCurrentUser().getUid()).child("images").child("foto_perfil").putFile(getImageUri(this, imageBitmap)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfileActivity.this, "Se ha actualizado su foto de perfil", Toast.LENGTH_SHORT).show();
                    Log.e("ZAFERROR", "SDFFSD");
                }
            });
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

}
