package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/15/2016.
 */
public class Point {
    float x = 0.0f;
    float y = 0.0f;

    public Point(float _x, float _y){
        x = _x;
        y = _y;
    }

    public float x(){
        return x;
    }

    public float y(){
        return y;
    }

    public void setX(float _x){
        x = _x;
    }

    public void setY(float _y){
        y = _y;
    }
}