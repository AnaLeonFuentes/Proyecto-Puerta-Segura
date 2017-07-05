package com.sombra.user.puertasegura.imagen;

public class ImagenReferencia {

    private String nombre;
    private String path;

    public ImagenReferencia(String nombre, String path){
        this.nombre = nombre;
        this.path = path;

    }

    public ImagenReferencia(){
        this.nombre = null;
        this.path = null;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNombre() {

        return nombre;
    }

    public String getPath() {
        return path;
    }
}
