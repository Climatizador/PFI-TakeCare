package com.example.pfi_tesst;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adpater_chat extends RecyclerView.Adapter<Holder_chat>{
    private List<Message> messages;

    private Context context;

    public Adpater_chat(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder_chat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new Holder_chat(LayoutInflater.from(context).inflate(R.layout.item_to_message, parent, false));
        } else {
            return new Holder_chat(LayoutInflater.from(context).inflate(R.layout.item_from_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder_chat holder, int position) {
        holder.mensagem.setText(messages.get(position).getText());
        Picasso.get().load(messages.get(position).getFt()).into(holder.foto_perfil);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getFromId().equals(FirebaseAuth.getInstance().getUid())) {
            return 0;
        } else {
            return 1;
        }
    }

}
