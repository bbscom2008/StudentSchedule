package zz.itcast.studentschedule.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.RestStatisticsBean;
import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.utils.MyConstance;

/**
 * 休息日统计
 */
public class RestStatitcsActivity extends BaseActivity {

    private List<String> teachers;

    private LinearLayout llTitle;

    private ListView listView;

    /**
     * 这段时间的所有的课表
     */
    private List<SsBean> classBeanList;
    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_statitcs);
        act = this;

        llTitle = (LinearLayout) findViewById(R.id.ll_list_title);

        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);

        // 处理标题
        tvTitle.setText("休息日统计");
        btnCheckState.setVisibility(View.GONE);
        tvState.setVisibility(View.GONE);

        fillData();
    }

    private void fillData() {
        // 添加listview 的标题
        addSubTitle();

        // 添加listView 中的内容
        addContent();

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setSelection(indexSelect);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                indexSelect = position;
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     *  选择的条目，默认是今天
     */
    private int indexSelect = -1;

    /**
     * 显示最近一个月的空闲时间
     */
    private void addContent() {

        long now = System.currentTimeMillis();
        long fromDate = now - MyConstance.One_Day * 15; // 前15天
        long toDate = now + MyConstance.One_Day * 15; // 后15天

        // 获得这段时间的课表
        classBeanList = eDao.getKebiaoByDate(fromDate, toDate);

        // 获得这段时间的日期
        List<SsBean> dateList = eDao.getDateByDate(fromDate, toDate);

        // listview 显示的内容
        rsBeans = new ArrayList<RestStatisticsBean>();

        for (int i = 0; i < dateList.size(); i++) {

            RestStatisticsBean rsBean = new RestStatisticsBean();
            rsBeans.add(rsBean);

            rsBean.date = dateList.get(i).date;
            rsBean.dateStr = dateList.get(i).dateStr.substring(dateList.get(i).dateStr.indexOf("/")+1);

            // 判断是否是今天
            if(DateUtils.isToday(rsBean.date)){
                indexSelect = i;
            }

            rsBean.cells = new ArrayList<RestStatisticsBean.RestCell>();

            // 根据集合 classBeanList 查询这个日期里，每个人，是否上班

            int num = 0;
            for (int j = 0; j < teachers.size(); j++) {
                RestStatisticsBean.RestCell cell = new RestStatisticsBean.RestCell();
                rsBean.cells.add(cell);
                cell.name = teachers.get(j);
                cell.state = getTeacherState(cell.name, rsBean.date);

                if(cell.state == null){

                    num++;
                }
            }
            rsBean.num = num;
        }
    }

    /**
     * 获得该教师当天的状态,是休息，还是在某班上课
     *
     * @param name
     * @param date
     * @return
     */
    private String getTeacherState(String name, long date) {

        String state = null;

        for (SsBean bean : classBeanList) {

            if (date == bean.date && name.equals(bean.teacher)) {

                if (TextUtils.isEmpty(bean.content)) { // 今天 没课
                    state = null;
                } else {
                    state = bean.grade.replace("就业_","");
                    state = state.replace("基础_","");
                }
                break;
            }
        }
        return state;
    }

    /**
     * 休息统计的数据
     */
    private List<RestStatisticsBean> rsBeans;


    private void addSubTitle() {
        teachers = eDao.getAllTeacher();
//        System.out.println("teachers.size():"+teachers.size());

        llTitle.setBackgroundResource(R.color.black_gray);

        addSubTitleText(" 日  期 ", llTitle);
        addSubTitleText("休息人数", llTitle);
        for (String teacher : teachers) {
            addSubTitleText(teacher, llTitle);
        }
    }


    /**
     * 添加listView 上的标题
     *
     * @param name
     */
    private void addSubTitleText(String name, ViewGroup layout) {

        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.bg_rect);
        textView.setTextColor(Color.BLACK);
        textView.setText(name);
        textView.setTextSize(12);
        textView.setPadding(15,15,15,15);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(110, -2);
        layout.addView(textView, param);
    }


    private MyAdapter adapter;


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rsBeans.size();
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
            LinearLayout view;

            if (convertView == null) {
                view = new LinearLayout(act);

                System.out.println("======position::"+position);
                // 根据标题中每个textView的大小来设置 每个格子的大小
                for (int i = 0; i < llTitle.getChildCount(); i++) {

                    View tv = llTitle.getChildAt(i); // 取出对应的第N个子view

                    TextView textView = new TextView(act);
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(15,15,15,15);
                    textView.setTextColor(Color.BLACK);
                    textView.setSingleLine();
                    textView.setTextSize(10);
                    textView.setBackgroundResource(R.drawable.bg_rect);

//                    tv.measure(0, 0);
//                    int width = tv.getMeasuredWidth();
//                    int height = tv.getMeasuredHeight();
//                    System.out.println("tv.getWidth():" + width);

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(110, ViewGroup.LayoutParams.MATCH_PARENT);
                    view.addView(textView, param);
                }
            } else {
                view = (LinearLayout) convertView;
            }

            RestStatisticsBean rsBean = rsBeans.get(position);


            List<RestStatisticsBean.RestCell> cellList = rsBean.cells;

            for (int i = 0; i < view.getChildCount(); i++) {

                TextView textView = (TextView) view.getChildAt(i);

                if (i == 0) {
                    textView.setText(rsBean.dateStr);
                } else if (i == 1) {
                    textView.setText(rsBean.num+"");
                } else {

                    String state = cellList.get(i - 2).state;
                    if(state == null){
                        textView.setText("休息");
                    }else{
                        textView.setText(state);
                    }
                }
            }

            if(position == indexSelect){
                view.setBackgroundResource(R.color.blue_sky);
            }else{
                view.setBackgroundResource(R.color.translucent);
            }

            return view;
        }
    }


}
