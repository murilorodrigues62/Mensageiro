package br.edu.ifspsaocarlos.sdm.trabalhofinal.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.model.Mensagem;

/**
 * Created by root on 09/07/16.
 */
public class ListaMensagemAdapter extends ArrayAdapter<Mensagem> {
    private LayoutInflater inflater;

    public ListaMensagemAdapter(Activity tela, List<Mensagem> objects) {
        super(tela, R.layout.item_mensagem, objects);
        inflater = (LayoutInflater) tela.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mensagem, null);
            holder = new ViewHolder();
            holder.txtOrigem = (TextView) convertView.findViewById(R.id.mensage_origem);
            holder.txtAssunto = (TextView) convertView.findViewById(R.id.mensagem_assunto);
            holder.txtCorpo = (TextView) convertView.findViewById(R.id.mensagem_corpo);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtOrigem.setText(getItem(position).getOrigem());
        holder.txtAssunto.setText(getItem(position).getAssunto());
        holder.txtCorpo.setText(getItem(position).getCorpo());

        return convertView;
    }

    static class ViewHolder {
        public TextView txtOrigem;
        public TextView txtAssunto;
        public TextView txtCorpo;
    }
}
