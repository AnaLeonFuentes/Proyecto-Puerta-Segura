package com.sombra.user.puertasegura.historial;

public class Elemento {
    String usuario;
    String tiempo;

    public Elemento(){
        usuario = null;
        tiempo = null;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public Elemento(String usuario, String tiempo){
        this.usuario = usuario;
        this.tiempo = tiempo;
    }
}
