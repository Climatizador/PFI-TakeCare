package com.example.pfi_tesst;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Ctt_ViewHolder extends RecyclerView.ViewHolder {

    ImageView foto_perfil;
    TextView nome, msg;

    public Ctt_ViewHolder(@NonNull View itemView){
        super(itemView);
        foto_perfil = itemView.findViewById(R.id.foto_perfil);
        nome = itemView.findViewById(R.id.nome);
        msg = itemView.findViewById(R.id.msg);
    }
}
