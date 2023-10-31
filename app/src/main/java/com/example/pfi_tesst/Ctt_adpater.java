package com.example.pfi_tesst;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Ctt_adpater extends RecyclerView.Adapter<Ctt_ViewHolder> {

    List<Cuidador_criancas> cuidadores;
    List<Contratante> contratantes;
    Context context;

    int type;

    public Ctt_adpater(List<Cuidador_criancas> cuidadores, List<Contratante> contratantes, Context context,int type) {
        this.cuidadores = cuidadores;
        this.contratantes = contratantes;
        this.context = context; this.type = type;
    }


    @NonNull
    @Override
    public Ctt_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Ctt_ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lista_ctt,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Ctt_ViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (type==0){
            Contratante contratante = contratantes.get(position);
            holder.nome.setText(contratante.getNome());
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("conversas").document(user_id).collection(contratante.getId()).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()){
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Message message =doc.toObject(Message.class);
                        holder.msg.setText(message.getText());
                    }
                }
            });
            Picasso.get().load(Uri.parse(contratante.getFoto_perfil())).into(holder.foto_perfil);
        }else{
            Cuidador_criancas cuidador = cuidadores.get(position);
            holder.nome.setText(cuidador.getNome());
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("conversas").document(user_id).collection(cuidador.getId()).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()){
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Message message =doc.toObject(Message.class);
                        holder.msg.setText(message.getText());
                    }
                }
            });
            Picasso.get().load(Uri.parse(cuidador.getFoto_perfil())).into(holder.foto_perfil);
        }

    }

    @Override
    public int getItemCount() {
        if (type==0){
            if (contratantes!=null)return contratantes.size();
        }else{
            if (cuidadores!=null)return cuidadores.size();
        }
        return 0;
    }
}
