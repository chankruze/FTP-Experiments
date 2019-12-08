/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 7:51 PM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.models;

public class Transfer {
    private String fileName;
    private int progress, drawableId;

    public Transfer(String fileName, int drawableId, int progress) {
        this.fileName = fileName;
        this.drawableId = drawableId;
        this.progress = progress;
    }

    public String getFileName() {
        return fileName;
    }

    public int getProgress() {
        return progress;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
