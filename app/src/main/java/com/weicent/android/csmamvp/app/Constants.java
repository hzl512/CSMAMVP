package com.weicent.android.csmamvp.app;

/**
 * Created by admin on 2017/3/9.
 * 保存项目中的常量
 */
public class Constants {

    //基础URL
//    public static final String BASE_URL = "http://192.168.1.226:8080/CampusSecondaryMarket/";
    //本地URL
    public static final String BASE_URL = "http://10.0.2.2:8080/CampusSecondaryMarket/";

    //网络连接超时时间
    public static final int NET_OUT_TIME=10000;//10秒

    //网络提示
    public static final String NET_OUT_MSG = "请求失败或超时，请稍后再试";
    public static final String REQUEST_OUT_MSG = "请求失败或超时，请点击重试按钮";
    public static final String NOT_DATA_MSG = "已显示全部内容";
    //共享引用的key
    public static final String SHARED_ID = "id";
    public static final String SHARED_USERNAME = "name";
    public static final String SHARED_PWD = "pwd";
    //Intent中需要的key
    public static final String MODEL_NAME = "model";
    public static final String INTENT_KEY_ID = "id";

    //商品添加URL
    public static final String URL_COMMODITY_ADD_SERVLET="CommodityAddServlet";
    //商品URL
    public static final String URL_COMMODITY_SERVLET="CommodityServlet";
    //用户URL
    public static final String URL_USERS_SERVLET="UsersServlet";
    //求购URL
    public static final String URL_BUYS_SERVLET="BuysServlet";
    //分类URL
    public static final String URL_CATEGORY_SERVLET="CategoryServlet";
    //院系URL
    public static final String URL_DEPARTMENTS_SERVLET="DepartmentsServlet";
    //专业URL
    public static final String URL_PROFESSION_SERVLET="ProfessionServlet";

    //下载更新
    public final static String DOWNLOAD_FILE_APK_NAME = "csma.apk";
    public final static String DOWNLOAD_UPDATE_SERVICE_BAG_NAME="com.weicent.android.csmamvp.services.DownloadUpdateService";
    public final static String
            UPDATE_APP_NAME="appname",  //版本名称
            UPDATE_VERSION="version",    // 版本号
            UPDATE_FORCED_UPDATE="forcedupdate", //是否强制更新
            UPDATE_DESCRIPTION="description",//描述
            UPDATE_URL="update_url",  //新app下载地址
            UPDATE_TIME="update_time", //更新时间
            UPDATE_SIZE="update_size", //apk大小描述
            UPDATE_APKSIZE="update_apksize";//apk大小

}
