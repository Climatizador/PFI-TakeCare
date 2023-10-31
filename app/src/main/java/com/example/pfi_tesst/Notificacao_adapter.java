package com.example.pfi_tesst;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Notificacao_adapter extends RecyclerView.Adapter<Notificacao_holder> {

    String user_id;
    List<String> nomes;
    List<String> fts;
    List<String> ids;
    Context context;

    public Notificacao_adapter(String user_id, List<String> nomes, Context context, List<String> fts, List<String> ids) {
        this.user_id = user_id;
        this.nomes = nomes;
        this.context = context;
        this.fts = fts;
        this.ids = ids;

        Log.i("USER_ID: ",user_id);
        Log.i("NOMES: ",nomes.toString());
        Log.i("IDS: ",ids.toString());
    }

    @NonNull
    @Override
    public Notificacao_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Notificacao_holder(LayoutInflater.from(context).inflate(R.layout.item_notificacao, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Notificacao_holder holder, int position) {
        holder.nome.setText(nomes.get(position));
        holder.mensagem.setText("Solicitou contrato com você!");
        Picasso.get().load(fts.get(position)).into(holder.foto_perfil);

        holder.recusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Notificacoes").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("NOTIFCACAO: ","EXCLUIDA COM SUCESSO");
                        }
                    }
                });
                nomes.remove(position);
                fts.remove(position);
                ids.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.aceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            DocumentReference documentRef1 = db.collection("Cuidadores").document(user_id);
                            DocumentReference documentRef2 = db.collection("Contratantes").document(ids.get(position));

                            documentRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Cuidador_criancas cuidador_criancas = documentSnapshot.toObject(Cuidador_criancas.class);
                                    List<Contratante> amigos;
                                    if (cuidador_criancas.getAmigos()!=null){
                                        amigos = cuidador_criancas.getAmigos();
                                    }else {
                                        amigos = new ArrayList<>();
                                    }

                                    documentRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Contratante contratante = documentSnapshot.toObject(Contratante.class);
                                            if (!amigos.contains(contratante))amigos.add(contratante);
                                            documentRef1.update("amigos",amigos);

                                            List<Cuidador_criancas> amigos2;

                                            if (contratante.getAmigos()!=null) amigos2 = contratante.getAmigos();else amigos2 = new ArrayList<>();

                                            if (!amigos2.contains(cuidador_criancas))amigos2.add(cuidador_criancas);
                                            Log.i("amigos2 id: ",amigos2.get(0).getId());

                                            documentRef2.update("amigos",amigos2);
                                        }
                                    });
                                }
                            });

                           db.collection("Notificacoes").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Log.i("NOTIFCACAO: ","EXCLUIDA COM SUCESSO");
                                   }
                               }
                           });
                            Log.i("ID DO DELTE: ",user_id);
                            nomes.remove(position);
                            fts.remove(position);
                            ids.remove(position);
                            notifyItemRemoved(position);
                        }else{
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            DocumentReference documentRef1 = db.collection("Contratantes").document(user_id);
                            DocumentReference documentRef2 = db.collection("Cuidadores").document(ids.get(position));

                            documentRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Contratante contratante = documentSnapshot.toObject(Contratante.class);
                                    List<Cuidador_criancas> amigos;
                                    if (contratante.getAmigos()!=null){
                                        amigos = contratante.getAmigos();
                                    }else {
                                        amigos = new ArrayList<>();
                                    }

                                    documentRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Cuidador_criancas cuidador_criancas = documentSnapshot.toObject(Cuidador_criancas.class);
                                            if (!amigos.contains(cuidador_criancas))amigos.add(cuidador_criancas);
                                            documentRef1.update("amigos",amigos);

                                            List<Contratante> amigos2;

                                            if (cuidador_criancas.getAmigos()!=null) amigos2 = cuidador_criancas.getAmigos();else amigos2 = new ArrayList<>();

                                            if (!amigos2.contains(contratante))amigos2.add(contratante);
                                            Log.i("amigos2 id: ",amigos2.get(0).getId());

                                            documentRef2.update("amigos",amigos2);
                                        }
                                    });
                                }
                            });

                            db.collection("Notificacoes").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.i("NOTIFCACAO: ","EXCLUIDA COM SUCESSO");
                                    }
                                }
                            });
                            Log.i("ID DO DELTE: ",user_id);
                            nomes.remove(position);
                            fts.remove(position);
                            ids.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

}
