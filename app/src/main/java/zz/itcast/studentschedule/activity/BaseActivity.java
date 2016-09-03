package zz.itcast.studentschedule.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import zz.itcast.studentschedule.R;
import zz.itcast.studentschedule.db.ExcelDao;
import zz.itcast.studentschedule.utils.MyFinal;

public class BaseActivity extends FragmentActivity{

    protected ExcelDao eDao;

    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eDao = ExcelDao.getInstance(this);

        proDlg = new ProgressDialog(this);
        proDlg.setMessage("正在处理，请稍候...");
        proDlg.setCancelable(false); // 不可取消

        sp = getSharedPreferences(MyFinal.sp_name, Context.MODE_PRIVATE);


    }

    private ProgressDialog proDlg;

    protected  void showProgress(){

        if(!proDlg.isShowing()){
            proDlg.show();
        }
    }

    protected  void showProgressMessage(final String msg){

        showProgress();

        runOnUiThread(new Thread(){
                  @Override
            public void run() {
                super.run();
                proDlg.setMessage(msg);
            }
    }
        );
    }


    protected  void hideProgress(){

        if(proDlg!=null && proDlg.isShowing()){
            proDlg.dismiss();
        }

    }

    protected void showToast(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvState = (TextView) findViewById(R.id.tv_state);

        btnCheckState = (Button) findViewById(R.id.btn_check_state);


        btnCheckState = (Button) findViewById(R.id.btn_check_state);

    }

    public TextView tvTitle;

    public TextView tvState;

    public Button btnCheckState;


    public boolean isHaveNet(){

        ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = service.getActiveNetworkInfo();

        if(info == null){ // 无网络
            return false;
        }else{
            return true;
        }


    }

}
