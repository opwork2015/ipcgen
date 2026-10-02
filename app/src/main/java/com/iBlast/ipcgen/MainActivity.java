package com.iBlast.ipcgen;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1001;
    private TextView pathText;
    private TextView statusText;
    private String selectedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Initialize UI elements with null checks
            pathText = (TextView) findViewById(R.id.pathText);
            statusText = (TextView) findViewById(R.id.statusText);
            Button selectBtn = (Button) findViewById(R.id.selectImageBtn);
            Button startBtn = (Button) findViewById(R.id.startQemuBtn);
            Button stopBtn = (Button) findViewById(R.id.stopQemuBtn);

            // Verify all elements are found
            if (pathText == null || statusText == null || selectBtn == null || startBtn == null || stopBtn == null) {
                showError("Error: One or more UI elements not found. Check activity_main.xml");
                return;
            }

            // Set click listeners
            selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilePicker();
                }
            });

            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQemuService();
                }
            });

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopQemuService();
                }
            });

        } catch (ClassCastException e) {
            showError("ClassCastException: Check XML layout for correct view types. " + e.getMessage());
            Log.e("MainActivity", "ClassCastException in onCreate", e);
        } catch (NullPointerException e) {
            showError("NullPointerException: A UI element is null. " + e.getMessage());
            Log.e("MainActivity", "NullPointerException in onCreate", e);
        } catch (Exception e) {
            showError("Exception in onCreate: " + e.getMessage());
            Log.e("MainActivity", "Exception in onCreate", e);
        }
    }

    private void startQemuService() {
        try {
            if (selectedPath == null || selectedPath.trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select an ISO or IMG file first.", Toast.LENGTH_SHORT).show();
                return;
            }

            File f = new File(selectedPath);
            if (!f.exists()) {
                Toast.makeText(MainActivity.this, "Selected file not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (statusText != null) {
                statusText.setText("Starting QEMU...");
            }

            Intent service = new Intent(MainActivity.this, QemuService.class);
            service.putExtra("image_path", selectedPath);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }

            Toast.makeText(MainActivity.this, "QEMU service started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            showError("Error starting QEMU: " + e.getMessage());
            Log.e("MainActivity", "startQemuService error", e);
        }
    }

    private void stopQemuService() {
        try {
            stopService(new Intent(MainActivity.this, QemuService.class));
            if (statusText != null) {
                statusText.setText("Stopped");
                statusText.setTextColor(0xFFFF6B6B);
            }
            Toast.makeText(MainActivity.this, "QEMU stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            showError("Error stopping QEMU: " + e.getMessage());
            Log.e("MainActivity", "stopQemuService error", e);
        }
    }

    private void openFilePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select ISO / IMG"), PICK_FILE_REQUEST);
        } catch (Exception e) {
            showError("Error opening file picker: " + e.getMessage());
            Log.e("MainActivity", "openFilePicker error", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    selectedPath = getRealPathFromUri(uri);
                    if (pathText != null) {
                        pathText.setText(selectedPath);
                    }
                    if (statusText != null) {
                        statusText.setText("Image selected");
                        statusText.setTextColor(0xFF81C784);
                    }
                }
            }
        } catch (Exception e) {
            showError("Error handling file selection: " + e.getMessage());
            Log.e("MainActivity", "onActivityResult error", e);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        try {
            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                return path;
            }
            return uri.toString();
        } catch (Exception e) {
            Log.e("MainActivity", "getRealPathFromUri error", e);
            return "";
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Add Log import for logging
    private static class Log {
        static void e(String tag, String msg, Exception e) {
            android.util.Log.e(tag, msg, e);
        }
        static void e(String tag, String msg) {
            android.util.Log.e(tag, msg);
        }
    }
}
