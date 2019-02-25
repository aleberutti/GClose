package com.becafe.gclose.View;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    private EditText editUser, editPass;
    private Button btlogin;
    private TextView register, titulo;
    private ImageView logo;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar, mProgrssBarPic;

    private boolean flag, ingresoCorrecto;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] arr = {ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        while(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(this, arr, 0);

        }

        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.progressbar);

        editUser = findViewById(R.id.EditUsername);
        editPass = findViewById(R.id.EditPassword);
        btlogin = findViewById(R.id.BtLogin);
        register = findViewById(R.id.registro);
        logo = findViewById(R.id.imageView);
        titulo = findViewById(R.id.logo);

        logo.setTop(500);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(logo, "y", 420f);
        animatorY.setDuration(2500);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(titulo, View.ALPHA, 1.0f, 0.0f);
        alphaAnimator.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY, alphaAnimator);
        animatorSet.start();
        new ShowLogo().execute();

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
        ingresoCorrecto = false;

        if(email.isEmpty()){
            editUser.setError("El correo es requerido");
            editUser.requestFocus();
            flag = true;
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editUser.setError("El correo no es correcto");
            editUser.requestFocus();
            flag = true;
            return;
        }

        if(password.isEmpty()){
            editPass.setError("La contraseña es requerida");
            editPass.requestFocus();
            flag = true;
            return;
        }

        if(password.length() < 6){
            editPass.setError("Se requiere 6 o más caracteres");
            editPass.requestFocus();
            flag = true;
            return;
        }

        new MyTask(email, password).execute();

    }

    private void checkAcc(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            flag = true;
                            ingresoCorrecto = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            flag = true;
                            Toast.makeText(LoginActivity.this, "Usuario o contraseña inválidos",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    class MyTask extends AsyncTask<String, Integer, String> {

        private String email, password;

        public MyTask(String mail, String pass){
            this.email=mail;
            this.password=pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            flag = false;

            mProgressBar.setVisibility(View.VISIBLE);
            editUser.setVisibility(View.INVISIBLE);
            editPass.setVisibility(View.INVISIBLE);
            register.setVisibility(View.INVISIBLE);
            btlogin.setVisibility(View.INVISIBLE);

            checkAcc(email, password);

            //Toast.makeText(getApplicationContext(), "Pongo visible", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {

            while(!flag) {
                for (int i = 0; i <= 15; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    if (flag){
                        return "Fin";
                    }
                }
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

            if (ingresoCorrecto) {
                callActivity();
            }else{
                mProgressBar.setVisibility(View.INVISIBLE);
                editUser.setVisibility(View.VISIBLE);
                editPass.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
                btlogin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void callActivity(){

        // Sign in success, update UI with the signed-in user's information
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();

        //Muestro un Toast de ingreso correcto
        Toast.makeText(LoginActivity.this, "Ingreso correcto",
                Toast.LENGTH_LONG).show();


        TokenService.Token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("usuarios");
        db.child(id).child("messaging-token").setValue(FirebaseInstanceId.getInstance().getToken());
        Log.e("ZAF TOKENLOGIN",FirebaseInstanceId.getInstance().getToken());

        Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("USER_ID",  id);
        startActivity(i);

    }

    class ShowLogo extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(2300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Fin";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showElements();
        }
    }

    private void showElements(){
        titulo.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        editUser.setVisibility(View.VISIBLE);
        editPass.setVisibility(View.VISIBLE);
        btlogin.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume(){
        Log.e("ENTRAONRESUME", getApplicationContext().getApplicationInfo().toString());
        flag = true;
        super.onResume();
    }

}
