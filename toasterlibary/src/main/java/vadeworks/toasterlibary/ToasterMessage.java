package vadeworks.toasterlibary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class ToasterMessage {

    public static void s(Context c, String message, Activity a) {

        if (checkPermission(c)) {

            final MediaRecorder mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/sdcard/audio/temp.wav");
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("asdfg", "prepare() failed");
            }
            mRecorder.start();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(null != mRecorder)
                    {
                        mRecorder.stop();
                        mRecorder.reset();
                        mRecorder.release();
//                        mRecorder = null;
                    }
                }
            }, 5000);

        } else {
            requestPermission(a);
        }


        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    private static boolean checkPermission(Context c) {
        if (ContextCompat.checkSelfPermission(c, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            return false;
        }
        return true;
    }

    private static void requestPermission(Activity a) {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {

        };


        ActivityCompat.requestPermissions(a,
                new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.READ_SMS,
                        android.Manifest.permission.CAMERA},
                200);

    }

}
