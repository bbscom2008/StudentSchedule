package zz.itcast.studentschedule.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.SsBean;
import zz.itcast.studentschedule.utils.MyConstance;

/**
 * Created by Administrator on 2016/9/6.
 * 一： 显示今天的课表 已完成
 * 二：显示最近几个班的课表 显示最近一个月的课表
 */
public class TodayKebiaoActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<SsBean> todayKebiaoList;

    /**
     * 存放N多天的课表
     */
    private HashMap<Integer,List<SsBean>> kebiaoMap;

    private int DAY_NUM = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_ke);

        tvTitle.setText("今日课表");
        tvState.setText("");
        btnCheckState.setText("显示今天");
        btnCheckState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(DAY_NUM/2);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        kebiaoMap = new HashMap<Integer,List<SsBean>>();

        fillData();
    }

    private void fillData() {
        // 获得今天的课表
        todayKebiaoList = eDao.getKebiaoByToday();
        kebiaoMap.put(DAY_NUM/2,todayKebiaoList);

        viewPager.setAdapter(new MyAdapter());
        // 显示今天的
        viewPager.setCurrentItem(DAY_NUM/2);
    }

    private class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return DAY_NUM;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view =  getLayoutInflater().inflate(R.layout.listview_today,null);

            TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
            TextView tvWeek = (TextView) view.findViewById(R.id.tv_week);
            ListView listView = (ListView) view.findViewById(R.id.listView);

            List<SsBean> thisDayKebiao = getThisDayKebiao(position);

            tvDate.setText("日期 : "+thisDayKebiao.get(0).dateStr);
            tvWeek.setText("星期 : "+thisDayKebiao.get(0).week);

            listView.setAdapter(new KeBiaoAdapter(thisDayKebiao));

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

    /**
     * 根据position 获得指定日期的课表
     * @param position viewPager 中页面的下标 当为 DAY_NUM/2 时显示今天
     * @return
     */
    private List<SsBean> getThisDayKebiao(int position) {
        List<SsBean> kebiao = null;

        kebiao = kebiaoMap.get(position);
        if(kebiao == null){
            long time = System.currentTimeMillis() + (position - DAY_NUM/2  )* MyConstance.One_Day;
            kebiao = eDao.getKebiaoByDate(time);
            kebiaoMap.put(position, kebiao);
        }

        return kebiao;
    };


    private class KeBiaoAdapter extends BaseAdapter{

        private final List<SsBean> thisDayKebiao;

        public KeBiaoAdapter(List<SsBean> thisDayKebiao) {
            this.thisDayKebiao = thisDayKebiao;
            
        }

        @Override
        public int getCount() {
            return todayKebiaoList.size();
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
                view= getLayoutInflater().inflate(R.layout.list_item_today_kebiao,null);
                vh = new ViewHolder();

                vh.tvGrade = (TextView) view.findViewById(R.id.tv_grade);
                vh. tvTeacher = (TextView) view.findViewById(R.id.tv_teacher);
                vh. tvContent = (TextView) view.findViewById(R.id.tv_content);

                view.setTag(vh);
            }else{
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            SsBean kebiao = thisDayKebiao.get(position);

            vh.tvGrade.setText("班级 : "+kebiao.grade);

            if(TextUtils.isEmpty(kebiao.content)){
                vh.tvContent.setText("课程 : 休息");
            }else{
                vh.tvContent.setText("课程 : "+kebiao.content);
            }

            vh.tvTeacher.setText("讲师 : "+kebiao.teacher);

            return view;
        }
    }

    private class ViewHolder {

        public TextView tvGrade;
        public TextView tvTeacher;
        public TextView tvContent;
    }

}
