package zz.itcast.studentschedule.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.utils.MyUtils;

public class TeacherActivity extends BaseActivity {

    private ListView listView;

    private String teacher;

    private TextView tvState;
    private List<SsBean> beanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacher =  getIntent().getStringExtra("teacher");
        if(TextUtils.isEmpty(teacher)){
            throw new RuntimeException("没有传老师的姓名");
        }

        getActionBar().setTitle(teacher);

        listView = (ListView) findViewById(R.id.listView);
        tvState = (TextView) findViewById(R.id.tv_state);

        tvState.setText(teacher);

        fillData();

    }

    private void fillData() {


        beanList = eDao.getClassByTeacher(teacher);

        // 插入休息日
        insertRestDay();
        // 查找今天
        findToday();

        adapter = new MyAdapter();

        listView.setAdapter(adapter);
        listView.setDivider(getResources().getDrawable(R.drawable.bg_rect));

        if(today!=-1){
            listView.setSelection(today);
        }

    }

    private void findToday() {
        for(int i=0;i<allDay.size();i++){
            SsBean bean = allDay.get(i);
            if(DateUtils.isToday(bean.date)){
                today = i;
                break;
            }
        }
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 有冲突的日期
     */
    private HashSet<SsBean> clashDays;


    /**
     * 今天
     */
    private int today  = -1;


    private List<SsBean> allDay;
    /**
     * 插入休息日,同时，判断是否有冲突
     */
    private void insertRestDay() {

        clashDays = new HashSet<SsBean>();

        allDay = new ArrayList<SsBean>();

        for(int i=0;i<beanList.size();i++){

            SsBean bean = beanList.get(i); // 当前的

            allDay.add(bean);

            if(i == beanList.size()-1){
                break; // 结束了
            }

            SsBean nextBean = beanList.get(i+1); // 取出下一个

            // 判断二个bean之间是否是相邻的二天
            if(MyUtils.isNextDay(bean,nextBean)){
                continue;
            }else if(MyUtils.isSameDay(bean.date,nextBean.date)){ // 判断是否是同一天

                clashDays.add(bean);

                tvState.setText(teacher+" 注意有冲突!");
                tvState.setTextColor(Color.RED);


            }else{
                // 插入中间的天数


                int dayNum = (int) ((nextBean.date - bean.date)/ One_Day);
                // 中间间隔天数：
                dayNum--;

                // 插入空闲的天数
                for(int j=0;j<dayNum;j++){

                    SsBean restBean = new SsBean();

                    restBean.date = bean.date+One_Day*(j+1);

                    restBean.dateStr = sdf.format(restBean.date);

                    restBean.week = MyUtils.getWeek(restBean.date);

                    restBean.grade = "休息";

                    allDay.add(restBean);

                }
            }
        }
    }


    private int One_Day = 60 * 60 * 24 * 1000;


    MyAdapter adapter;

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return allDay.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ViewHolder vh;

            if(convertView == null){
                view = getLayoutInflater().inflate(R.layout.list_item_teacher,null);

                vh = new ViewHolder();

                vh.tvDate = (TextView) view.findViewById(R.id.tv_date);
                vh.tvWeek = (TextView) view.findViewById(R.id.tv_week);
                vh.tvGrade = (TextView) view.findViewById(R.id.tv_grade);

//                vh.tvDate.setBackgroundResource(R.drawable.bg_rect);
//                vh.tvWeek.setBackgroundResource(R.drawable.bg_rect);
//                vh.tvGrade.setBackgroundResource(R.drawable.bg_rect);

                view.setTag(vh);
            }else{
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            SsBean  bean = allDay.get(position);

            vh.tvDate.setText(bean.dateStr);

            vh.tvWeek.setText(bean.week);

            vh.tvGrade.setText(bean.grade);

            // 冲突的日期标红
            if(clashDays.contains(bean)){
                vh.tvDate.setBackgroundColor(Color.RED);
            }else{
                vh.tvDate.setBackgroundColor(Color.TRANSPARENT);
            }

            if(position == today){
                view.setBackgroundColor(0x6600ff00);
            }else{
                view.setBackgroundColor(0x00000000);
            }

            return view;
        }
    }

    private class ViewHolder{

        public TextView tvDate;
        public TextView tvWeek;
        public TextView tvGrade;
    }

}
