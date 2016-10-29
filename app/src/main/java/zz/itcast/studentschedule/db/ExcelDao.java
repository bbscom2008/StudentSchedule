package zz.itcast.studentschedule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.utils.FileUtil;
import zz.itcast.studentschedule.utils.MyConstance;
import zz.itcast.studentschedule.utils.MyFinal;
import zz.itcast.studentschedule.utils.MyUtils;


public class ExcelDao {

    private  Context ctx;

    SQLiteDatabase db;



    private ExcelDao(Context ctx) {
        this.ctx = ctx;
        try {
            File dbFile = FileUtil.getDatabaseFile(MyFinal.dbName);

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
     * 数据库的名称
     */
//    private  String DB_NAME = "school_time_table.db";

    /**
     * 删除旧的数据库，创建新的数据库
     */
    public void createNewDb(){

        File dbFile = FileUtil.getDatabaseFile(MyFinal.dbName);
        try {
            if(!dbFile.exists()){
                boolean newFile = dbFile.createNewFile();// 创建新的
                if(!newFile){
                    throw new RuntimeException("创建文件失败");
                }
                db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            /**
             * // 课程表
             * date 日期 date_str 日期的字符串形式 week 星期 content 课程内容 room 教室
             * teacher 老师 class_type 上课类型( 	1 讲师上课 2 学生自习 - 有助教 	3 正常休息 	4 节假日)
             * ps 备注（一般为空） grade 班级名称
             */
                db.execSQL("create table if not exists timetable(_id integer primary key autoincrement, " +
                        "date integer, date_str varchar(20), week varchar(20),content varchar(50),room varchar(20)," +
                        "teacher varchar(10),class_type integer,ps varchar(50),grade varchar(20));");
                // 班级信息表

                // 同事通讯录表

            }else{
                throw new RuntimeException("数据库已经存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int index_date = 1;
    private int index_date_str = 2;
    private int index_week = 3;
    private int index_content = 4;
    private int index_room = 5;
    private int index_teacher = 6;
    private int index_class_type = 7;
    private int index_ps = 8;
    private int index_grade = 9;

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

    /**
     * 注意，sdf 后面有一个空格
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd ");
//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /**
     * 添加或更新课程内容
     * @param sBean 课程内容
     */
    public void addKebiao(SsBean sBean) {

        ContentValues values = new ContentValues();

        values.put("week", sBean.week);
        values.put("content", sBean.content);
        values.put("room", sBean.room);
        values.put("teacher", sBean.teacher);
        values.put("ps", sBean.ps);

        String dateStr = sdf.format(new Date(sBean.date));

        values.put("date_str", dateStr);
        // 添加
        values.put("class_type", sBean.classType);
        values.put("date", sBean.date);
        values.put("grade", sBean.grade);
        db.insert(table_ss, null, values);
    }

    /**
     * 更新课程内容
     * @param sBean 课程内容
     */
    public void updateKebiao(SsBean sBean) {

        ContentValues values = new ContentValues();

        values.put("week", sBean.week);
        values.put("content", sBean.content);
        values.put("room", sBean.room);
        values.put("teacher", sBean.teacher);
        values.put("ps", sBean.ps);
        String dateStr = sdf.format(new Date(sBean.date));
        values.put("date_str", dateStr);
        // 更新
        db.update(table_ss,values," grade = ? AND date = ? ",new String[]{sBean.grade,sBean.date+""});

    }

    /**
     * 添加或更新课程内容
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

    public boolean isOpen(){
        if(db!=null && db.isOpen()){
            return true;
        }
        return false;
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

        if(!isOpen()){
            return allTeacher;
        }

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
    public List<SsBean> getKebiaoByDate(long fromDate, long toDate) {

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

    /**
     * 删除指定班级课表
     * @param grade
     */
    public int deleteGrade(String grade) {

       int num =  db.delete(table_ss," grade = ?",new String[]{grade} );

        return num;
    }

    public void test() {

        // 获得当天的课程
        String dateStr = sdf.format(new Date(System.currentTimeMillis()+ MyConstance.One_Day*3));
        System.out.println("dateStr:"+dateStr);
        Cursor cursor = db.query(table_ss, null, " date_str = ?", new String[]{dateStr}, null, null, " grade");

        MyUtils.printCursor(cursor);
    }

    /**
     * 获得今天的课表
     * @return
     */
    public List<SsBean> getKebiaoByToday() {
        return getKebiaoByDate(System.currentTimeMillis());
    }
    /**
     * 获得今天的课表
     * @return
     */
    public List<SsBean> getKebiaoByDate(long time) {

        List<SsBean> beanList = new ArrayList<SsBean>();
        // 今天的字符串格式
        String dateStr = sdf.format(new Date(time));

        Cursor cursor = db.query(table_ss, null, " date_str = ?", new String[]{dateStr}, null, null, " grade");


        beanList =  parseCursor2BeanList(cursor);

        return beanList;
    }

    public void clearData() {
        db.delete(table_ss,null,null);
        instance = null;
    }

    public void closeDb(){
        if(db!=null && db.isOpen()){
            db.close();;
        }
    }

}
