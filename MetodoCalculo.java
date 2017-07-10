package location.unir.es;

import java.util.List;

/**
 * Created by usuario on 10/07/2017.
 */

public interface MetodoCalculo {
    public UbicacionPoint getPunto(List<ReferencePoint> puntos_calculo, int x, int y);
}
