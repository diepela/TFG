package com.project.MiTenisApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MovementDetailsFragment extends Fragment {

    //Definición de variables
    TextView mDate, mUser, mDevice, mBrazo;
    ImageView img;
    MenuItem save, clear;
    String golpe;


    /**
     *  Constructor vacío necesario
     */
    public MovementDetailsFragment() {
    }

    /**
     * Método que define las acciones al crearse el fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     *  Método que define las acciones a realizar al crearse la vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Definir el layout a usar
        View view = inflater.inflate(R.layout.fragment_activity_detail, container, false);
        Log.i("funca", "aqui si");

        //Referencias a objetos del layout e inicialización
        mDate = (TextView) view.findViewById(R.id.register_value);
        mDate.setText(((MovementDetailsActivity)getActivity()).date);

        mUser = (TextView) view.findViewById(R.id.by_value);
        mUser.setText(((MovementDetailsActivity)getActivity()).user);

        mDevice = (TextView) view.findViewById(R.id.device_value);
        mDevice.setText(((MovementDetailsActivity)getActivity()).device);

        mBrazo = (TextView) view.findViewById(R.id.brazo_value);
        mBrazo.setText(((MovementDetailsActivity)getActivity()).brazo);

        img = view.findViewById(R.id.tipo_golpe_imagen);
        golpe = ((MovementDetailsActivity)getActivity()).tipoGolpe;
        if(golpe.equals("Derecha")){
            img.setImageResource(R.drawable.federer_drive);
        } else if(golpe.equals("Revés")){
            img.setImageResource(R.drawable.federer_reves);
        } else if(golpe.equals("Mala detección")){
            img.setImageResource(R.drawable.error);
        }

        return view;

    }


    /**
     * Método para crear el menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.save_activity, menu);
        save = menu.findItem(R.id.ic_save);
        clear = menu.findItem(R.id.ic_clear);

    }

    /**
     * Método que inicializa los elementos del menú
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (save != null) {
            save.setEnabled(false);
        }
        if (clear != null) {
            clear.setEnabled(false);
        }
    }



}
