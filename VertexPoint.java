package location.unir.es;

import android.util.Log;

import java.util.Observable;

/**
 * Created by usuario on 18/06/2017.
 */

public class VertexPoint extends AbstractPoint {

    private double latitude;
    private double longitude;

    private static final String TAG = "VERTICE";

    //CONSTRUCTORES
    public VertexPoint(int x, int y) {
        super(x, y);
        Log.i(TAG, "Vértice (" + x + "," + y + ") creado con éxito");
    }

    public VertexPoint(int x, int y, double latitude, double longitude) {
        super(x, y);
        this.latitude = latitude;
        this.longitude = longitude;
        Log.i(TAG, "Vértice (" + x + "," + y + ") - ("+ latitude + "," + longitude + ") creado con éxito");
    }

    //GETTERS
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    //SETTERS
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}