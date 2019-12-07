package in.geekofia.ftpfm.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.RemoteFile;
import in.geekofia.ftpfm.models.Profile;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class ListFTPFiles implements Runnable {
    private ArrayList<RemoteFile> newDirectories;
    private FTPClient mFTPClient;
    private Profile mProfile;
    private String mPath;

    public ListFTPFiles(FTPClient ftpClient, String path, ArrayList<RemoteFile> directories) {
        this.mFTPClient = ftpClient;
        this.mPath = path;
        this.newDirectories = directories;
        newDirectories.clear();
    }

    public ListFTPFiles(Profile profile, String path, ArrayList<RemoteFile> directories) {
        this.mFTPClient = new FTPClient();
        this.mProfile = profile;
        this.mPath = path;
        this.newDirectories = directories;
        newDirectories.clear();
    }

    @Override
    public void run() {

        mFTPClient.setControlEncoding("UTF-8");

        if (this.mProfile != null){
            boolean status = ftpConnect(mFTPClient, mProfile.getHost() , mProfile.getUser(), mProfile.getPass(), mProfile.getPort());
            if (!status){
                return;
            }
        }
        try {
            ArrayList<RemoteFile> files = new ArrayList<RemoteFile>();
            FTPFile[] mFiles = mFTPClient.listFiles(mPath);

            for (FTPFile mFile : mFiles) {
                if (mFile.isDirectory()) {
                    if (mPath.equals("/")) {
                        RemoteFile mDir = new RemoteFile(RemoteFile.DIRECTORY, mFile.getName(), 0, mFile.getName() + "/");
                        newDirectories.add(mDir);
                    } else {
                        RemoteFile mDir = new RemoteFile(RemoteFile.DIRECTORY, mFile.getName(), 0, mPath + mFile.getName() + "/");
                        newDirectories.add(mDir);
                    }

                } else {
                    String[] regexFileName = mFile.getName().toLowerCase().split("\\.");
                    String mFileExt = "";

                    if (regexFileName.length != 0) {
                        mFileExt = regexFileName[regexFileName.length - 1];
                    }

                    int mIconId, mFileType;

                    switch (mFileExt) {
                        case "apk":
                        case "aab":
                            mIconId = R.drawable.ic_file_apk;
                            mFileType = R.string.file_apk;
                            break;
                        case "jpg":
                        case "png":
                        case "webp":
                        case "svg":
                        case "ico":
                            mIconId = R.drawable.ic_file_image;
                            mFileType = R.string.file_image;
                            break;
                        case "mp3":
                        case "ogg":
                        case "aac":
                            mIconId = R.drawable.ic_file_audio;
                            mFileType = R.string.file_audio;
                            break;
                        case "mp4":
                        case "mkv":
                        case "avi":
                            mIconId = R.drawable.ic_file_video;
                            mFileType = R.string.file_video;
                            break;
                        case "pdf":
                            mIconId = R.drawable.ic_file_pdf;
                            mFileType = R.string.file_pdf;
                            break;
                        case "txt":
                        case "doc":
                        case "docx":
                            mIconId = R.drawable.ic_file_document;
                            mFileType = R.string.file_doucment;
                            break;
                        case "gif":
                            mIconId = R.drawable.ic_file_gif;
                            mFileType = R.string.file_gif;
                            break;
                        case "torrent":
                            mIconId = R.drawable.ic_file_torrent;
                            mFileType = R.string.file_torrent;
                            break;
                        case "zip":
                        case "tar":
                        case "gz":
                        case "rar":
                            mIconId = R.drawable.ic_file_archive;
                            mFileType = R.string.file_archive;
                            break;
                        case "exe":
                            mIconId = R.drawable.ic_file_exe;
                            mFileType = R.string.file_exe;
                            break;
                        case "html":
                            mIconId = R.drawable.ic_file_html;
                            mFileType = R.string.file_html;
                            break;
                        case "css":
                            mIconId = R.drawable.ic_file_css;
                            mFileType = R.string.file_css;
                            break;
                        case "js":
                            mIconId = R.drawable.ic_file_js;
                            mFileType = R.string.file_js;
                            break;
                        case "php":
                            mIconId = R.drawable.ic_file_php;
                            mFileType = R.string.file_php;
                            break;
                        case "sh":
                            mIconId = R.drawable.ic_file_shell;
                            mFileType = R.string.file_shell;
                            break;
                        case "lua":
                            mIconId = R.drawable.ic_file_lua;
                            mFileType = R.string.file_lua;
                            break;
                        case "mk":
                            mIconId = R.drawable.ic_file_makefile;
                            mFileType = R.string.file_mk;
                            break;
                        case "npm":
                            mIconId = R.drawable.ic_file_npm;
                            mFileType = R.string.file_npm;
                            break;
                        case "python":
                            mIconId = R.drawable.ic_file_python;
                            mFileType = R.string.file_py;
                            break;
                        case "json":
                            mIconId = R.drawable.ic_file_json;
                            mFileType = R.string.file_json;
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
                            mIconId = R.drawable.ic_file_certificate;
                            mFileType = R.string.file_cert;
                            break;
                        case "crypt12":
                        case "db":
                        case "sql":
                            mIconId = R.drawable.ic_file_db;
                            mFileType = R.string.file_db;
                            break;
                        case "key":
                        case "pgp":
                        case "pub":
                            mIconId = R.drawable.ic_file_key;
                            mFileType = R.string.file_key;
                            break;
                        default:
                            mIconId = R.drawable.ic_file_generic;
                            mFileType = R.string.file_generic;
                    }

                    RemoteFile remoteFile = new RemoteFile(RemoteFile.FILE, mFile, mIconId, mPath + mFile.getName() + "/", mFileType);
                    files.add(remoteFile);
                }
            }

            newDirectories.addAll(files);

//            System.out.println("## path " + mPath);

            if (!mPath.isEmpty()) {
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

//                System.out.println("## Parent Path : " + parentPath);
                newDirectories.add(0, new RemoteFile(RemoteFile.UP, mPath, parentPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RemoteFile> getNewDirectories() {
        return newDirectories;
    }
}