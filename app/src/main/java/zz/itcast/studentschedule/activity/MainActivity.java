package zz.itcast.studentschedule.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.bean.Version;
import zz.itcast.studentschedule.fragment.FudaoFragment;
import zz.itcast.studentschedule.fragment.JobFragment;
import zz.itcast.studentschedule.fragment.OtherFragment;
import zz.itcast.studentschedule.fragment.TeacherFragment;
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

    private RadioGroup radioGroup;

    private Button btnCheckState;

    private TextView tvState;
    private android.content.Context ctx;

    private TeacherFragment teacherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;

        // 注释中的字体怎么
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        btnCheckState = (Button) findViewById(R.id.btn_check_state);
        btnCheckState.setOnClickListener(this);
        btnCheckState.setText("检查更新");

        tvState = (TextView) findViewById(R.id.tv_state);

        radioGroup.setOnCheckedChangeListener(this);



        // 如果有文件，那么，显示第一个页面
//        String androidPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me/android";
//        File myFolder = new File(androidPath);
//        if (myFolder.exists()) {
            radioGroup.check(R.id.btn_teacher);
//        }

        if(isHaveNet()){
            // 后台检查更新课表
            updateVersion();
        }
    }

    /**
     * 获得版本信息
     */
    private void updateVersion() {

        HttpUtils.getInstance().get(MyFinal.versionUrl, new HttpUtils.ResultCallback<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    LogUtils.logleo("json:"+response);

                    JSONObject jObj = new JSONObject(response);
                    version = new Version();
                    version.apk_version = jObj.getInt("apk_version");
                    version.data_version = jObj.getInt("data_version");

                    handler.sendEmptyMessage(CHECK_UPDATE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                if(isBtnCheck){ // 点击按钮更新
                    isBtnCheck = false;
                    //TODO
                    hideProgress();
                }
            }
        });

    }

    private void showUpdateDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
        adb.setTitle("发现新版本是否更新?");
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

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(MyFinal.apkUrl));
                startActivity(intent);

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

    private boolean isBtnCheck ;

    @Override
    public void onClick(View v) {

        if(!isHaveNet()){
           showToast("当前没有网络，请检查后重试!");
            return  ;
        }

        isBtnCheck = true;
        showProgressMessage("正在处理...");
        updateVersion();
    }

    private void updateKebiao() {
        // 判断是否需要更新
        if(checkDataVersion()){
            showToast("课表已经是最新的，无需更新");
            hideProgress();
            return ;
        }

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



    private final int CHECK_UPDATE = 111;

    /**
     * 更新完成
     */
    private final int UPDATE_FINISH = CHECK_UPDATE+1;

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
                case CHECK_UPDATE: // 检查更新

                    PackageInfo info = null;
                    try {
                        info = getPackageManager().getPackageInfo(getPackageName(), 0);

                        // 如果版本不一样，就提示更新
                        if (version.apk_version != info.versionCode) {
                            hideProgress();
                            showUpdateDialog();
                            return;
                        }
                        // 如果版本号相同，判断数据版本
                        if (!checkDataVersion()) {
                            btnCheckState.setText("课表有更新");
                            btnCheckState.setTextColor(Color.RED);

                            boolean isAutoUpdate = sp.getBoolean(MyFinal.key_auto_update,false);
                            if(isBtnCheck || isAutoUpdate){
                                isBtnCheck = false; // 用了一次关闭开关
                                // 如果是手工点击按钮,或自动更新，则更新课表
                                updateKebiao();
                            }

                        }else{
                            showToast("当前课表是最新版本");
                            hideProgress();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    /**
     * 检查数据版本是否是最新
     * @return true 和服务器一致
     */
    private boolean checkDataVersion() {

        int dataV = sp.getInt(MyFinal.key_data_version, -1);

        if (version.data_version == dataV) {
            return true;
        }

        return false;
    }


    /**
     * 参数为 解压的文件名称
     * @param unZipNameList
     */
    private void parseXmlToDb(List<String> unZipNameList) {
        try {
            // android 课表所在目录
            String androidPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me/android";

            File myFolder = new File(androidPath);
            if (!myFolder.exists()) {
                LogUtils.logleo("android 课表所在目录,不存在");
                return;
            }

            eDao.createNewDb();// 创建新的数据库

            List<File> xlsFiles  = new ArrayList<File>();

            // 偏历所有文件
//            for (File file : myFolder.listFiles()) {
//                String fileName = file.getName(); // 文件名本身是 BGK 编码 ，但该方法 返回的是UTF-8 的字符
//                if ((fileName.contains("android") || fileName.contains("Android")
//                        || fileName.contains("java")|| fileName.contains("Java"))
//                        && fileName.endsWith(".xls")) {
//                    // 根据文件名称判断，符合条件，
//                    xlsFiles.add(file);
//                }
//            }

            // 解析当前下载的文件
            File dir = new File(androidPath);
            for(String name : unZipNameList){
                File xlsFile = new File(dir,name);
                xlsFiles.add(xlsFile);
            }

            // 解析XLS 文件
            for(int i=0;i<xlsFiles.size();i++){

                showProgressMessage("正在解析文件,共"+xlsFiles.size()+"个，当前解析第:"+(i+1)+"个");

                File file = xlsFiles.get(i);
                String fileName = file.getName();

                int indexDi = fileName.indexOf("第");
                int indexQi = fileName.indexOf("期");

                String grade = "android_" + fileName.substring(indexDi + 1, indexQi);

                System.out.println("select :: grade:" + grade + " : " + fileName);

                FileInputStream fin = new FileInputStream(file);

                List<SsBean> beanList = MyUtils.readExcel(fin);

                for (SsBean bean : beanList) {
                    bean.grade = grade;
                    eDao.addOrUpdate(bean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
