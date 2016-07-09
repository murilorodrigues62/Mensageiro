package br.edu.ifspsaocarlos.sdm.trabalhofinal.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.adapter.ListaMensagemAdapter;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.model.Mensagem;

public class MensagemActivity extends AppCompatActivity {
    private int usuarioOrigem;
    private int usuarioDestino;
    private String url;
    ListaMensagemAdapter mensagemAdapter;
    ArrayList<Mensagem> listMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem);

        //Busca o remetende salvo em SharedPreferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("PrefId", Context.MODE_PRIVATE);
        usuarioOrigem = Integer.parseInt(sharedPref.getString("id_usuario", "0"));
        usuarioDestino = getIntent().getIntExtra("id_destino", 0);

        url = getString(R.string.urlBase) + "mensagem";

        //Monta o adapter para a lista de mensagen
        ListView lista = (ListView) findViewById(R.id.lv_historico);
        listMensagem = new ArrayList<Mensagem>();
        mensagemAdapter = new ListaMensagemAdapter(this, listMensagem);
        lista.setAdapter(mensagemAdapter);

        listarMensagens();
    }

    public void enviarMensagem() {

        final EditText edtAssunto = (EditText) findViewById(R.id.edtAssunto);
        final EditText edtMensagem = (EditText) findViewById(R.id.edtMensagem);

        if (usuarioDestino == 0 || usuarioOrigem == 0 || edtAssunto.getText().toString().isEmpty() || edtMensagem.getText().toString().isEmpty()) {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(MensagemActivity.this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("origem_id", usuarioOrigem);
            jsonBody.put("destino_id", usuarioDestino);
            jsonBody.put("assunto", edtAssunto.getText());
            jsonBody.put("corpo", edtMensagem.getText());

            //Monta Request para enviar mensagem
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject s) {
                            Toast.makeText(MensagemActivity.this, R.string.msg_mensagem_enviada,
                                    Toast.LENGTH_SHORT).show();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // Thread atualiza dados na activity
                                    edtAssunto.setText("");
                                    edtMensagem.setText("");
                                    listarMensagens();
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(MensagemActivity.this, getString(R.string.msg_mensagem_erro) + "\n" + volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e("SDM", getString(R.string.msg_mensagem_erro));
        }
    }

    public void listarMensagens() {
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.pb_mensagem);

        mProgress.setVisibility(View.VISIBLE);

        String urlOrigem = url + "/1/" + String.valueOf(usuarioOrigem) + "/" + String.valueOf(usuarioDestino);
        String urlDestino = url + "/1/" + String.valueOf(usuarioDestino) + "/" + String.valueOf(usuarioOrigem);

        RequestQueue queue = Volley.newRequestQueue(MensagemActivity.this);
        listMensagem.clear();

        try {
            //Monta Requisição para buscar mensagens do usuário origem para o de destino
            JsonObjectRequest requestOrigemDestino = new JsonObjectRequest(Request.Method.GET, urlOrigem, null,
                    new MensagemListener(),
                    new MesagemErrorListener());
            //Monta Requisição para buscar mensagens do usuário destino para o de origem
            JsonObjectRequest requestDestinoOrigem = new JsonObjectRequest(Request.Method.GET, urlDestino, null,
                    new MensagemListener(),
                    new MesagemErrorListener());

            queue.add(requestOrigemDestino);
            queue.add(requestDestinoOrigem);

            mProgress.setVisibility(View.GONE);

        } catch (Exception e) {
            Toast.makeText(MensagemActivity.this, R.string.msg_json_erro + "\n" + e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            mProgress.setVisibility(View.GONE);
        }
    }

    public class MensagemListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject s) {
            JSONArray jsonArray;
            try {
                jsonArray = s.getJSONArray("mensagens");

                for (int indice = 0; indice < jsonArray.length(); indice++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(indice);

                    listMensagem.add(new Mensagem(jsonObject.getInt("id"),
                            jsonObject.getJSONObject("origem").getString("apelido"),
                            jsonObject.getJSONObject("destino").getString("apelido"),
                            jsonObject.getString("assunto"),
                            jsonObject.getString("corpo")));
                }

                Collections.sort(listMensagem, new Comparator<Mensagem>() {
                    @Override
                    public int compare(Mensagem m1, Mensagem m2) {
                        return m1.getId().compareTo(m2.getId());
                    }
                });

                mensagemAdapter.notifyDataSetChanged();
            } catch (JSONException je) {
                Toast.makeText(MensagemActivity.this, R.string.msg_json_erro + "\n" + je.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MesagemErrorListener implements Response.ErrorListener {
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(MensagemActivity.this, getString(R.string.msg_mensagem_erro) + "\n" + volleyError.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
