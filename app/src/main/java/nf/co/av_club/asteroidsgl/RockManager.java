package nf.co.av_club.asteroidsgl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by VF on 9/18/2016.
 */
public class RockManager {
    Set rocks;

    float[] T = new float[16];
    float[] R = new float[16];
    float[] S = new float[16];
    float[] TR = new float[16];
    float[] TRS = new float[16];

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {

    };

    float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };

    public boolean spawning = false;
    boolean currentCollision = false;

    public RockManager() {
        rocks = Collections.synchronizedSet(new HashSet<Shot>());

        // generate several rocks
        for( int i = 0; i < 10; i++ ){
            float newX = (Math.random() < 0.5) ? 1.0f : -1.0f;
            float newY = (Math.random() < 0.5) ? 1.0f : -1.0f;

            Rock r = new Rock( newX, newY);

            add(r);
        }

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public synchronized void logic(Ship s) {
        Iterator<Rock> itr = rocks.iterator();
        int rmCount = 0; // number removed

        while(itr.hasNext()){
            Rock r = itr.next();

            // if there is a current collision, then move nothing
            if(!currentCollision) {
                r.logic();


                //check if off screen
                if (r.getX() > 1.0f || r.getX() < -1.0f ||
                        r.getY() > 1.0f || r.getY() < -1.0f) {
                    itr.remove();
                    rmCount++;
                }
                //check for collisions with ship
                if (Math.abs((float) r.getX() - (float) s.getX()) < .1f &&
                        Math.abs((float) r.getY() - (float) s.getY()) < .1f) {
                    s.explode();
                    currentCollision = true;

                }
            }
        }

        // respawn number removed, must do this outside of iterator
        for( int i = 0; i < rmCount; i++ ) {
            spawn();
        }
    }

    private void draw(float[] mvpMatrix, int vertexCount) {
        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation

        // Matrix.setRotate (float degrees)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void drawAll(){
        Iterator<Rock> itr = rocks.iterator();

        while(itr.hasNext()) {
            Rock r = itr.next();

            ///////////////////////////////////////////////////////////////

            float[] rockVerts = r.getVerts();

            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (number of coordinate values * 4 bytes per float)
                    rockVerts.length * 4);
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());

            // create a floating point buffer from the ByteBuffer
            vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            vertexBuffer.put(rockVerts);
            // set the buffer to read the first coordinate
            vertexBuffer.position(0);

            int vertexCount = rockVerts.length / COORDS_PER_VERTEX;
            int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


            ///////////////////////////////////////////////////////////////

            Matrix.setIdentityM(T, 0);
            Matrix.translateM(T, 0, r.getX(), r.getY(), 0.0f );

            Matrix.setIdentityM(R, 0);
            Matrix.setRotateM(R, 0, r.getDirection(), 0.0f, 0.0f, 1.0f);

            Matrix.setIdentityM(S, 0);
            Matrix.scaleM(S, 0, 0.05f, 0.05f, 0.0f);

            Matrix.multiplyMM(TR, 0, T, 0, R, 0);
            Matrix.multiplyMM(TRS, 0, TR, 0, S, 0);

            // Add program to OpenGL environment
            GLES20.glUseProgram(mProgram);

            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                    mPositionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            draw(TRS, vertexCount);
        }
    }

    private synchronized void add(Rock r){
        rocks.add(r);
    }

    // expose rocks iterator for comparison to shots in the air
    public synchronized Iterator<Rock> getItr(){
        return rocks.iterator();
    }

    public synchronized void spawn() {
        float newX = (Math.random() < 0.5) ? 1.0f : -1.0f;
        float newY = (Math.random() < 0.5) ? 1.0f : -1.0f;

        Rock r = new Rock( newX, newY);

        add(r);
    }

    public void removeShaderProgram() {
        GLES20.glDeleteProgram(mProgram);
    }
}
