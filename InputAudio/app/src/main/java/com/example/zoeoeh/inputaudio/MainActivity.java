package com.example.zoeoeh.inputaudio;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

import java.io.FileOutputStream;

// import AudioRecord
//import android.media.AudioRecord;

// extends appCompat to allow for toolbar support
public class MainActivity extends AppCompatActivity {

    MediaRecorder recorder = new MediaRecorder();

    String myRecName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Test/";

    private void prepareRec()
    {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(myRecName);

        try {
            recorder.prepare();
        } catch (Exception e) {
            System.out.print("caught exception");
        }
    }

    private void switchActivityBtn()
    {
        Button switchBtn = (Button)findViewById(R.id.switchBtn);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playActivity = new Intent(MainActivity.this, PlayTune.class);
                startActivity(playActivity);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        switchActivityBtn();

        final boolean nameSet = false;

        final EditText nameField = (EditText)findViewById(R.id.editText);

        Button recBtn = (Button)findViewById(R.id.recBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.start();

                Toast.makeText(getBaseContext(), "Started", Toast.LENGTH_SHORT).show();
                //setText("stop");
            }
        });
        Button checkBtn = (Button)findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // check text field
                String text = nameField.getText().toString();
                boolean valid = true;

                for (int i = 0; i < text.length(); ++i)
                {
                    char x = text.charAt(i);
                    if (!Character.isLetterOrDigit(x))  // if not digit or letter valid = false break
                    {
                        valid = false;
                        Toast.makeText(getBaseContext(), "Filename is not valid", Toast.LENGTH_SHORT).show(); // show toast
                        break;
                    }
                }

                if (valid)
                {
                    myRecName += text + ".3gp";
                    prepareRec();
                }
            }
        });

        Button stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release(); // clear buffer
                recorder = null;    // delete pointer

                Toast.makeText(getBaseContext(), "Stopped", Toast.LENGTH_SHORT).show();
            }
        });



    }



}
