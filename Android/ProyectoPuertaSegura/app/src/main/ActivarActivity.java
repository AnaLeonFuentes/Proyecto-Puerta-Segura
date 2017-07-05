package com.sombra.user.puertasegura.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sombra.user.puertasegura.R;

public class ActivarActivity extends AppCompatActivity {

    TextView codigoTexto;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activar);
        contexto = getApplicationContext();

        codigoTexto = (TextView) findViewById(R.id.codigoTexto);
        codigoTexto.requestFocus();

        //Activa el teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        codigoTexto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) { //Cuando se apreta el DONE en el teclado.
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
    }

    void sendMessage() {

        //Oculta el teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(codigoTexto.getWindowToken(), 0);

        Log.i("Test", "" + codigoTexto.getText());
        final String claveIngresada = "" + codigoTexto.getText();


        //Obtiene la database y los sharedpreference
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("clave_inicial");
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtiene la clave a verificar.
                        String clave = dataSnapshot.getValue(String.class);

                        // Verifica la clave y guarda los flags correspondientes.
                        if (clave != null) {
                            if (clave.compareTo(claveIngresada) == 0) {
                                    editor.putBoolean("Autorizado", true);
                                    editor.putBoolean("Huella",false);
                                    editor.apply();
                                    Toast toast = Toast.makeText(contexto,"Clave correcta",Toast.LENGTH_LONG);
                                    toast.show();

                                    //Abre la activity para registrar usuario y contraseña.
                                    Intent intent = new Intent(contexto, RegistrarActivity.class);
                                    intent.putExtra("comportamiento", true);
                                    startActivity(intent);
                                }
                            else{
                                codigoTexto.setText("");
                                Toast toast = Toast.makeText(contexto,"Clave incorrecta",Toast.LENGTH_LONG);
                                toast.show();

                        }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
}

