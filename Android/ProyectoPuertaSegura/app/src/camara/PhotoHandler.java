package com.sombra.user.puertasegura.camara;

import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sombra.user.puertasegura.imagen.ImagenReferencia;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("deprecation")

public class PhotoHandler implements Camera.PictureCallback {

    private final Context context;
    private DatabaseReference myRef;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) { //lo que sucede cuando se toma la foto, o sea cuando la guarda y la sube al firebase.

        String DEBUG_TAG = "PhotoHandler";
        FirebaseStorage storage;
        StorageReference storageRef;
        FirebaseDatabase database;

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;
        File pictureFile = new File(filename);

        //Guarda la imagen.
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();

        } catch (Exception error) {
            Log.d(DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://proyectopuertasegura-ffb85.appspot.com");
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("storagereference");

        //Para poder subir las imagenes al storage se utiliza el uploadTask.
        Uri file = Uri.fromFile(new File(filename));
        StorageReference ref = storageRef.child("ImagenesPuertaSegura/"+file.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(file);

        //Cuando termina el uploadTask hace esto, o sea sube la informacion de la imagen en la base de datos, para asi poder accederla.
        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myRef.push().setValue(new ImagenReferencia( taskSnapshot.getMetadata().getName(),taskSnapshot.getDownloadUrl().toString()));
            }
        });

    }


    //Direccion en el celular donde se guarda la imagen. Necesario para tener algo que despues poder subir al storage.
    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "ImagenesPuertaSegura");
    }
}
