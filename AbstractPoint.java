package location.unir.es;

import android.graphics.Point;

import java.util.Observer;

/**
 * Created by usuario on 18/06/2017.
 */

public abstract class AbstractPoint extends Point implements Observer {

    //CONSTRUCTORES
    public AbstractPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    //GETTERS
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    //SETTERS
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
