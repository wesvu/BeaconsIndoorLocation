package location.unir.es;

import android.util.Log;

import java.util.Observable;

/**
 * Created by usuario on 18/06/2017.
 */

public class UbicacionPoint extends AbstractPoint {


    private static final String TAG = "UBICACION";

    //CONSTRUCTORES
    public UbicacionPoint(int x, int y) {
        super(x, y);
        Log.i(TAG, "Ubicación (" + x + "," + y + ") creado con éxito");
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}