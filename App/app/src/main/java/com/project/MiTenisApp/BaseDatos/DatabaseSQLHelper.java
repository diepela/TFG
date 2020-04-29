package com.project.MiTenisApp.BaseDatos;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Datafit.db";

    public DatabaseSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Definitions.UsuarioEntry.USER_TABLE + " ("
                + Definitions.UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Definitions.UsuarioEntry.FIELD_NAME + " TEXT NOT NULL,"
                + Definitions.UsuarioEntry.FIELD_AGE + " INTEGER NOT NULL,"
                + Definitions.UsuarioEntry.FIELD_WEIGHT + " DOUBLE NOT NULL,"
                + Definitions.UsuarioEntry.FIELD_HEIGHT + " INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE " + Definitions.MovimientoEntry.MOVS_TABLE + " ("
                + Definitions.MovimientoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Definitions.MovimientoEntry.MOV + " TEXT NOT NULL,"
                + Definitions.MovimientoEntry.TIMESTAMP + " INTEGER NOT NULL,"
                + Definitions.MovimientoEntry.ACC_X + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.ACC_Y + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.ACC_Z + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.GYR_X + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.GYR_Y + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.GYR_Z + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.MAG_X + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.MAG_Y + " DOUBLE NOT NULL,"
                + Definitions.MovimientoEntry.MAG_Z + " DOUBLE NOT NULL);");

        db.execSQL("CREATE TABLE " + Definitions.ActivityEntry.ACTS_TABLE + " ("
                + Definitions.ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Definitions.ActivityEntry.MOV + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.DATE + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.TIME + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.DEVICE + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.USER_ID + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.DURATION + " DOUBLE NOT NULL,"
                + Definitions.ActivityEntry.NAME + " TEXT NOT NULL,"
                + Definitions.ActivityEntry.AGE + " INTEGER NOT NULL,"
                + Definitions.ActivityEntry.WEIGHT + " DOUBLE NOT NULL,"
                + Definitions.ActivityEntry.HEIGHT + " INTEGER NOT NULL);");

        mockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No hay operaciones
    }


    ///////////////////////////////////// MÉTODOS USUARIOS /////////////////////////////////////////
   public long saveUser(Usuario user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                Definitions.UsuarioEntry.USER_TABLE,
                null,
                user.toContentValues());
    }

    public Cursor getAllUsers() {
        return getReadableDatabase()
                .query(
                        Definitions.UsuarioEntry.USER_TABLE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getUserById(String userId) {
         return getReadableDatabase().query(
                 Definitions.UsuarioEntry.USER_TABLE,
                null,
                 Definitions.UsuarioEntry._ID + " LIKE ?",
                new String[]{userId},
                null,
                null,
                null);
    }

    public Cursor getUserNameById(String userId) {
        return getReadableDatabase().query(
                Definitions.UsuarioEntry.USER_TABLE,
                new String[]{Definitions.UsuarioEntry.FIELD_NAME},
                Definitions.UsuarioEntry._ID + " LIKE ?",
                new String[]{userId},
                null,
                null,
                null);
    }

    public int deleteUser(String userId) {
        return getWritableDatabase().delete(
                Definitions.UsuarioEntry.USER_TABLE,
                Definitions.UsuarioEntry._ID + " = ?",
                new String[]{userId});
    }

    public int updateUser(Usuario user, String userId) {
        return getWritableDatabase().update(
                Definitions.UsuarioEntry.USER_TABLE,
                user.toContentValues(),
                Definitions.UsuarioEntry._ID + " LIKE ?",
                new String[]{userId});
    }




    //////////////////////////////// MÉTODOS MOVIMIENTOS ///////////////////////////////////////////

    public void mockData(SQLiteDatabase db) {
        Double[] ACC = {72.0,3.60,-6.70};
        Double[] GYR = {1.1,-2.8,-6.7};
        Double[] MAG = {0.0,-29.44,-32.512};
        Double[] quaternion = {1.0,0.3,0.0,0.5};
        Double[] euler = {74.1,98.2,54.2};

        db.insert(
                Definitions.MovimientoEntry.MOVS_TABLE,
                null,
                new Movimiento("df",456,ACC,GYR,MAG).toContentValues());
    }

    public long saveMovement(Movimiento mov) {
        return getWritableDatabase().insert(
                Definitions.MovimientoEntry.MOVS_TABLE,
                null,
                mov.toContentValues());

    }

    public Cursor getAllMovements() {
        return getReadableDatabase().query(
                        Definitions.MovimientoEntry.MOVS_TABLE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getMovementsByActivityId(String mov) {
        return getReadableDatabase().query(
                        Definitions.MovimientoEntry.MOVS_TABLE,
                       null,
                        Definitions.MovimientoEntry.MOV + " LIKE ?",
                        new String[]{mov},
                        null,
                        null,
                        null);
    }

    //Eliminar movimiento por identificador de actividad
    public int deleteMovementByMov(String mov) {
        return getWritableDatabase().delete(
                Definitions.MovimientoEntry.MOVS_TABLE,
                Definitions.MovimientoEntry.MOV + " LIKE ?",
                new String[]{mov}
        );
    }


    //////////////////////////////// MÉTODOS ACTIVIDADES ///////////////////////////////////////////

    public long saveActivity(Actividad act) {
        return getWritableDatabase().insert(
                Definitions.ActivityEntry.ACTS_TABLE,
                null,
                act.toContentValues());

    }

    //Ordenar por orden descendente según puntuación
    public Cursor getBestActivities() {
        return getReadableDatabase().query(
                Definitions.ActivityEntry.ACTS_TABLE,
                null,
                null,
                null,
                null,
                null,
                "mark DESC");
    }

    // Ordenar según fecha y hora descendentes
    public Cursor getActivitiesByUserId(String userId) {

        return getReadableDatabase().query(
                Definitions.ActivityEntry.ACTS_TABLE,
                null,
                Definitions.ActivityEntry.USER_ID + " LIKE ?",
                new String[]{userId},
                null,
                null,
                 "_id DESC");
    }

    // Filtrar según identificador de actividad
    public Cursor getActivityByMovId(String mov) {
        return getReadableDatabase().query(
                Definitions.ActivityEntry.ACTS_TABLE,
                null,
                Definitions.ActivityEntry.MOV + " LIKE ?",
                new String[]{mov},
                null,
                null,
                null);
    }


    public Cursor getAllActivities() {
        return getReadableDatabase()
                .query(
                        Definitions.ActivityEntry.ACTS_TABLE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }


    //Eliminar todas las actividades del usuario
    public int deleteActivityByUserId(String userId) {
        return getWritableDatabase().delete(
                Definitions.ActivityEntry.ACTS_TABLE,
                Definitions.ActivityEntry.USER_ID + " LIKE ?",
                new String[]{userId}
        );
    }


    //Eliminar una actividad por identificador de actividad
    public int deleteActivityByMovId(String mov) {
        return getWritableDatabase().delete(
                Definitions.ActivityEntry.ACTS_TABLE,
                Definitions.ActivityEntry._ID + " LIKE ?",
                new String[]{mov}
        );
    }



}
