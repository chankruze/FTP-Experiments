package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import in.geekofia.ftpfm.models.Item;


public class FTPClientFunctions {

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

    public static boolean ftpListDirs(FTPClient mFTPClient) {
        FTPFile[] ftpDirs = new FTPFile[0];
        String rootPath = new String();

        try {
            ftpDirs = mFTPClient.listDirectories();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ftpDirs.length; i++) {
            rootPath = ftpDirs[0].getName();
            Log.d("CONNECT", "Directories in the ftp server are "
                    + ftpDirs[i].getName());
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

    public static boolean ftpUpload(FTPClient mFTPClient, String srcFilePath, String desFileName, String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            // change working directory to the destination directory
            // if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            // }
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP FILE UPLOAD", "upload failed: " + e);
        }
        return status;
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    public static class ListFTPFiles implements Runnable {
        private List<Item> newDirectories;
        private FTPClient mFTPClient;
        private String mPath;

        public ListFTPFiles(FTPClient client, String path, List<Item> directories) {
            this.mFTPClient = client;
            this.mPath = path;
            this.newDirectories = directories;
            newDirectories.clear();
        }

        @Override
        public void run() {
            try {
                List<Item> files = new ArrayList<Item>();
                FTPFile[] mFiles = mFTPClient.listFiles(mPath);

                for (FTPFile mFile : mFiles) {
                    if (mFile.isDirectory()) {
                        Item mDir = new Item(Item.DIRECTORY, mFile.getName(), mFile.getSize(), mPath + mFile.getName() + "/");
                        newDirectories.add(mDir);
                    } else {
                        String[] regexFileName = mFile.getName().toLowerCase().split("\\.");
                        String mFileExt = "";

                        if (regexFileName.length != 0) {
                            mFileExt = regexFileName[regexFileName.length - 1];
                        }

                        int mFileType;

                        switch (mFileExt) {
                            case "apk":
                            case "aab":
                                mFileType = Item.FILE_APK;
                                break;
                            case "jpg":
                            case "png":
                            case "webp":
                                mFileType = Item.FILE_IMAGE;
                                break;
                            case "mp3":
                                mFileType = Item.FILE_AUDIO;
                                break;
                            case "mp4":
                                mFileType = Item.FILE_VIDEO;
                                break;
                            case "pdf":
                                mFileType = Item.FILE_PDF;
                                break;
                            case "txt":
                            case "doc":
                            case "docx":
                                mFileType = Item.FILE_DOCUMENT;
                                break;
                            case "gif":
                                mFileType = Item.FILE_GIF;
                                break;
                            case "torrent":
                                mFileType = Item.FILE_TORRENT;
                                break;
                            case "zip":
                            case "tar":
                            case "gz":
                            case "rar":
                                mFileType = Item.FILE_ARCHIVE;
                                break;
                            case "exe":
                                mFileType = Item.FILE_EXE;
                                break;
                            case "html":
                                mFileType = Item.FILE_HTML;
                                break;
                            case "css":
                                mFileType = Item.FILE_CSS;
                                break;
                            case "js":
                                mFileType = Item.FILE_JS;
                                break;
                            case "php":
                                mFileType = Item.FILE_PHP;
                                break;
                            case "sh":
                                mFileType = Item.FILE_SHELL;
                                break;
                            case "lua":
                                mFileType = Item.FILE_LUA;
                                break;
                            case "mk":
                                mFileType = Item.FILE_MK;
                                break;
                            case "npm":
                                mFileType = Item.FILE_NPM;
                                break;
                            case "python":
                                mFileType = Item.FILE_PY;
                                break;
                            case "json":
                                mFileType = Item.FILE_JSON;
                                break;
                            case "csr":
                            case "crt":
                            case "cer":
                            case "crl":
                            case "der":
                            case "p7b":
                            case "p7r":
                            case "spc":
                            case "sst":
                            case "stl":
                            case "pfx":
                            case "p12":
                                mFileType = Item.FILE_CERT;
                                break;
                            case "crypt12":
                            case "db":
                            case "sql":
                                mFileType = Item.FILE_DB;
                                break;
                            case "key":
                            case "pgp":
                            case "pub":
                                mFileType = Item.FILE_KEY;
                                break;
                            default:
                                mFileType = Item.FILE;
                        }

                        Item item = new Item(mFileType, mFile.getName(), mFile.getSize(), mPath + mFile.getName() + "/");
//                        System.out.println("#######################");
//                        System.out.println("## Name " + mFile.getName());
//                        System.out.println("## Size " + mFile.getSize());
//                        System.out.println("## RawListing " + mFile.getRawListing());
//                        System.out.println("## Timestamp " + mFile.getTimestamp());
//                        System.out.println("## User " + mFile.getUser());
//                        System.out.println("## Group " + mFile.getGroup());
//                        System.out.println("## Type " + mFile.getType());
//                        System.out.println("#######################");
//                        System.out.println("## File Name : " + item.getName());
//                        System.out.println("## File Size : " + item.getSize());
//                        System.out.println("## File Abs Path : " + item.getAbsolutePath());
//                        System.out.println("#######################");
                        files.add(item);
                    }
                }

                newDirectories.addAll(files);

                System.out.println("## path " + mPath);

                if (!mPath.isEmpty()){
                    String[] splitedPathString = mPath.split("/", 0);
                    List<String> splitedPath = new ArrayList<>(Arrays.asList(splitedPathString));

                    String parentPath = "";

                    List<String> filters = new ArrayList<>();
                    filters.add(parentPath);

                    splitedPath.removeAll(filters);

                    int depth = splitedPath.size();
                    int j = 0;

                    while (j < depth - 1) {
                        parentPath += splitedPath.get(j) + "/";
                        j++;
                    }

                    System.out.println("## Parent Path : " + parentPath);
                    newDirectories.add(0, new Item(Item.UP, mPath, parentPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public List<Item> getNewDirectories() {
            return newDirectories;
        }
    }
}
