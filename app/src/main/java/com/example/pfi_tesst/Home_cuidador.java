package com.example.pfi_tesst;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Home_cuidador extends AppCompatActivity implements LocationListener{
    private Animation rotateOpenAnimation;
    private Animation rotateCloseAnimation;
    private Animation from_bottom_anim;
    private Animation to_bottom_anim;
    private List<String> ids;

    private Bundle extras;
    private FloatingActionButton btn_start, fab1, fab3, fab4;
    private EditText home_search;
    private boolean vec_click = false;
    private FirebaseFirestore db;
    private ImageView notificacao, filtro;
    private RecyclerView recyclerView;

    private String lastSearchTerm = "";


    private Handler handler = new Handler();
    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {

            String searchTerm = lastSearchTerm;
            if (!searchTerm.isEmpty()) {
                buscar_vaga(searchTerm);
            } else {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                        get_ids(cuidador);
                    }
                });
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String searchTerm = charSequence.toString();

            if (!searchTerm.equals(lastSearchTerm)) {
                lastSearchTerm = searchTerm;
                handler.removeCallbacks(searchRunnable);

                handler.postDelayed(searchRunnable, 500); // 500 milissegundos de atraso
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_cuidador);

        recyclerView = findViewById(R.id.recycleView);
        notificacao = findViewById(R.id.imageView4);
        filtro = findViewById(R.id.imageView3);
        /*RecyclerView.ItemDecoration divisor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divisor);*/
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        extras = getIntent().getExtras();
        /*recyclerView.setAdapter(new MyAdpater(getApplicationContext(),items));*/
        getSupportActionBar().setTitle("Take Care");

        db = FirebaseFirestore.getInstance();


        ids = new ArrayList<>();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                get_ids(cuidador);
            }
        });
        Log.i("IDS: ",ids.toString());


        rotateOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        from_bottom_anim = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        to_bottom_anim = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        btn_start = findViewById(R.id.btn_start);
        fab1 = findViewById(R.id.fab1);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_btn_clicked();
            }
        });


        home_search = findViewById(R.id.home_search);

        home_search.addTextChangedListener(textWatcher);


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Perfil_cuidador_propio.class);
                intent.putExtra("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                abrirTela(MainActivity.class);
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Contatos.class);
            }
        });

        notificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Notificacao.class);
            }
        });

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Filtro.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Location location;
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) this);
            if (location != null) {
                // Localização
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.i("permission: "," concedida");
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.i("USER_ID: ",user_id);
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Cuidador_criancas cuidador_criancas = documentSnapshot.toObject(Cuidador_criancas.class);
                            cuidador_criancas.setEndereco(get_localizacao(location.getLatitude(),location.getLongitude()));
                            Log.i("ENDERECO: ",cuidador_criancas.getEndereco());
                            db.collection("Cuidadores").document(user_id).set(cuidador_criancas);
                        }
                    }
                });



            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private void buscar_vaga(String searchTerm){
        ids.clear();
        CollectionReference ref = db.collection("Contratantes");
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Contratante contratante = doc.toObject(Contratante.class);
                    if(contratante.getVaga_emprego()!=null){
                        Log.i("Verificacao: ",String.valueOf(contratante.getNome().toUpperCase().contains(searchTerm.toUpperCase())));
                        if(contratante.getNome().toUpperCase().contains(searchTerm.toUpperCase())){
                            ids.add(contratante.getId());
                        }
                    }
                }
                HashSet<Vaga_emprego> vagas = new HashSet<>();
                for (String id: ids) {
                    Log.i("entrou no: ","for");
                    Log.i("ID DA VEZ: ",id);
                    CollectionReference cuidadoresRef = db.collection("Vaga_emprego").document(id).collection("vagas");
                    cuidadoresRef.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Vaga_emprego vaga = document.toObject(Vaga_emprego.class);
                                        vagas.add(vaga);
                                    }
                                    Log.i("VAGAS: ",vagas.toString());
                                    for (Vaga_emprego vaga: vagas){
                                        Log.i("VAGA: ",vaga.getId());
                                    }
                                    Adapter_vaga adapter = new Adapter_vaga(getApplicationContext(), new ArrayList<>(vagas));
                                    recyclerView.setAdapter(adapter);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //tratar falhas
                                }
                            });
                }

                if(vagas.isEmpty()){
                    Adapter_vaga adapter = new Adapter_vaga(getApplicationContext(), new ArrayList<>(vagas));
                    recyclerView.setAdapter(adapter);
                }
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

                // Concatenar as informações de endereço desejadas
                if (address.getThoroughfare() != null) {
                    fullAddress.append(address.getThoroughfare()); // Avenida
                }
                if (address.getFeatureName() != null) {
                    fullAddress.append(", ").append(address.getFeatureName()); // Número
                }
                if (address.getSubAdminArea() != null) {
                    fullAddress.append(", ").append(address.getSubAdminArea() ); // Cidade
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
    private void get_ids(Cuidador_criancas cuidador){
        ids.clear();
        CollectionReference ref = db.collection("Contratantes");
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Contratante contratante = doc.toObject(Contratante.class);
                    if(contratante.getVaga_emprego()!=null){
                        Log.i("BAH1: ",String.valueOf(contratante.getEndereco().equals(cuidador.getEndereco())));
                        Log.i("BAH2: ",String.valueOf(contratante.getVaga_emprego().getDisponibilidade().equals(cuidador.getDisponibilidade())));

                        if (!cuidador.getApenas_cidade()){
                            if(contratante.getEndereco().equals(cuidador.getEndereco()) && contratante.getVaga_emprego().getDisponibilidade().equals(cuidador.getFiltro())){
                                Log.i("VAGA: ","EMPREGO");
                                ids.add(contratante.getId());
                            }
                        }else{
                            if(contratante.getEndereco().equals(cuidador.getEndereco())){
                                Log.i("VAGA: ","EMPREGO");
                                ids.add(contratante.getId());
                            }
                        }

                    }
                }
                Set<Vaga_emprego> vagas = new HashSet<>();
                for (String id: ids) {
                    CollectionReference cuidadoresRef = db.collection("Vaga_emprego").document(id).collection("vagas");
                    cuidadoresRef.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Vaga_emprego vaga = document.toObject(Vaga_emprego.class);
                                        vagas.add(vaga);
                                    }
                                    Log.i("VAGAS: ",vagas.toString());
                                    Adapter_vaga adapter = new Adapter_vaga(getApplicationContext(),new ArrayList<>(vagas));
                                    recyclerView.setAdapter(adapter);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //tratar falhas
                                }
                            });
                }
            }
        });
    }
    private void add_btn_clicked() {
        setVisibility(vec_click);
        setAnimation(vec_click);
        vec_click = !vec_click;
    }

    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }

    private void setVisibility(boolean vec_click) {
        if (!vec_click) {
            fab1.setVisibility(View.VISIBLE);
            fab3.setVisibility(View.VISIBLE);
            fab4.setVisibility(View.VISIBLE);
        } else {
            fab1.setVisibility(View.INVISIBLE);
            fab3.setVisibility(View.INVISIBLE);
            fab4.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean vec_click) {
        if (!vec_click) {
            fab1.startAnimation(from_bottom_anim);
            fab3.startAnimation(from_bottom_anim);
            fab4.startAnimation(from_bottom_anim);
            btn_start.startAnimation(rotateOpenAnimation);
        } else {
            fab1.startAnimation(to_bottom_anim);
            fab3.startAnimation(to_bottom_anim);
            fab4.startAnimation(to_bottom_anim);
            btn_start.startAnimation(rotateCloseAnimation);
        }
    }
}