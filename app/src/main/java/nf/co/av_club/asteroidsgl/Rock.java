package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/18/2016.
 */
public class Rock {
    private float xPos;
    private float yPos;
    private float direction;
    private float xSpeed;
    private float ySpeed;

    float[] rockVerts;
    final float speed = 0.005f;
    float rotation;
    float size;
    final float scale = 1.5f;

    public Rock(float _xPos, float _yPos){
        rockVerts = new float[30];

        // points for a pentagon
        rockVerts[0] = 1.0f*scale;
        rockVerts[1] = 0.0f*scale;
        rockVerts[3] = 0.3090f*scale;
        rockVerts[4] = 0.9510f*scale;

        rockVerts[6] = 0.3090f*scale;
        rockVerts[7] = 0.9510f*scale;
        rockVerts[9] = -0.8090f*scale;
        rockVerts[10] = 0.5877f*scale;

        rockVerts[12] = -0.8090f*scale;
        rockVerts[13] = 0.5877f*scale;
        rockVerts[15] = -0.8090f*scale;
        rockVerts[16] = -0.5877f*scale;

        rockVerts[18] = -0.8090f*scale;
        rockVerts[19] = -0.5877f*scale;
        rockVerts[21] = 0.3065f*scale;
        rockVerts[22] = -0.9518f*scale;

        rockVerts[24] = 0.3065f*scale;
        rockVerts[25] = -0.9518f*scale;
        rockVerts[27] = 1.0f*scale;
        rockVerts[28] = 0.0f*scale;

        xPos = _xPos;
        yPos = _yPos;

        direction = (float) Math.random() * 360;

        xSpeed = (float) Math.cos(direction) * speed;
        ySpeed = (float) Math.sin(direction) * speed;
    }

    public void logic() {
        xPos = xPos + xSpeed;
        yPos = yPos + ySpeed;
    }

    public float[] getVerts() {
        return rockVerts;
    }

    public float getX() {
        return xPos;
    }

    public float getY() {
        return yPos;
    }

    public float getDirection(){
        return direction;
    }

}
