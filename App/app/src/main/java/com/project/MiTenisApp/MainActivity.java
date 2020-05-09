package com.project.MiTenisApp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.MiTenisApp.BaseDatos.Golpe;
import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Movimiento;
import com.project.MiTenisApp.Usuarios.CreateNewUser;
import com.project.MiTenisApp.Usuarios.CreateNewUserFragment;
import com.project.MiTenisApp.Usuarios.UsersActivity;

import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    //Definición de variables
    public String mUserId;
    public String mUserName;
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USER_NAME= "extra_user_name";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private DatabaseSQLHelper mDatabaseSQLHelper;

    //Definición de fragments
    final Fragment registerActivity = new RegisterActivityFragment();
    final Fragment records = new RecordsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = registerActivity;

    /**
     * Método que define las acciones al crearse la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir el layout a usar
        setContentView(R.layout.activity_main);

        //Obtener los datos de la actividad anterior
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
          mUserName = b.getString(EXTRA_USER_NAME);
          mUserId = b.getString(EXTRA_USER_ID);
        }

        //Nueva instancia de conexión a la base de datos
        mDatabaseSQLHelper = new DatabaseSQLHelper(this);

        //Definición e inicialización de la barra de herramientas
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(mUserName);
        myToolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        //Añadir los fragments a usar y establecer el activo
        fm.beginTransaction().add(R.id.main_container, records, "2").hide(records).commit();
        fm.beginTransaction().add(R.id.main_container,registerActivity, "1").commit();

        //Referencias a objetos del layout
        BottomNavigationView bNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //Evento de la barra inferior de navegación
        bNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_register_activity:
                                fm.beginTransaction().hide(active).hide(records).show(registerActivity).commit();
                                active = registerActivity;
                                item.setChecked(true);
                                break;

                            case R.id.action_record:
                                fm.beginTransaction().hide(active).show(records).hide(registerActivity).commit();
                                active = records;
                                item.setChecked(true);
                                break;
                        }
                        return false;
                    }
                });
        bNavigation.setSelectedItemId(R.id.action_register_activity);
    }

    /**
     * Método que define las acciones al restablecer la actividad
     */
    protected void onRestart() {
        super.onRestart();
        Toolbar myToolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(mUserName);
    }

    /**
     * Método que define las acciones al destruirse la actividad
     */
    @Override
    protected void onDestroy(){
        mDatabaseSQLHelper.close();
        super.onDestroy();
    }

    /**
     * Método para crear el menú
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Método para volver atrás al pulsar el botón de atrás del dispositivo
     */
    public void onBackPressed(){
    }

    /**
     * Método que define las acciones a realizar al pulsar los elementos del menú
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                item.isEnabled();

                //Comprobar si posee permiso de almacenamiento y si no pedir al usuario
                if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }

                boolean saved = downloadPersonalMovements(mUserId);

                //Mostrar mensaje dependiendo de si se ha guardado con éxito o no
                if(saved) {
                    Toast.makeText(this,
                            "Archivos generados correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,
                            "Error al generar los archivos", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.action_change_user:
                item.isEnabled();
                this.setResult(Activity.RESULT_OK);
                this.finish();
                break;

            case R.id.action_change_data:
                item.isEnabled();
                showEditScreen();
                break;

            case R.id.action_delete_user:
                item.isEnabled();
                final AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setMessage("Se borrará el usuario con todos sus datos registrados. ¿Desea continuar?").setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                delete(mUserId, true);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            }
                        });
                AlertDialog aler = build.create();
                aler.show();
                break;

            case R.id.action_delete_data:
                item.isEnabled();
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Se borrarán todos los datos registrados de este usuario. ¿Desea continuar?").setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                delete(mUserId, false);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Método que define las acciones a realizar al recibir el resultado de otras actividades
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case CreateNewUser.REQUEST_ADD_USER:
                    Toast.makeText(this,
                            "Usuario modificado correctamente", Toast.LENGTH_SHORT).show();
                    Bundle b=data.getExtras();
                    if (b!=null){
                        mUserName=b.getString(CreateNewUserFragment.USER_NAME);
                    }
                    break;
            }
        }
    }


    /**
     * Método para comprobar resultado petición permiso escritura en almacenamiento externo
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Acceso limitado");
                    builder.setMessage("Se debe permitir acceso a la ubicación para poder guardar el fichero");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    /**
     * Método para eliminar al usuario, las actividades y sus movimientos de la base de datos
     * y mostrar la selección de usuarios
     * @param userId identificador del usuario
     * @param user true si se desea eliminar al usuario
     *             false si se desea eliminar solo las actividades y movimientos
     */
    private void delete(String userId, boolean user){
     int i=1, k=1;
      if(user){
          i = mDatabaseSQLHelper.deleteUser(userId);
      }
      Cursor c = mDatabaseSQLHelper.getActivitiesByUserId(userId);

      if (c.moveToFirst()) {
            //Recorrer el cursor hasta que no haya más registros
            do {
                Golpe g = new Golpe(c);
                String mov = g.getMov();
                mDatabaseSQLHelper.deleteMovementByMov(mov);
            } while(c.moveToNext());
        }
       int j = mDatabaseSQLHelper.deleteActivityByUserId(userId);

       showUsersScreen((i>0), user);
    }

    /**
     * Método para mostrar la actividad de selección de usuarios
     * @param success true si se ha eliminado correctamente
     *                false si ha habido un error
     */
    private void showUsersScreen(boolean success, boolean user) {
        if (!success) {
            if(!user) {
                Toast.makeText(this,
                        "Error al eliminar los datos ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,
                        "Error al eliminar el usuario ", Toast.LENGTH_SHORT).show();
            }
        }else {
            if (!user) {
                Toast.makeText(this,
                        "Datos eliminados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Usuario eliminado correctamente ", Toast.LENGTH_SHORT).show();
            }
        }
        this.setResult(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        this.finish();
    }

    /**
     * Método para mostrar la actividad de creación/edición de usuarios
     */
    private void showEditScreen() {
        Intent intent = new Intent(this, CreateNewUser.class);
        intent.putExtra(UsersActivity.EXTRA_USER_ID, mUserId);
        startActivityForResult(intent, CreateNewUser.REQUEST_ADD_USER);
    }

    /**
     * Método para guardar todas las actividades y movimientos del usuario en un fichero
     * @param userId identificador del usuario
     * @return true si se guarda correctamente
     *         false si hay error
     */
    private boolean downloadPersonalMovements(String userId){

        boolean i = true;
        Cursor c = mDatabaseSQLHelper.getActivitiesByUserId(userId);

        if (c.moveToFirst()) {
            //Recorrer el cursor hasta que no haya más registros
            do {
                Golpe g = new Golpe(c);
                i = i & saveActivity(g);
            } while(c.moveToNext());
        }
        return i;
    }

    /**
     * Método para guardar la información del golpe y de los movimientos en un fichero
     * @param g golpe a guardar
     * @return true si se guarda correctamente
     *         false si hay error
     */
    public boolean saveActivity(Golpe g) {
        boolean t = false;
        if (isExternalStorageWritable()) {
            String directory = getPublicDocStorageDir();
            boolean first = true;
            final String NEXT_LINE = "\n";
            Cursor c = mDatabaseSQLHelper.getMovementsByActivityId(g.getMov());
            try {
                FileWriter fw=new FileWriter(directory + g.getMov() + ".csv");
                if (c.moveToFirst()) {
                    //Recorrer el cursor hasta que no haya más registros
                    do {
                        if(first){
                            fw.write("Golpe:" + "," + g.getMov() + "," + "," + "," + "Usuario:" +  "," + g.getName() + NEXT_LINE);
                            fw.write("Fecha y hora:" + "," + g.getDate() + " , "  + g.getTime() + "," + "," + "Edad:" +  "," + g.getAge().toString() + NEXT_LINE);
                            fw.write("Dispositivo:" + "," + g.getDevice() + "," + "," + "," + "Brazo dominante:" +  "," + g.getBrazo() + NEXT_LINE);
                            fw.write("Duración (s): " + "," + g.getDuration().toString() + "," + "," + "," + NEXT_LINE + NEXT_LINE);
                            fw.write("ID, Tiempo, ACC_X ,ACC_Y ,ACC_Z, GYR_X, GYR_Y, GYR_Z, MAG_X, MAG_Y, MAG_Z" + NEXT_LINE);
                        }
                        Movimiento m = new Movimiento(c);
                        fw.write(m.getId().toString() + ",");
                        fw.write(m.getTimestamp().toString() + ",");
                        fw.write(m.getAcc_X().toString() + ",");
                        fw.write(m.getAcc_Y().toString() + ",");
                        fw.write(m.getAcc_Z().toString() + ",");
                        fw.write(m.getGyr_X().toString() + ",");
                        fw.write(m.getGyr_Y().toString() + ",");
                        fw.write(m.getGyr_Z().toString() + ",");
                        fw.write(m.getMag_X().toString() + ",");
                        fw.write(m.getMag_Y().toString() + ",");
                        fw.write(m.getMag_Z().toString() + ",");
                        fw.append(NEXT_LINE);
                        first = false;

                    } while (c.moveToNext());
                    fw.close();
                }
                t= true;

            } catch (IOException e) {
                // Error al crear el archivo
                t= false;
            }
        }

        return t;
    }

    /**
     * Método para comprobar si el almacenamiento externo está disponible para escribir
     * @return true si se puede escribir
     *         false si no se puede escribir
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Método para conseguir la ruta del directorio público de los documentos del usuario
     * @return la ruta del directorio
     */
    public String getPublicDocStorageDir() {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getPath();
    }


}