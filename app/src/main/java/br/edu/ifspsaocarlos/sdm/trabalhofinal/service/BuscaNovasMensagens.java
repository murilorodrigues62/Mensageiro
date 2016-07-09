package br.edu.ifspsaocarlos.sdm.trabalhofinal.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
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

import br.edu.ifspsaocarlos.sdm.trabalhofinal.CadastroActivity;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.Contato;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
public class BuscaNovasMensagens extends Service implements Runnable  {
    private boolean appAberta;
    private boolean primeiraBusca;
    private static int ultimoNumeroContatos;
    private static int ultimoNumeroMensagem;
    private static int novoNumeroMensagem;
    private static String id;
    private static ArrayList<Contato> listaContatos;
    private static ArrayList<Contato> listaContatosNova;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    public BuscaNovasMensagens() {

    }

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate() {
        super.onCreate();
        appAberta = true;
        primeiraBusca = true;
        ultimoNumeroContatos = 0;
        ultimoNumeroMensagem = 0;
        novoNumeroMensagem = 1;
        listaContatos = new ArrayList<Contato>();
        listaContatos = new ArrayList<Contato>();



        sharedPref = getApplicationContext().getSharedPreferences("PrefId",MODE_PRIVATE);
        editor = sharedPref.edit();
        id = sharedPref.getString("id_usuario", "0");

        new Thread(this).start();

        Log.e("SDM", "SERVICE LIGADO");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void run() {
        while (appAberta) {
            try {
                //caso o usuário já tenha sido criado, senão fica esperando para ligar o service
                if(id != "0"){
                    Thread.sleep(3000);
                    buscaContatos();
                    Thread.sleep(2000);

                    novoNumeroMensagem = ultimoNumeroMensagem;
                      for(Contato c: listaContatos) {
                        buscaMensagens(c.getId());
                        Thread.sleep(100);
                    }
                    primeiraBusca = false;
                    ultimoNumeroMensagem = novoNumeroMensagem;
                }else {
                    editor = sharedPref.edit();
                    id = sharedPref.getString("id_usuario", "0");
                }
            }catch (InterruptedException ie) {
                Log.e("SDM", "Erro na thread de recuperação de mensagens");
            }
        }
   }

    private void buscaMensagens(int id_origem){
        RequestQueue queue = Volley.newRequestQueue(BuscaNovasMensagens.this);
        final int ultima_mensagem = ultimoNumeroMensagem+1;
            String url = getString(R.string.urlBase) + "mensagem/"+ultima_mensagem+"/"+id_origem+"/"+id;
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

                                    JSONArray mensagens = (JSONArray)s.getJSONArray("mensagens");
                                    if(mensagens.length() > 0){
                                        JSONObject data = mensagens.getJSONObject(mensagens.length()-1);
                                        //caso seja a primeira busca a ultima mensagem que já chegou pra ele
                                        if(primeiraBusca){
                                            Log.e("SDM", Integer.toString(data.getInt("id")));
                                            if(data.getInt("id") >= novoNumeroMensagem){
                                                novoNumeroMensagem = data.getInt("id");
                                            }
                                        }else{ //verifica qual vai ser a nova ultima mensagem e guarda as notificações para exibir
                                            if(data.getInt("id") >= ultima_mensagem){
                                                if(data.getInt("id") >= novoNumeroMensagem){
                                                    novoNumeroMensagem = data.getInt("id");
                                                }

                                                for (int i = 0; i < mensagens.length();i++){
                                                    Log.e("SDM", "NOVA MENSAGEM");
                                                    JSONObject noti = mensagens.getJSONObject(i);
                                                    NotificationManager nm = (NotificationManager)
                                                            getSystemService(NOTIFICATION_SERVICE);
                                                    Intent intent = new Intent(BuscaNovasMensagens.this, CadastroActivity.class);
                                                    intent.putExtra("id_contato",noti.getInt("origem_id"));
                                                    PendingIntent p = PendingIntent.getActivity(BuscaNovasMensagens.this, 0, intent, 0);
                                                    Notification.Builder builder = new Notification.Builder(BuscaNovasMensagens.this);
                                                    builder.setSmallIcon(R.mipmap.ic_launcher);
                                                    builder.setTicker("Nova Mensagem");
                                                    builder.setContentTitle(noti.getString("assunto"));
                                                    builder.setContentText(noti.getString("corpo"));
                                                    builder.setWhen(System.currentTimeMillis());
                                                    builder.setContentIntent(p);

                                                    Notification notification = builder.build();
                                                    notification.vibrate = new long[] {100, 250};
                                                    nm.notify(R.mipmap.ic_launcher, notification);
                                                }

                                                mensagens = null;
                                                data = null;
                                            }
                                        }
                                    }
                                }catch (JSONException je) {
                                    Log.e("SDM", "Erro na thread de recuperação de mensagens");
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e("SDM", "Erro na thread de recuperação de mensagens3333");
                            }
                        });
                queue.add(jsonObjectRequest);
            }catch(Exception e) {
                Log.e("SDM", "Erro na thread de recuperação de mensagens44444");
            }
    }

    private void buscaContatos(){
        RequestQueue queue = Volley.newRequestQueue(BuscaNovasMensagens.this);
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
                                //caso seja a primeira busca ou os contatos sejam diferentes
                                if(primeiraBusca || ultimoNumeroContatos != contatos.length()){
                                    //limpa a lista


                                    for(int i=ultimoNumeroContatos;i<contatos.length();i++){
                                        JSONObject data = contatos.getJSONObject(i);
                                        //alimenta o vetor com os contatos, não adiciona ele mesmo
                                        if(data.getInt("id") != id_usuario ){
                                            listaContatos.add(new Contato(data.getInt("id"),data.getString("nome_completo"),data.getString("apelido")));
                                        }
                                    }
                                    Log.e("SDM", "LISTA ATUALIZADA");
                                    ultimoNumeroContatos = contatos.length();
                                }
                            }catch (JSONException je) {
                                Log.e("SDM", "Erro na thread de recuperação de contato");
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("SDM", "Erro na thread de recuperação de contato");
                        }
                    });
            queue.add(jsonObjectRequest);
        }catch(Exception e) {
            Log.e("SDM", "Erro na thread de recuperação de contato");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        appAberta = false;
        stopSelf();
    }


}
