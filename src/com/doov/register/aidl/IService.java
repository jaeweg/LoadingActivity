/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WORKSPACE\\doov_app_branch\\src\\com\\doov\\register\\aidl\\IService.aidl
 */
package com.doov.register.aidl;
public interface IService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.doov.register.aidl.IService
{
private static final java.lang.String DESCRIPTOR = "com.doov.register.aidl.IService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.doov.register.aidl.IService interface,
 * generating a proxy if needed.
 */
public static com.doov.register.aidl.IService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.doov.register.aidl.IService))) {
return ((com.doov.register.aidl.IService)iin);
}
return new com.doov.register.aidl.IService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getUserData:
{
data.enforceInterface(DESCRIPTOR);
com.doov.register.aidl.UserData _result = this.getUserData();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.doov.register.aidl.IService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public com.doov.register.aidl.UserData getUserData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.doov.register.aidl.UserData _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUserData, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.doov.register.aidl.UserData.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getUserData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public com.doov.register.aidl.UserData getUserData() throws android.os.RemoteException;
}
