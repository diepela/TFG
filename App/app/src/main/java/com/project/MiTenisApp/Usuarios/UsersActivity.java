package com.project.MiTenisApp.Usuarios;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.project.MiTenisApp.R;

public class UsersActivity extends AppCompatActivity {

    //Definición de variables
    public static final String EXTRA_USER_ID = "extra_user_id";

    /**
     * Método que define las acciones al crearse la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir el layout a usar
        setContentView(R.layout.activity_users);

        //Definición e inicialización de la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Definición de un fragment
        UsersFragment fragment = (UsersFragment)
                getSupportFragmentManager().findFragmentById(R.id.users_container);

        //Añadir un nuevo fragment
        if (fragment == null) {
            fragment = UsersFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.users_container, fragment)
                    .commit();
        }

        //Definición y evento del botón
        Button new_user = findViewById(R.id.new_user);
       new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    /**
     * Método que define las acciones al restablecer la actividad
     */
    protected void onRestart(){
        super.onRestart();

        //Definición del fragment
        UsersFragment fragment = (UsersFragment)
                getSupportFragmentManager().findFragmentById(R.id.users_container);

        //Añadir un nuevo fragment
        if (fragment == null) {
            fragment = UsersFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.users_container, fragment)
                    .commit();
        }

    }
}