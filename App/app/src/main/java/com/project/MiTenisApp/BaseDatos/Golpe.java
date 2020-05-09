package com.project.MiTenisApp.BaseDatos;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Golpe {

    private Integer id;
    private String mov;
    private String date;
    private String time;
    private String device;
    private String user_id;
    private Double duration;
    private String name;
    private Integer age;
    private String brazo;
    private String tipo;


    public Golpe(String mov, String device, String user_id, Double duration, String name, Integer age, String brazo, String tipo) {

        this.mov = mov;
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        this.date = date.format(new Date());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        this.time = time.format(new Date());
        this.device = device;
        this.user_id = user_id;
        this.duration = duration;
        this.name = name;
        this.age = age;
        this.brazo = brazo;
        this.tipo = tipo;
        // this.weight = weight;
        // this.height = height;
    }

    public Golpe(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(Definitions.GolpeEntry._ID));
        mov = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.MOV));
        date = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.DATE));
        time = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.TIME));
        device = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.DEVICE));
        user_id = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.USER_ID));
        duration = cursor.getDouble(cursor.getColumnIndex(Definitions.GolpeEntry.DURATION));

        name= cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.NAME));
        age= cursor.getInt(cursor.getColumnIndex(Definitions.GolpeEntry.AGE));
        brazo = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.BRAZO));
        tipo = cursor.getString((cursor.getColumnIndex(Definitions.GolpeEntry.TIPO)));
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Definitions.GolpeEntry.MOV, mov);
        values.put(Definitions.GolpeEntry.DATE, date);
        values.put(Definitions.GolpeEntry.TIME, time);
        values.put(Definitions.GolpeEntry.DEVICE, device);
        values.put(Definitions.GolpeEntry.USER_ID, user_id);
        values.put(Definitions.GolpeEntry.DURATION, duration);
        values.put(Definitions.GolpeEntry.NAME, name);
        values.put(Definitions.GolpeEntry.AGE, age);
        values.put(Definitions.GolpeEntry.BRAZO, brazo);
        values.put(Definitions.GolpeEntry.TIPO, tipo);

        return values;
    }

    public Integer getId(){
        return id;
    }

    public String getMov() {
        return mov;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDevice() {
        return device;
    }

    public String getUser() {
        return user_id;
    }

    public Double getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getBrazo() {
        return brazo;
    }

    public String getTipo(){
        return tipo;
    }

}
