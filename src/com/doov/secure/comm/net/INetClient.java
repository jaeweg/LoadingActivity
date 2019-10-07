package com.doov.secure.comm.net;

public interface INetClient {
	void onSuccess(String url, Object obj);

	void onFailure(String url, int errorCode);
}
