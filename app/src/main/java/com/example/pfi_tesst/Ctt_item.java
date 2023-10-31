package com.example.pfi_tesst;

public class Ctt_item {

    private Contratante contratante;
    private Cuidador_criancas cuidador;
    private int tipo_user;

    public Ctt_item(Contratante contratante, Cuidador_criancas cuidador, int tipo_user) {
        this.contratante = contratante;
        this.cuidador = cuidador;
        this.tipo_user = tipo_user;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public void setContratante(Contratante contratante) {
        this.contratante = contratante;
    }

    public Cuidador_criancas getCuidador() {
        return cuidador;
    }

    public void setCuidador(Cuidador_criancas cuidador) {
        this.cuidador = cuidador;
    }

    public int getTipo_user() {
        return tipo_user;
    }

    public void setTipo_user(int tipo_user) {
        this.tipo_user = tipo_user;
    }
}
