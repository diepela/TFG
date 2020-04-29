package com.project.MiTenisApp.Usuarios;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Definitions;
import com.project.MiTenisApp.MainActivity;
import com.project.MiTenisApp.R;

public class UsersFragment extends Fragment {

    private DatabaseSQLHelper mDatabaseSQLHelper;
    private UserCursorAdapter mUserAdapter;
    public static final int REQUEST_USER_MAIN = 2;

    /**
     *  Constructor vacío necesario
     */
    public UsersFragment() {
    }

    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment UsersFragment.
     */
    public static UsersFragment newInstance() {
        return new UsersFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Definir el layout a usar
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        //Referencias a objetos del layout
        ListView mUsersList = (ListView) root.findViewById(R.id.users_list);
        Button mAddButton = getActivity().findViewById(R.id.new_user);

        //Creación de un nuevo adaptador de cursor de la clase UserCursorAdapter
        mUserAdapter = new UserCursorAdapter(getActivity(), null);

        //Inicialización de la lista con el adaptador
        mUsersList.setAdapter(mUserAdapter);

        //Evento al seleccionar un usuario de la lista
        mUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cursor c = (Cursor) mUserAdapter.getItem(i);
                        String currentUserId = c.getString(c.getColumnIndex(Definitions.UsuarioEntry._ID));
                        String currentUserName = c.getString(c.getColumnIndex(Definitions.UsuarioEntry.FIELD_NAME));
                        showMainActivity(currentUserId,currentUserName);
                    }
        });

        //Nueva instancia de conexión a la base de datos
        mDatabaseSQLHelper = new DatabaseSQLHelper(getActivity());

        // Carga de datos
        loadUsers();

        //Evento del botón
        mAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddScreen();
                    }
                });

        return root;
    }

    /**
     * Método para mostrar la actividad de edición/creación de usuarios
     */
    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), CreateNewUser.class);
        startActivityForResult(intent, CreateNewUser.REQUEST_ADD_USER);
    }

    /**
     * Método para mostrar la actividad principal
     */
    private void showMainActivity(String userId,String userName) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USER_ID, userId);
        intent.putExtra(MainActivity.EXTRA_USER_NAME, userName);
        startActivityForResult(intent, REQUEST_USER_MAIN);
    }

    /**
     * Método que define las acciones a realizar al recibir el resultado de otras actividades
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_USER_MAIN:
                   loadUsers();
                    break;

                case CreateNewUser.REQUEST_ADD_USER:
                    Toast.makeText(getActivity(),
                            "Usuario guardado correctamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    break;

                default:
                    loadUsers();
                    break;
            }
        }
    }

    /**
     *  Método que carga la información de cada usuario en el adaptador
     */
    private void loadUsers(){
        Cursor c = mDatabaseSQLHelper.getAllUsers();
        if (c != null && c.getCount() >= 0) {
            mUserAdapter.swapCursor(c);
        } else {

        }
    }

}
