package br.edu.ifspsaocarlos.sdm.trabalhofinal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by AvellB155MAX on 06/07/2016.
 */
public class ListaContatoAdapter extends ArrayAdapter<Contato> {


    private LayoutInflater inflador;
    public ListaContatoAdapter(Activity tela, List<Contato> ListaContato) {
        super(tela, R.layout.item_contato, ListaContato);
        inflador = (LayoutInflater) tela.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if ( convertView == null ) {
            // infla uma nova célula
            convertView = inflador.inflate(R.layout.item_contato, null);
            holder = new ViewHolder();
            holder.txtNome = (TextView) convertView.findViewById(R.id.nome_contato);
            holder.txtApelido = (TextView) convertView.findViewById(R.id.apelido_contato);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        //getItem(postion)
        holder.txtNome.setText(getItem(position).getNome());
        holder.txtApelido.setText(getItem(position).getApelido());

        return convertView;
    }

    static class ViewHolder {
        public TextView txtNome;
        public TextView txtApelido;

    }
}
