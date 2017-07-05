package com.sombra.user.puertasegura.bluetooth;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sombra.user.puertasegura.historial.Elemento;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// enviar = x
// enviar = 2

public class RSSPullService extends IntentService {

    boolean salida = true;
    public RSSPullService() {
        super("RSSPullService");
    }
    private static final String TAG = "EnviarMensaje";
    private ArrayList<BluetoothDevice> listaConectados;

    private BluetoothAdapter mBtAdapter;
    private Handler bluetoothIn;
    private final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ArrayList<String> mPairedDevicesArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;
    Context contexto;

    FirebaseDatabase database;
    private DatabaseReference myRef;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onHandleIntent(Intent workIntent) {

        Bundle bundle = workIntent.getExtras();
        String enviar= bundle.getString("Valor");
        contexto = this;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("historial");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        listaConectados = new ArrayList<>();

        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        checkBTState();

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayList<>();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBtAdapter.getBondedDevices();  // Get a set of currently paired devices and append to 'pairedDevices'
        btSocket = null;
        BluetoothDevice device = null;

        IntentFilter filter = new IntentFilter();

        //Necesario para tener un filter que haga discovery y obtenga los dispositivos cercanos.

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);


        while(device == null && salida) {
            mBtAdapter.startDiscovery(); //obtener dispositivos cercanos

            ArrayList<BluetoothDevice> test = new ArrayList<>();

           if(pairedDevices.size() > 0){
            if(listaConectados.size() > 0){
                test.addAll(listaConectados);
                test.addAll(pairedDevices);

               listaConectados.retainAll(pairedDevices); //se filtran dejando solo aquellos que tambien estan emparejados.

                /*Set<BluetoothDevice> Unique_set = new HashSet<BluetoothDevice>(test); //utiliza unique_set para eliminar los repetidos.

                for (BluetoothDevice elemento : Unique_set) {
                if (elemento.getAddress().compareTo("20:17:02:15:23:53") == 0) { //obtiene si esta emparejado a la puerta y tambien esta conectado
                            device = mBtAdapter.getRemoteDevice(elemento.getAddress());
                            break;
                        }*/

             for(BluetoothDevice elemento1: listaConectados){
                 if (elemento1.getAddress().compareTo("20:17:02:15:23:53") == 0) { //obtiene si esta emparejado a la puerta y tambien esta conectado
                     device = mBtAdapter.getRemoteDevice(elemento1.getAddress());
                     break;
                 }
            }

            mPairedDevicesArrayAdapter.clear();
            listaConectados.clear();
        }}
        }

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
        }
        try
        {
            //conecta al bluetooth.
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {

            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write(enviar);

        //espera un rato para evitar problemas.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try
        {
            mConnectedThread.close();
            btSocket.close();
        } catch (IOException e2) {
            //Hacer algo
        }


        //Guardar en el historial si es que se envio abrir puerta.
        if(enviar.compareTo("x")== 0) {

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            String nombre = settings.getString("nombreUsuario", "DEFAULT");

            Elemento elemento = new Elemento(nombre,date);
            myRef.push().setValue(elemento);
        }


        // Do work here, based on the contents of dataString
        // Puts the status into the Intent
        String status = "..."; // any data that you want to send back to receivers
        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION)
                        .putExtra(Constants.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unregisterReceiver(mReceiver);
        if(mConnectedThread != null) {
            try {
                mConnectedThread.close();
                btSocket.close();
            } catch (IOException e3) {

            }
            salida = false;
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    //revisa si hay blueooth y si esta encendido.
    private void checkBTState() {
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null) {
            Toast.makeText(contexto, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
            }
        }
    }

    //filtro para el discovery y obtener los dispositivos conectados.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listaConectados.add(device);
            }
        }
    };


    //Se usa para escribir y mandar por bluetooth, tambien podria servir para recibir.
    private class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
        }
        //write method

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //finish();
            }

        }

        public void close(){
            if (mmInStream!= null) {
                try {mmInStream.close();} catch (Exception e) {}
                mmInStream = null;
            }

            if (mmOutStream != null) {
                try {mmOutStream.close();} catch (Exception e) {}
                mmOutStream = null;
            }
        }
    }
}