package location.unir.es;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by usuario on 09/07/2017.
 */

public class Localizacion extends Observable implements Observer {

    private LocationArea area;
    private int precision;
    private int intervalo;
    private HashMap<String, Double> valores_RSSI;
    private BeaconController controlador;
    private int x;
    private int y;
    private String metodo;
    private static final String TAG = "LOCALIZACION";

    public Localizacion (Context context, LocationArea area, int precision, int intervalo, String metodo, Region region) {
        this.area = area;
        this.precision = precision;
        this.intervalo = intervalo;
        this.metodo = metodo;
        controlador = new BeaconController(context, region);
        controlador.addObserver(this);
        controlador.setListener(precision,true);
        controlador.activarLocalizacion(intervalo);
        valores_RSSI =  new HashMap<String, Double>();
    }

    //GETTERS
    public int getPrecision() {
        return precision;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public LocationArea getArea() {return this.area; }

    public HashMap<String, Double> getValores_RSSI(){ return this.valores_RSSI; }

    public BeaconController getControlador() {
        return controlador;
    }

    public String getMetodo() {
        return metodo;
    }

    //SETTERS
    public void setX (int x) { this.x = x; }

    public void setY (int y) { this.y = y; }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }

    public void setValores_RSSI (HashMap<String, Double> v){ this.valores_RSSI = v; }

    public void setArea (LocationArea a) { this.area = a; }

    public void setControlador(BeaconController controlador) {
        this.controlador = controlador;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.valores_RSSI = (HashMap<String, Double>) arg;
        calcularPosicion();
    }

    public void detenerLocalizacion(){
        controlador.detenerLocalizacion();
    }

    public void activarLocalizacion(int i) {
        controlador.activarLocalizacion(i);
    }

    public void calcularPosicion () {
        List<ReferencePoint> puntos_calculo = new ArrayList<ReferencePoint>();
        List<ReferencePoint> puntos_referencia = area.getPuntos_referencia();
        List<String> keys = new ArrayList(valores_RSSI.keySet());
        for (int i = 0; i < puntos_referencia.size(); i++) {
            double distancia = 0.0;
            for (int j = 0; j< keys.size(); j++) {
                String c = keys.get(j);
                double a = valores_RSSI.get(keys.get(j));
                double b = puntos_referencia.get(i).getValores_RSSI().get(keys.get(j));
                distancia = valores_RSSI.get(keys.get(j)) - puntos_referencia.get(i).getValores_RSSI().get(keys.get(j));
                puntos_referencia.get(i).addDistancia(Math.abs(distancia));
            }

            Log.i (TAG, "Distnacia total al punto ("
                    + puntos_referencia.get(i).getX()
                    + ","
                    + puntos_referencia.get(i).getY()
                    + ") "
                    + puntos_referencia.get(i).getDistancia());
            
            boolean ordenado = false;
            
            if (puntos_calculo.size() == 0) {
                puntos_calculo.add(puntos_referencia.get(i));
            } else {
                for (int g = 0; g < puntos_calculo.size() && !ordenado ; g++) {
                    if (puntos_referencia.get(i).getDistancia() < puntos_calculo.get(g).getDistancia()) {
                        //Si es menor que el que estoy comparando lo meto en esa posicion
                        puntos_calculo.add(g, puntos_referencia.get(i));
                        ordenado = true;
                    }
                }
                //Si no es menor que ninguno lo meto al final
                if (!ordenado) {
                    puntos_calculo.add(puntos_referencia.get(i));
                }
            }
        }

        /*
        //LIMPIAMOS LAS DISTANCIAS
        for ( int k=0 ; k<puntos_calculo.size(); k++) {
            puntos_calculo.get(k).setDistancia(0);
        }

        //CALCULAMOS X e Y
        double baricentro_X =0.0;
        double baricentro_Y =0.0;

        for ( int k=0 ; k<3; k++) {
            baricentro_X = baricentro_X + puntos_calculo.get(k).getX();
            baricentro_Y = baricentro_Y + puntos_calculo.get(k).getY();
            Log.i (TAG, "Punto " + " (" + puntos_calculo.get(k).getX() + "," +puntos_calculo.get(k).getY() + ")");
        }
        Log.i (TAG, "Baricentro Acumulado X: " + baricentro_X);
        Log.i (TAG, "Baricentro Acumulado Y: " + baricentro_Y);
        x = (int) Math.round(baricentro_X/3.0);
        y = (int) Math.round(baricentro_Y/3.0);
        Log.i (TAG, "Localizado en X: " + x + " Y: " + y);
        */

        MetodoCalculo metodoCaluclo;

        if (metodo=="P0NDERADO") {
            metodoCaluclo = new PonderedBarycenter();

        } else{
            metodoCaluclo = new Barycenter();
        }

        UbicacionPoint punto = metodoCaluclo.getPunto(puntos_calculo,x,y);
        setChanged();
        notifyObservers(punto);
    }

    public List<LocationArea> estaEnArea () {
        List<LocationArea> contenido_en = new ArrayList<LocationArea>();
        if (area.getSub_areas()!=null){
            for (int i=0; i< area.getSub_areas().size(); i++){
                if (contains(area.getSub_areas().get(i))){
                    contenido_en.add(area.getSub_areas().get(i));
                }
            }
        }

        if (contains(area)){
            contenido_en.add(area);
        }

        return contenido_en;
    }

    public boolean contains(LocationArea area) {
        int i;
        int j;
        boolean result = false;
        List<VertexPoint> points = area.getVertices();;

        for (int m = 0; m < area.getVertices().size(); m++){
            for (i = 0, j = area.getVertices().size() - 1; i < area.getVertices().size(); j = i++) {
                if ((points.get(i).getY() > this.y) != (points.get(j).getY() > this.y) &&
                        (this.x < (points.get(j).getX() - points.get(i).getX()) * (this.y - points.get(i).getY()) / (points.get(j).getY()-points.get(i).getY()) + points.get(i).getX())) {
                    result = !result;
                }
            }
        }

        return result;
    }
}
