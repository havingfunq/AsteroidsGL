package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/15/2016.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    MyGLRenderer mRenderer;

    //programmatic instantiation
    public MyGLSurfaceView(Context context)
    {
        this(context, null);
    }

    //XML inflation/instantiation
    public MyGLSurfaceView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView

        mRenderer = new MyGLRenderer();

        setRenderer(mRenderer);

    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Refactor
                mRenderer.scanButtons(x, y, this.getWidth(), this.getHeight());
                //Log.i("Touch", "Down: " + String.valueOf(x) + ", " + String.valueOf(y));

                //Log.i("Touch", String.valueOf(y));

                break;


            case MotionEvent.ACTION_UP:
                //Log.i("Touch", "Action Up Case");
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
