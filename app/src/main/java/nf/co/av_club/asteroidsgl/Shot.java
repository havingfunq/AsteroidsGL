package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/18/2016.
 */
public class Shot {
    private float xPos;
    private float yPos;
    private float direction;
    private float xSpeed;
    private float ySpeed;
    private float shotSpeed = 0.01f;

    public Shot(float _xPos, float _yPos, float _xSpeed, float _ySpeed, float _direction){
        xPos = _xPos;
        yPos = _yPos;
        direction = _direction;
        xSpeed = (float) Math.cos( Math.toRadians( (double) direction) ) * shotSpeed + _xSpeed;
        ySpeed = (float) Math.sin( Math.toRadians( (double) direction) ) * shotSpeed + _ySpeed;
    }

    public void logic() {
        xPos += xSpeed;
        yPos += ySpeed;
    }

    public float getX(){
        return xPos;
    }

    public float getY(){
        return yPos;
    }

    public float getDirection() { return direction; }
}
