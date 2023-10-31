package com.example.pfi_tesst;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder_chat extends RecyclerView.ViewHolder {

    ImageView foto_perfil;
    TextView mensagem;


    public Holder_chat(@NonNull View itemView) {
        super(itemView);
        foto_perfil = itemView.findViewById(R.id.foto_perfil);
        mensagem = itemView.findViewById(R.id.mensagem);
    }
}
