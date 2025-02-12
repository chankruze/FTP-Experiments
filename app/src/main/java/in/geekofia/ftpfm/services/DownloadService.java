/*
 * Created by chankruze (Chandan Kumar Mandal) on 8/12/19 10:32 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.managers.TransferThreadPoolManager;
import in.geekofia.ftpfm.callables.DownloadCallable;

public class DownloadService extends Service {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

//    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String DOWNLOAD_ACTION = "in.geekofia.ftpfm.services.DownloadService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);

        DownloadCallable downloadCallable = new DownloadCallable(this, intent, notificationLayout);
        TransferThreadPoolManager transferThreadPoolManager = TransferThreadPoolManager.getsInstance();
        downloadCallable.setCustomThreadPoolManager(transferThreadPoolManager);
        transferThreadPoolManager.addCallable(downloadCallable);

        stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
