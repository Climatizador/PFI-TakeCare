package com.example.pfi_tesst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cadastro_3 extends AppCompatActivity {
    private TextView text_titulo;
    private EditText preco_diario, preco_mensal;

    private Intent intent2;
    private Bundle extras;

    private Bundle cadastro_1;
    private Button btn_cadastrar;

    private RadioGroup periodos;

    /*private CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5 ,checkbox6, checkbox7;*/

    private CheckBox[] list_dias;

    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();

    private ImageView seta_direita, seta_esquerda;


    private int cont_image;

    private String[] titulos;

    private Set<Integer> demarcados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro3);
        text_titulo = findViewById(R.id.text_titulo);
        preco_diario = findViewById(R.id.cadastro_preco_diario);
        preco_mensal = findViewById(R.id.cadastro_preco_mensal);
        periodos = findViewById(R.id.xunxo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);
        btn_cadastrar = findViewById(R.id.cadastro_continuar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");
        demarcados = new HashSet<>();

        titulos = new String[]{"Manhã", "Noite", "Tarde"};
        cont_image = 0;

        intent2 = getIntent();
        extras = getIntent().getExtras();
        cadastro_1 = (Bundle) extras.get("dados_cadastro_1");

        def_disponibilidade();

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4), findViewById(R.id.checkBox5), findViewById(R.id.checkBox6), findViewById(R.id.checkBox7)
        };

        seta_direita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image == 2) {
                    cont_image = 0;
                } else cont_image++;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        seta_esquerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image == 0) {
                    cont_image = 2;
                } else cont_image--;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preco_diario.getText().toString().isEmpty() || preco_mensal.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                }
                cadastrar_user();
            }
        });

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            list_dias[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    atualizar_disponibilidade(b, finalI);
                }
            });
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }
    private void definir_diponibilidade() {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        HashMap<String, Boolean> bah = disponibilidade.get(text_titulo.getText().toString());
        for (int i = 0; i < 7; i++) {
            list_dias[i].setChecked(bah.get(dias_semana[i]));
        }
    }

    private void atualizar_disponibilidade(boolean b, int i) {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        HashMap<String, Boolean> temporario = new HashMap<>();
        int cont = 0;
        for (String dia : dias_semana) {
            boolean vec = list_dias[cont].isChecked();
            temporario.put(dia, vec);
            cont++;
        }
        disponibilidade.put(text_titulo.getText().toString(), temporario);
        Log.i("DISPONIBILIDADE", String.valueOf(disponibilidade));
        Log.i("DEMARCADOS ", String.valueOf(demarcados));
    }


    private void def_disponibilidade() {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        for (String periodo : new String[]{"Manhã", "Tarde", "Noite"}) {
            HashMap<String, Boolean> temporario = new HashMap<>();
            for (String dia : dias_semana) {
                temporario.put(dia, false);
            }
            disponibilidade.put(periodo, temporario);
        }

        Log.i("DISPONIBILIDADE", String.valueOf(disponibilidade));
    }


    private void cadastrar_user() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword((String) cadastro_1.get("email"), (String) cadastro_1.get("senha")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    selecionarImagemPerfil((Uri) extras.get("foto"),Filtro.class);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta ja foi cadastrada";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }
                    Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrir_tela_geral(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void salvar_dados() {
        if (preco_diario.getText().toString().equals("") || preco_mensal.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show(); abrir_tela_geral(Cadastro_3.class);
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("EXTRAS: ", extras.toString());
        Log.i("PRECO DIARIO: ", preco_diario.getText().toString());
        Log.i("PRECO MENSAL: ", preco_mensal.getText().toString());
        int idade = (int) cadastro_1.get("idade");
        Log.i("IDADE: ", String.valueOf(idade));
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Cuidador_criancas user = new Cuidador_criancas();
        user.setNome((String) cadastro_1.get("nome_user"));
        user.setTipo("Cuidador de Crianças");
        user.setEmail((String) cadastro_1.get("email"));
        user.setDescricao((String) extras.get("descricao_perfil"));
        user.setFoto_perfil((String) extras.get("foto"));
        user.setIdade(idade);
        user.setEndereco((String) extras.get("endereco"));
        user.setPreco_diario(Float.parseFloat(preco_diario.getText().toString()));
        user.setPreco_mensal(Float.parseFloat(preco_mensal.getText().toString()));
        user.setDispponibilidade(disponibilidade);
        user.setAmigos(null);
        user.setSenha((String) extras.get("senha"));
        user.setId(user_id);



        DocumentReference documentReference = db.collection("Cuidadores").document(user_id);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao salvar os dados. " + e.toString());
            }
        });
    }
    public void selecionarImagemPerfil(Uri imagem,Class<?> tela) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (imagem == null) {
            imagem = Uri.parse("android.resource://com.example.pfi_tesst/drawable/user_ft");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("imagens/" + currentUser.getUid());
        Toast.makeText(getApplicationContext(), "Aguarde um instante", Toast.LENGTH_SHORT).show();
        reference.putFile(imagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (preco_diario.getText().toString().equals("") || preco_mensal.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show(); abrir_tela_geral(Cadastro_3.class);
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Log.i("EXTRAS: ", extras.toString());
                        Log.i("PRECO DIARIO: ", preco_diario.getText().toString());
                        Log.i("PRECO MENSAL: ", preco_mensal.getText().toString());
                        int idade = (int) cadastro_1.get("idade");
                        Log.i("IDADE: ", String.valueOf(idade));
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Cuidador_criancas user = new Cuidador_criancas();
                        user.setNome((String) cadastro_1.get("nome_user"));
                        user.setTipo("Cuidador de Crianças");
                        user.setEmail((String) cadastro_1.get("email"));
                        user.setDescricao((String) extras.get("descricao_perfil"));
                        user.setFoto_perfil(uri.toString());
                        user.setIdade(idade);
                        user.setEndereco((String) extras.get("endereco"));
                        user.setPreco_diario(Float.parseFloat(preco_diario.getText().toString()));
                        user.setPreco_mensal(Float.parseFloat(preco_mensal.getText().toString()));
                        user.setDispponibilidade(disponibilidade);
                        user.setAmigos(null);
                        user.setSenha((String) cadastro_1.get("senha"));
                        user.setId(user_id);



                        DocumentReference documentReference = db.collection("Cuidadores").document(user_id);
                        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    abrir_tela_geral(tela);
                                    Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }

}