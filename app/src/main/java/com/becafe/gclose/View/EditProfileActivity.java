package com.becafe.gclose.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.becafe.gclose.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText work, studies, location, desc;
    private TextView tvWork, tvStudies, tvLoc, tvDesc;
    private View divider;
    private MaterialButton btnPerfil, btnPortada, btnGaleria, btnGuardarCambios;
    private static final int GALLERY_INTENT=1;
    private static final int GALLERY_INTENT_PERFIL=2;
    private static final int CROP_IMAGE=3;
    private static final int CAMERA=4;

    private FirebaseAuth mAuth;

    private DatabaseReference myRef;
    private StorageReference storageRef;
    private ProgressBar mProgressBar;
    private boolean flagPerfil;
    public boolean flagOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        flagPerfil = false;

        mProgressBar = findViewById(R.id.progressbar);

        work = findViewById(R.id.EditWork);
        studies = findViewById(R.id.EditStudies);
        location = findViewById(R.id.EditLocation);
        desc = findViewById(R.id.EditDesc);

        tvWork = findViewById(R.id.tvWork);
        tvStudies = findViewById(R.id.tvStudies);
        tvLoc = findViewById(R.id.tvLocation);
        tvDesc = findViewById(R.id.tvDesc);

        divider = findViewById(R.id.divider2);

        flagOut = false;

        Bundle b = getIntent().getExtras();
        if (b!=null) {
            if (b.getString("educacion") != null) {
                studies.setText(b.getString("educacion"));
            }
            if (b.getString("trabajo") != null) {
                work.setText(b.getString("trabajo"));
            }
            if (b.getString("localidad") != null) {
                location.setText(b.getString("localidad"));
            }
            if (b.getString("descripcion") != null) {
                desc.setText(b.getString("descripcion"));
            }
        }

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
                Toast.makeText(EditProfileActivity.this, "Se han actualizado con éxito los datos", Toast.LENGTH_SHORT).show();
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.MyDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(EditProfileActivity.this);
                }
                builder.setTitle("ELIGE UNA APP PARA CARGAR LA FOTO")
                        .setPositiveButton("Galería", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image/*");
                                flagPerfil = true;
                                startActivityForResult(i, GALLERY_INTENT);
                            }
                        })
                        .setNeutralButton("Cámara", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (i1.resolveActivity(getPackageManager()) != null) {
                                    flagPerfil = true;
                                    startActivityForResult(i1, CAMERA);
                                }
                            }
                        });
                Dialog diag = builder.create();
                diag.show();
            }
        });

        btnPortada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.MyDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(EditProfileActivity.this);
                }
                builder.setTitle("ELIGE UNA APP PARA CARGAR LA FOTO")
                        .setPositiveButton("Galería", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image/*");
                                flagPerfil = false;
                                startActivityForResult(i, GALLERY_INTENT);
                            }
                        })
                        .setNeutralButton("Cámara", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (i1.resolveActivity(getPackageManager()) != null) {
                                    flagPerfil = false;
                                    startActivityForResult(i1, CAMERA);
                                }
                            }
                        });
                Dialog diag = builder.create();
                diag.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String hijo = "";
        if (flagPerfil) {
            hijo = "foto_perfil";
        }else{
            hijo = "foto_portada";
        }

        Log.e("ZAF HABERFLAG2",String.valueOf(flagOut));
        new MyTask().execute();
        Log.e("ZAF HABERFLAG3",String.valueOf(flagOut));

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            Uri uri = data.getData();
            storageRef.child(mAuth.getCurrentUser().getUid()).child("images").child(hijo).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    flagOut = true;
                    Toast.makeText(EditProfileActivity.this, "Se ha actualizado su foto", Toast.LENGTH_SHORT).show();
                    flagOut = true;
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    flagOut = true;
                    Toast.makeText(EditProfileActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            storageRef.child(mAuth.getCurrentUser().getUid()).child("images").child(hijo).putFile(getImageUri(this, imageBitmap)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    flagOut = true;
                    Toast.makeText(EditProfileActivity.this, "Se ha actualizado su foto", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    flagOut = true;
                    Toast.makeText(EditProfileActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

    public boolean isFlagOut() {
        return flagOut;
    }

    public void setFlagOut(boolean flagOut) {
        this.flagOut = flagOut;
    }

    class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            Log.e("ZAF HABERFLAG1",String.valueOf(flagOut));
            mProgressBar.setVisibility(View.VISIBLE);
            work.setVisibility(View.INVISIBLE);
            studies.setVisibility(View.INVISIBLE);
            location.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
            btnPerfil.setVisibility(View.INVISIBLE);
            btnPortada.setVisibility(View.INVISIBLE);
            btnGaleria.setVisibility(View.INVISIBLE);
            btnGuardarCambios.setVisibility(View.INVISIBLE);
            tvDesc.setVisibility(View.INVISIBLE);
            tvLoc.setVisibility(View.INVISIBLE);
            tvStudies.setVisibility(View.INVISIBLE);
            tvWork.setVisibility(View.INVISIBLE);
            divider.setVisibility(View.INVISIBLE);

            //Toast.makeText(getApplicationContext(), "Pongo visible", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("ZAF HABERFLAG4",String.valueOf(flagOut));
            do {
                for (int i = 0; i <= 15; i++) {
                    Log.e("ZAF HABERFLAG",String.valueOf(flagOut));
                    try {
                        Log.e("ZAF HABERFLAG0",String.valueOf(flagOut));
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    if (flagOut){
                        Log.e("ZAF HABERFLAG99",String.valueOf(flagOut));
                        return "Fin";
                    }
                }
            }while(!flagOut);
            Log.e("ZAF HABERFLAG5",String.valueOf(flagOut));
            return "Fin";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            flagOut = false;

            mProgressBar.setVisibility(View.INVISIBLE);
            work.setVisibility(View.VISIBLE);
            studies.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            btnPerfil.setVisibility(View.VISIBLE);
            btnPortada.setVisibility(View.VISIBLE);
            btnGaleria.setVisibility(View.VISIBLE);
            btnGuardarCambios.setVisibility(View.VISIBLE);
            tvDesc.setVisibility(View.VISIBLE);
            tvLoc.setVisibility(View.VISIBLE);
            tvStudies.setVisibility(View.VISIBLE);
            tvWork.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

        }
    }

}
