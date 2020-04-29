package com.project.MiTenisApp.Usuarios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.project.MiTenisApp.R;

public class CreateNewUser extends AppCompatActivity {

    //  Definición de variables
    public static final int REQUEST_ADD_USER = 1;

    /**
     * Método que define las acciones al crearse la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir el layout a usar
        setContentView(R.layout.activity_new_user);

        //Definición de la barra de herramientas
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inicializar variable con los datos de la actividad anterior
        String userId = getIntent().getStringExtra(UsersActivity.EXTRA_USER_ID);

        //Definir el título de la barra de herramientas
        setTitle(userId == null ? "Añadir usuario" : "Editar usuario");

        //Definición de un fragment
        CreateNewUserFragment CreateNewUserFrag = (CreateNewUserFragment)
                    getSupportFragmentManager().findFragmentById(R.id.create_newuser_container);

        //Añadir fragment
        if (CreateNewUserFrag == null) {
            CreateNewUserFrag = CreateNewUserFragment.newInstance(userId);
            getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.create_newuser_container, CreateNewUserFrag)
                        .commit();
        }
    }

    /**
     * Método para volver atrás al pulsar el botón de atrás del menú
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
