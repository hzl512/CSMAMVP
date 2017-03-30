package com.weicent.android.csmamvp.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ab.util.AbDateUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.app.MyApplication;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.result.model.ResUpdate;
import com.weicent.android.csmamvp.ui.other.UpdateDialogActivity;
import com.weicent.android.csmamvp.util.SPUtil;

import org.apache.http.Header;

import java.io.File;

/**
 * 下载更新服务
 */
public class DownloadUpdateService extends Service {

    private static final String TAG = "DownloadUpdateService";

    //下载配置
    private DownloadManager mDownload;//下载管理
    private DownloadCompleteReceiver mReceiver;
    private boolean mIsDown = false;//是否在进行下载
    private long mDownId = -1;
    public Handler mHandler;
    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public DownloadUpdateService getService() {
            return DownloadUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    //定义接口
    public interface ICallBack {
        void onUpdate(boolean b, String msg);
    }

    ICallBack icallback = null;

    public void setOnCheckUpdateListener(ICallBack back) {
        icallback = back;
    }

    //检查更新
    public synchronized void Check() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsDown) {//判断是否在下载,如在下载就不检测更新
                    if (getUpdate()) {
                        checkUpdate();
                    } else {
                        if (icallback != null) icallback.onUpdate(false, "获取更新信息失败");
                    }
                } else {
                    if (icallback != null) icallback.onUpdate(false, "正在下载更新");
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        //下载管理设置
        mDownload = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mReceiver = new DownloadCompleteReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //代替了以前的onstart方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra("type", 0);
            switch (type) {
                case 1://开启app第一时间查看是否需要更新
                {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            getUpdate();
                            checkUpdate();
                        }
                    };
                    mainHandler.post(myRunnable);
                }
                break;
                case 3://下载管理
                    //先判断保存的文件中有已经存的更新文件，就不用重新下载
                    boolean isReDown = true;//是否重新下载
                    try {
                        AbLogUtil.d(TAG, "UPDATE_VERSION:" + SPUtil.get(this, Constants.UPDATE_VERSION, "-2").toString()+ " getVersionCode:" +getVersionCode());

                        if (Integer.parseInt(SPUtil.get(this, Constants.UPDATE_VERSION, "-2").toString())  > getVersionCode()) {  //记录的版本号，大于当前版本号 ,查看是否已经存在安装包
                            File file = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            if (file.isDirectory()) {
                                File[] childFiles = file.listFiles();
                                for (int i = 0; i < childFiles.length; i++) {  //遍历子文件
                                    long updateTime = AbDateUtil.getDateByFormat(String.valueOf(SPUtil.get(this, Constants.UPDATE_TIME, "1970-0-0 00:00:00")), AbDateUtil.dateFormatYMDHMS).getTime();
                                    AbLogUtil.d(TAG, "filename:" + childFiles[i].getName() + " lastModified:" + childFiles[i].lastModified() + " updatetime:" + updateTime);
                                    AbLogUtil.d(TAG, "length:" + childFiles[i].length() + "UPDATE_apksize:" + String.valueOf(SPUtil.get(this, Constants.UPDATE_APKSIZE, (long)0)));
                                    //如果存在文件名，并且最后修改时间，大于最新版本更新时间,文件大小等于成功下载后的文件大小
                                    if (childFiles[i].getName().equals(Constants.DOWNLOAD_FILE_APK_NAME) &&
                                            childFiles[i].lastModified() >= updateTime && childFiles[i].length() > 1000 &&
                                            childFiles[i].length()==(long)SPUtil.get(this, Constants.UPDATE_APKSIZE, (long)0)) {
                                        isReDown = false;
                                        AbLogUtil.d(TAG, "启动安装1...");
                                        //更新安装软件
//                                        Intent it = new Intent(Intent.ACTION_VIEW);
//                                        it.setDataAndType(Uri.fromFile(childFiles[i]), "application/vnd.android.package-archive");
//                                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(it);
                                        installAPK(DownloadUpdateService.this,getRealFilePath(DownloadUpdateService.this,Uri.fromFile(childFiles[i])));
                                        break;
                                    }
                                }
                            } else {
                                AbLogUtil.d(TAG, TAG + "没有子文件");
                            }
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        AbLogUtil.d(TAG,e.toString());
                    }
                    //判断是否重新下载
                    if (isReDown) {
                        if (!mIsDown) {
                            try {
                                //下载前，清空指定文件夹
                                File file = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                                AbFileUtil.deleteFile(file);
                                //创建下载请求
                                DownloadManager.Request down = new DownloadManager.Request(Uri.parse(String.valueOf(SPUtil.get(this, Constants.UPDATE_URL, ""))));
                                //设置允许使用的网络类型，这里是移动网络和wifi都可以
                                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                //禁止发出通知，既后台下载
//			                down.setShowRunningNotification(false);
                                down.setTitle(getString(R.string.app_name) + SPUtil.get(this, Constants.UPDATE_APP_NAME, ""));
                                down.setDescription("正在下载" + getString(R.string.app_name) + "最新版本");
                                down.setNotificationVisibility(View.VISIBLE);
                                //不显示下载界面
                                down.setVisibleInDownloadsUi(false);
                                //设置下载后文件存放的位置  (指定存储文件的路径是应用在外部存储中的专用文件夹)
                                down.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, Constants.DOWNLOAD_FILE_APK_NAME);
                                //将下载请求放入队列
                                mDownId = mDownload.enqueue(down);
                                mIsDown = true;
                            } catch (Exception e) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "下载失败(可能下载路径错误)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "已在下载", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    break;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    //接受下载完成后的intent ,更新软件
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                AbLogUtil.d(TAG, " download complete! id : " + reference);
                if (mDownId == reference) {
                    mIsDown = false;
                    try {
                        int statue = DownloadManager.STATUS_FAILED;//下载状态,默认下载完成
                        int reason = -1;//失败原因
                        String fileName = "";
                        long fileByte=0;
                        //下载完成后，通过查询，得出是否下载成功或。失败，并记录文件大小
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(reference);
                        Cursor myCursor = mDownload.query(query);
                        if (myCursor.moveToFirst()) {
                            int fileNameIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                            int fileUriIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            int fileTotalSizeIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                            int fileByteIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                            int statueIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int reasonIdx = myCursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                            String fileUri = myCursor.getString(fileUriIdx);
                            long fileTotalSize = myCursor.getLong(fileTotalSizeIdx);//总大小
                            fileByte = myCursor.getLong(fileByteIdx);//文件大小
                            statue = myCursor.getInt(statueIdx);//状态
                            reason = myCursor.getInt(reasonIdx);//原因
                            AbLogUtil.d(TAG, "fileNameIdx2:" +fileNameIdx);
                            fileName = myCursor.getString(fileNameIdx);
                            AbLogUtil.d(TAG, "fileName:" + fileName + " fileUri:" + fileUri + " fileTotalSize:" + fileTotalSize + " fileByte:" + fileByte);
                            if (fileName.endsWith(Constants.DOWNLOAD_FILE_APK_NAME)) {
                                //保存文件总大小
                                SPUtil.put(MyApplication.getContext(), Constants.UPDATE_APKSIZE, fileTotalSize);
                            }
                        }
                        myCursor.close();

                        if (fileName.endsWith(Constants.DOWNLOAD_FILE_APK_NAME)&&fileByte>0) {
                            if (statue == DownloadManager.STATUS_SUCCESSFUL) {//当下载成功完成
                                installAPK(context,getRealFilePath(context,mDownload.getUriForDownloadedFile(mDownId)));
                                AbLogUtil.d(TAG, "启动安装2..."+fileByte+ "getRealFilePath "+getRealFilePath(context,mDownload.getUriForDownloadedFile(mDownId)));
                                //更新安装软件
//                                Intent it = new Intent(Intent.ACTION_VIEW);
//                                it.setDataAndType(uri, "application/vnd.android.package-archive");
//                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(it);
                            } else {
                                //下载失败，删除文件
                                mDownload.remove(mDownId);
                                switch (reason) {
                                    case DownloadManager.ERROR_CANNOT_RESUME:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "一些可能的瞬态错误但我们不能恢复下载。", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "没有外部存储装置", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "目标文件已经存在", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case DownloadManager.ERROR_FILE_ERROR:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "存储空间不足", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "存储空间不足", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    default:
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                }
                            }
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
//                Toast.makeText(context, intent.getAction()+"id : "+downId, Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void installAPK(Context context,String path) {
        File file = new File(path);
        if(file.exists()){
            openFile(file,context);
        }else{
            Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     *重点在这里
     */
    private void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext().getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        }else{
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

//    public static boolean deleteFileWithPath(String filePath) {
//        SecurityManager checker = new SecurityManager();
//        File f = new File(filePath);
//        checker.checkDelete(filePath);
//        if (f.isFile()) {
//            f.delete();
//            return true;
//        }
//        return false;
//    }

    private boolean mCheck = false;

    private boolean getUpdate() {
        NetWorkWeb.getInstance().doRequest("UpdateServlet", new ResultHandlerForJson<ResUpdate>(ResUpdate.class) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, ResUpdate resultJson) {
                if (resultJson.errorcode == 0) {
                    if (resultJson.data != null) {
                        NetWorkWeb.getInstance().doLogResultModelString(TAG, resultJson.data);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_APP_NAME, resultJson.data.versionName);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_VERSION, resultJson.data.versionNumber);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_FORCED_UPDATE, resultJson.data.forcedUpdate);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_DESCRIPTION, resultJson.data.description);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_URL, resultJson.data.url);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_TIME, resultJson.data.createTime);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_SIZE, resultJson.data.size);
                        SPUtil.put(DownloadUpdateService.this, Constants.UPDATE_APKSIZE, resultJson.data.apkSize);
                        mCheck = true;
                    } else {
                        if (icallback != null) icallback.onUpdate(false, "获取更新信息失败1");
                        mCheck = false;
                    }
                } else {
                    if (icallback != null) icallback.onUpdate(false, "获取更新信息失败2");
                    mCheck = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                if (icallback != null) icallback.onUpdate(false, "获取更新信息失败3");
                mCheck = false;
            }
        }, "", 5);
        NetWorkWeb.getInstance().doLogResultModelString(TAG, " " + mCheck);
        return mCheck;
    }

    public boolean checkUpdate() {
        try {
            if (Integer.parseInt(SPUtil.get(this, Constants.UPDATE_VERSION, "0").toString()) > getVersionCode()) {
                //弹出对话框，确认更新
                Intent it = new Intent(this, UpdateDialogActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                if (icallback != null) icallback.onUpdate(true, "获取更新信息成功");
                return true;
            } else {
                if (icallback != null) icallback.onUpdate(false, "当前已是最新版本，无需更新");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (icallback != null) icallback.onUpdate(false, "获取更新信息失败4");
            return false;
        }
    }

    //获取版本号
    public int getVersionCode() {
        int code = -1;
        try {
            code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) unregisterReceiver(mReceiver);
    }

}