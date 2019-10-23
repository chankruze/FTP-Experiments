package in.geekofia.ftpfm.utils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class MyInputStream implements Runnable{
    InputStream inputStream = null;
    FTPClient mFTPClient;
    String mFilePath;

    public MyInputStream(FTPClient ftpClient, String remoteFilePath) {
        this.mFTPClient = ftpClient;
        this.mFilePath = remoteFilePath;
    }

    @Override
    public void run() {
        try {
            inputStream = mFTPClient.retrieveFileStream(mFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
