package com.project.MiTenisApp.BaseDatos;

import android.content.ContentValues;
import android.database.Cursor;

public class Usuario {

    private String name;
    private Integer age;
    private String brazo;


    public Usuario(String name, Integer age, String brazo) {
        this.name = name;
        this.age = age;
        this.brazo = brazo;
    }

    public Usuario(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_NAME));
        age = cursor.getInt(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_AGE));
        brazo = cursor.getString(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_BRAZO));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Definitions.UsuarioEntry.FIELD_NAME,name);
        values.put(Definitions.UsuarioEntry.FIELD_AGE,age);
        values.put(Definitions.UsuarioEntry.FIELD_BRAZO, brazo);
        // values.put(Definitions.UsuarioEntry.FIELD_WEIGHT,weight);
        // values.put(Definitions.UsuarioEntry.FIELD_HEIGHT,height);
        return values;
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

}


