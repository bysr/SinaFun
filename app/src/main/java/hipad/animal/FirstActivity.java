package hipad.animal;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.hss01248.dialog.StyledDialog;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hipad.dialog.DialogActivity;
import hipad.unittest.R;

public class FirstActivity extends AppCompatActivity {

    Button button, button1, button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        final Sample sample = new Sample(Color.RED, "hahah");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                transitionToActivity(SecondActivity.class, sample);

//                transitionToActivity(SecondActivity.class, sample, R.string.app_name);
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);

            }


        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, LineChartActivity2.class);
                startActivity(intent);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, LineChartActivityColored.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, LineChartTime.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, SinaActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstActivity.this, SinaActivity2.class);
                startActivity(intent);
            }
        });

        StyledDialog.init(getApplicationContext());
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstActivity.this, CubicLineChartActivity.class);
                startActivity(intent);

                new BottomDialog.Builder(FirstActivity.this)
                        .setTitle("Awesome!")
                        .setContent("What can we improve? Your feedback is always welcome.")
                        .setIcon(R.mipmap.ic_launcher)
                        //.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_launcher))
                        .show();


            }
        });

        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstActivity.this, DialogActivity.class);
                startActivity(intent);


            }
        });


//        HighLightView showTipForView = new HighLightView(this);
//        showTipForView.showTipForView(button1, "你好", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(FirstActivity.this, "nii", Toast.LENGTH_SHORT).show();
//            }
//        });


    }


    private void transitionToActivity(Class target, Sample sample, int transitionName) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, false,
                new Pair<>(button, getString(transitionName)));
        startActivity(target, pairs, sample);
    }

    private void startActivity(Class target, Pair<View, String>[] pairs, Sample sample) {
        Intent i = new Intent(this, target);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        i.putExtra("sample", sample);
        this.startActivity(i, transitionActivityOptions.toBundle());
    }

}
