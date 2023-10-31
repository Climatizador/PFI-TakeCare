package com.example.pfi_tesst;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Cadastro_2 extends AppCompatActivity implements LocationListener {
    private Button continuar;
    private static final int PERMISSAO_LEITURA_ARMAZENAMENTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Location location;
    private EditText descricao;
    private LocationManager locationManager;
    private Address endereco;

    private ImageView foto_perfil;

    private TextView informativo;

    private String url_ft;

    StorageReference storageReference;
    LinearProgressIndicator progress;
    Uri imagem;
    MaterialButton selectImage, uploadImage;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                continuar.setEnabled(true);
                if (result.getData() != null) {
                    imagem = result.getData().getData();
                    Glide.with(getApplicationContext()).load(imagem).into(foto_perfil);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Por favor seleicone uma imagem", Toast.LENGTH_SHORT).show();
            }
        }
    });


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);
        continuar = findViewById(R.id.enviar);
        descricao = findViewById(R.id.descricao);
        foto_perfil = findViewById(R.id.foto_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Log.i("IDADE:   ", extras.get("idade").toString());
        Log.i("ENTRANDO NO IF DA: ", "LOCALIZACAO");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, é solicitada
            Log.i("PERMISSAO CONCEDIDA: ", "Não");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // A permissão foi concedida, pega a localização
            Log.i("PERMISSAO CONCEDIDA: ", "SIM");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) this);
                // Localização
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                try {
                    endereco = buscar_endereco(latitude, longitude);
                    if (endereco != null) {
                        Log.i("Endereço", "CIDADE: " + endereco.getLocality() + "  ESTADO:  " + endereco.getAdminArea() + "  PAÍS:  " + endereco.getCountryName() + "");
                        Log.i("LOCALIZAÇÂO", "LATITUDE:  " + latitude + "  LONGITUDE:  " + longitude);
                    }
                } catch (IOException e) {
                    Log.e("GPS", "Erro ao buscar endereço: " + e.getMessage());
                }

            }
        }


        if (extras.get("tipo_user").equals("Contratante")) {
            continuar.setText("Cadastrar-se");
        }


        FirebaseApp.initializeApp(Cadastro_2.this);
        storageReference = FirebaseStorage.getInstance().getReference();


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
                if (imagem == null) {
                    Toast.makeText(getApplicationContext(), "Selecione uma imagem de perfil!", Toast.LENGTH_SHORT).show();
                    return;
                }
                cadastrar_user(extras);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }

    private Address buscar_endereco(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address endereco = null;
        List<Address> enderecos;

        geocoder = new Geocoder(getApplicationContext());

        enderecos = geocoder.getFromLocation(latitude, longitude, 1);
        if (!enderecos.isEmpty()) {
            endereco = enderecos.get(0);
        }
        return endereco;
    }

    private void abrir_tela_geral(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void cadastrar_user(Bundle extras) {
        if (extras.get("tipo_user").equals("Contratante")) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword((String) extras.get("email"), (String) extras.get("senha")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        selecionarImagemPerfil(imagem, extras, Filtro.class);
                    } else {
                        // Tratamento de erros de autenticação
                        String erro;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            erro = "Digite uma senha com no mínimo 6 caracteres";
                        } catch (FirebaseAuthUserCollisionException e) {
                            erro = "Esta conta já foi cadastrada";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erro = "E-mail inválido";
                        } catch (Exception e) {
                            erro = "Erro ao cadastrar usuário";
                        }
                        Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), Cadastro_3.class);
            intent.putExtra("dados_cadastro_1", extras);
            intent.putExtra("descricao_perfil", descricao.getText().toString());
            if (location == null) {
                Log.i("PERMISSAO CONCEDIDA: ", "SIM");
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) this);
                        // Localização
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        try {
                            endereco = buscar_endereco(latitude, longitude);
                            if (endereco != null) {
                                Log.i("Endereço", "CIDADE: " + endereco.getLocality() + "  ESTADO:  " + endereco.getAdminArea() + "  PAÍS:  " + endereco.getCountryName() + "");
                                Log.i("LOCALIZAÇÂO", "LATITUDE:  " + latitude + "  LONGITUDE:  " + longitude);
                            }
                        } catch (IOException e) {
                            Log.e("GPS", "Erro ao buscar endereço: " + e.getMessage());
                        }

                    }
                }
            }
            intent.putExtra("endereco",get_localizacao(location.getLatitude(),location.getLongitude()));
            intent.putExtra("foto", imagem);
            startActivity(intent);
        }
    }

    private void salvar_dados(Bundle extras, String imagemUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Contratante user = new Contratante();
        user.setNome((String) extras.get("nome_user"));
        user.setTipo("Contratante");
        user.setEmail((String) extras.get("email"));
        user.setDescricao(descricao.getText().toString());
        user.setFoto_perfil(imagemUrl);
        user.setIdade((int) extras.get("idade"));
        user.setVaga_emprego(null);
        user.setAmigos(null);
        user.setSenha((String) extras.get("senha"));
        user.setId(user_id);

        DocumentReference documentReference = db.collection("Contratantes").document(user_id);
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


    private void abrirTela(boolean user) {
        Intent intent;
        if (user) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, Cadastro_3.class);
        }
        startActivity(intent);
    }


    public void selecionarImagemPerfil(Uri imagem, Bundle extras,Class<?> tela) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (imagem == null) {
            imagem = Uri.parse("android.resource://com.example.pfi_tesst/drawable/user_ft");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("imagens/" + currentUser.getUid());
        Toast.makeText(getApplicationContext(), "Aguarde um instante.", Toast.LENGTH_SHORT).show();
        reference.putFile(imagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Contratante user = new Contratante();
                        user.setNome((String) extras.get("nome_user"));
                        user.setTipo("Contratante");
                        user.setEmail((String) extras.get("email"));
                        user.setDescricao(descricao.getText().toString());
                        user.setFoto_perfil(uri.toString());
                        user.setIdade((int) extras.get("idade"));
                        if (location == null) {
                            Log.i("PERMISSAO CONCEDIDA: ", "SIM");
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (ActivityCompat.checkSelfPermission(Cadastro_2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Cadastro_2.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {

                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) Cadastro_2.this);
                                    // Localização
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    try {
                                        endereco = buscar_endereco(latitude, longitude);
                                        if (endereco != null) {
                                            Log.i("Endereço", "CIDADE: " + endereco.getLocality() + "  ESTADO:  " + endereco.getAdminArea() + "  PAÍS:  " + endereco.getCountryName() + "");
                                            Log.i("LOCALIZAÇÂO", "LATITUDE:  " + latitude + "  LONGITUDE:  " + longitude);
                                        }
                                    } catch (IOException e) {
                                        Log.e("GPS", "Erro ao buscar endereço: " + e.getMessage());
                                    }

                                }
                            }
                        }
                        user.setEndereco(get_localizacao(location.getLatitude(),location.getLongitude()));
                        user.setVaga_emprego(null);
                        user.setAmigos(null);
                        user.setSenha((String) extras.get("senha"));
                        user.setId(user_id);

                        DocumentReference documentReference = db.collection("Contratantes").document(user_id);
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

    private String get_localizacao(Double latitude, Double longitude){
        Log.i("LATITUDE: ", latitude.toString());
        Log.i("LONGITUDE: ", longitude.toString());
        Geocoder geocoder;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        String city = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("ADRESSES: ", addresses.toString());
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder fullAddress = new StringBuilder();

                if (address.getThoroughfare() != null) {
                    fullAddress.append(address.getThoroughfare()); // Avenida
                }
                if (address.getFeatureName() != null) {
                    fullAddress.append(", ").append(address.getFeatureName()); // Número
                }
                if (address.getSubAdminArea() != null) {
                    fullAddress.append(", ").append(address.getSubAdminArea()); // Cidade
                }
                if (address.getAdminArea() != null) {
                    fullAddress.append(" - ").append(address.getAdminArea()); // Estado
                }

                city = fullAddress.toString();
                Log.i("City: ", city);
            } else {
                Log.i("ERRO: ", "Nenhum endereço encontrado.");
            }

        } catch (IOException e) {
            Log.e("EXCEÇÃO: ", "Erro ao obter endereço", e);
        }

        return city;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageViewProfile = findViewById(R.id.foto_perfil);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // o usuario seleciona uma imagem da galeria
            Uri imageUri = data.getData();

            // exibe a imagem selecionada na ImageView
            imageViewProfile.setImageURI(imageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // o usuário capturou uma imagem da camera (se implementado)
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // exibe a imagem capturada na ImageView
            imageViewProfile.setImageBitmap(photo);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}


