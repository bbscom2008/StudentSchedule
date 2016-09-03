package zz.itcast.studentschedule.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;

import java.util.List;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.activity.KeBiaoActivity;
import zz.itcast.studentschedule.db.ExcelDao;
import zz.itcast.studentschedule.utils.MyFinal;


/**
 * Created by Administrator on 2016/6/2.
 */
public class OtherFragment extends Fragment {


    private ExcelDao eDao;

    private GridView gridView;

    private CheckBox checkBox;

    private SharedPreferences sp;

    private CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            sp.edit().putBoolean(MyFinal.key_auto_update,isChecked).commit();

        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sp = getActivity().getSharedPreferences(MyFinal.sp_name, Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_other,null);
        gridView = (GridView) view.findViewById(R.id.gridView);

        checkBox = (CheckBox) view.findViewById(R.id.cb_auto_update);
        checkBox.setOnCheckedChangeListener(checkListener);

        boolean isAutoUpdate = sp.getBoolean(MyFinal.key_auto_update, false);
        checkBox.setChecked(isAutoUpdate);


        eDao = ExcelDao.getInstance(getActivity());
        gradeList = eDao.getAllGrade();

        myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);

        return view;
    }


    private   List<String> gradeList;

    private MyAdapter myAdapter;

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return gradeList.size();
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

            btn.setText(gradeList.get(position));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), KeBiaoActivity.class);
                    Button btn  = (Button) v;
                    intent.putExtra("grade",btn.getText().toString()); // 将课表名称传递过去
                    getActivity().startActivity(intent);
                }
            });

            return view;
        }
    }


}
