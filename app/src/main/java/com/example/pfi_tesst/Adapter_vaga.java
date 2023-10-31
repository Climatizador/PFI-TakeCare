package com.example.pfi_tesst;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Adapter_vaga extends RecyclerView.Adapter<Holder_vaga>{
    Context context;
    List<Vaga_emprego> vagas;

    public Adapter_vaga(Context context, List<Vaga_emprego> vagas) {
        this.context = context;
        this.vagas = vagas;
    }

    @NonNull
    @Override
    public Holder_vaga onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder_vaga(LayoutInflater.from(context).inflate(R.layout.item_lista_vaga,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder_vaga holder, int position) {
        Vaga_emprego vaga = vagas.get(position);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Contratantes").document(vaga.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                Picasso.get().load(Uri.parse(contratante.getFoto_perfil())).into(holder.image);
            }
        });
        holder.dias.setText("Horas semanais: "+String.valueOf(calc_horas(vaga.getDisponibilidade())));
        holder.salario.setText("Salário: R$ "+String.valueOf(vaga.getPreco()));
        if (vaga.getMensal()) holder.tipo_contratacao.setText("Tipo da contratação: Mensal");
        else holder.tipo_contratacao.setText("Tipo da contratação: Único");
        holder.endereco.setText(vaga.getEndereco());
        holder.report2.setImageResource(R.drawable.report2);
        holder.report2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap<String,String> denunciador = new HashMap<>();
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Cuidador_criancas cuidador_criancas = documentSnapshot.toObject(Cuidador_criancas.class);
                        denunciador.put("nome_denunciador",cuidador_criancas.getNome());
                        db.collection("Denuncias").document(vaga.getNome()).set(denunciador);
                        Toast.makeText(context,"Usuário Reportado com sucesso!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.ver_detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Perfil_vaga.class);
                intent.putExtra("id", vaga.getId());
                intent.putExtra("position",position);
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }


    private int calc_horas(HashMap<String, HashMap<String, Boolean>> dias){
        String[] titulos = new String[]{"Manhã", "Noite", "Tarde"};
        int hrs =0;
        for (String titulo: titulos){
            HashMap<String,Boolean> semana = dias.get(titulo);
            for (Boolean periodo: semana.values()){
                hrs+=4;
            }
        }
        return hrs;
    }



    @Override
    public int getItemCount() {
        return vagas.size();
    }
}
