package location.unir.es;

import android.util.Log;

import java.util.List;

/**
 * Created by usuario on 10/07/2017.
 */

public class PonderedBarycenter implements MetodoCalculo {

    private static final String TAG = "BARICENTRO PONDERADO";

    @Override
    public UbicacionPoint getPunto(List<ReferencePoint> puntos_calculo, int x, int y){
        //SUMAMOS LAS LAS DISTANCIAS
        double Q = 0.0;
        for ( int k=0 ; k<puntos_calculo.size(); k++) {
            Q = Q + puntos_calculo.get(k).getDistancia();
        }

        //CALCULAMOS X e Y
        double baricentro_X =0.0;
        double baricentro_Y =0.0;

        for ( int k=0 ; k<3; k++) {
            baricentro_X = baricentro_X + puntos_calculo.get(k).getX()*(puntos_calculo.get(k).getDistancia()/Q);
            baricentro_Y = baricentro_Y + puntos_calculo.get(k).getY()*(puntos_calculo.get(k).getDistancia()/Q);
            Log.i (TAG, "Punto " + " (" + puntos_calculo.get(k).getX() + "," +puntos_calculo.get(k).getY() + ")");
        }
        Log.i (TAG, "Baricentro Acumulado X: " + baricentro_X);
        Log.i (TAG, "Baricentro Acumulado Y: " + baricentro_Y);
        x = (int) Math.round(baricentro_X/3.0);
        y = (int) Math.round(baricentro_Y/3.0);
        Log.i (TAG, "Localizado en X: " + x + " Y: " + y);

        UbicacionPoint punto = new UbicacionPoint(x,y);

        return punto;
    }

}
