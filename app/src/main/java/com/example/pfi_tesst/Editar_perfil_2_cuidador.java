package com.example.pfi_tesst;




import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Editar_perfil_2_cuidador extends AppCompatActivity {
    private FirebaseFirestore db; private Bundle extras; private EditText descricao;
    private ImageView foto_perfil; Uri imagem; private Button continuar, avancar;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                continuar.setEnabled(true);
                if (result.getData() != null) {
                    imagem = result.getData().getData();
                    Glide.with(getApplicationContext()).load(imagem).into(foto_perfil);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Por favor seleicone uma imagem", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_2_cuidador);
        foto_perfil = findViewById(R.id.foto_perfil);
        continuar = findViewById(R.id.concluir);
        descricao = findViewById(R.id.descricao);
        avancar = findViewById(R.id.avancar);
        db = FirebaseFirestore.getInstance();
        extras = getIntent().getExtras();
        Log.i("EXTRAS: ",extras.toString());
        recuperar_dados();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();
        getSupportActionBar().setTitle("Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String[] tipo = {""};

        DocumentReference docRef = db.collection("Cuidadores").document(user_id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                Log.i("TIPO: ",contratante.getTipo());
                imagem= Uri.parse(contratante.getFoto_perfil());
                tipo[0] = contratante.getTipo();
            }
        });

        foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference documentRef = db.collection("Cuidadores").document(user_id);
                try {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("nome", extras.get("nome"));
                    updates.put("email", extras.get("email"));
                    updates.put("senha", extras.get("senha"));
                    updates.put("descricao", descricao.getText().toString());
                    updates.put("foto_perfil", imagem.toString());

                    documentRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("USAURIO ATUALIZADO COM SUCESSO:","");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("ERRO AO ATUALIZAR USUARIO:","");
                        }
                    });

                    abrirTela(Home_cuidador.class);
                }catch (Exception e){
                    db.collection("Cuidadores").document(user_id).update("nome", extras.get("nome"),"email",extras.get("email"),"senha",extras.get("senha"),"descricao",descricao.getText().toString(),"foto",imagem.toString());
                    /*abrir_tela(Cuidador.class)*/
                }
            }
        });

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Editar_perfil_3.class);
                intent.putExtra("nome", (String) extras.get("nome"));
                intent.putExtra("email",(String)extras.get("email"));
                intent.putExtra("senha", (String) extras.get("senha"));
                intent.putExtra("descricao", descricao.getText().toString());
                intent.putExtra("foto", imagem.toString());
                startActivity(intent);
            }
        });


    }
    private void abrirTela(Class<?> tela){
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void recuperar_dados() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("Cuidadores").document(user_id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Contratante contratante = documentSnapshot.toObject(Contratante.class);

                    descricao.setText(contratante.getDescricao());
                    Glide.with(getApplicationContext()).load(contratante.getFoto_perfil()).into(foto_perfil);


                } else {
                    Log.i("DOCUMENTO: ","NÃO EXISTE");
                }
            }
        });
    }
}
