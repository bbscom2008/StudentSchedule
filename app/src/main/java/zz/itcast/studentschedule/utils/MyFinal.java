package zz.itcast.studentschedule.utils;

import android.os.Environment;
import android.text.format.Time;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import zz.itcast.studentschedule.bean.SsBean;

public class MyFinal {

    /**
     * SD 卡径
     */
    public static String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();


    /**
     * 工作目录
     */
    public static String workDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me";


    /**
     * 课表文件的下载地址 ( 固定不变的)
     */
    public static String versionUrl = "http://raw.githubusercontent.com/bbscom2008/zzitcast/master/version.json";


    /**
     * 课表文件的下载地址 ( 固定不变的)
     */
    public static String kebiaoUrl = "http://raw.githubusercontent.com/bbscom2008/zzitcast/master/android.zip";

    /**
     * 课表文件的本地地址( 固定不变的)
     */
    public static String kebiaoFilePath = workDir+"/android.zip";


    /**
     * APK下载地址 ( 固定不变的)
     */
    public static String apkUrl = "http://raw.githubusercontent.com/bbscom2008/zzitcast/master/apk/app_kebiao.apk";

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
    public static String key_auto_update = "key_auto_update";
}
