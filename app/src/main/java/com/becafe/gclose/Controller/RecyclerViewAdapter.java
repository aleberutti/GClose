package com.becafe.gclose.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.becafe.gclose.View.LoginActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView tvEdad, tvNombre;
        private ImageView imgProfile;
        private FloatingActionButton btMatch, btNo;
        private ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvEdad = itemView.findViewById(R.id.tvEdad);
            imgProfile = itemView.findViewById(R.id.imageProfile);
            btMatch = itemView.findViewById(R.id.btnFloatingMatch);
            btNo = itemView.findViewById(R.id.btnFloatingNot);
            mProgressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    private List<Usuario> usuarioLista;
    private List<String> listUids;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private boolean[] flagOut;
    private StorageReference storageRef;
    private final long ONE_MEGABYTE = 1024 * 1024 * 5;
    private ImageView profile;
    private int contador = 0, contador2 = 0;
    private ViewGroup padre;

    private Context contexto;

    public RecyclerViewAdapter(List<Usuario> usuarioLista, List<String> placesUids) {
        this.usuarioLista = usuarioLista;
        this.listUids = placesUids;
        this.flagOut = new boolean[listUids.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        contexto = parent.getContext();
        padre = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_close_card,parent,false);
        view.setTag(contador);
        ViewHolder viewHolder = new ViewHolder(view);
        contador++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int index = position;
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        profile=holder.imgProfile;
        flagOut[index] = false;
        new LoadProfilePicture(holder).execute();
        Log.e("ZAF PASABUCLE", "SDFDSFG");
        try {
            storageRef = FirebaseStorage.getInstance().getReference().child("/" + listUids.get(index) + "/images/foto_perfil");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Glide.with(contexto).load(imageURL).into((ImageView) padre.getChildAt(index).findViewById(R.id.imageProfile));
                    flagOut[index] = true;
                    contador2 = index;
                    Log.e("ZAF HABERFLAG",String.valueOf(contador2));
                    Log.e("ZAF SALEPORALGUNLADO", "SDFDSFG");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //QUEDA LA POR DEFECTO
                    profile.setImageBitmap(BitmapFactory.decodeResource(contexto.getResources(), R.drawable.logotest));
                    flagOut[index] = true;
                    contador2++;
                    Log.e("ZAF SALEPORALGUNLADO", "SDFDSFG");
                }
            });
        }catch (Exception ex){
            profile.setImageBitmap(BitmapFactory.decodeResource(contexto.getResources(), R.drawable.logotest));
            flagOut[index] = true;
            contador2++;
            Log.e("ZAF SALEPORALGUNLADO", "SDFDSFG");
        }
        String name = usuarioLista.get(position).getNombre()+" "+ usuarioLista.get(position).getApellido();
        String edad = "Edad: " + usuarioLista.get(position).getAge();
        holder.tvNombre.setText(name);
        holder.tvEdad.setText(edad);
        holder.btMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ZAF APRIETA BOTON", "SDFDSFG");
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("likes").push().setValue(listUids.get(index));
                myRef.child("usuarios").child(listUids.get(index)).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        Log.e("ZAF LLEGAANOTIFICACION", "SDFDSFG");
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            i++;
                            Log.e("ZAF LLEGAANOTIFICACION1", "SDFDSFG");
                            if (child.getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                Log.e("ZAF LLEGAANOTIFICACION", "SDFDSFG");
                                sendNotification(index, true);
                                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("matchs").push().setValue(listUids.get(index));
                                myRef.child("usuarios").child(listUids.get(index)).child("matchs").push().setValue(mAuth.getCurrentUser().getUid());
                                // ITS A FUCKING MATCH
                                Intent destino = new Intent(contexto, LoginActivity.class);
                                destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                createNotificationChannel();
                                PendingIntent pendingIntent =
                                        PendingIntent.getActivity(contexto, 0, destino, 0);
                                Log.e("ZAF GENERARNOTIFICACION", "SDFDSFG");
                                NotificationCompat.Builder mBuilder = new
                                        NotificationCompat.Builder(contexto, "2")
                                        .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                                        .setContentTitle("♥ ♥ ♥ Parece que tienes una cita ! ! !")
                                        .setContentText("Presiona aquí para comenzar una conversación")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager =
                                        NotificationManagerCompat.from(contexto);
                                notificationManager.notify(99, mBuilder.build());
                                Log.e("ZAF SALEDEGENERAR", "SDFDSFG");
                                usuarioLista.remove(index);
                                listUids.remove(index);
                                RecyclerViewAdapter.this.notifyDataSetChanged();
                                return ;
                            }
                            if (i == dataSnapshot.getChildrenCount()){
                                usuarioLista.remove(index);
                                listUids.remove(index);
                                RecyclerViewAdapter.this.notifyDataSetChanged();
                                sendNotification(index, false);
                                return ;
                            }
                        }
                        if (i == dataSnapshot.getChildrenCount()){
                            sendNotification(index, false);
                            usuarioLista.remove(index);
                            listUids.remove(index);
                            RecyclerViewAdapter.this.notifyDataSetChanged();
                            return ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("unlikes").push().setValue(listUids.get(index));
                usuarioLista.remove(index);
                listUids.remove(index);
                RecyclerViewAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarioLista.size();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User request";
            String description = "New user in place";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    contexto.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void sendNotification(int index, boolean isMatch) {
        Log.e("ZAF ENTRANOTIFICACION1", "SDFDSFG");
        final boolean match = isMatch;
        myRef.child("usuarios").child(listUids.get(index)).child("messaging-token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                JSONObject obj = null;
                JSONObject objData = null;
                JSONObject dataobjData = null;

                try {

                    obj = new JSONObject();
                    objData = new JSONObject();

                    dataobjData = new JSONObject();
                    dataobjData.put("message", mAuth.getCurrentUser().getUid());
                    Log.e("Valor de match", String.valueOf(match));
                    if (!match){
                        dataobjData.put("match-request", "true");
                    }else{
                        dataobjData.put("is-match", "true");
                    }

                    objData.put("content_available","true");
                    objData.put("priority", "high");
//            objData.put("body", "LA ALL BOYS"); /* ESTO LLEGA COMO NOTIFICACION */

                    Log.e("ZAFTEXTOOOOOOOOOO", "ASSSSSSSSSFF");
                    Log.wtf("ZAFTOKEN", dataSnapshot.getValue().toString());
                    obj.put("to", dataSnapshot.getValue().toString());

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
                        headers.put("Authorization", "key=" + contexto.getString(R.string.messagingClave));
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(contexto);
                int socketTimeout = 1000 * 60;// 60 seconds
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsObjRequest.setRetryPolicy(policy);
                requestQueue.add(jsObjRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class LoadProfilePicture extends AsyncTask<Void, Integer, String> {

        private ViewHolder holder;

        public LoadProfilePicture(ViewHolder holder){
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            Log.e("ZAF HABERFLAG1",String.valueOf(flagOut));
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.tvNombre.setVisibility(View.INVISIBLE);
            holder.tvEdad.setVisibility(View.INVISIBLE);
            holder.imgProfile.setVisibility(View.INVISIBLE);
            holder.btMatch.setVisibility(View.INVISIBLE);
            holder.btNo.setVisibility(View.INVISIBLE);

            //Toast.makeText(getApplicationContext(), "Pongo visible", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... strings) {
//            Log.e("ZAF HABERFLAG4",String.valueOf(flagOut));
//            while (contador2!=holder.getAdapterPosition()){
//                Log.e("ZAF FLAG",String.valueOf(contador2));
//                Log.e("ZAF ADAPTERPOSITION",String.valueOf(holder.getAdapterPosition()));
//            }
            while(!flagOut[holder.getAdapterPosition()]) {
                for (int i = 0; i <= 15; i++) {
                    Log.e("ZAF HABERFLAG",String.valueOf(contador2));
                    try {
//                        Log.e("ZAF HABERFLAG0",String.valueOf(flagOut));
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    if (flagOut[holder.getAdapterPosition()]){
//                        Log.e("ZAF HABERFLAG99",String.valueOf(flagOut));
                        return "Fin";
                    }
                }
            }
//            Log.e("ZAF HABERFLAG5",String.valueOf(flagOut));
            return "Fin";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            holder.mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            flagOut[holder.getAdapterPosition()] =  false;

            holder.mProgressBar.setVisibility(View.INVISIBLE);
            holder.tvNombre.setVisibility(View.VISIBLE);
            holder.tvEdad.setVisibility(View.VISIBLE);
            holder.imgProfile.setVisibility(View.VISIBLE);
            holder.btMatch.setVisibility(View.VISIBLE);
            holder.btNo.setVisibility(View.VISIBLE);

        }
    }

}