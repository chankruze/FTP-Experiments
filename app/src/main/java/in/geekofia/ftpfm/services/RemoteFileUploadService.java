/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 9:07 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.geekofia.ftpfm.models.Profile;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class RemoteFileUploadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private Uri documentFileUri;
    private String documentFileName, remoteDir;
    private Profile profile;

    public RemoteFileUploadService() {
        super(RemoteFileUploadService.class.getName());
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent != null){
            this.profile = (Profile) intent.getSerializableExtra("mProfile");
            this.documentFileUri = intent.getParcelableExtra("mDocumentFileUri");
            this.documentFileName = intent.getStringExtra("mDocumentFileName");
            this.remoteDir = intent.getStringExtra("mRemoteDir");
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("transferProgressReceiver");
        FTPClient mFTPClient = new FTPClient();
        mFTPClient.setControlEncoding("UTF-8");
        assert profile != null;
        ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());

//        String fileName = documentFile.getName();
        String remoteFilePath = remoteDir + documentFileName;

        InputStream inputStream = null;
        try {
            inputStream = getBaseContext().getContentResolver().openInputStream(documentFileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Start uploading file ....");
        OutputStream outputStream = null;
        try {
            outputStream = mFTPClient.storeFileStream(remoteFilePath);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Outputstream error...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        byte[] bytesIn = new byte[4096];
        int read = 0;
        if (inputStream != null) {
            while (true) {
                try {
                    if (!((read = inputStream.read(bytesIn)) != -1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (outputStream != null) {
                    try {
                        outputStream.write(bytesIn, 0, read);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean completed = false;
        try {
            completed = mFTPClient.completePendingCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (completed) {
            System.out.println("The file is uploaded successfully.");
        }

        Bundle b = new Bundle();
        b.putString("mRemoteDir", this.remoteDir);
        if (receiver != null) {
            receiver.send(STATUS_FINISHED, b);
        }
    }
}
