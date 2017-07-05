package com.sombra.user.puertasegura.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.sombra.user.puertasegura.R;
import com.sombra.user.puertasegura.shake.ShakeDetector;
import com.sombra.user.puertasegura.bluetooth.RSSPullService;

    public class AlarmaActivity extends AppCompatActivity {
    Intent mServiceIntent;
    Context ThisActivity;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    protected Firebase firebase;
    String origen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        ThisActivity = this;

        origen = "";

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            origen += bundle.getString("origen");
        }

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://proyectopuertasegura-ffb85.firebaseio.com/");

        final String desactivado = "0";

        mServiceIntent = new Intent(this, RSSPullService.class);


        Button boton = (Button) findViewById(R.id.botonAlarma);

        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mServiceIntent.putExtra("Valor", "2");
                ThisActivity.startService(mServiceIntent);
                //finish();
            }
        });


        firebase.addValueEventListener(new ValueEventListener() {
            /*Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

                Log.i("PruebaPrueba", "entro");

                String estadoAlarma = "";
                String estadoClaveEmergencia = "";
                String estadoPuerta = "";

                estadoAlarma += (String) dataSnapshot.child("alarma_activada").getValue();
                estadoClaveEmergencia += (String) dataSnapshot.child("clave_emergencia").getValue();
                estadoPuerta += (String) dataSnapshot.child("puerta_forzada").getValue();

                Log.i("PruebaPrueba", estadoAlarma);
                Log.i("PruebaPrueba", estadoClaveEmergencia);
                Log.i("PruebaPrueba", estadoPuerta);

                if(estadoAlarma.compareTo("0") == 0 || estadoPuerta.compareTo("0") == 0){
                    Log.i("PruebaPrueba", "entro2");
                    finish();
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}*/

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("PruebaPrueba", "entro");

                String estadoAlarma = "";
                String estadoClaveEmergencia = "";
                String estadoPuerta = "";

                estadoAlarma += (String) dataSnapshot.child("alarma_activada").getValue();
                estadoClaveEmergencia += (String) dataSnapshot.child("clave_emergencia").getValue();
                estadoPuerta += (String) dataSnapshot.child("puerta_forzada").getValue();

                Log.i("PruebaPrueba", estadoAlarma);
                Log.i("PruebaPrueba", estadoClaveEmergencia);
                Log.i("PruebaPrueba", estadoPuerta);

                if(origen.compareTo("estadoalarma") == 0){
                    if(estadoAlarma.compareTo("0") == 0) {
                        finish();
                    }
                }
                if(origen.compareTo("estadopuerta") == 0){
                   if( estadoPuerta.compareTo("0") == 0){
                       finish();
                   }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                if(count == 3)
                    handleShakeEvent(count);
            }
        });
    }

    void handleShakeEvent(Integer a){

        //Log.i("TEST",a.toString());
        mServiceIntent.putExtra("Valor", "2");
        ThisActivity.startService(mServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}

