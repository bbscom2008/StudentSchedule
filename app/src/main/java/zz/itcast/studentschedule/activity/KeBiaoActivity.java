package zz.itcast.studentschedule.activity;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.bean.SsBean;

/**
 * 显示某个班级的课表
 */
public class KeBiaoActivity extends BaseActivity {

    private String grade;

    private TextView tvGrade;

    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ke_biao);
        tvGrade = (TextView) findViewById(R.id.tv_grade);
        listView = (ListView) findViewById(R.id.listView);

        grade = getIntent().getStringExtra("grade");
        if(grade == null){
            throw new RuntimeException("没有带班级信息!!");
        }
        tvGrade.setText(grade);

        fillData();

        AppWidgetManager.getInstance(this);

    }

    private List<SsBean> beanList;;

    private void fillData() {

        beanList = eDao.getClassByGrade(grade);

        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        moveToday();
    }

    private int index_today = -1;
    /**
     * 显示今天，如果课表中有今天的话
     */
    private void moveToday() {

        for(int i=0;i<beanList.size();i++){
            SsBean bean = beanList.get(i);

            if(DateUtils.isToday(bean.date)){
                index_today = i;
                break;
            };
        }

        if(index_today!=-1){
            listView.setSelection(index_today);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index_today = position;
                myAdapter.notifyDataSetChanged();
            }
        });


    }

    private MyAdapter myAdapter;

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return beanList.size();
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

            if (convertView == null){
                view = getLayoutInflater().inflate(R.layout.list_item_kebiao,null );
                vh = new ViewHolder();

                vh.tvDate = (TextView) view.findViewById(R.id.tv_date);
                vh.tvWeek = (TextView) view.findViewById(R.id.tv_week);
                vh.tvTeacher = (TextView) view.findViewById(R.id.tv_teacher);
                vh.tvContent = (TextView) view.findViewById(R.id.tv_content);

                view.setTag(vh);
            }else{
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            // 赋值

            SsBean  bean= beanList.get(position);

            StringBuffer content = new StringBuffer(bean.dateStr+" \t\t"+bean.week+" \t\t");

            content.append(TextUtils.isEmpty(bean.teacher)?"\t\t\t\t\t\t":bean.teacher);
            content.append("\t\t"+bean.content);

//            tvKeBiao.setText(content);

            vh.tvDate.setText(bean.dateStr);

            vh.tvWeek.setText(bean.week);
            vh.tvTeacher.setText(bean.teacher);
            vh.tvContent.setText(bean.content);



            if(index_today == position){
                view.setBackgroundColor(0x550000FF);
            }else{
                view.setBackgroundColor(0x00000000);
            }

            return view;
        }
    }

    private class  ViewHolder{

        private TextView tvDate;
        private TextView tvWeek;
        private TextView tvTeacher;
        private TextView tvContent;

    }

}
