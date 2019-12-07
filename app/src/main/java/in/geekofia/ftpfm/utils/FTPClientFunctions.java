package in.geekofia.ftpfm.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import in.geekofia.ftpfm.R;

import static in.geekofia.ftpfm.utils.CustomFunctions.fetchString;

public class FTPClientFunctions {

    private static String DOWNLOAD_CHANNEL_ID = "FTP_DOWNLOAD";

    public static boolean ftpConnect(FTPClient mFTPClient, String host, String username, String password, int port) {
        try {
            // connecting to the host
            mFTPClient.connect(host, port);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);
                /*
                 * Set File Transfer Mode
                 * To avoid corruption issue you must specified a correct
                 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                 * transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP CONNECT", "Error: could not connect to host " + host);
        }
        return false;
    }

    public static boolean ftpDisconnect(FTPClient mFTPClient) {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP DISCONNECT", "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    /**
     * FTP FILE DOWNLOAD
     */
    public static void ftpFileDownload(final FTPClient mFTPClient, Context mContext, final String mRemoteFilePath, String mRemoteFileName, String localFilePath, String localFileName, long mFileSize) throws IOException {
        String mLocalFilePath, mLocalFileName;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = fetchString(mContext, R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, name, importance);
            notificationManagerCompat.createNotificationChannel(channel);
        }

        final int progressMax = 100;
        double currentProgress = 0;
        long downloadedFileSize = 0;
        int DOWNLOAD_NOTIFICATION_ID = (int) (Math.random() * 100);

        if (localFilePath == "" || localFilePath == null) {
            String FTP_FOLDER = "FTP";
            File FTP_folder = new File(Environment.getExternalStorageDirectory(), FTP_FOLDER);
            if (!FTP_folder.exists()) {
                FTP_folder.mkdirs();
            }
            mLocalFilePath = FTP_folder.toString();
//            System.out.println("## Local File Path" + mLocalFilePath);
        } else {
            mLocalFilePath = localFilePath;
        }

        if (localFileName == "" || localFileName == null) {
            mLocalFileName = mRemoteFileName;
        } else {
            mLocalFileName = localFileName;
        }

        File downloadFile = new File(mLocalFilePath + "/" + mLocalFileName);

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("## Exception [outputStream] ");
        }

        inputStream = mFTPClient.retrieveFileStream(mRemoteFilePath);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(mRemoteFileName)
                .setContentText(currentProgress + " %")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, false);

        notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notification.build());

        byte[] bytesArray = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
            if (outputStream != null) {
                outputStream.write(bytesArray, 0, bytesRead);
                downloadedFileSize += bytesRead;
                currentProgress = Double.parseDouble((new DecimalFormat("##.##").format(100.0 * downloadedFileSize / mFileSize)));
                notification.setProgress(progressMax, (int) currentProgress, false)
                        .setContentText(currentProgress + " %");
                notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notification.build());
            }
        }

        success = mFTPClient.completePendingCommand();

        if (success) {
            notification.setContentText("Download finished")
                    .setOngoing(false);
            notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notification.build());
            System.out.println(mRemoteFileName + " has been downloaded successfully.");
        }

        if (outputStream != null) {
            outputStream.close();
        }

        inputStream.close();
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    public static void ftpFileUpload(Context context, FTPClient ftpClient, DocumentFile documentFile, String remoteDir) throws IOException {
        String fileName = documentFile.getName();
        String remoteFilePath = remoteDir + fileName;

        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(documentFile.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Start uploading file ....");
        OutputStream outputStream = null;
        try {
            outputStream = ftpClient.storeFileStream(remoteFilePath);
        } catch (IOException e) {
            Toast.makeText(context, "Outputstream error...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        byte[] bytesIn = new byte[4096];
        int read = 0;
        if (inputStream != null) {
            while ((read = inputStream.read(bytesIn)) != -1) {
                if (outputStream != null) {
                    outputStream.write(bytesIn, 0, read);
                }
            }
        }

        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        boolean completed = ftpClient.completePendingCommand();

        if (completed) {
            System.out.println("The file is uploaded successfully.");
        }
    }
}
