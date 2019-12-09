package in.geekofia.ftpfm.callables;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.managers.TransferThreadPoolManager;
import in.geekofia.ftpfm.models.Profile;
import in.geekofia.ftpfm.utils.Util;

import static in.geekofia.ftpfm.utils.CustomFunctions.fetchString;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class DownloadCallable implements Callable {

    // Keep a weak reference to the CustomThreadPoolManager singleton object, so we can send a
    // message. Use of weak reference is not a must here because CustomThreadPoolManager lives
    // across the whole application lifecycle
    private WeakReference<TransferThreadPoolManager> mTransferThreadPoolManagerWeakReference;
    private Intent intent;
    private Context mContext;
    private String mRemoteFilePath, mRemoteFileName, mLocalFilePath, mLocalFileName;
    private long mFileSize;
    private Profile profile;
    private LocalBroadcastManager mLocalBroadcastManager;
    private static final String DOWNLOAD_CHANNEL_ID = "FTP_DOWNLOAD";
    private RemoteViews mNotificationLayout;

    public DownloadCallable(Context context, Intent intent) {
        this.mContext = context;
        this.intent = intent;
    }

    public DownloadCallable(Context context, Intent intent, RemoteViews notificationLayout) {
        this.mContext = context;
        this.intent = intent;
        this.mNotificationLayout = notificationLayout;
    }

    public DownloadCallable(Context context, Intent intent, LocalBroadcastManager localBroadcastManager) {
        this.mContext = context;
        this.intent = intent;
        this.mLocalBroadcastManager = localBroadcastManager;
    }


    @Override
    public Object call() throws Exception {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();

            if (intent != null) {
                this.profile = (Profile) intent.getSerializableExtra("mProfile");
                this.mRemoteFilePath = intent.getStringExtra("mRemoteFilePath");
                this.mRemoteFileName = intent.getStringExtra("RemoteFileName");
                this.mLocalFilePath = intent.getStringExtra("mLocalFilePath");
                this.mLocalFileName = intent.getStringExtra("mLocalFileName");
                this.mFileSize = intent.getLongExtra("mFileSize", 0);
//                this.receiver = intent.getParcelableExtra("transferProgressReceiver");
            }

            FTPClient mFTPClient = new FTPClient();
            mFTPClient.setControlEncoding("UTF-8");
            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());


            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = fetchString(mContext, R.string.channel_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, name, importance);
                notificationManagerCompat.createNotificationChannel(channel);
            }

            String localFilePath, localFileName;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            boolean success;

//            final int progressMax = 100;
//            double currentProgress = 0;
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

            mNotificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_cloud_download);
            mNotificationLayout.setTextViewText(R.id.notification_title, mRemoteFileName);
            mNotificationLayout.setTextViewText(R.id.notification_content, "Download in progress");

            NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cloud_download)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(mNotificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);

            notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notification.build());

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
//                    currentProgress = Double.parseDouble((new DecimalFormat("##.##").format(100.0 * downloadedFileSize / mFileSize)));
                }
            }

            try {
                success = mFTPClient.completePendingCommand();

                if (success) {
                    mNotificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_done_all);
                    mNotificationLayout.setTextViewText(R.id.notification_content, "Download finished");

                    notification.setCustomContentView(mNotificationLayout)
                            .setOngoing(false);
                    notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notification.build());
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

            // After work is finished, send a message to CustomThreadPoolManager
            Message message = Util.createMessage(Util.MESSAGE_ID, "Thread " +
                    String.valueOf(Thread.currentThread().getId()) + " " +
                    String.valueOf(Thread.currentThread().getName()) + " completed");

            if (mTransferThreadPoolManagerWeakReference != null
                    && mTransferThreadPoolManagerWeakReference.get() != null) {

                mTransferThreadPoolManagerWeakReference.get().sendMessageToUiThread(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCustomThreadPoolManager(TransferThreadPoolManager transferThreadPoolManager) {
        this.mTransferThreadPoolManagerWeakReference = new WeakReference<TransferThreadPoolManager>(transferThreadPoolManager);
    }
}
