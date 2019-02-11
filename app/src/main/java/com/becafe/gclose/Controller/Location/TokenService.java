package com.becafe.gclose.Controller.Location;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenService extends FirebaseMessagingService {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public static String Token;

    public TokenService() {
    }

    @Override
    public void onNewToken(String token) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        /*mDatabase.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("messaging-token")
                .setValue(token);
    */
        Token=token;
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject json = new JSONObject(remoteMessage.getData());
        try {
            Log.e("ZAFLLEGA NOTIFICACION", remoteMessage.getNotification().getBody());
            Intent asd = new Intent(getApplicationContext(), Ubicacion.class);
            asd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            asd.putExtra("msj", json.getString("message"));
            startActivity(asd);
        } catch (JSONException e) {
            e.printStackTrace();
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
