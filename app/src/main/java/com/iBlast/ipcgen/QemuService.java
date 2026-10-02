package com.iBlast.ipcgen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class QemuService extends Service {

    private static final String TAG = "QemuService";
    private static final String CHANNEL_ID = "ipcgen_channel";
    private static final int NOTIFICATION_ID = 1;
    private Process qemuProcess;
    private boolean isRunning = false;
    private Thread monitorThread;

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            Log.d(TAG, "Service onCreate called");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d(TAG, "onStartCommand called");

            // Create notification channel first
            createNotificationChannel();
            startForegroundNotification();

            if (intent == null) {
                Log.w(TAG, "Intent is null");
                return START_STICKY;
            }

            String imagePath = intent.getStringExtra("image_path");
            if (imagePath == null || imagePath.trim().isEmpty()) {
                Log.w(TAG, "Image path is empty");
                return START_STICKY;
            }

            Log.d(TAG, "Starting QEMU with image: " + imagePath);
            startQemu(imagePath);
            return START_STICKY;

        } catch (Exception e) {
            Log.e(TAG, "Error in onStartCommand: " + e.getMessage());
            return START_STICKY;
        }
    }

    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "ipcgen Service",
                        NotificationManager.IMPORTANCE_LOW
                );
                channel.setDescription("QEMU VM Service");

                NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification channel: " + e.getMessage());
        }
    }

    private void startForegroundNotification() {
        try {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(
                        this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );
            } else {
                pendingIntent = PendingIntent.getActivity(
                        this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, CHANNEL_ID);
            } else {
                builder = new Notification.Builder(this);
            }

            builder.setContentTitle("ipcgen")
                    .setContentText("QEMU VM is running")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentIntent(pendingIntent);

            startForeground(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Foreground service started");
        } catch (Exception e) {
            Log.e(TAG, "Error in startForegroundNotification: " + e.getMessage());
        }
    }

    private void startQemu(String imagePath) {
        try {
            File qemuBin = findQemuBinary();
            if (qemuBin == null) {
                Log.e(TAG, "QEMU binary not found in any location");
                return;
            }

            File imgFile = new File(imagePath);
            if (!imgFile.exists()) {
                Log.e(TAG, "Image file not found: " + imagePath);
                return;
            }

            String[] command = {
                    qemuBin.getAbsolutePath(),
                    "-m", "1024",
                    "-smp", "2",
                    "-drive", "file=" + imgFile.getAbsolutePath() + ",if=virtio,format=raw",
                    "-net", "nic,model=virtio",
                    "-net", "user",
                    "-audiodev", "none,id=snd0",
                    "-machine", "accel=kvm:tcg",
                    "-display", "none",
                    "-daemonize"
            };

            qemuProcess = Runtime.getRuntime().exec(command);
            isRunning = true;
            Log.d(TAG, "QEMU process started: " + qemuBin.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "IOException starting QEMU: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception starting QEMU: " + e.getMessage());
        }
    }

    private File findQemuBinary() {
        String[] possiblePaths = {
                "/data/local/tmp/qemu-system-x86_64",
                "/system/bin/qemu-system-x86_64",
                "/system/xbin/qemu-system-x86_64",
                "/data/data/com.iBlast.ipcgen/files/qemu-system-x86_64"
        };

        for (String path : possiblePaths) {
            try {
                File f = new File(path);
                if (f.exists() && f.canExecute()) {
                    Log.d(TAG, "Found QEMU binary: " + path);
                    return f;
                }
            } catch (Exception e) {
                Log.d(TAG, "Error checking " + path + ": " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            if (qemuProcess != null && isRunning) {
                qemuProcess.destroy();
                isRunning = false;
                Log.d(TAG, "QEMU process destroyed");
            }
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
