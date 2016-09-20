package nf.co.av_club.asteroidsgl;


/**
 * Created by VF on 9/15/2016.
 */

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.WindowManager;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mProjectionMatrix = new float[16];
    private float ratio;
    private boolean gameOver = false;

    //opengl api does not allow constructor for GLSurfaceView.Renderer
    //these are intended to be global, is there a way to fix this?
    Button up;
    Button down;
    Button left;
    Button right;
    Button fire;
    Ship ship;
    ShotManager sm;
    RockManager rm;
    GameOverScreen gos;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        ship = new Ship(0.0f, 0.0f);

        sm = new ShotManager();
        rm = new RockManager();

        gos = new GameOverScreen();

    }

    // main loop, its a horid state machine, really no way around this
    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(!gameOver) {
            // Draw background color

            // draw buttons
            up.draw();
            down.draw();
            left.draw();
            right.draw();
            fire.draw();

            // draw ship
            ship.logic();
            ship.draw();

            // manages phasor blasts
            sm.logic(rm);
            sm.drawAll();

            // iterates rocks, checks for collisions
            rm.logic(ship);
            rm.drawAll();

            if (ship.crashed && ship.explosionDone) {
                Lives.decrement();

                ship.removeShaderProgram();
                sm.removeShaderProgram();
                rm.removeShaderProgram();

                ship = new Ship(0.0f, 0.0f);

                sm = new ShotManager();
                rm = new RockManager();

                int lives = Lives.livesLeft();

                if (lives == 0) {
                    gos = new GameOverScreen();

                    gameOver = true;
                }
            }
        } else {
            //game over

            gos.incr();
            if(gos.stopGOSAnimation == true) {
                Score.reset();
                Lives.reset();
                gameOver = false;
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        ratio = (float) width / height;

        float top = 1.0f;
        float bottom = -1.0f;
        float left_ = -1.0f;
        float right_ = 1.0f;

        // these are normalized device coordinates, not pixels
        up = new Button(left_ + .3f, bottom + 1.05f, 0.25f, ratio);
        down = new Button(left_ + .3f, bottom + 0.55f, 0.25f, ratio);
        left = new Button(left_ + .01f, bottom + 0.75f, 0.25f, ratio);
        right = new Button(left_ + .583f, bottom + 0.75f, 0.25f, ratio);
        fire = new Button(right_ - 0.3f, bottom + 0.75f, 0.25f, ratio);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void newGame(){

    }

    public void scanButtons(float x, float y, float screenWidth, float screenHeight) {
        if(!ship.crashed()) {
            if (up.checkTouch(x, y, screenWidth, screenHeight)) {
                ship.accelerate();
            }

            if (down.checkTouch(x, y, screenWidth, screenHeight)) {
                ship.decelerate();
            }

            if (left.checkTouch(x, y, screenWidth, screenHeight)) {
                ship.turnDeccelerate();
            }

            if (right.checkTouch(x, y, screenWidth, screenHeight)) {
                ship.turnAccelerate();
            }

            if (fire.checkTouch(x, y, screenWidth, screenHeight)) {
                sm.fire(ship.getX(), ship.getY(), ship.getXSpeed(), ship.getYSpeed(), ship.getDirection());
            }
        }
    }
}

