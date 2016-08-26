package zz.itcast.studentschedule.bean;

import java.util.List;

/**
 * 休息统计的bean
 * Created by Administrator on 2016/6/12.
 */
public class RestStatisticsBean {

    /**
     * 日期
     */
    public long date;

    /**
     * 日期
     */
    public String dateStr;

    /**
     * 休息的人数
     */
    public int num;


    /**
     * 当天的休息清况
     */
    public List<RestCell> cells;


    public static class RestCell{

        public String name;

        /**
         * 上课，或者是休息，如果是休息，是字符串为 null  如果是上课，那么是班级的名称 如  android_15
         */
        public String state;

    }
}
