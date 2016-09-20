package nf.co.av_club.asteroidsgl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLES20Activity extends Activity {

    private GLSurfaceView mGLView;
    int score = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pass the activity to scores and lives so its
        //possible to update the textviews
        Score.setActivity(this);
        Lives.setActvity(this);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        setContentView(R.layout.activity_open_gles20);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.

        mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
        mGLView.onResume();
    }

    public void setScore(){


    }
}
