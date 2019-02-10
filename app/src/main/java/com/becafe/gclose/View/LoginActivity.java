package com.becafe.gclose.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.becafe.gclose.Controller.ImageDecoder;
import com.becafe.gclose.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.becafe.gclose.R;

import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editUser, editPass;
    private Button btlogin;
    private TextView register;
    private ImageView logo;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //logo = findViewById(R.id.logo);

        //ImageDecoder dec = new ImageDecoder();
        //Bitmap log = dec.decodeSampledBitmapFromResource(getResources(),R.drawable.logotest, 250, 250);

        //logo.setImageBitmap(log);

        editUser = findViewById(R.id.EditUsername);
        editPass = findViewById(R.id.EditPassword);
        btlogin = findViewById(R.id.BtLogin);
        register = findViewById(R.id.registro);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }


    public void loginUser(){

        String email = editUser.getText().toString().trim();
        String password = editPass.getText().toString().trim();

        if(email.isEmpty()){
            editUser.setError("El correo es requerido");
            editUser.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editUser.setError("El correo no es correcto");
            editUser.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editPass.setError("La contraseña es requerida");
            editPass.requestFocus();
            return;
        }

        if(password.length() < 6){
            editPass.setError("Se requiere 6 o más caracteres");
            editPass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String id = user.getUid();

                            //Muestro un Toast de ingreso correcto
                            Toast.makeText(LoginActivity.this, id,
                                    Toast.LENGTH_LONG).show();

                            Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("USER_ID",  id);
                            startActivity(i);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }




}
