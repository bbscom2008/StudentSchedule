package zz.itcast.studentschedule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zz.itcast.studentschedule.bean.SsBean;


public class ExcelDao {

    private  Context ctx;

    SQLiteDatabase db;



    private ExcelDao(Context ctx) {
        this.ctx = ctx;
        try {
            File dbFile = getDatabasePath("school_time_table.db");

            if(dbFile.exists()){ // 之前已经存在
                db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE);
            }else{
                // 没有数据库
                createNewDb();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 指定数据库的位置
     * // 默认在 SD卡/me 文件夹
     */
    public File getDatabasePath(String name) {

        File dbFile = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File sdFile = Environment.getExternalStorageDirectory();

            File dir = new File(sdFile, "me"); //
            if (!dir.exists()) {
                dir.mkdir();
            }

            dbFile = new File(dir, name);

        }else{
            throw new RuntimeException("没有安装SD卡，无法工作");
        }
        return dbFile;
    }

    /**
     * 数据库的名称
     */
    private  String DB_NAME = "school_time_table.db";

    /**
     * 删除旧的数据库，创建新的数据库
     */
    public void createNewDb(){



        File dbFile = getDatabasePath(DB_NAME);

        if(dbFile.exists()){
            dbFile.delete(); // 删除以前的
        }

        try {
            dbFile.createNewFile(); // 创建新的
        } catch (IOException e) {
            e.printStackTrace();
        }


        db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        db.execSQL("create table if not exists timetable(_id integer primary key autoincrement, " +
                "date integer, date_str varchar(20), week varchar(20),content varchar(50),room varchar(20)," +
                "teacher varchar(10),ps varchar(50),grade varchar(20));");


    }

    private int index_date = 1;
    private int index_date_str = 2;
    private int index_week = 3;
    private int index_content = 4;
    private int index_room = 5;
    private int index_teacher = 6;
    private int index_ps = 7;
    private int index_grade = 8;

    private static ExcelDao instance;


    /**
     *  在 SD卡中me 文件夹中，创建数据库，每次都会删除旧的，创建新的 ,目前先这样，等数据库成熟以后，考滤使用一个。
     * @param ctx
     * @return
     */
    public static synchronized ExcelDao getInstance(Context ctx) {
        if (instance == null) {
            instance = new ExcelDao(ctx);
        }
        return instance;
    }


    private  String table_ss = "timetable";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 添加课程内容
     *
     * @param sBean 课程内容
     */
    public void addOrUpdate(SsBean sBean) {

        ContentValues values = new ContentValues();

        values.put("week", sBean.week);
        values.put("content", sBean.content);
        values.put("room", sBean.room);
        values.put("teacher", sBean.teacher);
        values.put("ps", sBean.ps);

        String dateStr = sdf.format(new Date(sBean.date));

        values.put("date_str", dateStr);

        if(isHave(sBean)){
            // 更新
            db.update(table_ss,values," grade = ? AND date = ? ",new String[]{sBean.grade,sBean.date+""});

        }else{
            // 添加
            values.put("date", sBean.date);
            values.put("grade", sBean.grade);
            db.insert(table_ss, null, values);
        };


    }

    /**
     * 判断某个天的课是否存在
     * @param sBean
     * @return
     */
    private boolean isHave(SsBean sBean) {

        boolean isHave = false;

        Cursor cursor = db.query(table_ss, null, " grade = ? AND date = ? ", new String[]{sBean.grade, sBean.date + ""}, null, null, null);

        if (cursor.moveToNext()) {
            // 找到了
            isHave = true;
        }
        cursor.close();
        return isHave;
    }

    /**
     * 获得所有的班级信息
     * @return
     */
    public List<String> getAllGrade(){

        List<String> gradeList = new ArrayList<String>();

        Cursor cursor = db.query(table_ss, null, null, null, " grade", null, null);

        while(cursor.moveToNext()){
            String grade = cursor.getString(index_grade);
            gradeList.add(grade);
        }
        cursor.close();

        return gradeList;
    }


    /**
     * 获得指定班级的课程
     * @param grade
     * @return
     */
    public List<SsBean> getClassByGrade(String grade) {

        List<SsBean> beanList = new ArrayList<SsBean>();
        Cursor cursor = db.query(table_ss, null, " grade = ?", new String[]{grade}, null, null, " date ");

        beanList = parseCursor2BeanList(cursor);

        return beanList;
    }

    /**
     * 将cursor中的内容读取为beanList
     * @param cursor
     * @return
     */
    private List<SsBean> parseCursor2BeanList(Cursor cursor){
        List<SsBean> beanList = new ArrayList<SsBean>();

        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){

            SsBean bean = new SsBean();
            bean.date = cursor.getLong(index_date);

            bean.dateStr = cursor.getString(index_date_str);
            bean.week = cursor.getString(index_week);
            bean.content = cursor.getString(index_content);
            bean.teacher = cursor.getString(index_teacher);

            bean.grade = cursor.getString(index_grade);

            beanList.add(bean);
        }
        cursor.close();
        return beanList;
    }


    public List<String> getAllTeacher() {

        List<String> allTeacher = new ArrayList<String>();

        Cursor cursor = db.query(table_ss, null, null, null, "teacher", null, null);
        while(cursor.moveToNext()){

            String teacher = cursor.getString(index_teacher);
            if(!TextUtils.isEmpty(teacher) && !teacher.contains("就业")){
                allTeacher.add(teacher);
            }
        }

        cursor.close();;

        return allTeacher;
    }

    /**
     * 获得一个老师的所有课程
     * @param teacher
     * @return
     */
    public List<SsBean> getClassByTeacher(String teacher) {

        List<SsBean> beanList = new ArrayList<SsBean>();

        Cursor cursor = db.query(table_ss, null, " teacher = ? ", new String[]{teacher}, null, null," date " );

        beanList = parseCursor2BeanList(cursor);

        return beanList;
    };

    /**
     * 获得指定日期内的所有数据
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<SsBean> getClassByDate(long fromDate, long toDate) {

        List<SsBean> beanList = new ArrayList<SsBean>();

        Cursor cursor =  db.query(table_ss,null,"date > ? AND date < ?",new String[]{fromDate+"",toDate+""},null,null," date ");
        beanList =  parseCursor2BeanList(cursor);

        return beanList;
    }

    /**
     * 获得这段时间的具体的日期，及星期
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<SsBean> getDateByDate(long fromDate, long toDate) {

        List<SsBean> beanList = new ArrayList<SsBean>();

        Cursor cursor =  db.query(table_ss,null,"date > ? AND date < ?",new String[]{fromDate+"",toDate+""}," date ",null," date ");
        beanList =  parseCursor2BeanList(cursor);

        return beanList;
    }
}
