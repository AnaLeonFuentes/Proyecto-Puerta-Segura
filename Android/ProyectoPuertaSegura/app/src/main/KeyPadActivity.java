package com.sombra.user.puertasegura.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sombra.user.puertasegura.camara.PhotoHandler;
import com.sombra.user.puertasegura.R;

@SuppressWarnings("deprecation")

public class KeyPadActivity extends AppCompatActivity {

    int contador;
    TextView contraseña;
    String clave = "1234";
    String ingresado;
    String validacion = "Password";
    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;
    Button boton6;
    Button boton7;
    Button boton8;
    Button boton9;
    Toast toast5;

    TextView enter;
    TextView atras;
    ImageView imagen1;
    ImageView imagen2;
    ImageView imagen3;

    private Camera camera;
    private int cameraId = 0;
    String DEBUG_TAG = "KeyPadCamara";
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contador = 0;
        ingresado = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keypad);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        clave = settings.getString("contraseña", "1234");

        contraseña = (TextView)findViewById(R.id.contraseña);
        boton1 = (Button)findViewById(R.id.boton1);
        boton2 = (Button)findViewById(R.id.boton2);
        boton3 = (Button)findViewById(R.id.boton3);
        boton4 = (Button)findViewById(R.id.boton4);
        boton5 = (Button)findViewById(R.id.boton5);
        boton6 = (Button)findViewById(R.id.boton6);
        boton7 = (Button)findViewById(R.id.boton7);
        boton8 = (Button)findViewById(R.id.boton8);
        boton9 = (Button)findViewById(R.id.boton9);
        enter = (TextView)findViewById(R.id.enter);
        atras = (TextView)findViewById(R.id.atras);
        imagen1 = (ImageView)findViewById(R.id.imagen1);
        imagen2 = (ImageView)findViewById(R.id.imagen2);
        imagen3 = (ImageView)findViewById(R.id.imagen3);
        Toast toast5;


        //Si tiene camara y si tiene camara frontal
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId); //crea la camara
            }
        }


    }

    //obtiene el id de la camara frontal
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.boton1: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("1");
                    ingresado+="1";
                }
                else{
                    ingresado+="1";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case R.id.boton2: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("2");
                    ingresado+="2";
                }
                else{
                    ingresado+="2";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case  R.id.boton3: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("3");
                    ingresado+="3";
                }
                else{
                    ingresado+="3";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case R.id.boton4: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("4");
                    ingresado+="4";
                }
                else{
                    ingresado+="4";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }
            case  R.id.boton5: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("5");
                    ingresado+="5";
                }
                else{
                    ingresado+="5";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case R.id.boton6: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("6");
                    ingresado+="6";
                }
                else{
                    ingresado+="6";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case  R.id.boton7: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("7");
                    ingresado+="7";
                }
                else{
                    ingresado+="7";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case R.id.boton8: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("8");
                    ingresado+="8";
                }
                else{
                    ingresado+="8";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }
            case  R.id.boton9: {
                String prueba = (String)contraseña.getText();
                if(prueba.compareTo(validacion) == 0){
                    contraseña.setText("9");
                    ingresado+="9";
                }
                else{
                    ingresado+="9";
                    contraseña.setText(ingresado);
                }
                Log.i("Prueba",ingresado);
                break;
            }

            case R.id.enter: {
                String prueba = (String) contraseña.getText();
                if(prueba.compareTo(clave) == 0){
                    Log.i("Prueba", "clave correcta");
                    contador = 0;
                    //Intent intent = new Intent(this, FingerprintActivity.class);
                    Intent intent = new Intent(this, BluetoothActivity.class);
                    startActivity(intent);

                }
                else{
                    Log.i("Prueba","clave incorrecta");
                    contador++;
                    contraseña.setText("");
                    ingresado = "";
                    if(contador == 1)
                        imagen1.setImageResource(R.drawable.botonrojo);
                    if(contador == 2)
                        imagen2.setImageResource(R.drawable.botonrojo);
                    if(contador == 3){
                        imagen3.setImageResource(R.drawable.botonrojo);

                        //crea un preview vacio, si no la camara no funciona.
                        camera.startPreview();

                        //toma la foto utilizando un photoHandler para el manejo de la misma.
                        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));

                        Handler handler = new Handler();

                        boton1.setEnabled(false);
                        boton2.setEnabled(false);
                        boton3.setEnabled(false);
                        boton4.setEnabled(false);
                        boton5.setEnabled(false);
                        boton6.setEnabled(false);
                        boton7.setEnabled(false);
                        boton8.setEnabled(false);
                        boton9.setEnabled(false);
                        enter.setEnabled(false);
                        atras.setEnabled(false);

                        Toast toast = Toast.makeText(this,"Espere 10 segundos para volver a intentar",Toast.LENGTH_SHORT);
                        Toast toast1 = Toast.makeText(this,"Espere 8 segundos para volver a intentar",Toast.LENGTH_SHORT);
                        Toast toast2 = Toast.makeText(this,"Espere 6 segundos para volver a intentar",Toast.LENGTH_SHORT);
                        Toast toast3 = Toast.makeText(this,"Espere 4 segundos para volver a intentar",Toast.LENGTH_SHORT);
                        Toast toast4 = Toast.makeText(this,"Espere 2 segundos para volver a intentar",Toast.LENGTH_SHORT);

                        toast.show();
                        toast1.show();
                        toast2.show();
                        toast3.show();
                        toast4.show();

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                imagen1.setImageResource(R.drawable.botonblanco);
                                imagen2.setImageResource(R.drawable.botonblanco);
                                imagen3.setImageResource(R.drawable.botonblanco);
                                contador = 0;
                                boton1.setEnabled(true);
                                boton2.setEnabled(true);
                                boton3.setEnabled(true);
                                boton4.setEnabled(true);
                                boton5.setEnabled(true);
                                boton6.setEnabled(true);
                                boton7.setEnabled(true);
                                boton8.setEnabled(true);
                                boton9.setEnabled(true);
                                enter.setEnabled(true);
                                atras.setEnabled(true);
                            }
                        }, 10000);
                    }
                }
                break;
            }

            case R.id.atras: {
                String prueba = (String) contraseña.getText();

                if(prueba.compareTo(validacion) != 0){
                    Log.i("Test", "entra");

                    if(prueba.compareTo("") != 0)
                        prueba = prueba.substring(0, prueba.length() - 1);
                    if(ingresado.compareTo("") != 0)
                        ingresado = ingresado.substring(0, ingresado.length() - 1);
                    contraseña.setText(prueba);
                }
                break;
            }


        }
    }
}
