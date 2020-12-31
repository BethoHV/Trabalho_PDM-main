package com.example.trabalho_pdm.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rastreamento {
    private int usuario_id;
    private String local;
    private String dataRegistro;

    public Rastreamento(int usuario_id, String local, String dataRegistro) {
        this.usuario_id = usuario_id;
        this.local = local;
        this.dataRegistro = dataRegistro;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDataRegistro() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date data = new Date();
        return dateFormat.format(data);
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @NonNull
    @Override
    public String toString() {
        return (dataRegistro + "          " + local);
    }
}
