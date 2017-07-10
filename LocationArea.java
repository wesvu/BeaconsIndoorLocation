package location.unir.es;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Region;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by usuario on 18/06/2017.
 */

public class LocationArea extends Observable implements Observer {

    private String nombre;
    private List<ReferencePoint> puntos_referencia;
    private List<VertexPoint> vertices;
    private List<LocationArea> sub_areas;
    private Context context;
    private Color color;
    private Localizacion localizacion;
    private UbicacionPoint ubicacion;
    private String metodoCalculo;

    private static final String TAG = "AREA";
    private Region ALL_ESTIMOTE_BEACONS;

    //CONSTRUCTORES
    public LocationArea(Context context, String nombre, Region region) {
        this.nombre = nombre;
        vertices = new ArrayList<VertexPoint>();
        puntos_referencia = new ArrayList<ReferencePoint>();
        this.context = context;
        this.metodoCalculo = "NORMAL";
        this.ALL_ESTIMOTE_BEACONS = region;
        Log.i(TAG,"Área " + nombre + " creada con éxito");
    }

    //GETTERS
    public String getNombre() {
        return nombre;
    }

    public List<ReferencePoint> getPuntos_referencia() {
        return puntos_referencia;
    }

    public List<VertexPoint> getVertices() {
        return vertices;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public UbicacionPoint getUbicacion(){
        return ubicacion;
    }

    public Color getColor() { return this.color; }

    public List<LocationArea> getSub_areas() { return this.sub_areas; }

    public String getMetodoCalculo(){ return this.metodoCalculo; }

    //SETTERS
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPuntos_referencia(List<ReferencePoint> puntos_referencia) {
        this.puntos_referencia = puntos_referencia;
    }

    public void setVertices(List<VertexPoint> vertices) {
        this.vertices = vertices;
    }

    public void setColor (Color color) {this.color = color; }

    public void setSub_areas (List<LocationArea> sub_areas) { this.sub_areas=sub_areas; }

    public void setMetodoCalculo (String metodo){ this.metodoCalculo = metodo; }

    public void setLocalizacion(Localizacion localizacion) { this.localizacion = localizacion; }

    //METODOS PUBLICOS
    public void nuevoVertice(int x, int y)  {
        vertices.add(new VertexPoint(x, y));
    }

    public void nuevoPuntoReferencia(int x, int y, int precision, int intervalo, Region region)  {
        puntos_referencia.add(new ReferencePoint(this.context, x, y, precision, intervalo, region));
    }

    public void eliminarPuntosReferencia(){
        puntos_referencia.clear();
    }

    public void eliminarVertices(){
        vertices.clear();
    }

    public int modoLocalizacion(int precision, int intervalo){

        if (puntos_referencia.size() < 3) {
            Toast.makeText(this.context, "No hay puntos de referencia suficientes", Toast.LENGTH_SHORT).show();
            return 1;
        } else {
            localizacion = new Localizacion (context, this, precision, intervalo,  metodoCalculo, ALL_ESTIMOTE_BEACONS);
            localizacion.addObserver(this);
            return 0;
        }
    }

    public void activarLocalizacion (int i) {
        if (localizacion!=null) {
            localizacion.activarLocalizacion(i);
        }
    }

    public void detenerLocalizacion () {
        if (localizacion!=null) {
            localizacion.detenerLocalizacion();
        }
    }

    public void eliminarSubAreas() {
        this.sub_areas.clear();
    }

    public void addSubArea(LocationArea A){
        sub_areas.add(A);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.ubicacion = (UbicacionPoint) arg;
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        String texto = "Hora: " + hourFormat.format(date) + " Ubicacion: (" + ubicacion.getX() + "," + ubicacion.getY() + ")";
        setChanged();
        notifyObservers(texto);
    }
}
