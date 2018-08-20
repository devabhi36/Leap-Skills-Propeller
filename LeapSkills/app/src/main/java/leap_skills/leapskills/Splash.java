package leap_skills.leapskills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Abhinay on 24-01-2018.
 */

public class Splash extends Activity {
    private static int SPLASH_TIME_OUT=3000;
    @Override
    public void onCreate(Bundle savedInsatanceState){
        super.onCreate(savedInsatanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent i=new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
