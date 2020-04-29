package com.project.MiTenisApp.BaseDatos;

import android.provider.BaseColumns;

public class Definitions {

    public static abstract class MovimientoEntry implements BaseColumns {

        //Nombre de la tabla
        public static final String MOVS_TABLE = "MOVEMENTS";

        //Columnas de la tabla
        public static final String MOV = "mov";
        public static final String TIMESTAMP = "timestamp";
        public static final String ACC_X= "ACC_X";
        public static final String ACC_Y= "ACC_Y";
        public static final String ACC_Z= "ACC_Z";
        public static final String GYR_X= "GYR_X";
        public static final String GYR_Y= "GYR_Y";
        public static final String GYR_Z= "GYR_Z";
        public static final String MAG_X= "MAG_X";
        public static final String MAG_Y= "MAG_Y";
        public static final String MAG_Z= "MAG_Z";
    }

    public static abstract class ActivityEntry implements BaseColumns {

        //Nombre de la tabla
        public static final String ACTS_TABLE = "ACTIVITIES";

        //Columnas de la tabla
        public static final String MOV = "mov";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String DEVICE = "device";
        public static final String USER_ID = "user_id";
        public static final String DURATION = "duration";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String BRAZO = "brazo";
        // public static final String WEIGHT = "weight";
        // public static final String HEIGHT = "height";
    }


    public static abstract class UsuarioEntry implements BaseColumns {

        public static final String USER_TABLE = "USERS";

   //     public static final String ID = "id";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_AGE = "age";
        public static final String FIELD_BRAZO = "brazo";
        // public static final String FIELD_WEIGHT = "weight";
        // public static final String FIELD_HEIGHT = "height";

    }
}
