package nf.co.av_club.asteroidsgl;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by VF on 9/19/2016.
 */
public class Lives {
    static int lives = 3;
    static Activity act;

    static synchronized void decrement(){
        lives--;
        print();
    }

    static int livesLeft(){
        return lives;
    }

    static void setActvity(Activity _act){
        act = _act;
    }

    static void reset(){
        lives = 3;
        print();
    }

    private static void print(){
        act.runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = (TextView) act.findViewById(R.id.lives);
                tv.setText("Lives: " + String.valueOf(lives));
            }
        });
    }
}
