package nf.co.av_club.asteroidsgl;

/**
 * Created by VF on 9/19/2016.
 */

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

public class Score {
    static double score;
    static Activity act; // just a handle so its possible to update a textview

    static void incrScore(double amount){
        score += amount;

        // textview updates must be run on original thread, thanks to stackoverflow for this bridge
        act.runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = (TextView) act.findViewById(R.id.score);

                // take the ".0" off double value
                String _score = String.valueOf(score);
                _score = _score.substring(0, _score.length()-2);

                tv.setText("Score: " + _score);
            }
        });
    }

    static void setActivity(Activity _act){
        act = _act;
    }

    static void reset(){
        score = 0;
    }
}
