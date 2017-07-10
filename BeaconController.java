package location.unir.es;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created by usuario on 09/07/2017.
 */

public class BeaconController extends Observable {

    private BeaconManager controlador;
    private int periodo;
    private int muestras;
    private boolean background;
    private Context context;
    private HashMap<String, List<Integer>> hashMap = new HashMap<String, List<Integer>>();

    private static final String TAG = "CONTROLADOR";
    private Region ALL_ESTIMOTE_BEACONS;

    private int contador;

    public BeaconController(Context context, Region region) {
        this.context = context;
        controlador = new BeaconManager(context);
        this.ALL_ESTIMOTE_BEACONS = region;
        Log.i(TAG, "Entro a crear controlador");
        activarAhorroEnergia();
    }

    public void activarAhorroEnergia(){
        controlador.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                if (periodo>0){
                    activarLocalizacion(periodo);
                } else {
                    activarLocalizacion(0);
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                detenerLocalizacion();
            }
        });
    }

    public void setListener(int i, boolean v_background) {
        Log.i(TAG, "Entro LISTENER");
        // Establecemos el valor 20 para las muestras por defecto
        if (i == 0) {
            muestras = 20;
        } else {
            muestras = i;
        }

        background = v_background;

        controlador.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                for (int i = 0; i < beacons.size(); i++) {
                    contador = contador + 1;
                    String beaconID = beacons.get(i).getMajor() + "-" + beacons.get(i).getMinor();

                    if (!hashMap.containsKey(beaconID)) {
                        List<Integer> list = new ArrayList<Integer>();
                        list.add(beacons.get(i).getRssi());

                        hashMap.put(beaconID, list);
                    } else {
                        hashMap.get(beaconID).add(beacons.get(i).getRssi());
                    }

                    Log.i("CONTADOR", "CONTADOR: " + contador + " Beacon " + beaconID);

                    if ((contador % muestras) == 0) {
                        detenerLocalizacion();
                        caculaRSSIPunto(hashMap);
                    }
                }
            }
        });
    }

    public void caculaRSSIPunto (HashMap<String, List<Integer>> fingerprint) {
        List<String> keys = new ArrayList(fingerprint.keySet());
        HashMap<String, Double> valores_RSSI = new HashMap<String, Double>();
        double suma;
        List<Integer> valores;
        valores_RSSI.clear();
        //Garantizar mínimo la señal de tres balizas para realizar el cálculo
        if (keys.size()>2) {
            for (int g = 0; g < keys.size(); g++) {
                valores = fingerprint.get(keys.get(g));
                String clave = keys.get(g);
                suma = 0.0;
                for (int c=0; c<valores.size();c++) {
                    suma = suma + valores.get(c);
                }
                Double media = suma/valores.size();

                Log.i (TAG, "Media " + clave + ": " + media);

                valores_RSSI.put(clave,media);
            }
            setChanged();
            notifyObservers(valores_RSSI);
            if (!background) {
                Toast.makeText(this.context, "Valor RSSI asignado con éxito", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this.context, "ERROR\nNo ha podido asignarse valor RSSI", Toast.LENGTH_SHORT).show();
        }
        if (background) { activarLocalizacion(periodo); }
        hashMap.clear();
    }

    public void activarLocalizacion(int i){
        Log.i(TAG, "Activar localizacion");
        // Establecemos el valor 200 msegundos para el escaneo por defecto
        if (i == 0) {
            periodo = 200;
        } else {
            periodo = i;
        }
        controlador.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                controlador.setForegroundScanPeriod(periodo, 0);
                controlador.startRanging(ALL_ESTIMOTE_BEACONS);
            }
        });

        Log.i(TAG, "Comienza la monitorizacion");
    }

    public void detenerLocalizacion(){
        controlador.stopRanging(ALL_ESTIMOTE_BEACONS);
    }
}