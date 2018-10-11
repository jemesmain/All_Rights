package com.example.jemesmain.all_rights;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final String TAG = "taq auth";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gestion des droits aide pedro
        //checkAndRequestPermissions();
        Button ask_perm_btn = (Button) findViewById(R.id.ask_perm);
        ask_perm_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions/35495855#35495855
                if (checkAndRequestPermissions()) {
                    // carry on the normal flow, as the case of  permissions  granted.
                    Log.d(TAG, "Normal Flow permission granted you can send email");
                    sendEmail();
                }
            }
        });

        Button revoke_perm_btn = (Button) findViewById(R.id.revoke_perm);
        revoke_perm_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions/35495855#35495855

                Log.d(TAG, "Enter Revoke permission ");
                revokePermissions();

            }
        });
    }


//https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions/35495855#35495855
private  boolean checkAndRequestPermissions() {
    //int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
    //int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    int permissionStorageWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int permissionStorageRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

    List<String> listPermissionsNeeded = new ArrayList<>();

    Log.d(TAG, "Permission check and request permission entered");
/*
    if (locationPermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "Permission location added");
    }
    if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        Log.d(TAG, "Permission send sms added");
    }
*/
    if (permissionStorageWrite != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(TAG, "Permission store write added to list");
    }
    if (permissionStorageRead != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "Permission store read added to list");
    }
    if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
        return false;
    }
    return true;
}


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                // perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                // perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    //perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                    //        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            ) {
                        Log.d(TAG, "read and write and other(s) services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Read / Write permission to generate vcard",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }

                        /*
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                            showDialogOK("On a besoin d'écrire pour génerer la vcard contenant vos coordonnées",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ) {
                            showDialogOK("On a besoin de lire la vcard de vos coordonnées pour la joindre à l'email de votre demmande ",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        */
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private  boolean revokePermissions() {

        return true;


    }


    protected void sendEmail() {
        Log.d("taq send email", "sendEmail begin");

        String[] TO = {"contact@bicyclopresto.fr"};
        String[] CC = {"app@bicyclopresto.fr"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "All_rights: test attachment");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Bonjour email généré par l'application All Rights \n\n");
        try {
            startActivity(Intent.createChooser(emailIntent, "Choisissez votre programme de mail"));
            Log.d("taq test",Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString());
            finish();
            Log.d("taq sending email", "Finished sending email...");
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d("taq found exception", "no email client installed");
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }
// aide de pedro
/*
    //public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    // on regarde quel permissions sont accordées
    private boolean checkAndRequestPermissions()
    {


        final int storageReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        final List<String> listPermissionsNeeded = new ArrayList<>();

        if (storageReadPermission != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d("taq auth", "read external storage granted");
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            Log.d("taq auth", "read external storage empty or not granted");
            return false;
        }

        return true;
    }


    //on gère les résultats

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String permissions[], final int[] grantResults)
    {
        for (int i = 0; i < permissions.length; i++)
        {
            if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i]) && (grantResults[i] == PackageManager.PERMISSION_GRANTED))
            {
                final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("storage_write_auth", true);
                editor.apply();

                Log.d("taq auth", "read external on request result");
            }
        }
    }

*/

}