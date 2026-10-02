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
    private Process qemuProcess;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        
        String imagePath = intent.getStringExtra("image_path");
        if (imagePath == null || imagePath.trim().isEmpty()) {
            stopSelf();
            return START_NOT_STICKY;
        }

        startQemu(imagePath);
        return START_STICKY;
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ipcgen";
            String channelId = CHANNEL_ID;

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    name,
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("ipcgen")
                .setContentText("QEMU VM is running in background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }

    private void startQemu(String imagePath) {
        try {
            File qemuBin = findQemuBinary();

            if (qemuBin == null || !qemuBin.exists()) {
                Log.e(TAG, "QEMU binary not found.");
                stopSelf();
                return;
            }

            File imgFile = new File(imagePath);
            if (!imgFile.exists()) {
                Log.e(TAG, "Image file not found: " + imagePath);
                stopSelf();
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

            Log.d(TAG, "QEMU executed: " + qemuBin.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "QEMU start failed: " + e.getMessage(), e);
            isRunning = false;
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            isRunning = false;
            stopSelf();
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
            File f = new File(path);
            if (f.exists() && f.canExecute()) {
                return f;
            }
        }

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (qemuProcess != null) {
            try {
                qemuProcess.destroy();
                isRunning = false;
            } catch (Exception e) {
                Log.e(TAG, "Error destroying process: " + e.getMessage());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
