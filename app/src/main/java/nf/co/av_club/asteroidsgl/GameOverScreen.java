package nf.co.av_club.asteroidsgl;


/**
 * Created by VF on 9/15/2016.
 */
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class GameOverScreen {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
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

    private FloatBuffer vertexBuffer = null;
    private ShortBuffer drawListBuffer = null;
    private int mProgram = 0;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float[] squareCoords = new float[16];

    private final short drawOrder[] = { 0, 1 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 0.15f };

    float[] S;

    float scale = 0.0f;

    boolean stopGOSAnimation = false;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public GameOverScreen() {
        S = new float[16];
    }

    public void drawG() {
        drawLine(-0.6f, 1.0f, -1.0f, 1.0f);
        drawLine(-1.0f, 1.0f, -1.0f, 0.2f);
        drawLine(-1.0f, 0.2f, -0.6f, 0.2f);
        drawLine(-0.6f, 0.2f, -0.6f, 0.6f);
        drawLine(-0.6f, 0.6f, -0.8f, 0.6f);
    }

    public void drawA() {
        drawLine(-0.1f, 0.2f, -0.1f, 1.0f);
        drawLine(-0.1f, 1.0f, -0.5f, 1.0f);
        drawLine(-0.5f, 1.0f, -0.5f, 0.2f);
        drawLine(-0.5f, 0.6f, -0.1f, 0.6f);
    }

    public void drawM() {
        drawLine(0.5f, 0.2f, 0.5f, 1.0f);
        drawLine(0.5f, 1.0f, 0.3f, 0.6f);
        drawLine(0.3f, 0.6f, 0.1f, 1.0f);
        drawLine(0.1f, 1.0f, 0.1f, 0.2f);
    }

    public void drawE() {
        drawLine(1.0f, 0.2f, 0.6f, 0.2f);
        drawLine(0.6f, 0.2f, 0.6f, 1.0f);
        drawLine(0.6f, 1.0f, 1.0f, 1.0f);
        drawLine(0.6f, 0.6f, 1.0f, 0.6f);
    }

    public void drawO() {
        drawLine(-0.6f, -1.0f, -0.6f, -0.2f);
        drawLine(-0.6f, -0.2f, -1.0f, -0.2f);
        drawLine(-1.0f, -0.2f, -1.0f, -1.0f);
        drawLine(-1.0f, -1.0f, -0.6f, -1.0f);
    }

    public void drawV() {
        drawLine(-0.5f, -0.2f, -0.3f, -1.0f);
        drawLine(-0.3f, -1.0f, -0.1f, -0.2f);
    }

    // its easier to create E2, rather than
    // have to create a separate
    public void drawE2(){
        drawLine(0.1f, -0.2f, 0.5f, -0.2f);
        drawLine(0.1f, -0.6f, 0.5f, -0.6f);
        drawLine(0.1f, -1.0f, 0.5f, -1.0f);
        drawLine(0.1f, -0.2f, 0.1f, -1.0f);
    }

    public void drawR(){
        drawLine(0.6f, -1.0f, 0.6f, -0.2f);
        drawLine(0.6f, -0.2f, 1.0f, -0.2f);
        drawLine(1.0f, -0.2f, 1.0f, -0.6f);
        drawLine(1.0f, -0.6f, 0.6f, -0.6f);
        drawLine(0.6f, -0.6f, 1.0f, -1.0f);
        //drawLine(0.1f, -0.6f, 0.5f, -0.6f);
        //drawLine(0.1f, -1.0f, 0.5f, -1.0f);
        //drawLine(0.1f, -0.2f, 0.1f, -1.0f);
    }

    public void incr(){
        scale += .01f;

        Matrix.setIdentityM(S, 0);
        Matrix.scaleM(S, 0, scale, scale, 0.0f);

        // this is slow. too many calls to drawLine
        // switch to texturing
        drawG();
        drawA();
        drawM();
        drawE();

        drawO();
        drawV();
        drawE2();
        drawR();

        if(scale > 0.5f){
            stopGOSAnimation = true;
        }
    }

    public void drawLine(float x1, float y1, float x2, float y2){
        float[] points = {
                x1, y1, 0.0f,
                x2, y2, 0.0f
        };

        setQuadVerts(points);

        draw(S);
    }

    public void draw(float[] mvpMatrix) {
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
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_LINES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void setQuadVerts(float[] verts) {
        // verts must be passed by value here
        // maybe not

        squareCoords = new float[verts.length];

        for(int i = 0; i < verts.length - 1; i++){
            squareCoords[i] = verts[i];
        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }
}
