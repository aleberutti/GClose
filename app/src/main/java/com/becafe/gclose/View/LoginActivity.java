package com.becafe.gclose.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.becafe.gclose.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.becafe.gclose.R;

public class LoginActivity extends AppCompatActivity {

    private EditText EditUser, EditPass;
    private Button btlogin;
    private TextView register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditUser = findViewById(R.id.EditUsername);
        EditPass = findViewById(R.id.EditPassword);
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Muestro un Toast de ingreso correcto
                            Toast.makeText(LoginActivity.this, "Autenticación correcta.",
                                    Toast.LENGTH_LONG).show();

                            Intent i = new Intent(LoginActivity.this, IndexActivity.class);
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
