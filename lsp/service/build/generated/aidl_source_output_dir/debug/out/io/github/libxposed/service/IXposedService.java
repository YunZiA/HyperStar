/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: C:\Users\25748\AppData\Local\Android\Sdk\build-tools\35.0.0\aidl.exe -pC:\Users\25748\AppData\Local\Android\Sdk\platforms\android-34\framework.aidl -oC:\Users\25748\StudioProjects\HyperStar_it\lsp\service\build\generated\aidl_source_output_dir\debug\out -IC:\Users\25748\StudioProjects\HyperStar_it\lsp\service\service\interface\src\main\aidl -IC:\Users\25748\StudioProjects\HyperStar_it\lsp\service\src\debug\aidl -dC:\Users\25748\AppData\Local\Temp\aidl15579188553519370674.d C:\Users\25748\StudioProjects\HyperStar_it\lsp\service\service\interface\src\main\aidl\io\github\libxposed\service\IXposedService.aidl
 */
package io.github.libxposed.service;
public interface IXposedService extends android.os.IInterface
{
  /** Default implementation for IXposedService. */
  public static class Default implements io.github.libxposed.service.IXposedService
  {
    // framework details
    @Override public int getAPIVersion() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public java.lang.String getFrameworkName() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getFrameworkVersion() throws android.os.RemoteException
    {
      return null;
    }
    @Override public long getFrameworkVersionCode() throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public int getFrameworkPrivilege() throws android.os.RemoteException
    {
      return 0;
    }
    // scope utilities
    @Override public java.util.List<java.lang.String> getScope() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void requestScope(java.lang.String packageName, io.github.libxposed.service.IXposedScopeCallback callback) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String removeScope(java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    // remote preference utilities
    @Override public android.os.Bundle requestRemotePreferences(java.lang.String group) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void updateRemotePreferences(java.lang.String group, android.os.Bundle diff) throws android.os.RemoteException
    {
    }
    @Override public void deleteRemotePreferences(java.lang.String group) throws android.os.RemoteException
    {
    }
    // remote file utilities
    @Override public java.lang.String[] listRemoteFiles() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.ParcelFileDescriptor openRemoteFile(java.lang.String name) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean deleteRemoteFile(java.lang.String name) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements io.github.libxposed.service.IXposedService
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an io.github.libxposed.service.IXposedService interface,
     * generating a proxy if needed.
     */
    public static io.github.libxposed.service.IXposedService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof io.github.libxposed.service.IXposedService))) {
        return ((io.github.libxposed.service.IXposedService)iin);
      }
      return new io.github.libxposed.service.IXposedService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_getAPIVersion:
        {
          int _result = this.getAPIVersion();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getFrameworkName:
        {
          java.lang.String _result = this.getFrameworkName();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getFrameworkVersion:
        {
          java.lang.String _result = this.getFrameworkVersion();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getFrameworkVersionCode:
        {
          long _result = this.getFrameworkVersionCode();
          reply.writeNoException();
          reply.writeLong(_result);
          break;
        }
        case TRANSACTION_getFrameworkPrivilege:
        {
          int _result = this.getFrameworkPrivilege();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getScope:
        {
          java.util.List<java.lang.String> _result = this.getScope();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_requestScope:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          io.github.libxposed.service.IXposedScopeCallback _arg1;
          _arg1 = io.github.libxposed.service.IXposedScopeCallback.Stub.asInterface(data.readStrongBinder());
          this.requestScope(_arg0, _arg1);
          break;
        }
        case TRANSACTION_removeScope:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.removeScope(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_requestRemotePreferences:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.Bundle _result = this.requestRemotePreferences(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_updateRemotePreferences:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.Bundle _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.os.Bundle.CREATOR);
          this.updateRemotePreferences(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteRemotePreferences:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteRemotePreferences(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_listRemoteFiles:
        {
          java.lang.String[] _result = this.listRemoteFiles();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_openRemoteFile:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.ParcelFileDescriptor _result = this.openRemoteFile(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_deleteRemoteFile:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.deleteRemoteFile(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements io.github.libxposed.service.IXposedService
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
      // framework details
      @Override public int getAPIVersion() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAPIVersion, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getFrameworkName() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFrameworkName, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getFrameworkVersion() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFrameworkVersion, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getFrameworkVersionCode() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFrameworkVersionCode, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getFrameworkPrivilege() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFrameworkPrivilege, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // scope utilities
      @Override public java.util.List<java.lang.String> getScope() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getScope, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void requestScope(java.lang.String packageName, io.github.libxposed.service.IXposedScopeCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeStrongInterface(callback);
          boolean _status = mRemote.transact(Stub.TRANSACTION_requestScope, _data, null, android.os.IBinder.FLAG_ONEWAY);
        }
        finally {
          _data.recycle();
        }
      }
      @Override public java.lang.String removeScope(java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_removeScope, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // remote preference utilities
      @Override public android.os.Bundle requestRemotePreferences(java.lang.String group) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.Bundle _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(group);
          boolean _status = mRemote.transact(Stub.TRANSACTION_requestRemotePreferences, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, android.os.Bundle.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void updateRemotePreferences(java.lang.String group, android.os.Bundle diff) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(group);
          _Parcel.writeTypedObject(_data, diff, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_updateRemotePreferences, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteRemotePreferences(java.lang.String group) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(group);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteRemotePreferences, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // remote file utilities
      @Override public java.lang.String[] listRemoteFiles() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_listRemoteFiles, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.ParcelFileDescriptor openRemoteFile(java.lang.String name) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.ParcelFileDescriptor _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          boolean _status = mRemote.transact(Stub.TRANSACTION_openRemoteFile, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, android.os.ParcelFileDescriptor.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean deleteRemoteFile(java.lang.String name) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteRemoteFile, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_getAPIVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getFrameworkName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getFrameworkVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getFrameworkVersionCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getFrameworkPrivilege = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getScope = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_requestScope = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_removeScope = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_requestRemotePreferences = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_updateRemotePreferences = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_deleteRemotePreferences = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_listRemoteFiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_openRemoteFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_deleteRemoteFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "io.github.libxposed.service.IXposedService";
  public static final int API = 100;
  public static final int FRAMEWORK_PRIVILEGE_ROOT = 0;
  public static final int FRAMEWORK_PRIVILEGE_CONTAINER = 1;
  public static final int FRAMEWORK_PRIVILEGE_APP = 2;
  public static final int FRAMEWORK_PRIVILEGE_EMBEDDED = 3;
  public static final String AUTHORITY_SUFFIX = ".XposedService";
  public static final String SEND_BINDER = "SendBinder";
  // framework details
  public int getAPIVersion() throws android.os.RemoteException;
  public java.lang.String getFrameworkName() throws android.os.RemoteException;
  public java.lang.String getFrameworkVersion() throws android.os.RemoteException;
  public long getFrameworkVersionCode() throws android.os.RemoteException;
  public int getFrameworkPrivilege() throws android.os.RemoteException;
  // scope utilities
  public java.util.List<java.lang.String> getScope() throws android.os.RemoteException;
  public void requestScope(java.lang.String packageName, io.github.libxposed.service.IXposedScopeCallback callback) throws android.os.RemoteException;
  public java.lang.String removeScope(java.lang.String packageName) throws android.os.RemoteException;
  // remote preference utilities
  public android.os.Bundle requestRemotePreferences(java.lang.String group) throws android.os.RemoteException;
  public void updateRemotePreferences(java.lang.String group, android.os.Bundle diff) throws android.os.RemoteException;
  public void deleteRemotePreferences(java.lang.String group) throws android.os.RemoteException;
  // remote file utilities
  public java.lang.String[] listRemoteFiles() throws android.os.RemoteException;
  public android.os.ParcelFileDescriptor openRemoteFile(java.lang.String name) throws android.os.RemoteException;
  public boolean deleteRemoteFile(java.lang.String name) throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
