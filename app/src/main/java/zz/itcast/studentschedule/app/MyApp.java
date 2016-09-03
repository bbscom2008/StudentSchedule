package zz.itcast.studentschedule.app;

import android.app.Application;
/**
 * Created by Administrator on 2016/8/19.
 */
public class MyApp extends Application{

    public static Application app;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;


    }
}
