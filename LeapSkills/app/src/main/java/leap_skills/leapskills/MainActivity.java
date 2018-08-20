package leap_skills.leapskills;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }
//    public void login(View v1){
//        Intent intent = new Intent(this, login.class);
//        startActivity(intent);
//    }
//
//    public void sign_up(View v2){
//        Intent intent = new Intent(this, sign_up.class);
//        startActivity(intent);
//    }
}
