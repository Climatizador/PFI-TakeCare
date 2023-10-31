package com.example.pfi_tesst;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Editar_perfil_1 extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText nome_user, email, senha;
    private Button concluir, avancar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_1);
        nome_user = findViewById(R.id.nome_user);
        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        concluir = findViewById(R.id.concluir);
        avancar = findViewById(R.id.avancar);
        db = FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recuperar_dados();

        concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                try {
                    db.collection("Contratante").document(user_id).update("nome", nome_user.getText().toString(),"email",email.getText().toString(),"senha",senha.getText().toString());
                    abrirTela(Home_empregador.class);
                }catch (Exception e){
                    db.collection("Cuidadores").document(user_id).update("nome", nome_user.getText().toString(),"email",email.getText().toString(),"senha",senha.getText().toString());
                    /*abrir_tela(Cuidador.class)*/
                }

            }
        });

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String user_id = auth.getCurrentUser().getUid();
                DocumentReference docRef = db.collection("Contratantes").document(user_id);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Intent intent = new Intent(getApplicationContext(),Editar_perfil_2.class);
                            intent.putExtra("nome", nome_user.getText().toString());
                            intent.putExtra("email",email.getText().toString());
                            intent.putExtra("senha", senha.getText().toString());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(),Editar_perfil_2_cuidador.class);
                            intent.putExtra("nome", nome_user.getText().toString());
                            intent.putExtra("email",email.getText().toString());
                            intent.putExtra("senha", senha.getText().toString());
                            startActivity(intent);
                        }
                    }
                });

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void recuperar_dados() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("Contratantes").document(user_id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Contratante contratante = documentSnapshot.toObject(Contratante.class);

                    nome_user.setText(contratante.getNome());
                    email.setText(contratante.getEmail());
                    senha.setText(contratante.getSenha());


                } else {
                    db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);

                            nome_user.setText(cuidador.getNome());
                            email.setText(cuidador.getEmail());
                            senha.setText(cuidador.getSenha());
                        }
                    });
                }
            }
        });
    }

    private void abrirTela(Class<?> tela){
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }
}