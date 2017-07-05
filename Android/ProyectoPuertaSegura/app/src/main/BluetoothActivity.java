package com.sombra.user.puertasegura.main;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sombra.user.puertasegura.alarma.FireService;
import com.sombra.user.puertasegura.bluetooth.Constants;
import com.sombra.user.puertasegura.R;
import com.sombra.user.puertasegura.bluetooth.RSSPullService;
import com.sombra.user.puertasegura.galeriaFotos.GalleryActivity;

public class BluetoothActivity extends AppCompatActivity{
    Notification n;
    Intent mServiceIntent;
    Intent alarmServiceIntent;
    Activity ThisActivity;
    Button botonActivacion;
    Button botonFotos;
    Button botonCambio;
    Button botonHistorial;
    NotificationManager notificationManager;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ToggleButton botonHuella;

    int taskcomplete = 0;
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ThisActivity = this;
        settings = getSharedPreferences(PREFS_NAME, 0);
        boolean huella = settings.getBoolean("Huella",false);

        mServiceIntent = new Intent(this, RSSPullService.class);

        alarmServiceIntent = new Intent(this, FireService.class);
        ThisActivity.startService(alarmServiceIntent);

        //Lo necesita para poder hacer el discovery
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        //ThisActivity = this;
        botonActivacion = (Button) findViewById(R.id.botonActivacion);
        botonFotos = (Button) findViewById(R.id.botonFotos);
        botonHuella = (ToggleButton) findViewById(R.id.botonhuella);
        botonCambio = (Button)findViewById(R.id.botonCambio);
        botonHistorial = (Button)findViewById(R.id.botonHistorial);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Cambia el valor del boton de huella segun como estaba definido.
        if(!huella){
            botonHuella.setChecked(false);
        }
        if(huella){
            botonHuella.setChecked(true);
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://proyectopuertasegura-ffb85.appspot.com");
        database = FirebaseDatabase.getInstance();

        //obtiene la referencia a los elementos del storage --> nombre y direccion de las imagenes
        myRef = database.getReference("storagereference");

        botonActivacion.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Le indica al servicio que manda bluetooth el valor "x" --> o sea que abra la puerta.
                mServiceIntent.putExtra("Valor", "x");
                ThisActivity.startService(mServiceIntent);
                notificacion(notificationManager);
            }
        });

        final Button botonDesactivacion = (Button) findViewById(R.id.botonDesactivacion);
        botonDesactivacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ThisActivity.stopService(mServiceIntent);
                cancelNotification();
            }
        });

        botonFotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Ingresa a la view de la galeria de imagenes.
                Intent intent = new Intent(BluetoothActivity.this,GalleryActivity.class);
                startActivity(intent);
            }
        });

        botonHuella.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Si el boton de la huella cambio de estado y cambia la configuracion con respecto a eso.
                SharedPreferences.Editor editor = settings.edit();

                if(isChecked) {
                    PackageManager manager = getPackageManager();
                    if (manager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                        editor.putBoolean("Huella", true);
                        // Commit the edits!
                        editor.apply();

                        Log.i("TestHuella", "activado");
                    }
                    else{
                        Toast toast = Toast.makeText(ThisActivity,"No posee lector de Huellas",Toast.LENGTH_SHORT);
                        toast.show();
                        botonHuella.setChecked(false);
                    }
                }
                else{
                    editor.putBoolean("Huella", false);
                    editor.apply();
                    Log.i("TestHuella", "desactivado");
                }
            }}
        );

        botonCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Boton si se desea cambiar usuario o contraseña.
                Intent intent = new Intent(ThisActivity,RegistrarActivity.class);
                intent.putExtra("comportamiento", false);
                startActivity(intent);
            }
        });

        botonHistorial.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Boton si se desea revisar el historias de ingresos por la puerta.
                Intent intent = new Intent(ThisActivity,HistorialActivity.class);
                startActivity(intent);
            }
        });

        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        MyResponseReceiver responseReceiver =
                new MyResponseReceiver();
        // Registers the MyResponseReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver, statusIntentFilter);
    }

    //Maneja las notificaciones en la barra, o sea el simbolo de bluetooth.
    public void notificacion(NotificationManager notificationManager){
        //NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("id", 123);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        n  = new Notification.Builder(this)
                //.setContentTitle("My message")
                //.setContentText("Subject")
                .setSmallIcon(R.drawable.bluetooth_test)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
        //.setStyle(new Notification.BigTextStyle().bigText(""+msg.get(3))).build();
        //  .addAction(R.drawable.line, "", pIntent).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);

    }

    public void cancelNotification() {
        //notificationManager.cancelAll();
        notificationManager.cancel(0);
    }

    //Cuando termina el servicio, tiene que tambien sacar la notificacion.
    private class MyResponseReceiver extends BroadcastReceiver {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive@
        public void onReceive(Context context, Intent intent) {
            notificationManager.cancel(0);
        }
    }
}



