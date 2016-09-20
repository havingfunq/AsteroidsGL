package nf.co.av_club.asteroidsgl;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Set;

import android.opengl.GLES20;

/**
 * Created by VF on 9/18/2016.
 */
public class ShotManager {
    Set shots;

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
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };

    public ShotManager() {
        //shots = new LinkedList<Shot>();
        shots = Collections.synchronizedSet(new HashSet<Shot>());

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

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

    private void draw(float[] mvpMatrix) {
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

    public synchronized void logic(RockManager rm){
        Iterator<Shot> itr = shots.iterator();

        while(itr.hasNext()) {
            Shot s = itr.next();
            s.logic();
        }

        // rewind
        itr = shots.iterator();

        while(itr.hasNext()) {
            Shot s = itr.next();

            if(s.getX() > 1.0f || s.getX() < -1.0f ||
                    s.getY() > 1.0f || s.getY() < -1.0f ) {
                itr.remove();
            }
        }

        //collisions
        itr = shots.iterator();

        while(itr.hasNext()) {
            Shot s = itr.next();

            Iterator<Rock> rockItr = rm.getItr();
            int respawnCount = 0;

            while(rockItr.hasNext()){
                Rock r = rockItr.next();

                if( Math.abs((double)s.getX() - (double)r.getX()) < .05f &&
                        Math.abs((double)s.getY() - (double)r.getY()) < .05f )
                {
                    rockItr.remove();
                    itr.remove();
                    respawnCount++;

                    Score.incrScore(10);

                    break; // for the case that one shot comes close enough to two rocks
                }
            }

            // respawn the rocks
            for(int i = 0; i < respawnCount; i++){
                rm.spawn();
            }
        }
    }

    public synchronized void  drawAll(){
        Iterator<Shot> itr = shots.iterator();

        while(itr.hasNext()) {
            Shot s = itr.next();

            Matrix.setIdentityM(T, 0);
            Matrix.translateM(T, 0, s.getX(), s.getY(), 0.0f );

            Matrix.setIdentityM(R, 0);
            Matrix.setRotateM(R, 0, s.getDirection(), 0.0f, 0.0f, 1.0f);

            Matrix.setIdentityM(S, 0);
            Matrix.scaleM(S, 0, 0.05f, 0.05f, 0.0f);

            Matrix.multiplyMM(TR, 0, T, 0, R, 0);
            Matrix.multiplyMM(TRS, 0, TR, 0, S, 0);

            draw(TRS);
        }
    }

    public synchronized void fire(float posX, float posY, float speedX, float speedY, float direction) {
        if (shots.size() < 8) {
            shots.add(new Shot(posX, posY, speedX, speedY, direction));
        }
    }

    public void removeShaderProgram(){
        GLES20.glDeleteProgram(mProgram);
    }
}
