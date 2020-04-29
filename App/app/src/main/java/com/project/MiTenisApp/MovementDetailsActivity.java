package com.project.MiTenisApp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.MiTenisApp.BLE.ScanActivity;
import com.project.MiTenisApp.BaseDatos.Actividad;
import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Movimiento;
import com.github.mikephil.charting.data.Entry;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MovementDetailsActivity extends AppCompatActivity {

    //Definición de variables
    public static final String ACT_ID= "act_id";
    public static final int SUCCESS = 1;
    public String ID;

    DatabaseSQLHelper mDatabaseSQLHelper;
    String date, user, age, height, weight, device, mov, duration;
    MenuItem save;
    ArrayList<Integer> x = new ArrayList<>();
    ArrayList<Entry> y_acc_x =  new ArrayList<>();
    ArrayList<Entry> y_acc_y =  new ArrayList<>();
    ArrayList<Entry> y_acc_z =  new ArrayList<>();

    //Definición de fragmentos
    final Fragment MovementDetails = new MovementDetailsFragment();
    final Fragment SaveActivity = new SaveActivityFragment();
    final FragmentManager fm = getSupportFragmentManager();

    /**
     * Método que define las acciones al crearse la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir el layout a usar
        setContentView(R.layout.activity_mov_details);

        //Obtener los datos de la actividad o fragment anterior
        Bundle b = getIntent().getExtras();
        if (b != null) {
            ID = b.getString(ACT_ID);
        }

        //Nueva instancia de conexión a la base de datos
        mDatabaseSQLHelper = new DatabaseSQLHelper(this);

        //Obtener los movimientos y la actividad a representar
        getMovements();
        getActivity();

        //Definición e inicialización de la barra de herramientas
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Actividad " + mov);

        //Añadir un nuevo fragment
        fm.beginTransaction().add(R.id.container_4, MovementDetails).commit();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_activity, menu);
        save = menu.findItem(R.id.ic_save);
        return true;
    }

    /**
     * Método que inicializa los elementos del menú
     * @return true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (save != null) {
            //Habilitar el botón de guardado en el menú
            save.setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Método que define las acciones a realizar al pulsar los elementos del menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_save) {
            //Cambiar de fragment al pulsar el botón de guardar
            fm.beginTransaction().add(R.id.container_4,SaveActivity).hide(MovementDetails).commit();
            return true;
        }

        if (id == R.id.ic_clear) {
            boolean saved = deleteActivity();
            //Mostrar mensaje dependiendo de si se ha eliminado con éxito o no
            if(saved) {
                Toast.makeText(this,
                        "Actividad eliminada correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,
                        "Error al eliminar la actividad", Toast.LENGTH_SHORT).show();
            }
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * Método para volver atrás al pulsar el botón de atrás del dispositivo
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ScanActivity.class);
        this.setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
        finish();
    }

    /**
     * Método para cargar la información de la actividad a mostrar
     */
    private void getActivity(){
        Cursor c= mDatabaseSQLHelper.getActivityByMovId(ID);
        if (c != null && c.moveToLast()) {
            Actividad a = new Actividad(c);
            date = a.getDate() + " , "  + a.getTime();
            user = a.getName();
            age = a.getAge().toString();
            height = a.getHeight().toString();
            weight = a.getWeight().toString();
            mov = a.getId().toString();
            device = a.getDevice();
            duration = a.getDuration().toString();
        }
    }

    /**
     * Método para cargar los movimientos a mostrar en un ArrayList
     */
    private void getMovements(){
        Cursor c= mDatabaseSQLHelper.getMovementsByActivityId(ID);
        if (c.moveToFirst()) {
            //Recorrer el cursor hasta que no haya más registros
            do {
                Movimiento m = new Movimiento(c);
                y_acc_x.add(new Entry(m.getTimestamp(),m.getAcc_X().floatValue()));
                y_acc_y.add(new Entry(m.getTimestamp(),m.getAcc_Y().floatValue()));
                y_acc_z.add(new Entry(m.getTimestamp(),m.getAcc_Z().floatValue()));
            } while(c.moveToNext());
        }
    }

    /**
     * Método para guardar la información de la actividad y de los movimientos en un fichero
     * @param nameFile Nombre del fichero a generar
     * @return true si se guarda correctamente
     *         false si hay error
     */
    public boolean saveActivity(String nameFile) {
        boolean t = false;
        if (isExternalStorageWritable()) {
            String directory = getPublicDocStorageDir();
            boolean first = true;
            final String NEXT_LINE = "\n";
            Cursor c = mDatabaseSQLHelper.getMovementsByActivityId(ID);
            try {
                FileWriter fw=new FileWriter(directory + nameFile + ".csv");
                if (c.moveToFirst()) {
                    //Recorrer el cursor hasta que no haya más registros
                    do {
                        //Definición de la cabecera
                        if(first){
                            fw.write("Actividad:" + "," + mov + "," + "," + "," + "Usuario:" +  "," + user + NEXT_LINE);
                            fw.write("Fecha y hora:" + "," + date + "," + "," + "Edad:" +  "," + age + NEXT_LINE);
                            fw.write("Dispositivo:" + "," + device + "," + "," + "," + "Altura (cm):" +  "," + height + NEXT_LINE);
                            fw.write("Duracion (s): " + "," + duration + "," + "," + "," + "Peso (kg):" +  "," + weight + NEXT_LINE + NEXT_LINE);
                                fw.write("ID, Tiempo, ACC_X ,ACC_Y ,ACC_Z, GYR_X, GYR_Y, GYR_Z, MAG_X, MAG_Y, MAG_Z" + NEXT_LINE);
                        }
                        Movimiento m = new Movimiento(c);
                        //Introducción de todos los elementos de la clase Movimiento
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


    /**
     * Método para eliminar la actividad y sus movimientos de la base de datos
     * @return  true si ha tenido éxito
     *          false si no
     */
    private boolean deleteActivity(){

        int i = mDatabaseSQLHelper.deleteMovementByMov(mov);

        int j = mDatabaseSQLHelper.deleteActivityByMovId(mov);

        return((i+j)>0);
    }
}
