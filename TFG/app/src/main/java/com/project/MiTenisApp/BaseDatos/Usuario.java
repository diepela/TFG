package com.project.MiTenisApp.BaseDatos;

import android.content.ContentValues;
import android.database.Cursor;

public class Usuario {

    private String name;
    private Integer age;
    private Double weight;
    private Integer height;


    public Usuario(String name, Integer age, Double weight, Integer height) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public Usuario(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_NAME));
        age = cursor.getInt(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_AGE));
        weight = cursor.getDouble(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_WEIGHT));
        height = cursor.getInt(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_HEIGHT));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Definitions.UsuarioEntry.FIELD_NAME,name);
        values.put(Definitions.UsuarioEntry.FIELD_AGE,age);
        values.put(Definitions.UsuarioEntry.FIELD_WEIGHT,weight);
        values.put(Definitions.UsuarioEntry.FIELD_HEIGHT,height);
        return values;
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


