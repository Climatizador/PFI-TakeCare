package com.example.pfi_tesst;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Esqueceu_senha extends AppCompatActivity{

    private EditText email;
    private Button enviar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esqueceu_senha);
        email = findViewById(R.id.codigo);
        enviar = findViewById(R.id.enviar);
        getSupportActionBar().setTitle("Recuperar Senha");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth db_auth = FirebaseAuth.getInstance();

                String email2 = email.getText().toString();

                db_auth.sendPasswordResetEmail(email2)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.i("EMAIL: ","ENVIADO COM SUCESSO");
                                Toast.makeText(getApplicationContext(),"ENVIAMOS UM EMAIL DE VERFICAÇÂO PARA VOCÊ!",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),"Por favor aguarde alguns instantes!",Toast.LENGTH_SHORT).show();
                                abrirTela(MainActivity.class);
                            } else {
                                Log.i("ERRO AO ENVIAR EMAIL: ",".");
                                Toast.makeText(getApplicationContext(),"ERRO AO ENVIAR EMAIL!",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
