package in.geekofia.ftpfm.utils;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPClientFunctions {

    public static boolean ftpConnect(FTPClient mFTPClient, String host, String username, String password, int port) {
        try {
            mFTPClient.connect(host, port);
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(username, password);
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
}
