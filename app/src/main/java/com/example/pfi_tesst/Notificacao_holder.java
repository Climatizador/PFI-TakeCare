package com.example.pfi_tesst;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Notificacao_holder extends RecyclerView.ViewHolder{

    ImageView foto_perfil;

    TextView nome, mensagem;

    Button aceitar, recusar;

    public Notificacao_holder(@NonNull View itemView) {
        super(itemView);
        foto_perfil = itemView.findViewById(R.id.foto_perfil);
        nome=itemView.findViewById(R.id.nome);
        mensagem = itemView.findViewById(R.id.msg);
        aceitar = itemView.findViewById(R.id.aceitar);
        recusar = itemView.findViewById(R.id.recusar);
    }
}
