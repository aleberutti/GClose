package com.becafe.gclose.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.becafe.gclose.Controller.Location.TokenService;
import com.becafe.gclose.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editUser, editPass;
    private Button btlogin;
    private TextView register;
    private ImageView logo;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.progressbar);

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

                            //LLAMO AL PROGRESS BAR PARA EL NUEVO INTENT
                            new MyTask().execute();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }


    class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            editUser.setVisibility(View.INVISIBLE);
            editPass.setVisibility(View.INVISIBLE);
            register.setVisibility(View.INVISIBLE);
            btlogin.setVisibility(View.INVISIBLE);

            //Toast.makeText(getApplicationContext(), "Pongo visible", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {

            for (int i=0; i<=15; i++){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }

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

            Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();

            callActivity();

        }
    }

    public void callActivity(){

        // Sign in success, update UI with the signed-in user's information
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();

        //Muestro un Toast de ingreso correcto
        Toast.makeText(LoginActivity.this, id,
                Toast.LENGTH_LONG).show();


        TokenService.Token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("usuarios");
        db.child(id).child("messaging-token").setValue(TokenService.Token);

        Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("USER_ID",  id);
        startActivity(i);

    }


}
