package com.example.trabalho_pdm.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trabalho_pdm.R;
import com.example.trabalho_pdm.dao.RegistroDao;
import com.example.trabalho_pdm.model.Rastreamento;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class TelaPrincipalActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private final static String LINK = "https://trab-pdm.000webhostapp.com/select-registro.php";
    private String ID_USUARIO;
    private RegistroDao dao = new RegistroDao();

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_tela_principal);
        final String TITULO_APPBAR = "Olá, " + getIntent().getStringExtra("NOME");
        setTitle(TITULO_APPBAR);
        ID_USUARIO = getIntent().getStringExtra("ID_USUARIO");

        Button gerarNovoRastreamento = findViewById(R.id.activity_principal_botao_novo_registro);

        gerarNovoRastreamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaPrincipalActivity.this, CadastroRegistroActivity.class);
                intent.putExtra("ID_USUARIO", "" + ID_USUARIO);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        final ListView listaDeRegistros = findViewById(R.id.activity_principal_lista_registros);
        dao = new RegistroDao();

        BuscarDadosListaRegistros buscarDadosListaRegistros = new BuscarDadosListaRegistros();
        buscarDadosListaRegistros.execute(LINK, ID_USUARIO);

        listaDeRegistros.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                dao.todos()));

        listaDeRegistros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TelaPrincipalActivity.this, ContainerParaMapaActivity.class);
                intent.putExtra("DESTINO", dao.procurar(i));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        final ListView listaDeRegistros = findViewById(R.id.activity_principal_lista_registros);
        listaDeRegistros.setAdapter(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public class BuscarDadosListaRegistros extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("POST");

                ContentValues dto = new ContentValues();
                dto.put("usuario_id", strings[1]);

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
            dialog = new ProgressDialog(TelaPrincipalActivity.this);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String dto) {
            super.onPostExecute(dto);
            dialog.dismiss();

            if (dto != null) {
                try {
                    //JSONObject obj = new JSONObject(dto);
                    JSONArray ary = new JSONArray(dto);
                    for(int i=0; i<ary.length(); i++)
                    {
                        JSONObject obj=ary.getJSONObject(i);
                        Rastreamento novoRastreamento = new Rastreamento(obj.getInt("usuario_id"), obj.getString("local"), obj.getString("dataRegistro"));
                        dao.salva(novoRastreamento);
                    }
                } catch (Exception ex) {
                    Toast.makeText(TelaPrincipalActivity.this, "Nenhum registro encontrado",
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }else{
                Toast.makeText(TelaPrincipalActivity.this, "Erro na conexão",
                        Toast.LENGTH_LONG).show();
            }
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