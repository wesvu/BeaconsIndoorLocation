package location.unir.es;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Region;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by usuario on 18/06/2017.
 */

public class ReferencePoint extends AbstractPoint{

    private HashMap<String, Double> valores_RSSI;
    private double distancia;

    private static final String TAG = "PUNTO REFERENCIA";

    //CONSTRUCTORES
    public ReferencePoint(Context context, int x, int y, int precision, int intervalo, Region region) {
        super(x, y);
        this.valores_RSSI = new HashMap<String, Double>();
        this.distancia=0.0;
        Log.i(TAG, "Punto de referencia (" + x + "," + y + ") creado con éxito");
        BeaconController controlador = new BeaconController(context, region);
        if (precision<50) { precision = 50; }
        controlador.addObserver(this);
        controlador.setListener(precision, false);
        controlador.activarLocalizacion(intervalo);
    }

    public ReferencePoint(int x, int y, HashMap<String, Double> valor) {
        super(x, y);
        this.valores_RSSI = valor;
        this.distancia=0.0;
        Log.i(TAG, "Punto de referencia (" + x + "," + y + ") creado con éxito");
    }

    //GETTERS
    public HashMap<String, Double> getValores_RSSI() {
        return this.valores_RSSI;
    }

    public double getDistancia () {
        return this.distancia;
    }

    //SETTERS
    public void addDistancia (double nueva_distancia) {
        distancia = distancia + nueva_distancia;
    }

    public void setValores_RSSI(HashMap<String, Double> valor) {
        this.valores_RSSI = valor;
    }

    public void setDistancia (double v) {
        this.distancia = v;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.valores_RSSI = (HashMap<String, Double>) arg;
        Log.i (TAG, "Punto (" + x + "," + y + ") con RSSI asignado");
    }
}