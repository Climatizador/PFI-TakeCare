package com.example.pfi_tesst;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
public class Contratante extends Usuario implements Serializable {
    private Vaga_emprego vaga_emprego;
    private HashMap<String, HashMap<String, Boolean>> filtro;
    private List<Cuidador_criancas> amigos;

    public Contratante() {
    }

    public HashMap<String, HashMap<String, Boolean>> getFiltro() {
        return filtro;
    }

    public void setFiltro(HashMap<String, HashMap<String, Boolean>> filtro) {
        this.filtro = filtro;
    }

    public Vaga_emprego getVaga_emprego() {
        return vaga_emprego;
    }

    public void setVaga_emprego(Vaga_emprego vaga_emprego) {
        this.vaga_emprego = vaga_emprego;
    }

    public List<Cuidador_criancas> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Cuidador_criancas> amigos) {
        this.amigos = amigos;
    }
}
