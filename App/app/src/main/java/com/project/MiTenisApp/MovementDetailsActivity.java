package com.project.MiTenisApp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.MiTenisApp.BLE.ScanActivity;
import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Golpe;
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
    String date, user, age, brazo, tipoGolpe, device, mov, duration;
    MenuItem save;

    // Variables para obtener datos medidos
    Float acc_x;
    Float acc_y;
    Float acc_z;

    Float gyr_x;
    Float gyr_y;
    Float gyr_z;

    Float mag_x;
    Float mag_y;
    Float mag_z;

    // Donde se almacenará el cuaternión tras pasar por Madgwick
    float[] quaternion;
    float[] qF = new float[4];

    // Objeto Madgwick (Beta 0.041 valor recomendado, habrá que hacer pruebas)
    private MadgwickAHRS mMadgwickAHRS = new MadgwickAHRS(0.02f, 0.041f);

    // Arrays donde meteremos los ejes del cuaternión
    ArrayList<Entry> quatX = new ArrayList<>();
    ArrayList<Entry> quatY = new ArrayList<>();
    ArrayList<Entry> quatW = new ArrayList<>();
    ArrayList<Entry> quatZ = new ArrayList<>();

    // Definición de fragmentos
    final Fragment MovementDetails = new MovementDetailsFragment();
    final Fragment MovementGraficas = new MovementGraficasFragment();
    final Fragment SaveActivity = new SaveActivityFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = MovementDetails;

    public MovementDetailsActivity() {
    }

    /**
     * Método que define las acciones al crearse la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        Log.i("qf-x", ""+qF[0]);
        Log.i("qf-y", ""+qF[1]);
        Log.i("qf-w", ""+qF[2]);
        Log.i("qf-z", ""+qF[3]);
        detectarGolpe();        // Con los datos de Madgwick detectamos qué tipo de golpe se ha realizado

        //Definición e inicialización de la barra de herramientas
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Actividad " + mov);

        //Añadir un nuevo fragment
        fm.beginTransaction().add(R.id.container_4, MovementGraficas, "2").hide(MovementGraficas).commit();
        fm.beginTransaction().add(R.id.container_4, MovementDetails, "1").commit();

        // Referencias a objetos del layout
        BottomNavigationView bNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //Evento de la barra inferior de navegación
        bNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_detalles_golpe:
                                fm.beginTransaction().hide(active).hide(MovementGraficas).show(MovementDetails).commit();
                                active = MovementDetails;
                                item.setChecked(true);
                                break;

                            case R.id.action_graficas_golpe:
                                fm.beginTransaction().hide(active).show(MovementGraficas).hide(MovementDetails).commit();
                                active = MovementGraficas;
                                item.setChecked(true);
                                break;
                        }
                        return false;
                    }
                });
        bNavigation.setSelectedItemId(R.id.action_detalles_golpe);
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
                        "Golpe eliminado correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,
                        "Error al eliminar el golpe", Toast.LENGTH_SHORT).show();
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
            Golpe g = new Golpe(c);
            date = g.getDate() + " , "  + g.getTime();
            user = g.getName();
            age = g.getAge().toString();
            brazo = g.getBrazo();
            tipoGolpe = g.getTipo();
            mov = g.getId().toString();
            device = g.getDevice();
            duration = g.getDuration().toString();
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

                // Tomamos los datos de los 3 sensores
                acc_x = m.getAcc_X().floatValue();
                acc_y = m.getAcc_Y().floatValue();
                acc_z = m.getAcc_Z().floatValue();

                gyr_x = m.getGyr_X().floatValue();
                gyr_y = m.getGyr_Y().floatValue();
                gyr_z = m.getGyr_Z().floatValue();

                mag_x = m.getMag_X().floatValue();
                mag_y = m.getMag_Y().floatValue();
                mag_z = m.getMag_Z().floatValue();


                // Le pasamos a la clase AHRS los valores obtenidos para que haga lo suyo
                // y nos actualice el quaternion
                mMadgwickAHRS.update(acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, mag_x, gyr_y, gyr_z);

                quaternion = mMadgwickAHRS.getQuaternion();

                // Añadimos esos datos a nuestros array para representarlo
                quatX.add(new Entry(m.getTimestamp(), quaternion[0]));
                quatY.add(new Entry(m.getTimestamp(), quaternion[1]));
                quatW.add(new Entry(m.getTimestamp(), quaternion[2]));
                quatZ.add(new Entry(m.getTimestamp(), quaternion[3]));

            } while(c.moveToNext());

            // Obtenemos el valor final del cuaternión
            qF = quaternion;
        }
    }

    /**
     * Aquí realizamos las operaciones para obtener el tipo de golpe
     * Utilizamos el valor final del cuaternión para ello, mediante
     * umbrales
     */

    public void detectarGolpe(){

        if(tipoGolpe.equals("Sin analizar")) {
            if((qF[0] < 0.60 && qF[0] > 0.25) && (qF[1] < -0.4 && qF[1] > -0.75) && (qF[2] < -0.47 && qF[2] > -0.65) && (qF[3] < -0.125 && qF[3] > -0.55)){
                tipoGolpe = "Derecha";
            } else if((qF[0] < 0.75 && qF[0] > 0.45) && (qF[1] < -0.15 && qF[1] > -0.35) && (qF[2] < -0.6 && qF[2] > -0.95) && (qF[3] < 0.2 && qF[3] > -0.1)){
                tipoGolpe = "Revés";
            } else {
                tipoGolpe = "Mala detección";
            }

            int prueba = mDatabaseSQLHelper.updateGolpe(tipoGolpe, ID);
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
                            fw.write("Dispositivo:" + "," + device + "," + "," + "," + "Brazo dominante:" +  "," + brazo + NEXT_LINE);
                            fw.write("Duracion (s): " + "," + duration + "," + "," + "," + "Tipo de golpe: " + tipoGolpe + NEXT_LINE + NEXT_LINE);
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
