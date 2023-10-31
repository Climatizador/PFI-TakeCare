package com.example.pfi_tesst;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Cuidador_criancas extends Usuario implements Serializable {

    private float preco_diario, preco_mensal;
    private HashMap<String, HashMap<String, Boolean>> filtro;
    private List<Contratante> amigos;
    private HashMap<String,HashMap<String, Boolean>> disponibilidade; // chave do período (manha,tarde, noite) dps o dia da semana e se esta disponível(true)


    public Cuidador_criancas() {

    }

    public HashMap<String, HashMap<String, Boolean>> getFiltro() {
        return filtro;
    }

    public void setFiltro(HashMap<String, HashMap<String, Boolean>> filtro) {
        this.filtro = filtro;
    }
    public float getPreco_diario() {
        return preco_diario;
    }

    public void setPreco_diario(float preco_diario) {
        this.preco_diario = preco_diario;
    }

    public float getPreco_mensal() {
        return preco_mensal;
    }

    public void setPreco_mensal(float preco_mensal) {
        this.preco_mensal = preco_mensal;
    }

    public HashMap<String, HashMap<String, Boolean>> getDisponibilidade() {
        return disponibilidade;
    }

    public void setDispponibilidade(HashMap<String, HashMap<String, Boolean>> disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public List<Contratante> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Contratante> amigos) {
        this.amigos = amigos;
    }
}
