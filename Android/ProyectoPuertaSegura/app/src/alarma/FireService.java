package com.sombra.user.puertasegura.alarma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sombra.user.puertasegura.R;
import com.sombra.user.puertasegura.main.AlarmaActivity;
import com.sombra.user.puertasegura.main.BluetoothActivity;
//import com.sombra.user.puertasegura.main.MainActivity;

public class FireService extends Service {

    private static final String TAG = "FireService";
    protected Firebase firebase;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    Context contexto;
    //private NotificationCompat.Builder notificationBuilder;

    @Override
    public void onCreate() {

        contexto = this;

        Log.i(TAG, "Service onCreate");

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://proyectopuertasegura-ffb85.firebaseio.com/");

        Intent myIntent = new Intent(this, BluetoothActivity.class);

        pendingIntent = PendingIntent.getActivity(this, 0, myIntent, 0);
        //Initialize NotificationManager using Context.NOTIFICATION_SERVICE
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creo un nuevo hilo para mi servicio
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {

            @Override
            public void run() {

                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) { //este metodo es llamado cada vez q cambia un dato

                        String estadoAlarma = (String) dataSnapshot.child("alarma_activada").getValue();
                        String estadoClaveEmergencia = (String) dataSnapshot.child("clave_emergencia").getValue();
                        String estadoPuerta = (String) dataSnapshot.child("puerta_forzada").getValue();

                        String msgNotificacion;

                        if(estadoAlarma.compareTo("1") == 0){
                            msgNotificacion = "Alerta! Alarma Activada";
                            notificacionAlarma(msgNotificacion);

                            Intent intent = new Intent(contexto, AlarmaActivity.class);
                            intent.putExtra("origen","estadoalarma");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        if(estadoClaveEmergencia.compareTo("1") == 0){
                            msgNotificacion = "Alerta! Se ha ingresado la Clave Falsa";
                            notificacionAlarma(msgNotificacion);
                            //boolean test = false;
                            //firebase.child("clave_emergencia").setValue(test);
                        }
                        if(estadoPuerta.compareTo("1") == 0){
                            msgNotificacion = "Alerta! Alguien intenta entrar a la casa";
                            notificacionAlarma(msgNotificacion);

                            Intent intent = new Intent(contexto, AlarmaActivity.class);
                            intent.putExtra("origen", "estadopuerta");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                //Detengo el servicio una vez q la tarea finaliza
                //stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


   public void  notificacionAlarma(String msg){

       NotificationCompat.Builder notificationBuilder;

           //Preparo la Notificacion Builder con el mensaje y el icono de la app
           notificationBuilder = new NotificationCompat.Builder(this)
                   .setContentTitle("Puerta Segura").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pendingIntent)
                   .setContentText(msg).setAutoCancel(true); //con setContentIntent inicio la activity
           //agrego el sonido de la notificacion
           Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           notificationBuilder.setSound(uri);
           long[] v = {80, 260, 80};
           notificationBuilder.setVibrate(v); //vibracion
           notificationManager.notify(1, notificationBuilder.build());

    }

    //El sistema llama a este método cuando otro componente se quiere vincular con el servicio
    @Override
    public void onDestroy() {
        Log.i(TAG, "SERVICIO DESTRUIDO");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

}