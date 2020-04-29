package com.project.MiTenisApp.BaseDatos;

import android.content.ContentValues;
import android.database.Cursor;

public class Movimiento {

    private Integer id;
    private String mov;
    private Integer timestamp;
    private Double Acc_X, Acc_Y, Acc_Z;
    private Double  Gyr_X, Gyr_Y, Gyr_Z;
    private Double Mag_X, Mag_Y, Mag_Z;


    public Movimiento(String mov, Integer timestamp, Double[] Acc, Double[] Gyr, Double[] Mag) {
        this.mov = mov;
        this.timestamp = timestamp;
        this.Acc_X = Acc[0];
        this.Acc_Y = Acc[1];
        this.Acc_Z = Acc[2];
        this.Gyr_X = Gyr[0];
        this.Gyr_Y = Gyr[1];
        this.Gyr_Z = Gyr[2];
        this.Mag_X = Mag[0];
        this.Mag_Y = Mag[1];
        this.Mag_Z = Mag[2];
    }

    public Movimiento(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(Definitions.MovimientoEntry._ID));
        mov = cursor.getString(cursor.getColumnIndex(Definitions.MovimientoEntry.MOV));
        timestamp = cursor.getInt(cursor.getColumnIndex(Definitions.MovimientoEntry.TIMESTAMP));

        Acc_X = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.ACC_X));
        Acc_Y = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.ACC_Y));
        Acc_Z = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.ACC_Z));

        Gyr_X = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.GYR_X));
        Gyr_Y = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.GYR_Y));
        Gyr_Z = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.GYR_Z));

        Mag_X = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.MAG_X));
        Mag_Y = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.MAG_Y));
        Mag_Z = cursor.getDouble(cursor.getColumnIndex(Definitions.MovimientoEntry.MAG_Z));
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Definitions.MovimientoEntry.MOV, mov);
        values.put(Definitions.MovimientoEntry.TIMESTAMP, timestamp);
        values.put(Definitions.MovimientoEntry.ACC_X, Acc_X);
        values.put(Definitions.MovimientoEntry.ACC_Y, Acc_Y);
        values.put(Definitions.MovimientoEntry.ACC_Z, Acc_Z);
        values.put(Definitions.MovimientoEntry.GYR_X, Gyr_X);
        values.put(Definitions.MovimientoEntry.GYR_Y, Gyr_Y);
        values.put(Definitions.MovimientoEntry.GYR_Z, Gyr_Z);
        values.put(Definitions.MovimientoEntry.MAG_X, Mag_X);
        values.put(Definitions.MovimientoEntry.MAG_Y, Mag_Y);
        values.put(Definitions.MovimientoEntry.MAG_Z, Mag_Z);
        return values;
    }

    public Integer getId() {
        return id;
    }

    public String getMov() {
        return mov;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public Double getAcc_X() {
        return Acc_X;
    }

    public Double getAcc_Y() {
        return Acc_Y;
    }

    public Double getAcc_Z() {
        return Acc_Z;
    }

    public Double getGyr_X() {
        return Gyr_X;
    }

    public Double getGyr_Y() {
        return Gyr_Y;
    }

    public Double getGyr_Z() {
        return Gyr_Z;
    }

    public Double getMag_X() {
        return Mag_X;
    }

    public Double getMag_Y() {
        return Mag_Y;
    }

    public Double getMag_Z() {
        return Mag_Z;
    }

}
