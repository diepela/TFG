package com.project.MiTenisApp.BLE;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.project.MiTenisApp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevicesListFragment extends Fragment {

        private static final String DEVICE_NAME = "DEVICE_NAME";
        private static final String[] DEVICES = {"DEVICE_NAME"};
        private static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
        private static final String[] DEVICES_ADDRESS = {"DEVICE_ADDRESS"};

        private ListView mListView;
        private TextView mEmptyView;
        private String mSearching;
        private String mNoFound;
        private String mEmpty;

        private SimpleAdapter mAdapter;

        private OnDevicesFragmentInteractionListener mListener;

        private String[] mDevicesAddress ;

        private MenuItem mRefreshItem = null;
        private final  ArrayList<String> mBleDevices = new ArrayList();
        private List<Map<String, String>> items = new ArrayList<Map<String, String>>();

    /**
     * Método para crear una nueva instancia del fragment
     * @return nueva instancia del fragment DeviceListFragment.
     */
    public static DevicesListFragment newInstance(String[] device_add ) {
        //Obtener los datos de la actividad
        DevicesListFragment fragment = new DevicesListFragment();
        Bundle args = new Bundle();
        args.putStringArray(DEVICE_ADDRESS ,device_add);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *  Constructor vacío necesario
     */
    public DevicesListFragment() {
    }

    /**
     * Método que define las acciones al crearse el fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {


        if (getArguments() != null) {
            mDevicesAddress = getArguments().getStringArray(DEVICE_ADDRESS);
        }else{
            mDevicesAddress=null;
        }

        super.onCreate(savedInstanceState);
    }

    /**
     *  Método que define las acciones a realizar al crearse la vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Definir el layout a usar
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        // Mostrar barra de herramientas
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        mEmpty = "";
        mSearching = inflater.getContext().getString(R.string.searching);
        mNoFound = inflater.getContext().getString(R.string.noDevicesFound);

        mListView = (ListView) view.findViewById(R.id.devicesListView);
        setDevices(getContext(),mDevicesAddress);

        mEmptyView = (TextView) view.findViewById(R.id.emptyListView);
        mListView.setEmptyView(mEmptyView);

        mEmptyView.setText(mSearching);

        mAdapter = new SimpleAdapter(getContext(), items, R.layout.item_devices,  new String[] {"DEVICE_ADDRESS"}, new int[] {R.id.device_address});


        mListView.setEnabled(!((ScanActivity) getActivity()).mSearching);


        if(((ScanActivity) getActivity()).mSearching){
            if (mEmptyView != null) {
                    mEmptyView.setText(mSearching);
            }

        }else {

            if (mDevicesAddress != null) {
                mListView.setAdapter(mAdapter);
            } else {
                mListView.setEmptyView(mEmptyView);
                mEmptyView.setText(mNoFound);
            }


        }
        // Manage item click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mListener.OnDevicesFragmentInteractionListener(mDevicesAddress[i]);
                }
        });

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnDevicesFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
    * Almacenar la lista de dispositivos en un array
     * @param context contexto de la aplicación
     * @param deviceAddress array de strings que contiene los nombres de los dispositivos guardados
    */
    public void setDevices(Context context, String[] deviceAddress) {
            if (deviceAddress != null) {
                for (String device : deviceAddress) {
                    Map<String, String> item = new HashMap<String, String>();
                    item.put(DEVICE_ADDRESS, device);
                    items.add(item);
                }
            } else {
                items.clear();
            }
        }


    public interface OnDevicesFragmentInteractionListener {
       void OnDevicesFragmentInteractionListener(String deviceName);
    }

    /**
     * Método para crear el menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.device_list, menu);
        mRefreshItem = menu.findItem(R.id.ic_refresh);

    }

    /**
     * Método que inicializa los elementos del menú
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mRefreshItem != null) {
            mRefreshItem.setEnabled(mListView.isEnabled());
        }
    }

}

