package com.example.pfi_tesst;

public class Item {

    float diario;
    float Mensal;
    String cidade;
    String image;
    int report2;
    int ver_perfil;

    public Item(float diario, float mensal, String cidade, String image, int report2, int ver_perfil) {
        this.diario = diario;
        Mensal = mensal;
        this.cidade = cidade;
        this.image = image;
        this.report2 = report2;
        this.ver_perfil = ver_perfil;
    }


    public int getReport2() {
        return report2;
    }

    public void setReport2(int report2) {
        this.report2 = report2;
    }

    public int getVer_perfil() {
        return ver_perfil;
    }

    public void setVer_perfil(int ver_perfil) {
        this.ver_perfil = ver_perfil;
    }

    public float getDiario() {
        return diario;
    }

    public void setDiario(float diario) {
        this.diario = diario;
    }

    public float getMensal() {
        return Mensal;
    }

    public void setMensal(float mensal) {
        Mensal = mensal;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
