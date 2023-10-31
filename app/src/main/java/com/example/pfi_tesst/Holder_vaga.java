package com.example.pfi_tesst;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder_vaga extends RecyclerView.ViewHolder {
    ImageView image, report2;
    TextView salario, dias, endereco,tipo_contratacao;

    Button ver_detalhes;
    public Holder_vaga(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.foto_perfil);
        report2 = itemView.findViewById(R.id.report2);
        salario = itemView.findViewById(R.id.salario);
        dias = itemView.findViewById(R.id.dias);
        endereco = itemView.findViewById(R.id.endereço);
        ver_detalhes = itemView.findViewById(R.id.ver_detalhes);
        tipo_contratacao = itemView.findViewById(R.id.tipo_contratacao);
    }
}
