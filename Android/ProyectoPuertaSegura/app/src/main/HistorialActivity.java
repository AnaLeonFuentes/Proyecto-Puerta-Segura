package com.sombra.user.puertasegura.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sombra.user.puertasegura.R;
import com.sombra.user.puertasegura.historial.Elemento;

public class HistorialActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        FirebaseDatabase Ref = FirebaseDatabase.getInstance();
        DatabaseReference mRef = Ref.getReference("historial");

        ListView historial = (ListView) findViewById(R.id.historialView);

        FirebaseListAdapter mAdapter = new FirebaseListAdapter<Elemento>(this, Elemento.class, R.layout.historial_parte, mRef) {
            @Override
            protected void populateView(View view, Elemento myObj, int position) {
                //Set the value for the views
                ((TextView)view.findViewById(R.id.prueba1)).setText(myObj.getUsuario());
                ((TextView)view.findViewById(R.id.prueba2)).setText(myObj.getTiempo());
            }
        };

        historial.setAdapter(mAdapter);

    }
}
