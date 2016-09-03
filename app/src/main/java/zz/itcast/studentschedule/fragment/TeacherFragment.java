package zz.itcast.studentschedule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.activity.RestStatitcsActivity;
import zz.itcast.studentschedule.activity.TeacherActivity;
import zz.itcast.studentschedule.db.ExcelDao;


/**
 * Created by Administrator on 2016/6/2.
 */
public class TeacherFragment extends Fragment  {

    private GridView gridView;

    private ExcelDao eDao;

    private List<String> teacherList;

    private Button btnRest;
    private View view;

    private TextView tv_desc;

    @Override
    public void onResume() {
        super.onResume();
        fillData();

        System.out.println("teacherFragment.onResume===========");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teacher,null);

        System.out.println("onCreateView==========");
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);

        btnRest = (Button) view.findViewById(R.id.btn_rest);
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 休息日统计的页面
                Intent intent = new Intent(getActivity(),RestStatitcsActivity.class);
                startActivity(intent);


            }
        });

        gridView = (GridView) view.findViewById(R.id.gridView);

        eDao = ExcelDao.getInstance(getActivity());



        return view;
    }


    private void fillData() {

        teacherList = eDao.getAllTeacher();

        if(teacherList.size() == 0){
            tv_desc.setText("当前没有课表，请先更新!");
            btnRest.setVisibility(View.GONE);
            return ;
        }else{
            tv_desc.setText("讲师上课时间统计");
            btnRest.setVisibility(View.VISIBLE);
            gridView.setAdapter(new MyAdapter());
        }


    }

    public void flushView() {
        System.out.println("flushview=======");
        fillData();
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return teacherList.size();
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
            View view ;

            if(convertView == null){
                view = new Button(getActivity());
            }else{
                view = convertView;
            }

            Button btn = (Button) view;

            btn.setText(teacherList.get(position));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), TeacherActivity.class);
                    Button btn  = (Button) v;
                    intent.putExtra("teacher",btn.getText().toString()); // 将课表名称传递过去
                    getActivity().startActivity(intent);
                }
            });

            return view;
        }
    }

}
