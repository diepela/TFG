package com.project.MiTenisApp;



import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Definitions;



public class RecordsFragment extends Fragment {

    //Definición de variables
    public static final String ACT_ID= "act_id";
    private DatabaseSQLHelper mDatabaseSQLHelper;
    private MovementCursorAdapter mMovementAdapter;
    private ListView mListView;

    /**
     *  Constructor vacío necesario
     */
    public RecordsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        //Nueva instancia de conexión a la base de datos
        mDatabaseSQLHelper = new DatabaseSQLHelper(getContext());

        //Referencias a objetos del layout
        mListView = (ListView) view.findViewById(R.id.records_ListView);

        //Creación de un nuevo adaptador de cursor de la clase MovementCursorAdapter
        mMovementAdapter = new MovementCursorAdapter(getActivity(),null);

        //Inicialización de la lista con el adaptador
        mListView.setAdapter(mMovementAdapter);

        //Carga de datos en el adaptador
        loadActivities();

        //Evento al seleccionar un movimiento de la lista
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mMovementAdapter.getItem(i);
                String id = currentItem.getString(currentItem.getColumnIndex(Definitions.ActivityEntry.MOV));
                //Empezar una nueva actividad
                showDetailActivity(id);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivities();

    }

    /**
     *  Método que carga la información de cada actividad en el adaptador
     */
    private void loadActivities(){
        Cursor c = mDatabaseSQLHelper.getActivitiesByUserId(((MainActivity)getActivity()).mUserId);
        if (c != null && c.getCount() >= 0) {
            mMovementAdapter.swapCursor(c);
        }
    }

    /**
     *  Método para iniciar la actividad de detalle
     * @param act_id identificador de la actividad que se quiere ver en detalle
     */
    private void showDetailActivity(String act_id) {
        Intent intent = new Intent(getActivity(), MovementDetailsActivity.class);
        intent.putExtra(ACT_ID, act_id);
        startActivity(intent);
    }

}


