package com.project.MiTenisApp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class MovementGraficasFragment extends Fragment {

    //Definición de variables
    Button Button1, Button2, Button3, Button4;
    LineChart mLineChart;
    MenuItem save, clear;

    /**
     *  Constructor vacío necesario
     */
    public MovementGraficasFragment() {
    }

    /**
     * Método que define las acciones al crearse el fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     *  Método que define las acciones a realizar al crearse la vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Definir el layout a usar
        View view = inflater.inflate(R.layout.fragment_activity_grafica, container, false);

        //Referencias a objetos del layout e inicialización
        Button1 = (Button) view.findViewById(R.id.button1);
        Button2 = (Button) view.findViewById(R.id.button2);
        Button3 = (Button) view.findViewById(R.id.button3);
        Button4 = (Button) view.findViewById(R.id.button4);

        // Definición e inicialización de la gráfica con los datos a representar
        mLineChart = (LineChart) view.findViewById(R.id.lineChart);
        mLineChart.setNoDataText("Pulsa un botón para ver su gráfica correspondiente");

        // Representamos la gráfica correspondiente al botón que se ha pulsado

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDataSet set1 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatX, "quatX");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setDrawCircleHole(false);
                set1.setColor(Color.BLUE);

                LineData data = new LineData(set1);
                mLineChart.setData(data);
                mLineChart.invalidate();
            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDataSet set2 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatY, "quatY");
                set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                set2.setDrawCircleHole(false);
                set2.setColor(Color.RED);

                LineData data = new LineData(set2);
                mLineChart.setData(data);
                mLineChart.invalidate();
            }
        });

        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDataSet set3 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatW, "quatW");
                set3.setAxisDependency(YAxis.AxisDependency.LEFT);
                set3.setDrawCircleHole(false);
                set3.setColor(Color.GREEN);

                LineData data = new LineData(set3);
                mLineChart.setData(data);
                mLineChart.invalidate();
            }
        });

        Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDataSet set4 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatZ, "quatZ");
                set4.setAxisDependency(YAxis.AxisDependency.LEFT);
                set4.setDrawCircleHole(false);
                set4.setColor(Color.YELLOW);

                LineData data = new LineData(set4);
                mLineChart.setData(data);
                mLineChart.invalidate();
            }
        });

        return view;
    }


    /**
     * Método para crear el menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.save_activity, menu);
        save = menu.findItem(R.id.ic_save);
        clear = menu.findItem(R.id.ic_clear);

    }

    /**
     * Método que inicializa los elementos del menú
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (save != null) {
            save.setEnabled(false);
        }
        if (clear != null) {
            clear.setEnabled(false);
        }
    }




}
