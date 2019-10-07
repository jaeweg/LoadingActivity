package com.doov.register.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
	private String username;
	private String mobile;
	private String id;

	public UserData() {
	}

	public UserData(Parcel source) {
		username = source.readString();
		mobile = source.readString();
		id = source.readString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(username);
		dest.writeString(mobile);
		dest.writeString(id);
	}

	public static final Parcelable.Creator<UserData> CREATOR = new Creator<UserData>() {

		@Override
		public UserData[] newArray(int size) {
			return new UserData[size];
		}

		@Override
		public UserData createFromParcel(Parcel source) {
			return new UserData(source);
		}
	};

}
