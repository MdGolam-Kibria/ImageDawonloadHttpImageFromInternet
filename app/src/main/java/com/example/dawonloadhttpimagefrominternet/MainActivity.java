package com.example.dawonloadhttpimagefrominternet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private ListView listView;
    private LinearLayout linearLayout;
    private boolean succesfull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.btn);
        listView = (ListView) findViewById(R.id.listView);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        final String listLink[] = getResources().getStringArray(R.array.link_item);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.simple, R.id.modelText, listLink);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editText.setText(listLink[i]);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    linearLayout.setVisibility(View.VISIBLE);
                    MyThread myThread = new MyThread();
                    myThread.start();
                } else {
                    isStoragePermissionGranted();
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("loc", "Permission is granted");
                return true;
            } else {

                Log.v("loc", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("loc", "Permission is granted");
            return true;
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            dawonload(editText.getText().toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (succesfull = true) {
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    private boolean dawonload(String toString) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        URL url = null;
        FileOutputStream fileOutputStream = null;
        File file;
        try {
            url = new URL(toString.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = httpURLConnection.getInputStream();

            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + Uri.parse(toString).getLastPathSegment());
            fileOutputStream = new FileOutputStream(file);
            int startRead = -1;
            byte buffer[] = new byte[1024];
            while ((startRead = inputStream.read()) != -1) {
                fileOutputStream.write((byte) startRead);
                succesfull = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            succesfull = false;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return succesfull;
    }
}
