package com.example.pfi_tesst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private EditText email, senha;
    private TextView esqueceu_senha, not_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.enviar);
        email = findViewById(R.id.codigo);
        senha = findViewById(R.id.login_senha);
        esqueceu_senha = findViewById(R.id.esqueceu_senha);
        not_account = findViewById(R.id.text_cadastro);
        getSupportActionBar().setTitle("Take Care");

        not_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Cadastro.class);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vec_dados()) autenticarUser();
            }
        });

        esqueceu_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Esqueceu_senha.class);
            }
        });
    }

    private void autenticarUser() {
        Log.i("ENTROU NO AUTENTICAR USER: ","YEP");
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Log.i("CURRENT USER: ",user_id);
                    getUserId(user_id,"Contratantes");
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        erro = "Erro ao logar usuário";
                    }
                    Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserId(String id, String collection) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(collection).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    abrirTela(Home_empregador.class);
                } else {
                    abrirTela(Home_cuidador.class);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user_atual = FirebaseAuth.getInstance().getCurrentUser();

        if (user_atual != null){
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getUserId(user_id,"Contratantes");
        }
    }


    /*public interface MyCallback {
        void onCallback(String type);
    }*/
    private boolean vec_dados() {
        //vec email valido
        if (email.getText().toString().trim().equals("") || senha.getText().toString().trim().equals(""))
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        else {

            return true;
        }
        return false;
    }

    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }
}