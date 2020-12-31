package com.example.trabalho_pdm.dao;

import com.example.trabalho_pdm.model.Rastreamento;

import java.util.ArrayList;
import java.util.List;

public class RegistroDao {
    private final static List<Rastreamento> rastreamentos = new ArrayList<>();

    public void salva(Rastreamento rastreamento){
        rastreamentos.add(rastreamento);
    }

    public String procurar(int index){
        return rastreamentos.get(index).getLocal();
    }

    public List<Rastreamento> todos() {
        return new ArrayList<>(rastreamentos);
    }
}
