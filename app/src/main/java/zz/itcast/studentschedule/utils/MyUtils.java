package zz.itcast.studentschedule.utils;

import android.database.Cursor;
import android.text.TextUtils;
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

public class MyUtils {

    /**
     * 读取电子表格
     * @param is
     * @return
     */
    public static List<SsBean> readExcel(InputStream is) {

        List<SsBean> beanList = new ArrayList<SsBean>();

        try {
            Workbook rwb = Workbook.getWorkbook(is);

            Sheet st = rwb.getSheet(0);// 这里有两种方法获取sheet表,1为名字，而为下标，从0开始
            // Sheet st = rwb.getSheet("Android课表");

            int rs = st.getColumns(); // 列
            int rows = st.getRows(); // 行

            // 行数不应大于 150 行，但有一些空白行，导致 rows 会是 858
            if(rows>200){
                rows = 200;
            }

            System.out.println("列数===>" + rs + "行数：" + rows);

            // 课表前二行是标题，跳过，所以从 k = 2 开始
            for (int k = 2; k < rows; k++) {// 行
                StringBuffer sb = new StringBuffer();
                SsBean bean =  new SsBean();

                for (int i = 0; i < rs; i++) {// 列

                    Cell cell = st.getCell(i, k);

                    // 通用的获取cell值的方式,返回字符串
                    String celllStr = cell.getContents();

                    // 课表共6列
                    switch (i){
                        case 0:
                            // 0   DATE  日期 不能是 Empty
                            if (cell.getType() == CellType.DATE) {
                                // excel 类型为时间类型处理;
                                DateCell dc = (DateCell) cell;
                                bean.date = dc.getDate().getTime();

                                beanList.add(bean); // 只有当第一列有数据时，才加入集合

                            }else{
                                // 数据格式不正确,也有可能是已经结束了
                                wrongCellStyle(k,i,cell);
                                // 如果第一列不是日期，那就认为，结束了
                                return beanList;
                            }
                            break;
                        case 1:
                            // 1   label  星期 不能是 Empty
                            if (cell.getType() == CellType.LABEL) {
                                // 获得cell具体类型值的方式
                                bean.week = ((LabelCell) cell).getString();
                            }else{
                                // 数据格式不对
//                                wrongCellStyle(k,i,cell);
                            }

                            break;
                        case 2:
                            // 2   label  内容 可能是 Empty
                            if (cell.getType() == CellType.LABEL) {
                                // 获得cell具体类型值的方式
                                bean.content = ((LabelCell) cell).getString();
                            }else if( cell.getType() == CellType.EMPTY){
                                bean.content = "";
                            }else{
                                // 数据格式不对
                                wrongCellStyle(k,i,cell);
                            }
                            break;
                        case 3:
                            // 3   label  教室 可能是 Empty
                            if (cell.getType() == CellType.LABEL) {
                                // 获得cell具体类型值的方式
                                bean.room = ((LabelCell) cell).getString();
                            }else if( cell.getType() == CellType.EMPTY){

                                bean.room = "";
                            }else{
                                bean.room = cell.getContents();
                                // 数据格式不对
//                                wrongCellStyle(k,i,cell);
                            }

                            break;
                        case 4:
                            // 4   label  讲师 可能是 Empty 说明今天休息
                            if (cell.getType() == CellType.LABEL) {
                                // 获得cell具体类型值的方式
                                bean.teacher = ((LabelCell) cell).getString();
                            }else if( cell.getType() == CellType.EMPTY){

                                bean.teacher = "";
                            }else{
                                // 数据格式不对
                                wrongCellStyle(k,i,cell);
                            }

                            break;
                        case 5:
                            // 5   label  备注 可能是 Empty
                            if (cell.getType() == CellType.LABEL) {
                                // 获得cell具体类型值的方式
                                bean.ps = ((LabelCell) cell).getString();
                            }else if( cell.getType() == CellType.EMPTY){

                                bean.ps = "";
                            }else{
                                // 数据格式不对
                                wrongCellStyle(k,i,cell);
                            }

                            break;
                        default:
                            // 这里的内容多了，
//                            wrongCellStyle(k,i,cell);
                            break;
                    }
                    //  取出刚加入的bean 判断 这天的课程类型
                    SsBean sBean = beanList.get(beanList.size()-1);
                    if(!TextUtils.isEmpty(sBean.teacher)){ //  说明有老师上课
                        sBean.classType = 1;
                    }else{
                        // 没老师
                        String content = sBean.content; // 课程内容
                        if(TextUtils.isEmpty(content)){ // 空的
                            sBean.classType = 3;
                        }else if(content.contains("练习")){ // 练习课
                            sBean.classType = 2;
                        }else if(content.contains("假")){
                            sBean.classType = 2;
                        }
                    }

                }
            }
            // 关闭
            rwb.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return beanList;
    }

    /**
     * 当单元格的数据格式不正确时
     * @param k 行
     * @param i 列
     * @param cell 单元
     */
    private static void wrongCellStyle(int k, int i, Cell cell) {

        System.out.println(k+" 行 "+i+" 列的数据:"+cell.getContents()+"格式不正确，或课表已经结束!! 当前类型为:"+cell.getType());
    }

//    /**
//     * 读取Excel
//     *
//     * @param filePath
//     */
//    public static void readExcel(String filePath) {
//
//    }

    /**
     * 判断二个日期是否是同一天
     * @param day1
     * @param day2
     * @return
     */
    public static boolean isSameDay(long day1,long day2){
        Time time = new Time();
        time.set(day1);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(day2);
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }



    /**
     * 判断是否是相邻的二天
     * @param preBean 前一天
     * @param nextBean 后一天
     * @return
     */
    public static boolean isNextDay(SsBean preBean, SsBean nextBean) {
        long nextDay = preBean.date+60*60*24*1000; // 前一天加上一天
        boolean isSameDay = MyUtils.isSameDay(nextDay,nextBean.date);
        return isSameDay;
    }

    /**
     * 根据日期获得星期
     * @param date
     * @return
     */
    public static String getWeek(long date) {
        Time time = new Time();
        time.set(date);

        int week = time.weekDay; // 星期一 是 1
        String weekStr = null;

        switch (week){
            case 0:
                weekStr="星期日";
                break;
            case 1:
                weekStr="星期一";
                break;
            case 2:
                weekStr="星期二";
                break;
            case 3:
                weekStr="星期三";
                break;
            case 4:
                weekStr="星期四";
                break;
            case 5:
                weekStr="星期五";
                break;
            case 6:
                weekStr="星期六";
                break;
        }

        return weekStr;
    }

    /**
     * 返回当前时间
     * @return
     */
    public static String now(){
        return java.text.DateFormat.getTimeInstance().format(System.currentTimeMillis());

    }

    public static void printCursor(Cursor cursor){

        if(cursor == null){
            System.out.println("cursor == null");
            return ;
        }
        if(cursor.getCount() == 0){
            System.out.println("cursor.getCount() == 0");
            return ;
        }

        System.out.println("总行数：cursor.getCount():"+cursor.getCount());

        while(cursor.moveToNext()){
            System.out.println("当前行："+cursor.getPosition());
            int columnCount = cursor.getColumnCount();
            for(int i=0;i<columnCount;i++){
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                System.out.println(name + " : "+ value);
            }
        }
    }

}
