package zz.itcast.studentschedule;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.text.format.Time;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.db.ExcelDao;
import zz.itcast.studentschedule.utils.MyUtils;
import zz.itcast.studentschedule.utils.ZipUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {


    public ApplicationTest() {
        super(Application.class);





//        File meFile = new File(sdFile,"me");
//
//        System.out.println("::"+meFile.getAbsolutePath());
//
//        String[] fs = meFile.list();
//        for(String path : fs){
//            System.out.println("::"+fs);
//        }

//        testSDFile();

    }

    public void testDate(){

        ExcelDao eDao = ExcelDao.getInstance(getContext());

        eDao.test();

    }



    public void testDb(){

        String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/me/test.db";

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

        db.execSQL("create table if not exist timetable(_id integer primary key autoincrement, " +
                "date integer, week varchar(20),content varchar(50),room varchar(20)," +
                "teacher varchar(10),ps text);");


        System.out.println("DB  真的创建了吗？");

    }

    public void testPostJson(){

//        HttpUtils httpUtils = HttpUtils.getInstance();
//        httpUtils.postJson();

//        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        System.out.println("absolutePath:"+absolutePath);

        try {
            String filePath = "mnt/shared/Other/yeshen/郑州传智Android就业18期（20160618面授）课表.xls";

            File testFile= new File(filePath);
            System.out.println(testFile.exists());

            FileInputStream fin = new FileInputStream(testFile);

            List<SsBean> beans = MyUtils.readExcel(fin);
            for(SsBean bean : beans){
                System.out.println(bean);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    public void testZipFolder(){

        try {
            String filePath = "mnt/shared/Other/yeshen/tttt.zip";

            List<String> unzip = ZipUtil.unzip(filePath, null);
            for (String msg : unzip){
                System.out.println(msg);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void testSDFile() {

        File sdFile = Environment.getExternalStorageDirectory();

        File myFolder = new File(sdFile,"me");

        if(!myFolder.exists()){
            System.out.println("me 不存在");
            return ;
        }
            System.out.println("me 存在");


        System.out.println("sdFile:"+myFolder.getAbsolutePath());


        File[] fileList = myFolder.listFiles();
        for(File file : fileList){

            System.out.println("::"+file.getName());

        }
    }

    public void testWeek(){
        long day1 = 1466985600000l;

        Time time = new Time();
        time.set(day1);

        int week = time.weekDay;

        System.out.println(week);
    }

    /**
     * 测试 head 请求，获得文件大小
     */
//    public void testHead(){
//
//        // http://raw.githubusercontent.com/bbscom2008/zzitcast/master/andoid.rar
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//
////        String url = "http://raw.githubusercontent.com/bbscom2008/zzitcast/master/andoid.rar";
//
//        String url = "https://github.com/bbscom2008/yygame/tree/master/res";
//
//        Request request = new Request.Builder()
//                .url(url)
//                .method("GET",null)
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//
//       call.enqueue(new Callback() {
//           @Override
//           // 失败
//           public void onFailure(Call call, IOException e) {
//               e.printStackTrace();;
//           }
//
//           @Override
//           public void onResponse(Call call, Response response) throws IOException {
//
//               // 打印头
//               Headers headers = response.headers();
//              for(int i=0;i<headers.size();i++){
//                  String name = headers.name(i);
//                  String value = headers.get(name);
//                  System.out.println(name + " : "+value );
//              }
//               // 打印体
//               ResponseBody body = response.body();
//               InputStream inputStream = body.byteStream();
//
//               String str = StreamUtils.convertStream2Str(inputStream);
//
//               System.out.println("str:"+str);
//
//           }
//       });
//
//    }



}