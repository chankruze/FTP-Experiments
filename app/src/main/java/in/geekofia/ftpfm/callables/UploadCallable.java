package in.geekofia.ftpfm.callables;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileNotFoundException;
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

public class UploadCallable implements Callable {

    private WeakReference<TransferThreadPoolManager> mTransferThreadPoolManagerWeakReference;
    private Intent intent;
    private Context mContext;
    private Uri documentFileUri;
    private String documentFileName, remoteDir;
    private Profile profile;
    private LocalBroadcastManager mLocalBroadcastManager;
    private RemoteViews mNotificationLayout;
    private static final String UPLOAD_CHANNEL_ID = "FTP_UPLOAD";

    public UploadCallable(Intent intent, Context mContext) {
        this.intent = intent;
        this.mContext = mContext;
    }

    public UploadCallable(Context context, Intent intent, RemoteViews notificationLayout) {
        this.mContext = context;
        this.intent = intent;
        this.mNotificationLayout = notificationLayout;
    }


    @Override
    public Object call() throws Exception {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();

            if (intent != null) {
                this.profile = (Profile) intent.getSerializableExtra("mProfile");
                this.documentFileUri = intent.getParcelableExtra("mDocumentFileUri");
                this.documentFileName = intent.getStringExtra("mDocumentFileName");
                this.remoteDir = intent.getStringExtra("mRemoteDir");
            }

            FTPClient mFTPClient = new FTPClient();
            mFTPClient.setControlEncoding("UTF-8");
            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());

            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = fetchString(mContext, R.string.channel_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(UPLOAD_CHANNEL_ID, name, importance);
                notificationManagerCompat.createNotificationChannel(channel);
            }

            int UPLOAD_NOTIFICATION_ID = (int) (Math.random() * 100);

            String remoteFilePath = remoteDir + documentFileName;

            InputStream inputStream = null;
            try {
                inputStream = mContext.getContentResolver().openInputStream(documentFileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            System.out.println("Start uploading file ....");
            OutputStream outputStream = null;
            try {
                outputStream = mFTPClient.storeFileStream(remoteFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mNotificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_cloud_upload);
            mNotificationLayout.setTextViewText(R.id.notification_title, documentFileName);
            mNotificationLayout.setTextViewText(R.id.notification_content, "Upload in progress");

            NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, UPLOAD_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cloud_upload)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(mNotificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);

            notificationManagerCompat.notify(UPLOAD_NOTIFICATION_ID, notification.build());

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
                mNotificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_done_all);
                mNotificationLayout.setTextViewText(R.id.notification_content, "Upload finished");

                notification.setCustomContentView(mNotificationLayout)
                        .setOngoing(false);
                notificationManagerCompat.notify(UPLOAD_NOTIFICATION_ID, notification.build());
//                System.out.println("The file is uploaded successfully.");
            }

//            Bundle b = new Bundle();
//            b.putString("mRemoteDir", this.remoteDir);
//            if (receiver != null) {
//                receiver.send(STATUS_FINISHED, b);
//            }

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
