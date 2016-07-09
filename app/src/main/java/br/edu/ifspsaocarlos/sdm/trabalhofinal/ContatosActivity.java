package br.edu.ifspsaocarlos.sdm.trabalhofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ContatosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListaContatoAdapter adapter;
    ListView lista;
    ArrayList<Contato> listContatos;

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        setTitle("Contatos");

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("PrefId",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String id = sharedPref.getString("id_usuario", "0");


        //caso não tenha sido criado o usuário ainda, manda para a tela de cadastro.
        if (id == "0") {
            //manda para a tela que cadastra o usuário
            Intent listaContatos = new Intent(ContatosActivity.this,
                    CadastroActivity.class);
            startActivity(listaContatos);
        }

        serviceIntent = new Intent("BUSCAR_NOVA_MENSAGEM_SERVICE");
        // TODO: 09/07/16 Desfazer 
//        startService(serviceIntent); 

        //monta a estrutura do menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.menu_left);

        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.lista_menu)));
        drawerList.setOnItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.abrir_drawer,
                R.string.fechar_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //monta o adapter para a lista
        ListView lista = (ListView) findViewById(R.id.lv_contatos) ;
        listContatos = new ArrayList<Contato>();
        adapter = new ListaContatoAdapter(this,listContatos);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Contato c = (Contato)parent.getItemAtPosition(position);
                Intent intent = new Intent(ContatosActivity.this, CadastroActivity.class);
                intent.putExtra("id_contato",c.getId());
                startActivity(intent);
            }
        });

        //chamada para preecher a lista.
        preencherLista(id);

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        if (textView.getText().toString().equals("Meus Dados")) {
            Intent intent = new Intent(ContatosActivity.this, EditarActivity.class);
            startActivity(intent);
        }
        else if(textView.getText().toString().equals("Contatos")){
            Intent intent = new Intent(ContatosActivity.this, ContatosActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawers();
    }


    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void preencherLista(String id){
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.pb_carregando);

        mProgress.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(ContatosActivity.this);
        String url = getString(R.string.urlBase) + "/contato";
        final Integer id_usuario = Integer.parseInt(id);
        try{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject s) {
                            //chama o adaptador
                            try{
                            JSONArray contatos = (JSONArray)s.getJSONArray("contatos");

                            for(int i=0;i<contatos.length();i++){
                                JSONObject data = contatos.getJSONObject(i);
                                //alimenta o vetor com os contatos, não adiciona ele mesmo
                                if(data.getInt("id") != id_usuario ){
                                    listContatos.add(new Contato(data.getInt("id"),data.getString("nome_completo"),data.getString("apelido")));
                                }

                            }
                                Collections.sort(listContatos, new Comparator<Contato>() {
                                    @Override
                                    public int compare(Contato contato, Contato t1) {
                                        return contato.getNome().compareTo(t1.getNome());
                                    }
                                });
                            adapter.notifyDataSetChanged();

                            }catch (JSONException je) {
                                Toast.makeText(ContatosActivity.this, "Erro ao obter a lista, tente novamente dentre de alguns instantes por favor...",
                                        Toast.LENGTH_SHORT).show();
                            }
                            mProgress.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(ContatosActivity.this, "Erro ao obter a lista, tente novamente dentre de alguns instantes por favor...",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.setVisibility(View.GONE);
                        }
                    });
            queue.add(jsonObjectRequest);
        }catch(Exception e) {
            Toast.makeText(ContatosActivity.this, "Erro ao obter a lista, tente novamente dentre de alguns instantes por favor...", Toast.LENGTH_SHORT).show();
            mProgress.setVisibility(View.GONE);
        }
    }

}
