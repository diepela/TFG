package com.project.MiTenisApp.BLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.project.MiTenisApp.MovementDetailsActivity;
import com.project.MiTenisApp.R;

import java.util.Calendar;

public class newMovementFragment extends Fragment {

    //Definición de variables
    private ProgressBar circularProgressBar;
    private ToggleButton startButton;
    private Button cancelButton;
    private MenuItem mRefreshItem = null;
    long millisStart;
    long millisEnd;


    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment newMovementFragment.
     */
    public static newMovementFragment newInstance() {
        return new newMovementFragment();
    }


    /**
     *  Constructor vacío necesario
     */
    public newMovementFragment() {
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
        View view = inflater.inflate(R.layout.fragment_start_capture, container, false);

        // Ocultar barra de herramientas
       ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if (view != null) {
            // Referencias a elementos del layout e inicialización
            startButton = (ToggleButton) view.findViewById(R.id.start_button);
            cancelButton = (Button) view.findViewById(R.id.cancel_button);
            circularProgressBar = (ProgressBar) view.findViewById(R.id.circularProgress);
            startButton.setChecked(false);
            startButton.setText(R.string.start_capture);

            //Evento del botón
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (startButton.isChecked()){
                        //Si está pulsado
                        startButton.setText(R.string.stop);
                        circularProgressBar.setVisibility(View.VISIBLE);
                        ((ScanActivity)getActivity()).reset();
                        ((ScanActivity)getActivity()).enableNotificationsAndConfigureSensors();
                         millisStart = Calendar.getInstance().getTimeInMillis();

                    }else{
                        //Si no está pulsado
                        startButton.setText(R.string.start_capture);
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        millisEnd = Calendar.getInstance().getTimeInMillis();
                        String mov = ((ScanActivity)getActivity()).toDatabase((millisEnd-millisStart)/1000.0);
                        ((ScanActivity)getActivity()).disableNotifications(ScanActivity.mBleGatt);
                        showDetailActivity(mov);
                    }
                }
            });

            //Eventó del botón
            cancelButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ScanActivity)getActivity()).reset();
                       cancelButton.setEnabled(false);
                       getActivity().finish();
                }
            });

        }

        setHasOptionsMenu(false);
        return view;
    }

    /**
     * Método para crear el menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.device_list, menu);
        mRefreshItem = menu.findItem(R.id.ic_refresh);

    }

    /**
     * Método que inicializa los elementos del menú
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mRefreshItem != null) {
            mRefreshItem.setEnabled(false);
        }
    }

    /**
     *  Método para iniciar la actividad de detalle
     * @param act_id identificador de la actividad que se quiere ver en detalle
     */
    private void showDetailActivity(String act_id) {
        Intent intent = new Intent(getActivity(), MovementDetailsActivity.class);
        intent.putExtra("act_id", act_id);
        startActivityForResult(intent, MovementDetailsActivity.SUCCESS);
    }

    /**
     * Método que define las acciones a realizar al recibir el resultado de otras actividades
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case MovementDetailsActivity.SUCCESS:
                    getActivity().finish();
                    break;
            }
        }
    }


}
