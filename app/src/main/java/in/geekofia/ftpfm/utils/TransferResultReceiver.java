/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 8:34 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class TransferResultReceiver extends ResultReceiver {

    private TransferProgressReceiver transferProgressReceiver;
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public TransferResultReceiver(Handler handler, TransferProgressReceiver transferProgressReceiver) {
        super(handler);
        this.transferProgressReceiver = transferProgressReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (transferProgressReceiver != null) {
            transferProgressReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface TransferProgressReceiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setTransferProgressReceiver(TransferProgressReceiver transferProgressReceiver) {
        this.transferProgressReceiver = transferProgressReceiver;
    }

    public TransferProgressReceiver getTransferProgressReceiver() {
        return transferProgressReceiver;
    }
}
