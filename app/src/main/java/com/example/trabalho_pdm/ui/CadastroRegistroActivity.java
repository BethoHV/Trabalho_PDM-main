package com.example.trabalho_pdm.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho_pdm.R;
import com.example.trabalho_pdm.model.Rastreamento;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class CadastroRegistroActivity extends AppCompatActivity {
    public static final String TITULO_APPBAR = "Novo Registro";
    public static final String LINK = "https://trab-pdm.000webhostapp.com/insert-registro.php";
    private EditText txtDestino;

    private Rastreamento rastreamento = new Rastreamento(0, "", "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_registro);
        setTitle(TITULO_APPBAR);

        txtDestino = findViewById(R.id.activity_cadastro_destino);

        gerenciarBotaoRastrear();
    }

    private void gerenciarBotaoRastrear() {
        Button botaoRegistro = findViewById(R.id.activity_cadastro_registro_botao);

        botaoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirNovoRegistro();

                Intent intent = new Intent(CadastroRegistroActivity.this, ContainerParaMapaActivity.class);
                intent.putExtra("DESTINO", rastreamento.getLocal());

                startActivity(intent);
            }
        });
    }

    private void abrirNovoRegistro() {
        NovoRegistro novoRegistro = new NovoRegistro();
        String destino = txtDestino.getText().toString();
        rastreamento.setLocal(destino);

        novoRegistro.execute(LINK, destino);
    }

    public class NovoRegistro extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("POST");
                rastreamento.setUsuario_id(Integer.parseInt(getIntent().getStringExtra("ID_USUARIO")));
                ContentValues dto = new ContentValues();
                dto.put("local", strings[1]);
                dto.put("dataRegistro", rastreamento.getDataRegistro());
                dto.put("usuario_id", rastreamento.getUsuario_id());

                OutputStream out = conexao.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(getFormData(dto));
                writer.flush();

                int status = conexao.getResponseCode();

                if (status == 200) {
                    InputStream stream = new BufferedInputStream(conexao.getInputStream());
                    BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();
                    String str = "";
                    while ((str = buff.readLine()) != null) {
                        builder.append(str);
                    }
                    conexao.disconnect();

                    return builder.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CadastroRegistroActivity.this);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String dto) {
            super.onPostExecute(dto);
            dialog.dismiss();
            finish();
        }
    }

    private String getFormData(ContentValues dto) {
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : dto.valueSet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }

                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}