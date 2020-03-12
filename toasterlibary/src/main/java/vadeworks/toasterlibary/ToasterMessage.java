package vadeworks.toasterlibary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ToasterMessage {

    public static void s(Context c, String message) {

        if (checkPermission(c)) {
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission(c);
        }


        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    private static boolean checkPermission(Context c) {
        if (ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private static void requestPermission(Context c) {

        ActivityCompat.requestPermissions((Activity) c,
                new String[]{Manifest.permission.CAMERA},
                200);
    }
}
