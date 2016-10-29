package zz.itcast.studentschedule.utils;

import android.os.Environment;

public class MyFinal {

    /**
     * SD 卡径
     */
    public static String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static String dbName = "school_time_table.db";

    /**
     * 工作目录
     */
    public static String workDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me";


    /**
     * 课表文件的下载地址 ( 固定不变的)
     */
    public static String versionUrl = "https://raw.githubusercontent.com/bbscom2008/zzitcast/master/version.json";


    /**
     * 课表文件的下载地址 ( 固定不变的)
     *                               https://raw.githubusercontent.com/bbscom2008/zzitcast/master/android.zip
     */
    public static String kebiaoUrl = "https://raw.githubusercontent.com/bbscom2008/zzitcast/master/android.zip";

    /**
     * 课表文件的本地地址( 固定不变的)
     */
    public static String kebiaoFilePath = workDir+"/android.zip";


    /**
     * APK下载地址 ( 固定不变的)
     */
    public static String apkUrl = "https://raw.githubusercontent.com/bbscom2008/zzitcast/master/apk/app_kebiao.apk";

    /**
     * APK文件本地地址 ( 固定不变的)
     */
    public static String apkFilePath = workDir+"/app_kebiao.apk";


    /**
     * SP 文件的名称
     */
    public static String sp_name = "config";
    /**
     * SP 文件 数据的版本
     */
    public static String key_data_version = "key_data_version";

    /**
     * SP 文件 中自动更新的KEY
     */
//    public static String key_auto_update = "key_auto_update";
}
