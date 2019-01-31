package com.becafe.gclose.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.becafe.gclose.Controller.DatePickerFragment;
import com.becafe.gclose.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.becafe.gclose.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private EditText EditUser, EditPass, EditNombre, EditApellido, FechaNac;
    private Button BtRegister;
    Spinner Spinner_sexo, Spinner_interes;

    private FirebaseAuth mAuth;

    DatabaseReference bd;



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

        //Spinner Sexo
        Spinner_sexo = findViewById(R.id.Spinner_sexo);
        ArrayAdapter<CharSequence> adapterSpinner_sexo = ArrayAdapter.createFromResource(this, R.array.sexos, android.R.layout.simple_spinner_item);
        adapterSpinner_sexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_sexo.setAdapter(adapterSpinner_sexo);
        Spinner_sexo.setOnItemSelectedListener(this);

        //Spinner Interes
        Spinner_interes = findViewById(R.id.Spinner_interes);
        ArrayAdapter<CharSequence> adapterSpinner_interes = ArrayAdapter.createFromResource(this, R.array.sexos, android.R.layout.simple_spinner_item);
        adapterSpinner_interes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_interes.setAdapter(adapterSpinner_interes);
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
                           // FirebaseUser user = mAuth.getCurrentUser();



                            //Muestro un Toast de registro correcto
                            Toast.makeText(RegisterActivity.this, "Registro correcto.",
                                    Toast.LENGTH_LONG).show();

                            Intent i = new Intent(RegisterActivity.this, IndexActivity.class);
                            startActivity(i);
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
}
