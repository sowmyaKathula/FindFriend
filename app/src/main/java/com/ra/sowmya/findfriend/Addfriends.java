package com.ra.sowmya.findfriend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Addfriends extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String TAG = "Myproject";
    String coordinates;

    ListView list;
    private BroadcastReceiver broadcastReceiver;
    Hashtable<String, String> mmp = new Hashtable<>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                                SharedPreferences preferences = getSharedPreferences("MyData",0);
                                preferences.edit().remove("login").commit();
                                Intent intent = new Intent(this,Login.class);
                                startActivity(intent);
                                finish();
                                return true;
            default:
                                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast toast = Toast.makeText(getApplicationContext(),intent.getExtras().get("coordinates").toString(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    coordinates = intent.getExtras().get("coordinates").toString();
                    String[] gps = coordinates.split(" ");
                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    String phone = sharedPreferences.getString("login","");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("mylocation",coordinates);
                    editor.commit();

                    ServerDB.UPDATE_COORDINATES(phone,gps[1],gps[0],context);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);

        list = (ListView) findViewById(R.id.list);

        if(checkAndRequestPermissions()) {

            // carry on the normal flow, as the case of  permissions  granted.
            //To start a back ground service to access the location and update in database
            Log.d("Myproject","permissions accepted");
            startGPSService();
            //read the contacts from the phone and display in the list View
            getContact();
            ServerDB.GET_ALL_USERS(this, mmp, list);

        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout v = (LinearLayout) view;
                TextView textView = (TextView) v.getChildAt(0);
                String text = textView.getText().toString();
                String[] t = text.split("\n");
                String phone = t[1];
                ServerDB.GET_LOC(phone,getBaseContext());

            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
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
        String TAG="Myproject";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {

                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow

                        //To start a back ground service to access the location and update in database
                        startGPSService();
                        //read the contacts from the phone and display in the list View
                        getContact();
                        ServerDB.GET_ALL_USERS(this, mmp, list);


                        //else any one or both the permissions are not granted
                    } else {

                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    Toast.makeText(getApplicationContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                                            .show();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
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

    private void getContact(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));


                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur != null && pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mmp.put(SantString(phoneNo),name);
                    }
                    pCur.close();
                }
            }
        }
        cur.close();
    }

    public static String SantString(String Phonenumber){
        String temp=Phonenumber.replaceAll("[-+.^: ,()]","");
        if(temp.length()==10){
            return temp;
        }else{
            String x="";
            for (int i = 2; i <temp.length() ; i++) {
                x+=temp.charAt(i);
            }
            return x;
        }
    }

    private void startGPSService() {
        Intent intent = new Intent(getApplicationContext(),GPS_Service.class);
        startService(intent);
    }

}
