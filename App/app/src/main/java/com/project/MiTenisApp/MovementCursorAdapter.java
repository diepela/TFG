package com.project.MiTenisApp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.project.MiTenisApp.BaseDatos.Definitions;

public class MovementCursorAdapter  extends CursorAdapter {

        public MovementCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            //Definir el layout a usar
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.item_movements, viewGroup, false);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {

            // Referencias a elementos del layout
            TextView idText = (TextView) view.findViewById(R.id.mov_id);
            TextView multText = (TextView) view.findViewById(R.id.mult_id);
            TextView dateText = (TextView) view.findViewById(R.id.date);
            TextView timeText = (TextView) view.findViewById(R.id.time);
            TextView golpeText = (TextView) view.findViewById(R.id.golpe_value);
            TextView indiceText = (TextView) view.findViewById(R.id.indice_value);
            //TextView deviceText = (TextView) view.findViewById(R.id.device_value);


            // Obtener los valores de la base de datos
            String id = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry._ID));
            String date = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.TIME));
            String golpe = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.TIPO));
            //String device = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.DEVICE));
            String multiple = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.MULT));
            String indice = cursor.getString(cursor.getColumnIndex(Definitions.GolpeEntry.IND));

            // Asignar los valores a los elementos del layout
            idText.setText(id);
            dateText.setText(date);
            timeText.setText(time);
            golpeText.setText(golpe);
            //deviceText.setText(device);
            indiceText.setText(indice);
            if(multiple.equals("multiple")){
                multText.setText("múltiple");
            } else{
                multText.setText("individual");
            }

        }


}
