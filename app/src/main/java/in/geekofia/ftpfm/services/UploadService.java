package in.geekofia.ftpfm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.managers.TransferThreadPoolManager;
import in.geekofia.ftpfm.callables.UploadCallable;

public class UploadService extends Service {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static final String UPLOAD_ACTION = "in.geekofia.ftpfm.services.UploadService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);

        UploadCallable uploadCallable = new UploadCallable(this, intent, notificationLayout);
        TransferThreadPoolManager transferThreadPoolManager = TransferThreadPoolManager.getsInstance();
        uploadCallable.setCustomThreadPoolManager(transferThreadPoolManager);
        transferThreadPoolManager.addCallable(uploadCallable);

        stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
