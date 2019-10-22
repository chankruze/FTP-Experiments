package in.geekofia.ftpfm.models;

import in.geekofia.ftpfm.R;

public class Item {

    public static final int DIRECTORY = 1;
    public static final int FILE = 2;
    public static final int UP = 3;
    public static final int FILE_APK = 200;
    public static final int FILE_IMAGE = 201;
    public static final int FILE_AUDIO = 202;
    public static final int FILE_VIDEO = 203;
    public static final int FILE_PDF = 204;
    public static final int FILE_DOCUMENT = 205;
    public static final int FILE_EXE = 206;
    public static final int FILE_SHELL = 207;
    public static final int FILE_GIF = 208;
    public static final int FILE_DB = 209;
    public static final int FILE_KEY = 210;
    public static final int FILE_TORRENT = 211;
    public static final int FILE_ARCHIVE = 212;
    public static final int FILE_HTML = 213;
    public static final int FILE_CSS = 214;
    public static final int FILE_JS = 215;
    public static final int FILE_PHP = 216;
    public static final int FILE_PY = 217;
    public static final int FILE_LUA = 218;
    public static final int FILE_MK = 219;
    public static final int FILE_NPM = 220;
    public static final int FILE_CERT = 221;
    public static final int FILE_JSON = 222;

    private int typeItem, numItems, iconId, typeId;
    private String name, date, time, absolutePath;
    private long size;

    // Constructors //

    // for directory
    public Item(int typeItem, String name, int numItems, String absolutePath){
        this.iconId = R.drawable.ic_folder;
        this.typeItem = typeItem;
        this.name = name;
        this.numItems = numItems;
        this.absolutePath = absolutePath;
    }

    // for file
    public Item(int iconId, int typeItem, String name, long size, String date, String time, String absolutePath, int typeId){
        this.iconId = iconId;
        this.typeItem = typeItem;
        this.name = name;
        this.size = size;
        this.date = date;
        this.time = time;
        this.absolutePath = absolutePath;
        this.typeId = typeId;
    }

    // for UP link
    public Item(int typeItem, String display, String parentPath){
        this.iconId = R.drawable.ic_back;
        this.typeItem = typeItem;
        this.name = display;
        this.absolutePath = parentPath;
    }

    // Getters //
    public int getIconId(){
        return iconId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getSize() {
        return size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public int getNumItems() {
        return numItems;
    }

    public int getTypeItem() {
        return typeItem;
    }

    public int getTypeId() {
        return typeId;
    }

    // setters //

    public void setTypeItem(int typeItem) {
        this.typeItem = typeItem;
    }
}
