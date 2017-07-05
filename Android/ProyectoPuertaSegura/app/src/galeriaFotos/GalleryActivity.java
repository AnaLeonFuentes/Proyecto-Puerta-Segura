package com.sombra.user.puertasegura.galeriaFotos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sombra.user.puertasegura.R;
import com.sombra.user.puertasegura.imagen.ImagenReferencia;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;

    ArrayList<ImageModel> data = new ArrayList<>();
    ArrayList<ImagenReferencia> listaimagen = new ArrayList<>();

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_galeria);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        GridLayoutManager gm = new GridLayoutManager(GalleryActivity.this, 3);

        mRecyclerView.setLayoutManager(gm);
        mRecyclerView.setHasFixedSize(true);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://proyectopuertasegura-ffb85.appspot.com");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("storagereference");

        //para leer los datos en la base de datos todos de una.
        myRef.addListenerForSingleValueEvent(ve);

    }


    ValueEventListener ve = new ValueEventListener() {
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get user value

            listaimagen = new ArrayList<>();

            for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                listaimagen.add(dsp.getValue(ImagenReferencia.class));
            }

            int cont = 0;

            for (ImagenReferencia imagen : listaimagen) {

                cont++;
                ImageModel imageModel = new ImageModel();
                imageModel.setName("Image " + cont);
                imageModel.setUrl(imagen.getPath());
                Log.i("Testing", imagen.getPath());
                data.add(imageModel);

            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mAdapter = new GalleryAdapter(GalleryActivity.this, data);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(GalleryActivity.this,
                    new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {

                            Intent intent = new Intent(GalleryActivity.this, DetailActivity.class);
                            intent.putParcelableArrayListExtra("data", data);
                            intent.putExtra("pos", position);
                            startActivity(intent);

                        }
                    }));
        }
    };

}
