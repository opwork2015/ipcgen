package com.iBlast.ipcgen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    private static final int PICK_FILE_REQUEST = 1001;
    private TextView pathText;
    private TextView statusText;
    private String selectedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathText = findViewById(R.id.pathText);
        statusText = findViewById(R.id.statusText);

        Button selectBtn = findViewById(R.id.selectImageBtn);
        Button startBtn = findViewById(R.id.startQemuBtn);
        Button stopBtn = findViewById(R.id.stopQemuBtn);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPath == null || selectedPath.trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select an ISO or IMG file first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                File f = new File(selectedPath);
                if (!f.exists()) {
                    Toast.makeText(MainActivity.this, "Selected file was not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                statusText.setText("Starting QEMU...");
                Intent service = new Intent(MainActivity.this, QemuService.class);
                service.putExtra("image_path", selectedPath);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service);
                } else {
                    startService(service);
                }
                
                Toast.makeText(MainActivity.this, "QEMU service started", Toast.LENGTH_SHORT).show();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, QemuService.class));
                statusText.setText("Stopped");
                Toast.makeText(MainActivity.this, "Background service stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select ISO / IMG"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedPath = getRealPathFromUri(uri);
                pathText.setText(selectedPath);
                statusText.setText("Image selected");
                statusText.setTextColor(0xFF81C784);
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            return path;
        }
        return "";
    }
}
