package com.project.MiTenisApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project.MiTenisApp.BLE.ScanActivity;


public class RegisterActivityFragment extends Fragment {

    //Definición de variables
    private Button mNewActivity;
    private boolean multiple;

    /**
     *  Constructor vacío necesario
     */
    public RegisterActivityFragment() {
    }

    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment RegisterActivityFragment
     */
    public static RegisterActivityFragment newInstance() {
        return new RegisterActivityFragment();
    }

    /**
     * Método que define las acciones al crearse el fragment
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método que define las acciones al crearse la vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Definir el layout a usar
        View root= inflater.inflate(R.layout.fragment_register_activity, container, false);

        //Referencias a objetos del layout
        mNewActivity = (Button) root.findViewById(R.id.new_activity);

        //Evento del botón
        mNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el SharedPref
                MultipleToShared();
                // Empezar una nueva actividad
                NewRegisterActivity();
            }
        });

        return root;
    }

    /**
     *   Método para empezar una nueva actividad de búsqueda de dispositivos
     **/
    private void NewRegisterActivity(){
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(MainActivity.EXTRA_USER_ID,  ((MainActivity)getActivity()).mUserId);
        startActivity(intent);
    }

    private void MultipleToShared(){
        multiple = ((MainActivity)getActivity()).multiple;
        SharedPreferences prefs = (getActivity()).getSharedPreferences("multiple", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("multiple", multiple);
        editor.commit();
    }



}

