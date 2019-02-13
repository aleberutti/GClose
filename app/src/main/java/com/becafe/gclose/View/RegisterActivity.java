package com.becafe.gclose.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.becafe.gclose.Controller.DatePickerFragment;
import com.becafe.gclose.Controller.Location.TokenService;
import com.becafe.gclose.Model.SpinnerAdapter;
import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private EditText EditUser, EditPass, EditNombre, EditApellido, FechaNac;
    private Button BtRegister;
    Spinner Spinner_sexo, Spinner_interes;

    private FirebaseAuth mAuth;

    private DatabaseReference myRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        EditUser = findViewById(R.id.EditUsername);
        EditPass = findViewById(R.id.EditPassword);
        BtRegister = findViewById(R.id.btRegistro);
        FechaNac = findViewById(R.id.FechaNac);
        EditNombre = findViewById(R.id.EditNombre);
        EditApellido = findViewById(R.id.EditApellido);

        final ArrayList<String> spinnerData = new ArrayList<>();
        spinnerData.add("Femenino");
        spinnerData.add("Masculino");
        spinnerData.add("Indefinido");
        //Spinner Sexo
        Spinner_sexo = findViewById(R.id.Spinner_sexo);
        SpinnerAdapter adapterSpinner_sexo = new SpinnerAdapter(this, R.layout.spinner_layout, spinnerData);
        //adapterSpinner_sexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_sexo.setAdapter(adapterSpinner_sexo);
        Spinner_sexo.setOnItemSelectedListener(this);

        //Spinner Interes
        Spinner_interes = findViewById(R.id.Spinner_interes);
        Spinner_interes.setAdapter(adapterSpinner_sexo);
        Spinner_interes.setOnItemSelectedListener(this);

        BtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        FechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = dayOfMonth + "/" + month+1 + "/" + year;

        FechaNac.setText(currentDateString);

    }

    public void registerUser(){

        String email = EditUser.getText().toString().trim();
        String password = EditPass.getText().toString().trim();

        if(email.isEmpty()){
            EditUser.setError("El correo es requerido");
            EditUser.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EditUser.setError("El correo no es correcto");
            EditUser.requestFocus();
            return;
        }

        if(password.isEmpty()){
            EditPass.setError("La contraseña es requerida");
            EditPass.requestFocus();
            return;
        }

        if(password.length() < 6){
            EditPass.setError("Se requiere 6 o más caracteres");
            EditPass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            String id = task.getResult().getUser().getUid();
                            myRef = FirebaseDatabase.getInstance().getReference();
                            storageRef = FirebaseStorage.getInstance().getReference();


                            EditUser = findViewById(R.id.EditUsername);
                            EditPass = findViewById(R.id.EditPassword);
                            BtRegister = findViewById(R.id.btRegistro);
                            FechaNac = findViewById(R.id.FechaNac);
                            EditNombre = findViewById(R.id.EditNombre);
                            EditApellido = findViewById(R.id.EditApellido);

                            Usuario usuario = new Usuario();
                            usuario.setNombre(EditNombre.getText().toString());
                            usuario.setApellido(EditApellido.getText().toString());
                            usuario.setFecha_nac(FechaNac.getText().toString());
                            usuario.setSexo(Spinner_sexo.getSelectedItem().toString());
                            usuario.setInteres(Spinner_interes.getSelectedItem().toString());
                            usuario.setDescripcion("");
                            usuario.setEducacion("");
                            usuario.setTrabajo("");
                            usuario.setLocalidad("");


                            Log.e("ZAFID", String.valueOf(id));
                            myRef.child("usuarios").child(id).setValue(usuario);

                            //Muestro un Toast de registro correcto
                            Toast.makeText(RegisterActivity.this, "Registro correcto.",
                                    Toast.LENGTH_LONG).show();

                            //Creamos apartado del usuario en el storage
                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);
                            Log.e("ZAFIMAGEURI", getImageUri(RegisterActivity.this, b).toString());
                            TokenService.Token = FirebaseInstanceId.getInstance().getToken();
                            myRef.child("usuarios").child(id).child("messaging-token").setValue(TokenService.Token);
//                            storageRef.child(id).child("images").child("foto_perfil").child("default_pic").putFile(getImageUri(RegisterActivity.this, b)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Intent i = new Intent(RegisterActivity.this, NavigationActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("USER_ID", mAuth.getCurrentUser().getUid());
                                    startActivity(i);
//                                }
//                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Fallo el registro.",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }
}
