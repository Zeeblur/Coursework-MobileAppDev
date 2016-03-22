package com.example.zoeoeh.inputaudio;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.util.List;

// import AudioRecord
//import android.media.AudioRecord;

// extends appCompat to allow for toolbar support
public class Recorder extends Fragment {

    MediaRecorder recorder;
    ImageButton recBtn;
    EditText result;

    String recDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Test/";
    String fileExt = ".AAC";

    String tempName= "audio_recording";

    String temporaryRecording = recDir + tempName;
    int tempRecNameCount = 0;

    File tempFile;
    /**
     *
     File sdcard = Environment.getExternalStorageDirectory();
     File from = new File(sdcard,"from.txt");
     File to = new File(sdcard,"to.txt");
     from.renameTo(to);
     */

    private void prepareRec()
    {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(temporaryRecording);

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
                    // add one to count of recordings
                    tempRecNameCount++;
                    temporaryRecording = recDir + tempName + tempRecNameCount + fileExt;
                    prepareRec();
                    try {
                        recorder.start();
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();

                    // initialise file with temp recording path for renaming + deleting
                    tempFile = new File(temporaryRecording);

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

                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.prompt_save, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false) // cannot cancel

                                    // Positive button checks filename
                            .setPositiveButton("Save",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            result.setText(userInput.getText());
                                        }
                                    })

                                    // Negative button deletes file and closes dialog
                            .setNegativeButton("Delete",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // close dialog
                                            dialog.cancel();

                                            // delete file
                                            boolean deleted = tempFile.delete();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();


                }
            }
        });

        // components from main.xml
        Button button = (Button) myView.findViewById(R.id.buttonPrompt);
        result = (EditText) myView.findViewById(R.id.editTextResult);

        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.prompt_save, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false) // cannot cancel

                        // Positive button checks filename
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        result.setText(userInput.getText());
                                    }
                                })

                        // Negative button deletes file and closes dialog
                        .setNegativeButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // close dialog
                                        dialog.cancel();

                                        // delete file
                                        boolean deleted = tempFile.delete();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        return myView;
    }

    public boolean validateInputFileName(String input)
    {
        // check if existing file
        File file = new File(recDir + input + fileExt);
        if(file.exists())
        {
            return false;
        }

        return true;

    }

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

 */

}
