package com.project.MiTenisApp.Usuarios;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Usuario;
import com.project.MiTenisApp.MainActivity;
import com.project.MiTenisApp.R;


public class CreateNewUserFragment extends Fragment {

    //  Definición de variables
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    private String mUserId;
    private String mUserName;

    private DatabaseSQLHelper mDatabaseSQLHelper;

    private EditText mNameField;
    private EditText mAgeField;
    private EditText mWeightField;
    private EditText mHeightField;


    /**
     *  Constructor vacío necesario
     */
    public CreateNewUserFragment() {
    }

    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment CreateNewUserFragment.
     */
    public static CreateNewUserFragment newInstance(String userId) {
        CreateNewUserFragment fragment = new CreateNewUserFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID,userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Método que define las acciones al crearse el fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtener los datos de la actividad
        if (getArguments() != null) {
            mUserId = getArguments().getString(USER_ID);
        }
    }

   /**
    *  Método que define las acciones a realizar al crearse las vistas
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Definir el layout a usar
        View root = inflater.inflate(R.layout.fragment_new_user, container, false);

        //Referencias a objetos del layout
        FloatingActionButton mSaveButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mNameField = (EditText) root.findViewById(R.id.user_name_text);
        mAgeField = (EditText) root.findViewById(R.id.user_age_text);
        mWeightField = (EditText) root.findViewById(R.id.user_weight_text);
        mHeightField = (EditText) root.findViewById(R.id.user_height_text);
        mDatabaseSQLHelper = new DatabaseSQLHelper(getActivity());


        // Cargar y mostrar datos guardados del usuario
        if (mUserId != null) {
            getUserInfo();
        }

        // Evento del botón
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Capturar nueva información y almacenarla en una clase Usuario
                Usuario user = getNewInfo();

                //Crear usuario o modificarlo con la nueva información
                if (mUserId != null) {
                    updateUser(user);
                } else {
                    createUser(user);
                }
            }
        });
        return root;
    }

    /**
     *  Método para cargar la información guardada del usuario
     */
    private void getUserInfo(){
        Cursor c= mDatabaseSQLHelper.getUserById(mUserId);
        if (c != null && c.moveToLast()) {
            showUser(new Usuario(c));
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        }
        mDatabaseSQLHelper.getWritableDatabase().close();
    }

    /**
     *  Método para inicializar los elementos del layout con la información guardada del usuario
     * @param user usuario del cual se han de cargar los datos
     */
    private void showUser(Usuario user) {
        mNameField.setText(user.getName());
        mAgeField.setText(user.getAge().toString());
        mWeightField.setText(user.getWeight().toString());
        mHeightField.setText(user.getHeight().toString());
    }

    /**
     *  Método para almacenar la nueva información en un usuario de la clase Usuario
     * @return Usuario con los datos almacenados
     */
    private Usuario getNewInfo() {

        //Capturar nueva información
        String name = mNameField.getText().toString();
        Integer age = Integer.parseInt(mAgeField.getText().toString());
        Double weight = Double.parseDouble(mWeightField.getText().toString());
        Integer height = Integer.parseInt(mHeightField.getText().toString());

        //Actualizar variable
        mUserName=name;

        //Definir usuario con la nueva información
        Usuario user = new Usuario(name,age,weight,height);
        return user;
    }

    /**
     *  Método para guardar el nuevo usuario en la base de datos
     * @param users usuario a guardar
     */
    private void createUser(Usuario... users) {
        long i = mDatabaseSQLHelper.saveUser(users[0]);
        showUsersScreen(i>0);
    }

    /**
     *  Método para actualizar el usuario en la base de datos
     * @param users usuario a actualizar
     */
    private void updateUser(Usuario... users) {
        int i= mDatabaseSQLHelper.updateUser(users[0], mUserId);
        showUsersScreen(i > 0);
    }

    /**
     *  Método para mostrar la lista de selección de usuarios
     * @param check true si se ha actualizado correctamente la información
     *              false si han habido errores
     */
    private void showUsersScreen(Boolean check) {

        //Comprobar acciones correctamente realizadas
        if (!check) {
            Toast.makeText(getActivity(),
                    "Error al agregar nueva información", Toast.LENGTH_SHORT).show();
            getActivity().setResult(Activity.RESULT_CANCELED);
        } else {
            Intent intent = new Intent(getActivity(),MainActivity.class);
            intent.putExtra(USER_NAME, mUserName);
            getActivity().setResult(Activity.RESULT_OK,intent);
        }
        getActivity().finish();
    }
}