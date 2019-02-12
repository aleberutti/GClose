package com.becafe.gclose.Controller.Location;

import android.os.AsyncTask;
import android.util.Log;

import com.becafe.gclose.View.NavigationActivity;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MyAsyncTask extends AsyncTask<Void, Void, List<Place>> {
    private NavigationActivity n;
    private boolean chooseLocation;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    public MyAsyncTask(NavigationActivity nav, boolean chLocation){
        n=nav;
        chooseLocation = chLocation;
        Log.i("ZAFchLocation", String.valueOf(chLocation));
        Log.i("ZAFchooseLocation", String.valueOf(chooseLocation));
    }

    @Override
    protected void onPreExecute() {
        //--------------------------MOSTRAR PANTALLA DE CARGANDO LUGARES PLEASE------------------------------
    }


    @Override
    protected List<Place> doInBackground(Void... voids) {
        n.getPlaces();
        Log.i("ZAFentra", String.valueOf(n.isFlag()));
        while(!n.isFlag()){
//            Log.i("ZAFLAGCHECK", String.valueOf(u.getFlag()));
        }
        Log.i("ZAFSALE", String.valueOf(chooseLocation));
        return n.getListaLugares();
    }

    @Override
    protected void onPostExecute(List<Place> lista) {
        Log.i("ZAFchooseLocationAFTER", String.valueOf(chooseLocation));
        if (chooseLocation) {
           Log.i("ZAFLISTA1", n.getListaLugares().get(0).getName());
           n.showDialog();
       }else{
           Log.i("ZAFLISTA2", n.getListaLugares().get(0).getName());
           n.showValidatedPlaces();
       }
       n.setFlag(false);
       Log.i("ZAFLAGFALSE", String.valueOf(n.isFlag()));
    }

}
