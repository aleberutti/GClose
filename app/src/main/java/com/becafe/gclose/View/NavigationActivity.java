package com.becafe.gclose.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.becafe.gclose.Controller.Location.MyAsyncTask;
import com.becafe.gclose.Controller.Location.Receptor;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NavigationActivity extends AppCompatActivity {

    private boolean flag;
    private List<Place> listaLugares;
    private List listaLugaresConfirmados, listaDeseados;
    private String tag="";
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private static int cont = 0;
    private String texto;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment selectedFragment = null;


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    tag="profileFragment";
                    selectedFragment=getSupportFragmentManager().findFragmentByTag(tag);
                    if(selectedFragment==null)selectedFragment = new ProfileFragment();
                    return true;
                case R.id.navigation_get_close:
//                    tag="getCloseFragment";
//                    selectedFragment=getSupportFragmentManager().findFragmentByTag(tag);
//                    if(selectedFragment==null)selectedFragment = new GetCloseFragment();
                    return true;
                case R.id.navigation_crushes:
                    return true;
            }

            getSupportFragmentManager()
                    .beginTransaction().
                    replace(R.id.fragmentContainer, selectedFragment, tag)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment fragmentInicio = new ProfileFragment();
//        Fragment fragmentInicio = new GetCloseFragment();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // LOCATION SELECT
        this.flag = false;
        this.listaLugares = new ArrayList<Place>();

        //LLEGA LA NOTIFICACION
        if (getIntent().getExtras().get("msj")!=null){
            showNotificationArrive((String)getIntent().getExtras().get("msj"));
        }
        //BUSCA LUGARES PERO NO DEJA ELEGIR (COMPRUEBA QUE SE PERMANECIO EN UN LUGAR)
        if(getIntent().getExtras().get("choosePlaces")!=null && !(boolean)getIntent().getExtras().get("choosePlaces")) {
            this.checkPlaces();
            Log.wtf("ZAFBUSCA CHECK", "1");
        }
        //BUSCA LOS LUGARES Y DA A ELEGIR PARA LUEGO COMPROBARLOS (SOLO UNA VEZ POR INSTANCIA)
        if (cont<1) {
            Log.wtf("ZAFENTRA DIALOGO", "1");
            cont++;
            MyAsyncTask mat = new MyAsyncTask(this, true);
            mat.execute();
        }

//        Intent i = getIntent();
//        String id = i.getStringExtra("USER_ID");

//        Bundle bundle = new Bundle();
//        bundle.putString("USER_ID", id);
//        fragmentInicio.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.fragmentContainer, fragmentInicio)
                .commit();

    }

    public List<Place> getListaLugares() {
        return listaLugares;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void getPlaces(){

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
                            Toast.makeText(NavigationActivity.this, "No se encuentra en un lugar registrado", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(NavigationActivity.this, "ERROR BUSCANDO EL LUGAR", Toast.LENGTH_LONG).show();
                        }
                        exception.printStackTrace();
                        flag = true;
                    }
                }
            });
        }
    }

    public void showDialog() {
        //OFRECER COMENZAR BUSQUEDA
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(NavigationActivity.this, R.style.MyDialogTheme);
        } else {
            builder = new AlertDialog.Builder(NavigationActivity.this);
        }
        String[] names = new String[listaLugares.size()];
        boolean[] asd = new boolean[listaLugares.size()];
        for (Place p : listaLugares) {
            names[listaLugares.indexOf(p)] = p.getName();
            asd[listaLugares.indexOf(p)] = false;
        }
//        myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").setValue(null);
        listaDeseados = new ArrayList();
        builder.setTitle("CONFIRME SU UBICACION")
                .setMultiChoiceItems(names, asd, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Log.e("ZAFWHICH", String.valueOf(which));
                        if ((listaDeseados.isEmpty() || !listaDeseados.contains(listaLugares.get(which))) && isChecked) {
                            listaDeseados.add(listaLugares.get(which));
                        } else {
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
                            myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").setValue(listaDeseados);
                            Log.e("ZAFGUARDADESEADOS", String.valueOf(listaDeseados.size()));
                            // PROGRAMO VERIFICACION A FUTURO
                            scheduleCheck();
                        } catch (StringIndexOutOfBoundsException exc) {
                            Toast.makeText(NavigationActivity.this, "NO SE HA CONFIRMADO UBICACION", Toast.LENGTH_LONG).show();
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
        Toast.makeText(NavigationActivity.this, "Se ha cargado su ubicación correctamente.\nLuego de 5 minutos se visualará su usuario en el lugar.", Toast.LENGTH_SHORT).show();

        Receptor rc = new Receptor(this);
        rc.setAlarm(NavigationActivity.this);
    }

    public void checkPlaces(){
        MyAsyncTask mat = new MyAsyncTask(NavigationActivity.this, false);
        mat.execute();
    }

    public void showValidatedPlaces(){
        Log.e("ZAFTIENE QUE ENTRAR", "ASD");
        Log.e("ZAFSIZELISTALUGARES", String.valueOf(listaLugares.size()));
        myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ZAFSIZELISTADESEADOS", dataSnapshot.getValue().toString());
                Log.e("ZAFCount ", "" + dataSnapshot.getChildrenCount());
                listaDeseados = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    listaDeseados.add(dataSnapshot.child("" + i).getValue());
                }
                Log.e("ZAFSIZELISTADESEADOS235", String.valueOf(listaDeseados.size()));
                String lista = "";
                listaLugaresConfirmados = new ArrayList<HashMap<String, String>>();
                if (listaLugares.isEmpty()) {
                    Log.e("ZAF IF", "ENTRA 1");
                    Toast.makeText(NavigationActivity.this, "Ya no se encuentra en ningún sitio solicitaado.\nSu perfil no estará visible en el mismo", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ZAF IF", ((HashMap<String, String>) listaDeseados.get(0)).get("name"));
                    for (Object o : listaDeseados) {
                        for (int i = 0; i < listaLugares.size(); i++) {
                            if (listaLugares.get(i).getId().equals(((HashMap<String, String>) o).get("id"))) {
                                Log.e("ZAF IF2", "ENTRA 4");
                                listaLugaresConfirmados.add(o);
                                lista = lista + ((HashMap<String, String>) o).get("name") + ", ";
                                Log.d("ZAFLISTASTR", lista);
                            }
                        }
                    }
                    if (listaLugaresConfirmados.size() > 0){
                        Toast.makeText(NavigationActivity.this, "Se ha confirmado tu perfil en: " + lista.substring(0, lista.length() - 2), Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < listaLugaresConfirmados.size(); i++) {
                                FirebaseMessaging.getInstance().subscribeToTopic(((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("id"))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "SUSCRITO";
                                                if (!task.isSuccessful()) {
                                                    msg = "ERROR";
                                                } else {
                                                    //MANDO NOTIFICACION
                                                    Log.wtf("ZAFMANDONOT", "SUSCRITO");
                                                    sendNotification();
                                                    Log.wtf("ZAFMANDONOT", "TERMINO SEND");
                                                }
                                                Log.d("ZAFRESULT", msg);
    //                                            Toast.makeText(NavigationActivity.this, msg + " A " + ((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("name"), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                Log.e("ZAFHABER", ((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("id"));
                                myRef.child("places").child(((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("id")).push().setValue(mAuth.getCurrentUser().getUid());
                            }
                            Log.e("ZAF ALMACENA",mAuth.getCurrentUser().getUid());
                            myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("listaLugares").setValue(listaLugaresConfirmados);
                            Log.e("ZAF ALMACENO", "done");
                        } catch (Exception e) {
                            Log.e("ZAFNULL NULL NULL NULL", "NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
                            Log.d("ZAFEXCEPTION", e.getMessage() + " - " + e.getCause().toString());
                            Log.e("ZAFHABER", ((HashMap<String, String>) listaLugaresConfirmados.get(0)).get("id"));
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification() {

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {

            obj = new JSONObject();
            objData = new JSONObject();

            dataobjData = new JSONObject();
            dataobjData.put("message", mAuth.getCurrentUser().getUid());


            objData.put("content_available","true");
            objData.put("priority", "high");
//            objData.put("body", "LA CONCHA DE TU MADRE ALL BOYS"); /* ESTO LLEGA COMO NOTIFICACION */

            Log.e("ZAFTEXTOOOOOOOOOO", "ASSSSSSSSSFF");
            Log.wtf("ZAFTOKEN", FirebaseInstanceId.getInstance().getToken());
//            obj.put("to", FirebaseInstanceId.getInstance().getToken()/*mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("messaging-token").toString()*/);
            if (listaLugaresConfirmados.size()>1) {
                String sendTopics = "";
                for (int i = 0; i < listaLugaresConfirmados.size(); i++) {
                    sendTopics = sendTopics + "'" + ((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("id") + "' in topics ||";
                }
                obj.put("condition", sendTopics.substring(0, sendTopics.length() - 3));
            }else{
                obj.put("to", "/topics/" + ((HashMap<String, String>) listaLugaresConfirmados.get(0)).get("id"));
            }
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
    //NO DEBERIA EXISTIR ESTE DE ABAJO, SE DEBERIA REDIRECCIONAR DIRECTAMENTE DESDE TOKEN ACTIVITY AL FRAGMENT DE GET CLOSE
    private void showNotificationArrive(String message){
        //OFRECER COMENZAR BUSQUEDA
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(NavigationActivity.this, R.style.MyDialogTheme);
        } else {
            builder = new AlertDialog.Builder(NavigationActivity.this);
        }
        builder.setTitle("RECIBIO UNA NOTIFICACION")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
                    }
                });
        //.show();
        Dialog diag = builder.create();
        diag.show();
    }


}
