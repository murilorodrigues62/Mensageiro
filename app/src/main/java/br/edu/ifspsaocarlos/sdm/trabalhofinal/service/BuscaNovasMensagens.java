package br.edu.ifspsaocarlos.sdm.trabalhofinal.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
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

import br.edu.ifspsaocarlos.sdm.trabalhofinal.view.CadastroActivity;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.model.Contato;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.view.MensagemActivity;

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
    private static JSONObject data;
    private static JSONArray mensagens;
    private static String urlMensagem;
    private static RequestQueue queue ;

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
        novoNumeroMensagem = 0;
        listaContatos = new ArrayList<Contato>();
        queue = Volley.newRequestQueue(BuscaNovasMensagens.this);

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
                    // novoNumeroMensagem = ultimoNumeroMensagem+1;

                    for(Contato c: listaContatos) {
                        buscaMensagens(c.getId(),primeiraBusca);
                    }
                    primeiraBusca = false;
                    //ultimoNumeroMensagem = novoNumeroMensagem;
                    Log.e("SDM", "PASSOU POR AQUI");
                }else {
                    editor = sharedPref.edit();
                    id = sharedPref.getString("id_usuario", "0");
                }
            }catch (InterruptedException ie) {
                Log.e("SDM", "Erro na thread de recuperação de mensagens");
            }
        }
    }

    private void buscaMensagens(int id_origem,boolean primeiraVez){
        // final int ultima_mensagem = ultimoNumeroMensagem+1;
        urlMensagem = getString(R.string.urlBase) + "mensagem/"+(ultimoNumeroMensagem+1)+"/"+id_origem+"/"+id;
        final Integer id_usuario = Integer.parseInt(id);
        final boolean primeiraTentativa = primeiraVez;

        try{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    urlMensagem,
                    null,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject s) {
                            //chama o adaptador
                            try{
                                mensagens = (JSONArray)s.getJSONArray("mensagens");
                                if(mensagens.length() > 0){
                                    data = mensagens.getJSONObject(mensagens.length()-1);
                                    //caso seja a primeira busca a ultima mensagem que já chegou pra ele
                                    Log.e("SDM", "bbbb");
                                    if(primeiraTentativa){
                                        Log.e("SDM", "AAA"+data.getInt("id"));
                                        if(data.getInt("id") >= ultimoNumeroMensagem){
                                            ultimoNumeroMensagem = data.getInt("id");
                                        }
                                    }else{ //verifica qual vai ser a nova ultima mensagem e guarda as notificações para exibir
                                        if(data.getInt("id") >= ultimoNumeroMensagem){
                                            Log.e("SDM", "NOVA MENSAGEM"+data.getInt("id")+" - "+novoNumeroMensagem+" - "+ultimoNumeroMensagem);
                                            if(data.getInt("id") >= ultimoNumeroMensagem){
                                                ultimoNumeroMensagem = data.getInt("id");
                                            }
                                            for (int i = 0; i < mensagens.length();i++){
                                                //monta a notificação para as mensanges encontradas
                                                JSONObject noti = mensagens.getJSONObject(i);
                                                NotificationManager nm = (NotificationManager)
                                                        getSystemService(NOTIFICATION_SERVICE);
                                                Intent intent = new Intent(BuscaNovasMensagens.this, MensagemActivity.class);
                                                intent.putExtra("id_destino",noti.getInt("origem_id"));
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
                                    listaContatos.clear();

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
