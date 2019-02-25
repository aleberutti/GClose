package com.becafe.gclose.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class NavigationActivity extends AppCompatActivity{

    private boolean flag, flagOK;
    private List<Place> listaLugares;
    private List listaLugaresConfirmados, listaDeseados;
    private String tag="", user_id;
    private DatabaseReference myRef;
    private int cont = 0;
    private ProgressBar mProgressBar;
    private ProfileFragment fragmentoPerfil;
    private GetCloseFragment gCloseFragment;
    private BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment selectedFragment = null;


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    tag="profileFragment";
                    selectedFragment=getSupportFragmentManager().findFragmentByTag(tag);
                    if(selectedFragment==null){
                        selectedFragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("USER_ID", user_id);
                        selectedFragment.setArguments(bundle);
                    }else{
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(selectedFragment)
                                .hide(getSupportFragmentManager().findFragmentByTag("getCloseFragment"))
                                .setReorderingAllowed(true)
                                .commitNowAllowingStateLoss();
                    }
                    break;
                case R.id.navigation_get_close:
                    tag="getCloseFragment";
                    selectedFragment=getSupportFragmentManager().findFragmentByTag(tag);
                    if(selectedFragment==null){
                        Bundle bundle = new Bundle();
                        bundle.putString("USER_ID", user_id);
                        gCloseFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(getSupportFragmentManager().findFragmentByTag("profileFragment"))
                                .add(R.id.fragmentContainer, gCloseFragment, tag)
                                .addToBackStack(tag)
                                .commit();
                    }else{
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(selectedFragment)
                                .hide(getSupportFragmentManager().findFragmentByTag("profileFragment"))
                                .setReorderingAllowed(true)
                                .commitNowAllowingStateLoss();
                    }
                    break;
                case R.id.navigation_crushes:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        flagOK = false;

        gCloseFragment = new GetCloseFragment();
        fragmentoPerfil = new ProfileFragment();

        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        
        user_id = getIntent().getExtras().getString("USER_ID");

        myRef = FirebaseDatabase.getInstance().getReference();

        // LOCATION SELECT
        this.flag = false;
        this.listaLugares = new ArrayList<Place>();


        //BUSCA LUGARES PERO NO DEJA ELEGIR (COMPRUEBA QUE SE PERMANECIO EN UN LUGAR)
        Log.e("ZAFSONOALARMA10", "ASDF");
        if(getIntent().getExtras().get("choosePlaces")!=null && !(boolean)getIntent().getExtras().get("choosePlaces")) {
            Log.e("ZAFSONOALARMA0", "ASDF");
            navigation.setSelectedItemId(R.id.navigation_get_close);
            this.checkPlaces();
        }
        //BUSCA LOS LUGARES Y DA A ELEGIR PARA LUEGO COMPROBARLOS (SOLO UNA VEZ POR INSTANCIA)
        if (cont<1) {
            checkPlaces();
//            MyAsyncTask mat = new MyAsyncTask(this, true);
//            mat.execute();
            //LIMPIO PLACES
//            this.clearPlaces();
        }

//        Intent i = getIntent();
//        String id = i.getStringExtra("USER_ID");

//        Bundle bundle = new Bundle();
//        bundle.putString("USER_ID", id);
//        fragmentInicio.setArguments(bundle);
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", user_id);
//        bundle.putString("USER_ID", "u6X3zFjFVHNM5gTVZkUxUuDMTjM2");
        fragmentoPerfil.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, fragmentoPerfil, "profileFragment")
                .addToBackStack(tag)
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
//        myRef.child("usuarios").child(user_id).child("listaLugares").setValue(null);
        listaDeseados = new ArrayList();
        builder.setTitle("CONFIRME SU UBICACION")
                .setMultiChoiceItems(names, asd, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if ((listaDeseados.isEmpty() || !listaDeseados.contains(listaLugares.get(which))) && isChecked) {
                            listaDeseados.add(listaLugares.get(which));
                        } else {
                            if (listaDeseados.contains(listaLugares.get(which)) && !isChecked)
                                listaDeseados.remove(listaLugares.get(which));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            flagOK = true;
                            myRef.child("usuarios").child(user_id).child("listaLugares").setValue(listaDeseados);
                            // PROGRAMO VERIFICACION A FUTURO
                            scheduleCheck();
                        } catch (StringIndexOutOfBoundsException exc) {
                            Toast.makeText(NavigationActivity.this, "NO SE HA CONFIRMADO UBICACION", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
        Log.e("ZAFSVP1", "ASD");
        myRef.child("usuarios").child(user_id).child("listaLugares").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("ZAFSIZELISTADESEADOS", dataSnapshot.getValue().toString());
//                Log.e("ZAFCount ", "" + dataSnapshot.getChildrenCount());
                listaDeseados = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    listaDeseados.add(dataSnapshot.child("" + i).getValue());
                }
                Log.e("ZAFSVP2", "ASD");
                String lista = "";
                listaLugaresConfirmados = new ArrayList<HashMap<String, String>>();
                if (listaLugares.isEmpty()) {
                    Toast.makeText(NavigationActivity.this, "No posee ningún lugar cercano", Toast.LENGTH_SHORT).show();
                    listaDeseados.clear();
//                    Toast.makeText(NavigationActivity.this, "Ya no se encuentra en el/los sitio/s solicitado/s.\nSu perfil no estará visible en el mismo", Toast.LENGTH_SHORT).show();
                } else {
                    for (Object o : listaDeseados) {
                        for (int i = 0; i < listaLugares.size(); i++) {
                            if (listaLugares.get(i).getId().equals(((HashMap<String, String>) o).get("id"))) {
                                listaLugaresConfirmados.add(o);
                                lista = lista + ((HashMap<String, String>) o).get("name") + ", ";
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
                                                    sendNotification();
                                                }
    //                                            Toast.makeText(NavigationActivity.this, msg + " A " + ((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("name"), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                myRef.child("places").child(((HashMap<String, String>) listaLugaresConfirmados.get(i)).get("id")).push().setValue(user_id);
                            }
                            myRef.child("usuarios").child(user_id).child("listaLugares").setValue(listaLugaresConfirmados);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        myRef.child("usuarios").child(user_id).child("listaLugares").removeValue();
                        if (cont<1) {
                            Log.e("ZAFSVP3", "ASD");
                            listaLugares.clear();
                            MyAsyncTask mat = new MyAsyncTask(NavigationActivity.this, true);
                            mat.execute();
                            cont++;
                        }
                    }
                    listaDeseados.clear();
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
            dataobjData.put("message", user_id);
            dataobjData.put("is-match", "false");


            objData.put("content_available","true");
            objData.put("priority", "high");
//            objData.put("body", "LA ALL BOYS"); /* ESTO LLEGA COMO NOTIFICACION */

            Log.e("ZAF LISTA CONFIRMADOS", String.valueOf(listaLugaresConfirmados.size()));
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

    @Override
    public void onResume(){
//        Log.e("ZAFSONOALARMA10", "entra on resume " +String.valueOf(NavigationActivity.checkBroadcast));
        if (cont>0 && listaDeseados.size()>0){
            Log.e("ZAFSONOALARMA10", "entra on resume");
            navigation.setSelectedItemId(R.id.navigation_get_close);
            checkPlaces();
        }
        super.onResume();
    }

    private void clearPlaces(){
        myRef.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.e("HABERRR", String.valueOf(child.getChildrenCount()));
                    for (DataSnapshot chil : child.getChildren()) {
                        Log.e("HABERRR1", chil.getValue().toString());
                        if (chil.getValue().toString().equals(user_id)){
                            chil.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        //Llamo a la actividad login
        Intent i = new Intent(NavigationActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


}
