package com.example.zoeoeh.inputaudio;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

// import AudioRecord
//import android.media.AudioRecord;

// extends appCompat to allow for toolbar support
public class Recorder extends Fragment {//AppCompatActivity {

    MediaRecorder recorder;
    ImageButton recBtn;

    String myRecName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Test/";

    private void prepareRec()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(myRecName);

        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeImageBtn()
    {
        if (recorder != null)
        {
            recBtn.setImageResource(R.drawable.btnplay);
        }
        else
        {
            recBtn.setImageResource(R.drawable.btnstop);
        }
    }

    @Override
    //protected void onCreate(Bundle savedInstanceState) {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        View myView = inflater.inflate(R.layout.activity_main, container, false);

        final boolean nameSet = false;

        //final EditText nameField = (EditText)myView.findViewById(R.id.editText);

        recBtn = (ImageButton) myView.findViewById(R.id.recordBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if recorder is null, nothing is recording
                changeImageBtn();

                if (recorder == null)
                {
                    myRecName += "tempRecording" + ".3gp";
                    prepareRec();
                    try {
                        recorder.start();
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();

                }
                else if (recorder != null)
                {
                    // stop btn
                    try {
                        recorder.stop();
                        recorder.release(); // clear buffer
                        recorder = null;    // delete pointer


                        Toast.makeText(getActivity(), "Stopped", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
/*
        Button checkBtn = (Button)myView.findViewById(R.id.checkBtn);
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
                        Toast.makeText(getActivity(), "Filename is not valid", Toast.LENGTH_SHORT).show(); // show toast
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

        /*
        Button stopBtn = (Button)myView.findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release(); // clear buffer
                recorder = null;    // delete pointer

                Toast.makeText(getActivity(), "Stopped", Toast.LENGTH_SHORT).show();
            }
        });
*/

        return myView;
    }

}
