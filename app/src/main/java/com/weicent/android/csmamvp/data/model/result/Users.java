package com.weicent.android.csmamvp.data.model.result;

import android.os.Parcel;
import android.os.Parcelable;

import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.app.MyApplication;
import com.weicent.android.csmamvp.util.SPUtil;


/**
 * 
 */
public class Users implements Parcelable {
   	
   	public Users() {}
	     
    //
    public Integer id;
	//
    public String usersName;
	//
    public String usersPwd;
	//
    public String usersAddTime;
	//
    public String usersNickName;
	//
    public String usersPhone;
	//
    public String usersQQ;
	//
    public Integer departmentsID;
	//
    public Integer professionID;
	//
    public Integer usersGrade;

    public void setAll(String name, String password, Integer id) {
        SPUtil.put(MyApplication.getContext(),Constants.SHARED_ID,id);
        SPUtil.put(MyApplication.getContext(),Constants.SHARED_USERNAME, name);
        SPUtil.put(MyApplication.getContext(),Constants.SHARED_PWD, password);
    }

    //注销用户信息
    public void setClearAll() {
        SPUtil.remove(MyApplication.getContext(),Constants.SHARED_ID);
        SPUtil.remove(MyApplication.getContext(),Constants.SHARED_USERNAME);
        SPUtil.remove(MyApplication.getContext(),Constants.SHARED_PWD);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", usersName='" + usersName + '\'' +
                ", usersPwd='" + usersPwd + '\'' +
                ", usersAddTime='" + usersAddTime + '\'' +
                ", usersNickName='" + usersNickName + '\'' +
                ", usersPhone='" + usersPhone + '\'' +
                ", usersQQ='" + usersQQ + '\'' +
                ", departmentsID=" + departmentsID +
                ", professionID=" + professionID +
                ", usersGrade=" + usersGrade +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.usersName);
        dest.writeString(this.usersPwd);
        dest.writeString(this.usersAddTime);
        dest.writeString(this.usersNickName);
        dest.writeString(this.usersPhone);
        dest.writeString(this.usersQQ);
        dest.writeValue(this.departmentsID);
        dest.writeValue(this.professionID);
        dest.writeValue(this.usersGrade);
    }

    protected Users(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.usersName = in.readString();
        this.usersPwd = in.readString();
        this.usersAddTime = in.readString();
        this.usersNickName = in.readString();
        this.usersPhone = in.readString();
        this.usersQQ = in.readString();
        this.departmentsID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.professionID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.usersGrade = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel source) {
            return new Users(source);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}