package com.example.pfi_tesst;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Cadastro extends AppCompatActivity {
    private EditText nome,data,email,senha;
    private Button cadastro;
    private Spinner tipo_user;
    private Calendar data_calendar;

    private TextView text_possui_conta, informativo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        text_possui_conta = findViewById(R.id.possui_conta);
        informativo = findViewById(R.id.nome_user);
        data = findViewById(R.id.cadastro_date);
        tipo_user = findViewById(R.id.tipo_user);
        cadastro = findViewById(R.id.cadastro_continuar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nome.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                } else if (data.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                } else if (senha.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                }else if (data_calendar == null){
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (calc_idade(data_calendar)<18){
                    Toast.makeText(getApplicationContext(),"Você deve ser maior de idade!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(senha.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"Sua senha deve conter no mínimo 6 caracteres",Toast.LENGTH_SHORT).show();
                    return;
                }
                abrirTela();
            }
        });

        nome = findViewById(R.id.cadastro_nome_user);
        email = findViewById(R.id.cadastro_email);
        senha = findViewById(R.id.cadastro_senha);

        text_possui_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrir_tela_geral(MainActivity.class);
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }
    private void abrir_tela_geral(Class<?> activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        Calendar defaultCalendar = Calendar.getInstance();

        if (data_calendar != null) {
            defaultCalendar = data_calendar;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);//%02d siginifica completar com zero, 2 casas decimais de inteiros
                        data.setText(selectedDate);
                        data_calendar = Calendar.getInstance();
                        data_calendar.set(Calendar.YEAR, year);
                        data_calendar.set(Calendar.MONTH, monthOfYear);
                        data_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                },
                defaultCalendar.get(Calendar.YEAR),
                defaultCalendar.get(Calendar.MONTH),
                defaultCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }



    private int calc_idade(Calendar data){

        Calendar hoje = Calendar.getInstance();

        int idade = hoje.get(Calendar.YEAR) - data.get(Calendar.YEAR);


        if (hoje.get(Calendar.DAY_OF_YEAR) < data.get(Calendar.DAY_OF_YEAR)) {
            idade--;
        }
        return idade;
    }
    private void abrirTela(){
        Intent intent = new Intent(this, Cadastro_2.class);
        intent.putExtra("nome_user",nome.getText().toString());
        intent.putExtra("tipo_user",tipo_user.getSelectedItem().toString());
        intent.putExtra("idade",calc_idade(data_calendar));
        intent.putExtra("email",email.getText().toString());
        intent.putExtra("senha",senha.getText().toString());
        startActivity(intent);
    }

}