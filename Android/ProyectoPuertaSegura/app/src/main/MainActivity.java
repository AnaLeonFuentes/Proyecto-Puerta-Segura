package com.sombra.user.puertasegura.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sombra.user.puertasegura.R;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean huella = settings.getBoolean("Huella", false);
        boolean autorizado = settings.getBoolean("Autorizado", false);
        boolean registrado = settings.getBoolean("Registrado", false);

        //Selecciona en base a las flags por que activity va a empezar segun lo configurado.

        if(!autorizado) {
            Intent intent2 = new Intent(this, ActivarActivity.class);
            startActivity(intent2);
        }
        else {
            if(!registrado)
            {
                Intent intent2 = new Intent(this,RegistrarActivity.class);
                intent2.putExtra("comportamiento", true);
                startActivity(intent2);

            }
            else {
                if (!huella) {
                    Intent intent = new Intent(this, KeyPadActivity.class);
                    startActivity(intent);
                }
                if (huella) {
                    Intent intent = new Intent(this, FingerprintActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
}
