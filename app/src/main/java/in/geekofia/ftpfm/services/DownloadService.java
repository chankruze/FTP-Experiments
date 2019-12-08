/*
 * Created by chankruze (Chandan Kumar Mandal) on 8/12/19 10:32 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import in.geekofia.ftpfm.models.Profile;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class DownloadService extends Service {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private String mRemoteFilePath, mRemoteFileName, mLocalFilePath, mLocalFileName;
    private long mFileSize;
    private Profile profile;
    private ResultReceiver receiver;

    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String ACTION = "in.geekofia.ftpfm.services.DownloadService";

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Determine the number of cores on the device
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        // Construct thread pool passing in configuration options
        // int minPoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
        // BlockingQueue<Runnable> workQueue
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        if (intent != null){
            this.profile = (Profile) intent.getSerializableExtra("mProfile");
            this.mRemoteFilePath = intent.getStringExtra("mRemoteFilePath");
            this.mRemoteFileName = intent.getStringExtra("RemoteFileName");
            this.mLocalFilePath = intent.getStringExtra("mLocalFilePath");
            this.mLocalFileName = intent.getStringExtra("mLocalFileName");
            this.mFileSize = intent.getLongExtra("mFileSize", 0);
            this.receiver = intent.getParcelableExtra("transferProgressReceiver");
        }

        // Executes a task on a thread in the thread pool
        executor.execute(new Runnable() {
            public void run() {
                FTPClient mFTPClient = new FTPClient();
                mFTPClient.setControlEncoding("UTF-8");

                if (profile != null){
                    ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());
                }

                String localFilePath, localFileName;
                InputStream inputStream = null;
                OutputStream outputStream = null;
                boolean success;

                final int progressMax = 100;
                double currentProgress = 0;
                long downloadedFileSize = 0;
                int DOWNLOAD_NOTIFICATION_ID = (int) (Math.random() * 100);

                if (mLocalFilePath.equals("")) {
                    String FTP_FOLDER = "FTP";
                    File FTP_folder = new File(Environment.getExternalStorageDirectory(), FTP_FOLDER);
                    if (!FTP_folder.exists()) {
                        FTP_folder.mkdirs();
                    }
                    localFilePath = FTP_folder.toString();
                    System.out.println("## Local File Path" + localFilePath);
                } else {
                    localFilePath = mLocalFilePath;
                }

                if (mLocalFileName.equals("")) {
                    localFileName = mRemoteFileName;
                } else {
                    localFileName = mLocalFileName;
                }

                File downloadFile = new File(localFilePath + "/" + localFileName);

//        System.out.println("##########################");
//        System.out.println("### " + mRemoteFilePath);
//        System.out.println("### " + mRemoteFileName);
//        System.out.println("### " + mLocalFilePath);
//        System.out.println("### " + downloadFile);
//        System.out.println("### " + mFileSize);
//        System.out.println("##########################");
//        ##########################
//        ### 0/app-debug.apk/
//        ### app-debug.apk
//        ### /storage/emulated/0/FTP
//        ### /storage/emulated/0/FTP/app-debug.apk
//        ### 3355972
//        ##########################

                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("## Exception [outputStream] ");
                }

                try {
                    inputStream = mFTPClient.retrieveFileStream(mRemoteFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] bytesArray = new byte[4096];
                int bytesRead = -1;


                while (true) {
                    try {
                        if (!((bytesRead = inputStream.read(bytesArray)) != -1)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.write(bytesArray, 0, bytesRead);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        downloadedFileSize += bytesRead;
                        currentProgress = Double.parseDouble((new DecimalFormat("##.##").format(100.0 * downloadedFileSize / mFileSize)));
                        Intent intent = new Intent(ACTION);
                        intent.putExtra("file", mRemoteFileName);
                        intent.putExtra("progress", (int) currentProgress);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    }
                }

                try {
                    success = mFTPClient.completePendingCommand();

                    if (success) {
                        System.out.println(mRemoteFileName + " has been downloaded successfully.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Bundle b = new Bundle();
//                if (receiver != null) {
//                    receiver.send(STATUS_FINISHED, b);
//                }
//
                stopSelf();
            }
        });

        return START_NOT_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
