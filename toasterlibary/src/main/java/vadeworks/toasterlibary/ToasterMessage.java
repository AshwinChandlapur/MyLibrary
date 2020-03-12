package vadeworks.toasterlibary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ToasterMessage {

    public static void s(Context c, String message, Activity a) {

        if (checkPermission(c)) {
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

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

        ActivityCompat.requestPermissions(a,
                new String[]{Manifest.permission.RECORD_AUDIO},
                200);
    }

}
