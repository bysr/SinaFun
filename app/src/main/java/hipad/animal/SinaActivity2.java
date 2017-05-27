package hipad.animal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import hipad.unittest.R;

public class SinaActivity2 extends AppCompatActivity {


    private TextView frequency = null;

    private TextView phase = null;

    private TextView amplifier = null;

    private Button btnwave = null;

    private SineWave sineWare_one, sineWare_two;

//    SineWave sw = null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        sw = new SineWave(this);


        setContentView(R.layout.activity_sina2);

        sineWare_one = (SineWave) findViewById(R.id.sineWare_one);
//        sineWare_two = (SineWave) findViewById(R.id.sineWare_two);


        btnwave = (Button) findViewById(R.id.wave);

        frequency = (TextView) findViewById(R.id.frequency);

        phase = (TextView) findViewById(R.id.phase);

        amplifier = (TextView) findViewById(R.id.amplifier);


        btnwave.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // TODO Auto-generated method stub

                sineWare_one.Set(Float.parseFloat(amplifier.getText().toString()), Float.parseFloat(frequency.getText().toString()), Float.parseFloat(phase.getText().toString()));

            }

        });

//        sineWare_two.Set(Float.parseFloat("100.0"), Float.parseFloat("2.0"), Float.parseFloat("0.0"));
        SeekBar se = (SeekBar) findViewById(R.id.seekBar);
        se.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override

    protected void onStart() {

        // TODO Auto-generated method stub

        super.onStart();

        frequency.setText(Float.toString(sineWare_one.GetFrequency()));

        phase.setText(Float.toString(sineWare_one.GetPhase()));

        amplifier.setText(Float.toString(sineWare_one.GetAmplifier()));

        Log.d("vivi", "onStart: freq:==" + Float.toString(sineWare_one.GetFrequency()) + "   phase: " + Float.toString(sineWare_one.GetPhase()) + " amplifier: " + Float.toString(sineWare_one.GetAmplifier()));

    }


    @Override

    public boolean onTouchEvent(MotionEvent event) {

        // TODO Auto-generated method stub

        //float px = event.getX();

        //float py = event.getY();

        //sw.SetXY(px, py);

        return super.onTouchEvent(event);

    }

}
