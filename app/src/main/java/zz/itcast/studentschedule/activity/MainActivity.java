package zz.itcast.studentschedule.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.bean.Version;
import zz.itcast.studentschedule.db.ExcelDao;
import zz.itcast.studentschedule.fragment.FudaoFragment;
import zz.itcast.studentschedule.fragment.JobFragment;
import zz.itcast.studentschedule.fragment.OtherFragment;
import zz.itcast.studentschedule.fragment.TeacherFragment;
import zz.itcast.studentschedule.task.AutoCheckUpdateTask;
import zz.itcast.studentschedule.utils.FileUtil;
import zz.itcast.studentschedule.utils.HttpUtils;
import zz.itcast.studentschedule.utils.LogUtils;
import zz.itcast.studentschedule.utils.MyFinal;
import zz.itcast.studentschedule.utils.MyUtils;
import zz.itcast.studentschedule.utils.ZipUtil;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    /**
     * 功能描述：
     * 1- 下载课表，检查是否有冲突，即，一个一天在二个教室上课的情况
     * 2- 统计每个讲师本月的上课天数
     * 3- 显示某一天，所有班级的上课情况
     * 4- 显示某个人的休息和排表情况
     * 5- 列出最近几天每班的技术辅导的上班情况，
     * 6- 手工指定每位技术辅导，辅导的班级，并记录下来
     */

    // 底部导航
    private RadioGroup radioGroup;
    // 检查更新的按钮
    private Button btnCheckState;

//    private TextView tvState;
    private android.content.Context ctx;

    private TeacherFragment teacherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearOldData();

        setContentView(R.layout.activity_main);
        ctx = this;
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnCheckState = (Button) findViewById(R.id.btn_check_state);
        btnCheckState.setOnClickListener(this);
        btnCheckState.setText("检查更新");
        btnCheckState.setTextColor(Color.BLACK);
        radioGroup.setOnCheckedChangeListener(this);

        radioGroup.check(R.id.btn_teacher);


        if(isHaveNet()){
            // 后台检查更新课表
            new AutoCheckUpdateTask().execute(this);
        }
    }

    /**
     * 当前版本第一次运行时，清除以前的数据
     */
    private void clearOldData() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            // 当前版本第一次运行，清除以前的数据
            String clean_key = "clear_" + packageInfo.versionCode;
            boolean isRunning = sp.getBoolean(clean_key, false);
            if(!isRunning){
                clearAllData();
                sp.edit().putBoolean(clean_key,true).commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除数据
     */
    private void clearAllData() {
        if(eDao!=null){
            eDao.clearData();
        }
        // 删除文件
        File file = new File(MyFinal.workDir);
        FileUtil.deleteAllFile(file);
    }

    private void showUpdateDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
        adb.setTitle("发现新版本是否更新?");
        String msg = "";
        if(!TextUtils.isEmpty(version.desc)){
            msg = version.desc;
        }
        adb.setMessage(msg);

        adb.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                HttpUtils.getInstance().downloadFile(MyFinal.apkUrl, MyFinal.apkFilePath, new HttpUtils.ResultCallback<File>() {
//                    @Override
//                    public void onResponse(File apkFile) {
//
//                        Intent intent = new Intent("android.intent.action.VIEW");
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
//
//                        startActivity(intent);
//                    }
//                });

                // 开启浏览器下载APK
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(MyFinal.apkUrl));
                startActivity(intent);

                version.isHaveNewApk = false;

            }
        });
        adb.setPositiveButton("取消",null);
        adb.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (checkedId) {
            case R.id.btn_teacher:
                teacherFragment = new TeacherFragment();
                transaction.replace(R.id.ll_content, teacherFragment);
                break;

            case R.id.btn_fudao:
                transaction.replace(R.id.ll_content,  new FudaoFragment());
                break;

            case R.id.btn_job:
                transaction.replace(R.id.ll_content,  new JobFragment());
                break;

            case R.id.btn_other:
                transaction.replace(R.id.ll_content,  new OtherFragment());
                break;
        }

        transaction.commit();
    }

    @Override
    public void onClick(View v) {

        eDao = ExcelDao.getInstance(this);

        // 解压至同级目录
//        try {
//            String filePath = "mnt/shared/Other/yeshen/android.zip";
//            List<String> unZipNameList = ZipUtil.unzip(filePath, null);
//            LogUtils.logleo("解压完成");
//            // 解析
//            parseXmlToDb(unZipNameList);
//
//            // 更新页面
//            teacherFragment.flushView();
//            hideProgress();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (!isHaveNet()) {
            showToast("当前没有网络，请检查后重试!");
            return;
        }

        if (version.isHaveNewApk) {
            showUpdateDialog();
            return;
        }
        if (version.isHaveNewKeBiao) {
            updateKebiao();
            return;
        }
        // 联网检查
        new AutoCheckUpdateTask().execute(this);
        showProgressMessage("正在检查更新...");
    }

    private void updateKebiao() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 下载
                // 联网检查更新
                HttpUtils httpUtils = HttpUtils.getInstance();
                String destFileDir = MyFinal.kebiaoFilePath;

                showProgressMessage("正在下载文件");

                File workFile = new File(MyFinal.workDir);
                if(!workFile.exists()){
                    workFile.mkdirs();
                }
                // 下载文件
                httpUtils.downloadFile(MyFinal.kebiaoUrl, destFileDir, new HttpUtils.ResultCallback<File>() {
                    @Override
                    public void onResponse(File file) {
                        System.out.println("file::"+file.getAbsolutePath());
                        LogUtils.logleo("下载完成");
                        showProgressMessage("下载完成,正在解压文件");
                        try {
                            // 解压至同级目录
                            List<String> unZipNameList = ZipUtil.unzip(file.getAbsolutePath(), null);

                            LogUtils.logleo("解压完成");

                        // 解析
                        parseXmlToDb(unZipNameList);

                         // 更新本地存储版本
                         sp.edit().putInt(MyFinal.key_data_version,version.data_version).commit();
                         version.isHaveNewKeBiao = false;

                        // 更新页面
                        handler.sendEmptyMessage(UPDATE_FINISH);

                        } catch (IOException e) {
                            // 如 SD 卡不存在
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        hideProgress();
                    }
                });


            }
        }.start();
        ;
    }
    /**
     * 更新完成
     */
    private final int UPDATE_FINISH = 112;

    /**
     * 版本信息
     */
    private Version version;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_FINISH:
                    teacherFragment.flushView();
                    hideProgress();
                    // 保存版本号与服务器一致
                    sp.edit().putInt(MyFinal.key_data_version,version.data_version).commit();
                    //
                    btnCheckState.setText("检查更新");
                    btnCheckState.setTextColor(0xff000000);
                    break;
            }
        }
    };


    public void versionResult(Version version){
        this.version = version;
        hideProgress();

        if(version.isHaveNewKeBiao || version.isHaveNewApk){
            btnCheckState.setText("课表有更新");
            btnCheckState.setTextColor(Color.RED);
            showToast("发现新版本，请更新!");
        }else{
            btnCheckState.setText("检查更新");
            btnCheckState.setTextColor(Color.BLACK);
            showToast("当前已经是最新版本!");
        }

    }

    /**
     * 参数为 解压的文件名称
     * @param unZipNameList
     */
    private void parseXmlToDb(List<String> unZipNameList) {
        try {
            // android 课表所在目录
//            String androidPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me/android";
//
//            File myFolder = new File(androidPath);
//            if (!myFolder.exists()) {
//                LogUtils.logleo("android 课表所在目录,不存在,新创建");
//                FileUtil.buildFile(myFolder.getAbsolutePath(),true);
//            }
//            File dir = new File(androidPath);

            List<File> xlsFiles  = new ArrayList<File>(); // 获得需要处理的xls 文件列表

            // 偏历解析指定目录中的所有文件
//            for (File file : myFolder.listFiles()) {
//                String fileName = file.getName(); // 文件名本身是 BGK 编码 ，但该方法 返回的是UTF-8 的字符
//                if ( fileName.endsWith(".xls")) {
//                    // 根据文件名称判断，符合条件，
//                    xlsFiles.add(file);
//                }
//            }

            // 解析当前下载解压的文件

            for(String name : unZipNameList){
                File xlsFile = new File(name);
                xlsFiles.add(xlsFile);
            }

            // 解析XLS 文件
            for(int i=0;i<xlsFiles.size();i++){
                showProgressMessage("正在解析文件,共"+xlsFiles.size()+"个，当前解析第:"+(i+1)+"个");
                File file = xlsFiles.get(i);
                String fileName = file.getName();

                // 拼凑班级名称
                String grade = getGradeName(fileName);
                System.out.println("班级名称 :: grade:" + grade + " : " + fileName);

                FileInputStream fin = new FileInputStream(file);
                // xml文件中的数据
                List<SsBean> beanList = MyUtils.readExcel(fin);
                // TODO  此处效率应该改进。
                // 获得数据库中当前班级的数据，如果没有，说明是新班级，全部插入数据，
                // 如果有内容，说明班级有改动，应将需要改动的，更新至数据库，但比较麻烦，
                // 如果有内容，删除原有内容，添加新内容
                List<SsBean> dbBeanList = eDao.getClassByGrade(grade);
                if(dbBeanList.size() > 0){
                    int num = eDao.deleteGrade(grade);
                    System.out.println("删除以前的班级：Num: "+num);
                }

                for (SsBean bean : beanList) {
                    bean.grade = grade;
                    eDao.addKebiao(bean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据XLS 文件的名称，分析 班级名称
     * @param fileName
     * @return
     */
    private String getGradeName(String fileName) {
        // 班级名称
        String grade = "";

        /**
         *   android 课表
         *   郑州黑马Android就业22期（20161228面授）课表.xls
         *   郑州传智Android基础20期（20160718面授）课表.xls
         */
        fileName = fileName.trim();

        if(fileName.contains("Android就业") ){
            int indexDi = fileName.indexOf("Android就业");
            int indexQi = fileName.indexOf("期");
            // 拼凑班级名称
            grade = "android_就业_" + fileName.substring(indexDi + 9, indexQi).trim();

        }else
        if( fileName.contains("Android基础")){
            int indexDi = fileName.indexOf("Android基础");
            int indexQi = fileName.indexOf("期");
            // 拼凑班级名称
             grade = "android_基础_" + fileName.substring(indexDi + 9, indexQi).trim();
        }else

        /**
         * JavaEE 课表
         *  郑州黑马JavaEE基础1期（20160715双元）.xls
         *  郑州黑马JavaEE就业1期（20160901双元）.xls
         */
        if(fileName.contains("JavaEE基础") ){
            int start = fileName.indexOf("JavaEE");
            int end = fileName.indexOf("期");
            grade = "javaEE_基础_" + fileName.substring(start + 8, end).trim();
        } else

        if( fileName.contains("JavaEE就业")){
            int start = fileName.indexOf("JavaEE");
            int end = fileName.indexOf("期");
            grade = "javaEE_就业_" + fileName.substring(start + 8, end).trim();
        }

        return grade;
    };


}
