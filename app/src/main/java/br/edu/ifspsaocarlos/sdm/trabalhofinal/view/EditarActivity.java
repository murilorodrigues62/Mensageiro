package br.edu.ifspsaocarlos.sdm.trabalhofinal.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;

public class EditarActivity extends AppCompatActivity {


    String id;
    String nome;
    String apelido;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        setTitle("Editar");

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("PrefId",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("id_usuario", "0");
        nome = sharedPref.getString("nome_usuario", "");
        apelido = sharedPref.getString("apelido_usuario", "");

        ((EditText)findViewById(R.id.edtNomeUsuario)).setText(nome);
        ((EditText)findViewById(R.id.edtApelidoUsuario)).setText(apelido);

        //caso não tenha sido criado o usuário ainda, manda para a tela de cadastro.
        if (id == "0") {
            //manda para a tela que cadastra o usuário
            Intent listaContatos = new Intent(EditarActivity.this,
                    CadastroActivity.class);
            startActivity(listaContatos);
        }

    }

    public void cadastrar(View v) {
        final Button btnCadastro = (Button) findViewById(R.id.btnCadastroUsuario);
        final EditText edtNome = (EditText) findViewById(R.id.edtNomeUsuario);
        final EditText  edtApelido = (EditText) findViewById(R.id.edtApelidoUsuario);
        final String id_usuario = id;


        //faz as validações para o cadastro
        if(edtNome.getText().toString().length() == 0){
            Toast.makeText(EditarActivity.this, "Digite um nome para o usuário por favor.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(edtApelido.getText().toString().length() == 0){
            Toast.makeText(EditarActivity.this, "Digite um apelido para o usuário por favor.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnCadastro.setEnabled(false);

        RequestQueue queue = Volley.newRequestQueue(EditarActivity.this);
        String url = getString(R.string.urlBase) + "/contato/"+id_usuario;

        //monta a string para ser enviada
        String string = "{\"id\":\"" + id_usuario + "\"," +
                "\"nome_completo\":\"" + edtNome.getText().toString() + "\"," +
                "\"apelido\":\"" + edtApelido.getText().toString() + "\"}";

        try {
            final JSONObject jsonBody = new JSONObject(string);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject s) {
                            Toast.makeText(EditarActivity.this, "Usuário atualizado com sucesso!!",
                                    Toast.LENGTH_SHORT).show();
                            //pega o id cadastrado e salva no SharedPreferences
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("PrefId",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("nome_usuario",edtNome.getText().toString());
                            editor.putString("apelido_usuario",edtApelido.getText().toString());
                            editor.commit();

                            //manda para a tela que lista os contatos
                            Intent listaContatos = new Intent(EditarActivity.this,
                                    ContatosActivity.class);
                            startActivity(listaContatos);

                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(EditarActivity.this, "Erro ao atualizar o usuário, tente novamente dentro de alguns instantes por favor.",
                                    Toast.LENGTH_SHORT).show();
                            btnCadastro.setEnabled(true);
                        }
                    });
            queue.add(jsonObjectRequest);
        }catch(Exception e) {
            Toast.makeText(EditarActivity.this, "Erro ao atualizar o usuário, tente novamente dentro de alguns instantes por favor.", Toast.LENGTH_SHORT).show();
            btnCadastro.setEnabled(true);
        }

    }
}
