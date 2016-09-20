package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/16/2016.
 */
import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by VF on 9/15/2016.
 *
 * extending square this way is actually an OpenGL
 * anti pattern, will be corrected in later versions
 */
public class Button extends Square {
    boolean pressed;

    float[] coords = new float[12];

    public Button(float x, float y, float size, float ratio) {
        super();

        coords[0] = x;
        coords[1] = y;

        coords[3] = x;
        coords[4] = y - size*ratio;

        coords[6] = x + size;
        coords[7] = y - size*ratio;

        coords[9] = x + size;
        coords[10] = y;

        super.setQuadVerts(coords);
    }

    public void draw() {
        float[] T = new float[16];

        // identity
        Matrix.setIdentityM(T, 0);

        //no translation, verts already specified, its a non moving button
        super.draw(T);
    }

    public boolean checkTouch(float x, float y, float w, float h){

        // convert screen coordinates into NDC coordinates
        x = 2*(x / w);
        x = x - 1.0f;

        y = -2*(y / h);
        y = y + 1.0f;

        //  compare to this object's verts
        if(coords[0] < x && coords[1] > y && coords[3] < x && coords[4] < y &&
                coords[6] > x && coords[7] < y && coords[9] > x && coords[10] > y ) {
            return true;
        }

        return false;
    }

    public void press(){
        pressed = true;
    }

    public void release(){
        pressed = false;
    }
}