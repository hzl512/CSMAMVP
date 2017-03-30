package com.weicent.android.csmamvp.data.model.result;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2017/3/23.
 */
public class Update implements Parcelable {

    /**
     * id : 3
     * versionName : 1.0.1
     * versionNumber : 1
     * forcedUpdate : 0
     * description : 1：测试啊
     * 2：你说呢
     * 3：就是爽
     * url : http://192.168.1.226:8080/CampusSecondaryMarket/files/CSMA1.0.1.apk
     * createTime : 2017-03-23 15:02:09
     * size : 2.40M
     */

    public int id;
    public String versionName;
    public String versionNumber;
    public int forcedUpdate;
    public String description;
    public String url;
    public String createTime;
    public String size;
    public long apkSize;

    @Override
    public String toString() {
        return "Update{" +
                "id=" + id +
                ", versionName='" + versionName + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                ", forcedUpdate=" + forcedUpdate +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", createTime='" + createTime + '\'' +
                ", size='" + size + '\'' +
                ", apkSize='" + apkSize + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.versionName);
        dest.writeString(this.versionNumber);
        dest.writeInt(this.forcedUpdate);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.createTime);
        dest.writeString(this.size);
        dest.writeLong(this.apkSize);
    }

    public Update() {
    }

    protected Update(Parcel in) {
        this.id = in.readInt();
        this.versionName = in.readString();
        this.versionNumber = in.readString();
        this.forcedUpdate = in.readInt();
        this.description = in.readString();
        this.url = in.readString();
        this.createTime = in.readString();
        this.size = in.readString();
        this.apkSize = in.readLong();
    }

    public static final Parcelable.Creator<Update> CREATOR = new Parcelable.Creator<Update>() {
        @Override
        public Update createFromParcel(Parcel source) {
            return new Update(source);
        }

        @Override
        public Update[] newArray(int size) {
            return new Update[size];
        }
    };
}
