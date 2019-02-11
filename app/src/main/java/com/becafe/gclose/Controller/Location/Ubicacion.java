package com.becafe.gclose.Controller.Location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.becafe.gclose.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import androidx.annotation.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Ubicacion extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private boolean flag;

    private static String texto;


    public static List<Place> listaLugares = new ArrayList<Place>(), listaLugaresConfirmados = new ArrayList<Place>(), listaDeseados;

    public static int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        this.flag = false;

        try{
            if (getIntent().getExtras().get("msj")!=null){
                showNotificationArrive((String)getIntent().getExtras().get("msj"));
            }
        }catch (NullPointerException e){
            Log.e("ZAFNOT NULL", "NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
        }

        try{
            if(getIntent().getExtras().get("notmsj")!=null) {
                texto = (String) getIntent().getExtras().get("notmsj");
                Log.e("ZAFTEXT", texto);
            }
        }catch (NullPointerException e){
            Log.e("ZAFTEXT NULL", "NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
        }

        try {
            if(!(boolean)getIntent().getExtras().get("choosePlaces")) {
                this.checkPlaces();
                Log.wtf("ZAFBUSCA CHECK", "1");
            }
        }catch (NullPointerException e){
            if (cont<1) {
                Log.wtf("ZAFENTRA DIALOGO", "1");
                cont++;
                MyAsyncTask mat = new MyAsyncTask(this, true);
                mat.execute();
            }else{
                Log.e("ZAFNULL NULL NULL NULL", "NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
            }
        }

        /*
        ----------------------- PONER EN LoginActivity -------------------------
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);
        sendRegistrationToServer(token);

        private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_messaging_token))
                .setValue(token);
    }
         */
    }

    public boolean getFlag(){
        return flag;
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public void getPlaces(){

        String[] arr = {ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, arr, 0);

        Places.initialize(this, getString(R.string.mapClave));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(this, new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = task.getResult();
                        double diferencia;
                        double prob = response.getPlaceLikelihoods().get(0).getLikelihood();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            diferencia = prob - placeLikelihood.getLikelihood();
                            if (diferencia < 0.1) {
                                Log.e("ZAFGUARDA", placeLikelihood.getPlace().getName());
                                listaLugares.add(placeLikelihood.getPlace());
                            }else{
                                Log.e("ZAFNO HAY MAS LUGARES", "ASD");
                                Log.e("ZAFSIZE", String.valueOf(listaLugares.size()));
                                flag = true;
                                Log.i("ZAFLAGTRUE", String.valueOf(flag));
                                return ;
                            }
                        }
                    }else{
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("ZAF", "Place not found: " + apiException.getStatusCode());
                            Toast.makeText(Ubicacion.this, "No se encuentra en un lugar registrado", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(Ubicacion.this, "ERROR BUSCANDO EL LUGAR", Toast.LENGTH_LONG).show();
                        }
                        exception.printStackTrace();
                        flag = true;
                    }
                }
            });
        }
    }

    public void showDialog(){
        //OFRECER COMENZAR BUSQUEDA
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Ubicacion.this, R.style.MyDialogTheme);
        } else {
            builder = new AlertDialog.Builder(Ubicacion.this);
        }
        String[] names = new String[listaLugares.size()];
        boolean[] asd = new boolean[listaLugares.size()];
        for (Place p : listaLugares) {
            names[listaLugares.indexOf(p)] = p.getName();
            asd[listaLugares.indexOf(p)] = false;
        }
        listaDeseados = new ArrayList<Place>();
        builder.setTitle("CONFIRME SU UBICACION")
                .setMultiChoiceItems(names, asd, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Log.e("ZAFWHICH", String.valueOf(which));
                        if ((listaDeseados.isEmpty() || !listaDeseados.contains(listaLugares.get(which))) && isChecked) {
                            listaDeseados.add(listaLugares.get(which));
                        }else {
                            if (listaDeseados.contains(listaLugares.get(which)) && !isChecked)
                                listaDeseados.remove(listaLugares.get(which));
                        }
                        Log.e("ZAFLISTADESEADOS", String.valueOf(listaDeseados.size()));
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            // PROGRAMO VERIFICACION A FUTURO
                            scheduleCheck();
                        } catch (StringIndexOutOfBoundsException exc) {
                            Toast.makeText(Ubicacion.this, "NO SE HA CONFIRMADO UBICACION", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //.show();
        Dialog diag = builder.create();
        diag.show();
    }

    public void scheduleCheck(){
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser cUser = mAuth.getCurrentUser();
        //mDatabase.child("users").child(cUser.getUid()).child("listaLugaresDeseados").setValue(listaLugares);
        Toast.makeText(Ubicacion.this, "Se ha cargado su ubicación correctamente.\n Luego de 5 minutos se visualará su usuario en el lugar.", Toast.LENGTH_SHORT).show();

        Receptor rc = new Receptor(this);
        rc.setAlarm(Ubicacion.this);
    }

    public void checkPlaces(){
        MyAsyncTask mat = new MyAsyncTask(Ubicacion.this, false);
        mat.execute();
    }

    public void showValidatedPlaces(){
        Log.e("ZAFTIENE QUE ENTRAR", "ASD");
        Log.e("ZAFSIZELISTALUGARES", String.valueOf(listaLugares.size()));
        Log.e("ZAFSIZELISTADESEADOS", String.valueOf(listaDeseados.size()));
        String lista = "";
        if (listaLugares.isEmpty()){
            Toast.makeText(this, "Ya no se encuentra en ningún sitio solicitaado.\nSu perfil no estará visible en el mismo", Toast.LENGTH_SHORT).show();
        }else {
            for (Place p : listaDeseados) {
                if (listaLugares.contains(p)) {
                    listaLugaresConfirmados.add(p);
                    Log.e("ZAFELEMENT", p.getName());
                    lista = lista + p.getName() + ", ";
                    Log.d("ZAFLISTASTR", lista);
                }
            }
            Toast.makeText(this, "Se ha confirmado tu perfil en: " + lista.substring(0, lista.length() - 2), Toast.LENGTH_SHORT).show();
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(listaLugaresConfirmados.get(0).getId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "SUSCRITO";
                            if (!task.isSuccessful()) {
                                msg = "ERROR";
                            }else{
                                //MANDO NOTIFICACION
                                sendNotification();
                                Log.wtf("ZAFMANDONOT", "TERMINO SEND");
                            }
                            Log.d("ZAFRESULT", msg);
                            Toast.makeText(Ubicacion.this, msg + " A " + listaLugaresConfirmados.get(0).getName(), Toast.LENGTH_SHORT).show();
                        }
                });
                Log.e("ZAFHABER",listaLugaresConfirmados.get(0).getId());
            }catch(Exception e){
                Log.e("ZAFNULL NULL NULL NULL", "NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
                Log.d("ZAFEXCEPTION",e.getMessage() + " - " + e.getCause().toString());
                Log.e("ZAFHABER",listaLugaresConfirmados.get(0).getId());
                e.printStackTrace();
            }
        }
    }

    public void sendNotification() {

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {

            obj = new JSONObject();
            objData = new JSONObject();

            dataobjData = new JSONObject();
            dataobjData.put("message", texto);
            Log.e("ZAFTEXTOOOOOOOOOO", texto);

            objData.put("content_available","true");
            objData.put("priority", "high");
            objData.put("body", "LA CONCHA DE TU MADRE ALL BOYS");

            Log.wtf("ZAFTOKEN", FirebaseInstanceId.getInstance().getToken());
//            obj.put("to", FirebaseInstanceId.getInstance().getToken()/*mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("messaging-token").toString()*/);
            obj.put("to", "/topics/" + listaLugaresConfirmados.get(0).getId());
            obj.put("notification", objData);
            obj.put("data", dataobjData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ZAFSUCCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ZAFERROR", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=" + getString(R.string.messagingClave));
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    private void showNotificationArrive(String message){
        //OFRECER COMENZAR BUSQUEDA
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Ubicacion.this, R.style.MyDialogTheme);
        } else {
            builder = new AlertDialog.Builder(Ubicacion.this);
        }
        builder.setTitle("RECIBIO UNA NOTIFICACION")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        //.show();
        Dialog diag = builder.create();
        diag.show();
    }

}