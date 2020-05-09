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
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MovementDetailsFragment extends Fragment {

    //Definición de variables
    TextView mDate, mUser, mDevice, mDuration;
    LineChart mLineChart;
    MenuItem save, clear;

    /**
     *  Constructor vacío necesario
     */
    public MovementDetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_activity_detail, container, false);

        //Referencias a objetos del layout e inicialización
        mDate = (TextView) view.findViewById(R.id.register_value);
        mDate.setText(((MovementDetailsActivity)getActivity()).date);

        mUser = (TextView) view.findViewById(R.id.by_value);
        mUser.setText(((MovementDetailsActivity)getActivity()).user);

        mDevice = (TextView) view.findViewById(R.id.device_value);
        mDevice.setText(((MovementDetailsActivity)getActivity()).device);

        mDuration = (TextView) view.findViewById(R.id.duration_value);
        mDuration.setText(((MovementDetailsActivity)getActivity()).duration);

        //Definición e inicialización de la gráfica con los datos a representar
        mLineChart = (LineChart) view.findViewById(R.id.lineChart);

        LineDataSet set1 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatX, "quatX");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setDrawCircleHole(false);
        set1.setColor(Color.BLUE);

        LineDataSet set2 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatY, "quatY");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setDrawCircleHole(false);
        set2.setColor(Color.RED);

        /*LineDataSet set3 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatW, "quatW");
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setDrawCircleHole(false);
        set3.setColor(Color.GREEN);*/

        /*LineDataSet set4 = new LineDataSet(((MovementDetailsActivity)getActivity()).quatZ, "quatZ");
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);
        set4.setDrawCircleHole(false);
        set4.setColor(Color.YELLOW);*/



        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(set1);
        lineDataSets.add(set2);
        //lineDataSets.add(set3);
        //lineDataSets.add(set4);

        LineData data = new LineData(lineDataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();

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
