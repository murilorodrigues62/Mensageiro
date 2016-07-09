package br.edu.ifspsaocarlos.sdm.trabalhofinal.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;

/**
 * Created by root on 07/07/16.
 */
public class EnviarFragment extends Fragment {
    private Button btEnviar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enviar, container, false);

        btEnviar = (Button) view.findViewById(R.id.btnEnviar);
        btEnviar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ((MensagemActivity) getActivity()).enviarMensagem();
                                        }
                                    }
        );

        return view;
    }

}