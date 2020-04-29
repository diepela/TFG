package com.project.MiTenisApp.Usuarios;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.project.MiTenisApp.BaseDatos.Definitions;
import com.project.MiTenisApp.R;

public class UserCursorAdapter extends CursorAdapter {
    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        //Definir el layout a usar
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_users, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias a elementos del layout
        TextView nameText = (TextView) view.findViewById(R.id.user_textview_title);

        // Inicializar variables con la información de la base de datos
        String name = cursor.getString(cursor.getColumnIndex(Definitions.UsuarioEntry.FIELD_NAME));

        // Inicializar los elementos del layout con los datos almacenados
        nameText.setText(name);

    }

}
