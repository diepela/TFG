package com.project.MiTenisApp.BaseDatos;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Actividad {

    private Integer id;
    private String mov;
    private String date;
    private String time;
    private String device;
    private String user_id;
    private Double duration;
    private String name;
    private Integer age;
    private Double weight;
    private Integer height;


    public Actividad(String mov, String device, String user_id, Double duration, String name, Integer age, Double weight, Integer height) {

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
        this.weight = weight;
        this.height = height;
    }

    public Actividad(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(Definitions.ActivityEntry._ID));
        mov = cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.MOV));
        date = cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.DATE));
        time = cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.TIME));
        device = cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.DEVICE));
        user_id = cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.USER_ID));
        duration = cursor.getDouble(cursor.getColumnIndex(Definitions.ActivityEntry.DURATION));

        name= cursor.getString(cursor.getColumnIndex(Definitions.ActivityEntry.NAME));
        age= cursor.getInt(cursor.getColumnIndex(Definitions.ActivityEntry.AGE));
        weight = cursor.getDouble(cursor.getColumnIndex(Definitions.ActivityEntry.WEIGHT));
        height = cursor.getInt(cursor.getColumnIndex(Definitions.ActivityEntry.HEIGHT));
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Definitions.ActivityEntry.MOV, mov);
        values.put(Definitions.ActivityEntry.DATE, date);
        values.put(Definitions.ActivityEntry.TIME, time);
        values.put(Definitions.ActivityEntry.DEVICE, device);
        values.put(Definitions.ActivityEntry.USER_ID, user_id);
        values.put(Definitions.ActivityEntry.DURATION, duration);
        values.put(Definitions.ActivityEntry.NAME, name);
        values.put(Definitions.ActivityEntry.AGE, age);
        values.put(Definitions.ActivityEntry.WEIGHT, weight);
        values.put(Definitions.ActivityEntry.HEIGHT, height);

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

    public Double getWeight() {
        return weight;
    }

    public Integer getHeight() {
        return height;
    }


}
