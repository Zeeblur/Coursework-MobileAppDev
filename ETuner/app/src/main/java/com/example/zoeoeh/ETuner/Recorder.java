package com.example.zoeoeh.ETuner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class Recorder extends Fragment {

    private static final String TAG = "Recorder";

    private MediaRecorder recorder;
    private ImageButton recBtn;

    private String recDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Test/";
    private String fileExt = ".AAC";

    private String tempName= "audioRecording";

    private String temporaryRecording = recDir + tempName;
    private int tempRecNameCount = 0;

    private File tempFile;

    public final static String albumName = "ETunerRecordings";
    private String defaultDescription = "My Recording";

    private void prepareRec()
    {
        // instantiate new media recorder and set up sources/formats
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(temporaryRecording);

        // prepare throws two exceptions
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed" + e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "prepare() called after start() before setOutputFormat()" + e);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.activity_main, container, false);

        final boolean nameSet = false;

        recBtn = (ImageButton) myView.findViewById(R.id.recordBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if recorder is null, nothing is recording
                changeImageBtn();


                if (recorder == null)
                {
                    // add one to count of recordings
                    temporaryRecording = recDir + tempName + "0" + fileExt;
                    prepareRec();

                    // throws exception if called before prepare()
                    try {
                        recorder.start();
                    }catch(IllegalStateException e)
                    {
                        Log.e(TAG, "start() failed: " +e);
                    }

                    Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();

                    // initialise file with temp recording path for renaming + deleting
                    tempFile = new File(temporaryRecording);

                }
                else if (recorder != null)
                {
                    // if recorder is not null this button becomes a stop button

                    // stop() can throw two exceptions, if recorder is not initialised
                    // if stop() is called before start()
                    try
                    {
                        recorder.stop();
                        Toast.makeText(getActivity(), "Stopped", Toast.LENGTH_SHORT).show();
                    }
                    catch (NullPointerException e)
                    {
                        Log.e(TAG, "Stop() - recorder is not initialised: " +e);
                    }
                    catch (IllegalStateException e)
                    {
                        Log.e(TAG, "stop() error: " +e);
                    }

                    recorder.release(); // clear buffer
                    recorder = null;    // delete pointer

                    // show dialog to ask for filename change or delete
                    showDialog();

                }
            }
        });

        return myView;
    }

    public void showDialog()
    {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_save, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInputName = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        final EditText userInputDesc = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserDescription);


        // This is for checking the temporary name
        File fileCheck = new File(recDir + tempName + tempRecNameCount + fileExt);

        // if file name exists add count. This will allow for count to persist throughout close
        while(fileCheck.exists())
        {
            tempRecNameCount++;
            fileCheck = new File(recDir + tempName + tempRecNameCount + fileExt);
        }

        userInputName.setText(tempName + tempRecNameCount);
        userInputDesc.setText(defaultDescription);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false) // cannot cancel

                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Positive button checks filename
                                // Overriden below
                            }
                        })

                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Negative button deletes file and closes dialog
                                // overriden handler after dialog is shown
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //Override button handlers after show to stop from closing if title entered is invalid
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // get user input and set it to result
                // edit text

                boolean valid = validateInputFileName(userInputName.getText().toString());

                if (valid)
                {
                    alertDialog.dismiss();

                    defaultDescription = userInputDesc.getText().toString();

                    insertFileIntoDatabase(userInputName.getText().toString(), defaultDescription);
                }
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close dialog
                alertDialog.cancel();

                // delete file
                boolean deleted = tempFile.delete();
            }
        });
    }


    private void insertFileIntoDatabase(String fileName, String fileDesc)
    {
        File mySound = new File(recDir, fileName + fileExt);

        // rename file
        boolean rename = tempFile.renameTo(mySound);

        // add recording to media database
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.ARTIST, fileDesc);
        values.put(MediaStore.Audio.Media.ALBUM, albumName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/AAC");
        values.put(MediaStore.Audio.Media.DATA, mySound.getAbsolutePath());
        ContentResolver contentResolver = TabSwitcher.getmContext().getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        TabSwitcher.getmContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(TabSwitcher.getmContext(), "Added File " + newUri, Toast.LENGTH_LONG).show();

        // sets dirty flag for updating list from database
        TabSwitcher.setListDirty(true);
    }


    public boolean validateInputFileName(String input)
    {

        // check if existing file
        File file = new File(recDir + input + fileExt);
        if(file.exists())
        {
            // filename must be unique therefore return false
            Toast.makeText(TabSwitcher.getmContext(), "Filename is already in use.", Toast.LENGTH_SHORT).show(); // show toast
            return false;
        }

        // iterate through chars check if letter or digit, if any is not return false.
        for (int i = 0; i < input.length(); ++i)
        {
            char x = input.charAt(i);
            if (!Character.isLetterOrDigit(x))  // if not digit or letter valid = false break
            {
                Toast.makeText(TabSwitcher.getmContext(), "Filename is not valid: letters and numbers only.", Toast.LENGTH_SHORT).show(); // show toast
                return false;
            }
        }

        // ensures name is entered
        if(input.length() < 1)
        {
            Toast.makeText(TabSwitcher.getmContext(), "Filename cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}
