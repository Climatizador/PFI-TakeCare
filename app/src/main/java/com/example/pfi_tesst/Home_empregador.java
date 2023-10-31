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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home_empregador extends AppCompatActivity implements LocationListener{
    private Animation rotateOpenAnimation;
    private Animation rotateCloseAnimation;
    private Animation from_bottom_anim;
    private Animation to_bottom_anim;
    private Bundle extras;
    private FloatingActionButton btn_start, fab1, fab2, fab3, fab4;
    private EditText home_search;
    private boolean vec_click = false;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ImageView notificacao,filtro;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empregador);

        recyclerView = findViewById(R.id.recycleView);
        notificacao = findViewById(R.id.imageView4);
        filtro = findViewById(R.id.imageView3);
        extras = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        /*RecyclerView.ItemDecoration divisor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divisor);*/
        recyclerView.setHasFixedSize(true);
        getSupportActionBar().setTitle("Take Care");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*recyclerView.setAdapter(new MyAdpater(getApplicationContext(),items));*/

        List<Cuidador_criancas> cuidadores = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference cuidadoresRef = db.collection("Cuidadores");
        List<String> ids = new ArrayList<String>();

        String current_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Contratantes").document(current_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Contratante contratante = documentSnapshot.toObject(Contratante.class);

                Log.i("CONTRATANTE EMPTY: ", String.valueOf(contratante==null));

                cuidadoresRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Cuidador_criancas cuidador = document.toObject(Cuidador_criancas.class);
                                    String[] endereco_cuidador = cuidador.getEndereco().split(",");
                                    String[] endereco_contratante = contratante.getEndereco().split(",");
                                    if (contratante.getApenas_cidade()){
                                        if(endereco_cuidador[2].equals(endereco_contratante[2])){
                                            cuidadores.add(cuidador);
                                            ids.add(cuidador.getId());
                                        }
                                    }else{
                                        if(endereco_cuidador[2].equals(endereco_contratante[2]) && contratante.getFiltro().equals(cuidador.getDisponibilidade())){
                                            cuidadores.add(cuidador);
                                            ids.add(cuidador.getId());
                                        }
                                    }
                                    MyAdpater adpater = new MyAdpater(getApplicationContext(), cuidadores, R.drawable.report2, ids);
                                    recyclerView.setAdapter(adpater);
                                }
                            }
                        });
                        }
            });





        rotateOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        from_bottom_anim = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        to_bottom_anim = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        btn_start = findViewById(R.id.btn_start);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_btn_clicked();
            }
        });

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Filtro.class);
            }
        });

        home_search = findViewById(R.id.home_search);


        home_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchTerm = charSequence.toString();
                if (searchTerm!=""){
                    buscar_cuidador(searchTerm);
                }else{
                    cuidadores.clear();
                    ids.clear();
                    String current_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    db.collection("Contratantes").document(current_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Contratante contratante = documentSnapshot.toObject(Contratante.class);

                            cuidadoresRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                Cuidador_criancas cuidador = document.toObject(Cuidador_criancas.class);
                                                String[] endereco_cuidador = cuidador.getEndereco().split(",");
                                                String[] endereco_contratante = contratante.getEndereco().split(",");
                                                if (contratante.getApenas_cidade()){
                                                    if(endereco_cuidador[2].equals(endereco_contratante[2])){
                                                        cuidadores.add(cuidador);
                                                        ids.add(cuidador.getId());
                                                    }
                                                }else{
                                                    if(endereco_cuidador[2].equals(endereco_contratante[2]) && contratante.getFiltro().equals(cuidador.getDisponibilidade())){
                                                        cuidadores.add(cuidador);
                                                        ids.add(cuidador.getId());
                                                    }
                                                }
                                                MyAdpater adpater = new MyAdpater(getApplicationContext(), cuidadores, R.drawable.report2, ids);
                                                recyclerView.setAdapter(adpater);
                                            }
                                        }
                                    });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Perfil_empregador.class);
                intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Criar_vaga.class);
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
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();Log.i("USER_ID: ",user_id);
                db.collection("Contratantes").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Log.i("DOCUMENT: ",String.valueOf(documentSnapshot.exists()));
                            Contratante contratante = documentSnapshot.toObject(Contratante.class);
                            contratante.setEndereco(get_localizacao(location.getLatitude(),location.getLongitude()));
                            Log.i("ENDERECO: ",contratante.getEndereco());
                            db.collection("Contratantes").document(user_id).set(contratante);
                        }
                    }
                });



            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private void buscar_cuidador(String searchTerm){
        List<Cuidador_criancas> cuidadores = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        db.collection("Cuidadores").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Cuidador_criancas cuidador_criancas = doc.toObject(Cuidador_criancas.class);
                    if(cuidador_criancas.getNome().toUpperCase().contains(searchTerm.toUpperCase())){
                        cuidadores.add(cuidador_criancas);
                        ids.add(cuidador_criancas.getId());
                    }
                }
                MyAdpater adapter = new MyAdpater(getApplicationContext(),cuidadores,R.drawable.report2,ids);
                recyclerView.setAdapter(adapter);
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
            fab2.setVisibility(View.VISIBLE);
            fab3.setVisibility(View.VISIBLE);
            fab4.setVisibility(View.VISIBLE);
        } else {
            fab1.setVisibility(View.INVISIBLE);
            fab2.setVisibility(View.INVISIBLE);
            fab3.setVisibility(View.INVISIBLE);
            fab4.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean vec_click) {
        if (!vec_click) {
            fab1.startAnimation(from_bottom_anim);
            fab2.startAnimation(from_bottom_anim);
            fab3.startAnimation(from_bottom_anim);
            fab4.startAnimation(from_bottom_anim);
            btn_start.startAnimation(rotateOpenAnimation);
        } else {
            fab1.startAnimation(to_bottom_anim);
            fab2.startAnimation(to_bottom_anim);
            fab3.startAnimation(to_bottom_anim);
            fab4.startAnimation(to_bottom_anim);
            btn_start.startAnimation(rotateCloseAnimation);
        }
    }
}