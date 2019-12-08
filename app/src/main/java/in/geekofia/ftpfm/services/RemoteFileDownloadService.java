/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 8:39 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import in.geekofia.ftpfm.models.Profile;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class RemoteFileDownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static String DOWNLOAD_CHANNEL_ID = "FTP_DOWNLOAD";
    private String mRemoteFilePath, mRemoteFileName, mLocalFilePath, mLocalFileName;
    private long mFileSize;
    private Profile profile;

    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String ACTION = "in.geekofia.ftpfm.services.RemoteFileDownloadService";

    public RemoteFileDownloadService() {
        super(RemoteFileDownloadService.class.getName());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent != null){
            this.profile = (Profile) intent.getSerializableExtra("mProfile");
            this.mRemoteFilePath = intent.getStringExtra("mRemoteFilePath");
            this.mRemoteFileName = intent.getStringExtra("RemoteFileName");
            this.mLocalFilePath = intent.getStringExtra("mLocalFilePath");
            this.mLocalFileName = intent.getStringExtra("mLocalFileName");
            this.mFileSize = intent.getLongExtra("mFileSize", 0);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("transferProgressReceiver");
        FTPClient mFTPClient = new FTPClient();
        mFTPClient.setControlEncoding("UTF-8");

        if (profile != null){
            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());
        }

        String mLocalFilePath, mLocalFileName;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success;

        final int progressMax = 100;
        double currentProgress = 0;
        long downloadedFileSize = 0;
        int DOWNLOAD_NOTIFICATION_ID = (int) (Math.random() * 100);

        if (this.mLocalFilePath.equals("")) {
            String FTP_FOLDER = "FTP";
            File FTP_folder = new File(Environment.getExternalStorageDirectory(), FTP_FOLDER);
            if (!FTP_folder.exists()) {
                FTP_folder.mkdirs();
            }
            mLocalFilePath = FTP_folder.toString();
            System.out.println("## Local File Path" + mLocalFilePath);
        } else {
            mLocalFilePath = this.mLocalFilePath;
        }

        if (this.mLocalFileName.equals("")) {
            mLocalFileName = mRemoteFileName;
        } else {
            mLocalFileName = this.mLocalFileName;
        }

        File downloadFile = new File(mLocalFilePath + "/" + mLocalFileName);

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
                Intent progressIntent = new Intent(ACTION);
                progressIntent.putExtra("file", mRemoteFileName);
                progressIntent.putExtra("progress", (int) currentProgress);
                mLocalBroadcastManager.sendBroadcast(progressIntent);
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

        Bundle b = new Bundle();
        b.putInt("OPERATION", 1);
        if (receiver != null) {
            receiver.send(STATUS_FINISHED, b);
        }
    }
}
