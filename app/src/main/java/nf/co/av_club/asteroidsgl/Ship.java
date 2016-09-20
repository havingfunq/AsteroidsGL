package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/16/2016.
 *
 * logic ported from http://www.blitzbasic.com/Community/posts.php?topic=48800
 */
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by VF on 9/15/2016.
 */
public class Ship extends Triangle {
    private float[] mMVPMatrix = new float[16];

    float[] T = new float[16];
    float[] R = new float[16];
    float[] S = new float[16];
    float[] TR = new float[16];
    float[] TRS = new float[16];

    // these variables are tricky to set, review the code in logic()
    final float acceleration = 0.003f;
    final float friction = 0.000014f;
    final float topSpeed = 100.0f;
    final float turnAcceleration = 1.8f;
    final float turnFriction = 0.06f;
    final float turnMax = 3.0f;

    float turnSpeed;
    float xSpeed;
    float ySpeed;
    float direction;

    // center, not edges of spaceship
    float posX;
    float posY;

    boolean crashed = false;
    boolean explosionDone = true;
    float explosionDelta = 0.0f;

    public Ship(float x, float y){
        super();
    }

    public float getXSpeed(){
        return xSpeed;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    public float getX(){
        return posX;
    }

    public float getY(){
        return posY;
    }

    public float getDirection() {
        return direction;
    }

    public synchronized void draw(){
        if(!crashed) {
            Matrix.setIdentityM(T, 0);
            Matrix.translateM(T, 0, posX, posY, 0.0f);

            Matrix.setIdentityM(R, 0);
            Matrix.setRotateM(R, 0, direction, 0.0f, 0.0f, 1.0f);

            Matrix.setIdentityM(S, 0);
            Matrix.scaleM(S, 0, 0.125f, 0.125f, 0.0f);

            Matrix.multiplyMM(TR, 0, T, 0, R, 0);
            Matrix.multiplyMM(TRS, 0, TR, 0, S, 0);

            super.draw(TRS);
        } else if (!explosionDone){
            Matrix.setIdentityM(T, 0);
            Matrix.translateM(T, 0, posX, posY, 0.0f);

            Matrix.setIdentityM(R, 0);
            Matrix.setRotateM(R, 0, direction, 0.0f, 0.0f, 1.0f);

            Matrix.setIdentityM(S, 0);
            Matrix.scaleM(S, 0, 0.125f*explosionDelta*5, 0.125f*explosionDelta*5, 0.0f);

            Matrix.multiplyMM(TR, 0, T, 0, R, 0);
            Matrix.multiplyMM(TRS, 0, TR, 0, S, 0);

            //fade over time
            float[] color = { 1.0f - explosionDelta  , 1.0f - explosionDelta , 1.0f - explosionDelta , 1.0f };

            super.setColor(color);

            super.draw(TRS);

            if(explosionDelta > 1.0f) {
                explosionDone = true;
            } else {
                explosionDelta = explosionDelta + 0.01f;
                direction = direction + 1.0f;
            }
        }
    }

    public void accelerate(){
        xSpeed += (float) Math.cos(Math.toRadians((double)direction)) * acceleration;
        ySpeed += (float) Math.sin(Math.toRadians((double)direction)) * acceleration;
    }

    public void decelerate(){
        xSpeed -= (float) Math.cos(Math.toRadians((double)direction)) * acceleration;
        ySpeed -= (float) Math.sin(Math.toRadians((double)direction)) * acceleration;
    }

    public void turnAccelerate(){
        turnSpeed -= turnAcceleration;
    }

    public void turnDeccelerate(){
        turnSpeed += turnAcceleration;
    }

    public void logic(){
        float speedVectorLength = (float) Math.sqrt((double) xSpeed*xSpeed + ySpeed * ySpeed  );

        if( speedVectorLength > 0 ) {
            // decrease speed with friction of moving
            xSpeed -= (xSpeed/speedVectorLength)*friction;
            ySpeed -= (ySpeed/speedVectorLength)*friction;
        }

//        if ( speedVectorLength > topSpeed ) {
//            xSpeed += (xSpeed/speedVectorLength)*(topSpeed - speedVectorLength);
//            ySpeed += (ySpeed/speedVectorLength)*(topSpeed - speedVectorLength);
//        }

        posX += xSpeed;
        posY += ySpeed;

        // limit turnSpeed

        if(turnSpeed > turnMax ) { turnSpeed = turnMax; }
        if(turnSpeed < -turnMax ) { turnSpeed = -1.0f * turnMax; }

        direction += turnSpeed;

        //reset rotation,
        if(direction > 360) { direction -= 360f; }
        if(direction < 0) { direction += 360f; }

        if(turnSpeed > turnFriction) { turnSpeed -= turnFriction; }
        if(turnSpeed < -1.0f*turnFriction) { turnSpeed += turnFriction; }

        if((turnSpeed < turnFriction) && (turnSpeed > -1.0f*turnFriction )) { turnSpeed = 0.0f; }

        // reset ship coordinates if off screen
        if(posX > 1.0f ) { posX = -1.0f; }
        if(posX < -1.0f ) { posX = 1.0f; }
        if(posY > 1.0f ) { posY = -1.0f; }
        if(posY < -1.0f ) { posY = 1.0f; }
    }

    public float getX0(){
        return posX + super.X0();
    }

    public float getX1() {
        return posX + super.X1();
    }

    public float getX2() {
        return posX + super.X2();
    }

    public float getY0() {
        return posY + super.Y0();
    }

    public float getY1() {
        return posY + super.Y1();
    }

    public float getY2() {
        return posY + super.Y2();
    }

    public synchronized void explode() {
        crashed = true;

        explosionDone = false;
    }

    public boolean crashed(){
        return crashed;
    }

    //this is necessary
    public void removeShaderProgram(){
        super.removeShaderProgram();
    }
}