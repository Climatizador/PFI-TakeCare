package com.example.pfi_tesst;

import java.util.ArrayList;
import java.util.HashMap;

public class Vaga_emprego {

    private HashMap<String, HashMap<String, Boolean>> disponibilidade;
    private float preco;

    private String id;
    private String endereco;
    private String nome;
    private ArrayList<String> necessidades_criancas;
    private ArrayList<Integer> idade_criancas;
    private int qtd;
    private Boolean mensal;
    public Vaga_emprego() {
    }

    public HashMap<String, HashMap<String, Boolean>> getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(HashMap<String, HashMap<String, Boolean>> disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPreco() {
        return preco;
    }
    public void setPreco(float preco) {
        this.preco = preco;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<String> getNecessidades_criancas() {
        return necessidades_criancas;
    }

    public void setNecessidades_criancas(ArrayList<String> necessidades_criancas) {
        this.necessidades_criancas = necessidades_criancas;
    }

    public ArrayList<Integer> getIdade_criancas() {
        return idade_criancas;
    }

    public void setIdade_criancas(ArrayList<Integer> idade_criancas) {
        this.idade_criancas = idade_criancas;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public Boolean getMensal() {
        return mensal;
    }

    public void setMensal(Boolean mensal) {
        this.mensal = mensal;
    }
}

