package com.example.pfi_tesst;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView image, report2;
    TextView diario, mensal, cidade;

    Button ver_perfil;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.foto_perfil);
        diario = itemView.findViewById(R.id.salario);
        mensal = itemView.findViewById(R.id.dias);
        cidade = itemView.findViewById(R.id.endereço);
        report2 = itemView.findViewById(R.id.report2);
        ver_perfil = itemView.findViewById(R.id.ver_detalhes);
    }
}
