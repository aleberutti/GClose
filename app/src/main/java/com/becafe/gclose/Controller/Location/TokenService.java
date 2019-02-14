package com.becafe.gclose.Controller.Location;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.becafe.gclose.MainActivity;
import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;
import com.becafe.gclose.View.NavigationActivity;
import com.becafe.gclose.View.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TokenService extends FirebaseMessagingService {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String llegaUid;
    public static String Token;

    public TokenService() {
    }

    @Override
    public void onNewToken(String token) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase.child("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("messaging-token")
                .setValue(token);
        Token=token;
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject json = new JSONObject(remoteMessage.getData());
        try {
            Log.e("FAUSTO2", json.getString("is-match"));
            Log.e("FAUSTO2", json.getString("message"));
            if (json.getString("is-match") == null) {
                Intent destino = new Intent(getBaseContext(), MainActivity.class);
                destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                createNotificationChannel();
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(getBaseContext(), 0, destino, 0);
                NotificationCompat.Builder mBuilder = new
                        NotificationCompat.Builder(getBaseContext(), "2")
                        .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                        .setContentTitle("♥ ♥ ♥ Parece que tienes una cita ! ! !")
                        .setContentText("Presiona aquí para comenzar una conversación")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(getBaseContext());
                notificationManager.notify(99, mBuilder.build());
            } else {
                if (json.getString("match-request").equals("true")) {
                    llegaUid = json.getString("message");
                    mDatabase.child("usuarios").child(llegaUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Usuario emisor = dataSnapshot.getValue(Usuario.class);
                            Intent destino = new Intent(getBaseContext(), MainActivity.class);
                            destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            createNotificationChannel();
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(getBaseContext(), 0, destino, 0);
                            NotificationCompat.Builder mBuilder = new
                                    NotificationCompat.Builder(getBaseContext(), "2")
                                    .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                                    .setContentTitle("⚠ Tienes una nueva propuesta ⚠")
                                    .setContentText(emisor.getNombre() + " está en tu sitio y quiere concerte. \nPresiona para ver su perfil")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(getBaseContext());
                            notificationManager.notify(99, mBuilder.build());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else{
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mAuth = FirebaseAuth.getInstance();
                    //            Log.e("ZAFLLEGA NOTIFICACION", remoteMessage.getNotification().getBody());
                    Log.e("ZAFLLEGA NOTIFICACION", json.getString("message"));
                    llegaUid = json.getString("message");
                    Log.e("ZAFllegaUid", llegaUid);
                    // -------------------ACA HACER INTENT AL FRAGMENT DE GET CLOSE!!!!!!!!!!!!!! ------------------------------
                    Log.e("ZAFCONTEXT", getApplicationContext().getClass().getName());
                    if (llegaUid.equals(mAuth.getCurrentUser().getUid())) {
                        // ABRIR FRAGMENT GET CLOSE
                        Intent asd = new Intent(getApplicationContext(), NavigationActivity.class);
                        asd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        asd.putExtra("msj", json.getString("message"));
                        startActivity(asd);
                    } else {
                        mDatabase.child("usuarios").child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    if (child.getValue().toString().equals(llegaUid)) {
                                        return;
                                    }
                                }
                                mDatabase.child("usuarios").child("unlikes").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            if (child.getValue().toString().equals(llegaUid)) {
                                                return;
                                            }
                                        }
                                        //SI LLEGA ACÁ QUIERE DECIR QUE NUNCA HA VISTO EL PERFIL QUE SE ACABA DE REGISTRAR AL LUGAR
                                        mDatabase.child("usuarios").child(llegaUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dataSnapshot.getValue(Usuario.class);
                                                //ENVIAR NOTIFICACIÓN DE QUE SE HA REGISTRADO UN NUEVO USUARIO
                                                Intent destino = new Intent(getApplicationContext(), ProfileFragment.class);
                                                destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                destino.putExtra("uid", llegaUid);
                                                createNotificationChannel();
                                                PendingIntent pendingIntent =
                                                        PendingIntent.getActivity(getApplicationContext(), 0, destino, 0);
                                                NotificationCompat.Builder mBuilder = new
                                                        NotificationCompat.Builder(getApplicationContext(), "1")
                                                        .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                                                        .setContentTitle("Nuevo usuario en el lugar")
                                                        .setContentText("Presiona aquí para ver su perfil")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true);
                                                NotificationManagerCompat notificationManager =
                                                        NotificationManagerCompat.from(getApplicationContext());
                                                notificationManager.notify(99, mBuilder.build());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User request";
            String description = "New user in place";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    public void sendNotification() {
//
//        JSONObject obj = null;
//        JSONObject objData = null;
//        JSONObject dataobjData = null;
//
//        try {
//
//            obj = new JSONObject();
//            objData = new JSONObject();
//
//            dataobjData = new JSONObject();
//            dataobjData.put("message", "PRUEBA");
//            dataobjData.put("isSuccess", true);
//
//            objData.put("content_available","true");
//            objData.put("priority", "high");
//
//            obj.put("to", TokenService.Token/*mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("messaging-token").toString()*/);
//            obj.put("notification", objData);
//            obj.put("data", dataobjData);
//
//            Log.e("MYOBJs", obj.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.e("ZAFSUCCESS", response + "");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("ZAFERRORS", error + "");
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", "key=" + getString(R.string.messagingClave));
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        int socketTimeout = 1000 * 60;// 60 seconds
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsObjRequest.setRetryPolicy(policy);
//        requestQueue.add(jsObjRequest);
//    }
}
