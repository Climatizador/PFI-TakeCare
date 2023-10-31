package com.example.pfi_tesst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
public class MyAdpater extends RecyclerView.Adapter<MyViewHolder> {
    private static final int REQUEST_CODE = 1;
    Context context;
    List<Item> items;
    List<Cuidador_criancas> cuidadores;
    int report;

    List<String> ids;
    public MyAdpater(Context context, List<Cuidador_criancas> cuidadores, int report,List<String> ids) {
        this.context = context;this.cuidadores = cuidadores;
        this.report = report; this.ids = ids;
        Log.i("Cuidadores: ",cuidadores.toString());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lista,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cuidador_criancas cuidador = cuidadores.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("Cuidador: ",cuidador.toString());
        holder.diario.setText("Diário: R$"+ Float.toString(cuidador.getPreco_diario()));
        holder.mensal.setText("Mensal: R$"+Float.toString(cuidador.getPreco_mensal()));
        holder.cidade.setText(cuidador.getEndereco());
        Log.i("CIDADE: ",holder.cidade.toString());

        Picasso.get().load(cuidador.getFoto_perfil()).into(holder.image);

        holder.report2.setImageResource(report);
        holder.report2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap<String,String> denunciador = new HashMap<>();
                db.collection("Contratantes").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Contratante contratante = documentSnapshot.toObject(Contratante.class);
                        denunciador.put("id_denunciador",contratante.getNome());
                        db.collection("Denuncias").document(ids.get(position)).set(denunciador);
                        Toast.makeText(context,"Usuário Reportado com sucesso!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.ver_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Perfil_cuidador.class);
                intent.putExtra("id", ids.get(position));
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }


    @Override
    public int getItemCount(){
        return cuidadores.size();
    }


}
