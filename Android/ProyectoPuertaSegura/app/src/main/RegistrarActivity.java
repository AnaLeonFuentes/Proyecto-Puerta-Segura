package com.sombra.user.puertasegura.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sombra.user.puertasegura.R;

public class RegistrarActivity extends AppCompatActivity{
    EditText nombreUsuario;
    EditText contraseña;
    Button botonAceptar;
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    Context contexto;
    Boolean comportamiento;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //Si la activity es para editar usuario o contraseña o es el primer ingreso.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            comportamiento = bundle.getBoolean("comportamiento");
        }

        contexto = this;
        nombreUsuario = (EditText)findViewById(R.id.nombreUsuario);
        contraseña = (EditText)findViewById(R.id.contraseña);
        botonAceptar = (Button)findViewById(R.id.botonAceptar);

        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //se ingreso usuario/contraseña y despues se apreta el boton y verifica.
        botonAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                   Editable edit  = nombreUsuario.getText();
                   Editable edit2 = contraseña.getText();
                   String nombre = "";
                   nombre = nombre +  edit.toString();
                   String clave = "";
                   clave = clave + edit2.toString();

                if(comportamiento) {

                    if (!nombre.equals("")&& !clave.equals("")) {
                        editor.putString("nombreUsuario", nombre);
                        editor.putString("contraseña", clave);
                        editor.putBoolean("Registrado", true);
                        editor.apply();

                        //Al ser la primera entrada y ser todos validos entra a la activity principal.

                        Intent intent = new Intent(contexto,BluetoothActivity.class);
                        startActivity(intent);

                    } else {
                        Toast toast = Toast.makeText(contexto, "No ingreso nombre de usuario o contraseña", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }

                if(!comportamiento){
                    if(!nombre.equals("")){
                        editor.putString("nombreUsuario", nombre);
                        editor.apply();
                        nombreUsuario.setText("");
                        Toast toast = Toast.makeText(contexto, "Nombre de usuario modificado", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    if(!clave.equals("")){
                        editor.putString("contraseña", clave);
                        editor.apply();
                        contraseña.setText("");
                        Toast toast = Toast.makeText(contexto, "Contraseña modificada", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
            }
        });

    }
}
