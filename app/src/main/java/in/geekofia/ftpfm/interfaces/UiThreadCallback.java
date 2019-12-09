package in.geekofia.ftpfm.interfaces;

import android.os.Message;

// An interface for worker threads to send messages to the UI thread
public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
