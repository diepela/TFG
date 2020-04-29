package com.project.MiTenisApp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class SaveActivityFragment extends Fragment {

    //Inicialización de variables
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private String name_file;
    private EditText mNameFile;
    private MenuItem save, clear;


    /**
     *  Constructor vacío necesario
     */
    public SaveActivityFragment() {
    }

    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment SaveActivityFragment.
     */
    public static SaveActivityFragment newInstance() {
        return new SaveActivityFragment();
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
        View view= inflater.inflate(R.layout.fragment_save_activity, container, false);

        // Mostrar barra de herramientas
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Referencias a objetos del layout
        FloatingActionButton mDownloadButton = (FloatingActionButton) view.findViewById(R.id.download);
        mNameFile = (EditText) view.findViewById(R.id.file_name_text);

        //Definir nombre de archivo por defecto
        mNameFile.setText(((MovementDetailsActivity)getActivity()).ID);

        //Comprobar si posee permiso de almacenamiento y si no pedir al usuario
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        // Evento del botón
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Capturar nuevo nombre de archivo
                name_file = mNameFile.getText().toString();

                //Generar fichero con los datos
                boolean saved = ((MovementDetailsActivity)getActivity()).saveActivity(name_file);

                //Mostrar mensaje dependiendo de si se ha guardado con éxito o no
                if(saved) {
                    Toast.makeText(getActivity(),
                            "Archivo generado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),
                            "Error al generar archivo", Toast.LENGTH_SHORT).show();
                }

                //Finalizar la actividad
                getActivity().finish();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    /**
     * Método para comprobar resultado petición permiso escritura en almacenamiento externo
      */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Acceso limitado");
                    builder.setMessage("Se debe permitir acceso a la ubicación para poder guardar el fichero");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getActivity().finish();
                        }
                    });
                    builder.show();
                }
            }
        }
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
            save.setEnabled(false).setVisible(false);
        }
        if (clear != null) {
            clear.setEnabled(false).setVisible(false);
        }
    }

}
