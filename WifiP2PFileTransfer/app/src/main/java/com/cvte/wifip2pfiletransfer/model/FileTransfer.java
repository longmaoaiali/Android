package com.cvte.wifip2pfiletransfer.model;

import java.io.Serializable;

/**
 * Created by user on 2020/11/12.
 */

public class FileTransfer implements Serializable {
    private String filePath;
    private long fileLength;
    private String MD5;

    public FileTransfer(String filePath, long fileLength) {
        this.filePath = filePath;
        this.fileLength = fileLength;
    }

    @Override
    public String toString() {
        return "FileTransfer{" +
                "filePath='" + filePath + '\'' +
                ", fileLength=" + fileLength +
                '}';
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }
}
