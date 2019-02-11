package com.becafe.gclose.Controller.Location;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class MyAsyncTask extends AsyncTask<Void, Void, List<Place>> {
    private Ubicacion u;
    private boolean chooseLocation;

    public MyAsyncTask(Ubicacion ubicac, boolean chLocation){
        u=ubicac;
        chooseLocation = chLocation;
        Log.i("ZAFchLocation", String.valueOf(chLocation));
        Log.i("ZAFchooseLocation", String.valueOf(chooseLocation));
    }

    @Override
    protected void onPreExecute() {
        //--------------------------MOSTRAR PANTALLA DE CARGANDO LUGARES PLEASE------------------------------
        Ubicacion.listaLugares.clear();
    }


    @Override
    protected List<Place> doInBackground(Void... voids) {
        u.getPlaces();
        Log.i("ZAFentra", String.valueOf(u.getFlag()));
        while(!u.getFlag()){
//            Log.i("ZAFLAGCHECK", String.valueOf(u.getFlag()));
        }
        Log.i("ZAFSALE", String.valueOf(chooseLocation));
        return Ubicacion.listaLugares;
    }

    @Override
    protected void onPostExecute(List<Place> lista) {
        Log.i("ZAFchooseLocationAFTER", String.valueOf(chooseLocation));
        if (chooseLocation) {
           Log.i("ZAFLISTA1", Ubicacion.listaLugares.get(0).getName());
           u.showDialog();
       }else{
           Log.i("ZAFLISTA2", Ubicacion.listaLugares.get(0).getName());
           u.showValidatedPlaces();
       }
       u.setFlag(false);
       Log.i("ZAFLAGFALSE", String.valueOf(u.getFlag()));
    }

}
