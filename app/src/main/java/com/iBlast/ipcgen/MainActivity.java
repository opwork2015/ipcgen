package com.iBlast.ipcgen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

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

        selectBtn.setOnClickListener(v -> openFilePicker());

        startBtn.setOnClickListener(v -> {
            if (selectedPath == null || selectedPath.trim().isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.select_file_first, Toast.LENGTH_SHORT).show();
                return;
            }

            File f = new File(selectedPath);
            if (!f.exists()) {
                Toast.makeText(MainActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
                return;
            }

            statusText.setText(R.string.starting_qemu);
            Intent service = new Intent(MainActivity.this, QemuService.class);
            service.putExtra("image_path", selectedPath);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
            
            Toast.makeText(MainActivity.this, R.string.qemu_started, Toast.LENGTH_SHORT).show();
        });

        stopBtn.setOnClickListener(v -> {
            stopService(new Intent(MainActivity.this, QemuService.class));
            statusText.setText(R.string.ready);
            Toast.makeText(MainActivity.this, R.string.service_stopped, Toast.LENGTH_SHORT).show();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedPath = getRealPathFromUri(uri);
                pathText.setText(selectedPath);
                statusText.setText(R.string.no_file_selected);
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null && !path.isEmpty()) {
            return path;
        }
        return uri.toString();
    }
}
