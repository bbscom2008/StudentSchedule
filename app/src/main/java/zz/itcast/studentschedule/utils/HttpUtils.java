package zz.itcast.studentschedule.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import zz.itcast.studentschedule.app.MyApp;

public class HttpUtils {

    private static HttpUtils mInstance;

    private OkHttpClient mOkHttpClient;

    private static Handler handler;

    private HttpUtils() {

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(5, TimeUnit.SECONDS); // 5秒超时
        mOkHttpClient.setReadTimeout(5, TimeUnit.SECONDS); // 5秒超时

//        mOkHttpClient.setCookieHandler(new CookieManager(null,
//                CookiePolicy.ACCEPT_ORIGINAL_SERVER));

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case SHOW_TOAST:
                        String s = (String) msg.obj;
                        Toast.makeText(MyApp.app, s, Toast.LENGTH_LONG).show();
                        break;
                }


            }
        };

    }

    private static final int SHOW_TOAST = 100;


    public static HttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    // 下面的是提供的工具

    /**
     * 异步的get请求
     *
     * @param url 联网地址
     * @param callback 回调对象
     */
    public void get(String url, final ResultCallback<String> callback) {

        Request request = new Request.Builder().url(url).build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {
                callback.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Request request, IOException e) {
                callback.onError(request, e);
            }
        });

    }

    /**
     * 异步的post请求
     *
     * @param url 联网地址
     * @param callback 回调
     * @param params 参数
     */
    public void post(String url, Map<String, String> params,
                     final ResultCallback callback) {

        FormEncodingBuilder bodyBuild = new FormEncodingBuilder();

        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            String value = params.get(key);
            bodyBuild.add(key, value);
        }

        RequestBody body = bodyBuild.build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {
                callback.onResponse(response);
            }

            @Override
            public void onFailure(Request request, IOException e) {
                callback.onError(request, e);
            }
        });

    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    public void downloadFile(final String url, final String destFileDir,
                             final ResultCallback<File> callback) {

        Request request = new Request.Builder().url(url).build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {

                InputStream in = response.body().byteStream();

                File destFile = new File(destFileDir);
                FileOutputStream fout = new FileOutputStream(destFile);

                byte[] buffer = new byte[1024];
                int len = -1;

                try {
                    while ((len = in.read(buffer)) != -1) {
                        fout.write(buffer, 0, len);
                    }

                    // 回传File
                    callback.onResponse(destFile);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                    fout.close();
                }

            }

            @Override
            public void onFailure(Request request, IOException e) {
                callback.onError(request, e);
            }
        });

    }

    public static abstract class ResultCallback<T> {

        public void onError(Request request, Exception e) {
            e.printStackTrace();
            // 出错了
            showToast("联网失败，请稍后重试:" + e.getMessage());

        }

        public void showToast(String s) {
            Message msg = Message.obtain();
            msg.what = SHOW_TOAST;
            msg.obj = s;
            handler.sendMessage(msg);

        }

        ;

        public abstract void onResponse(T response);
    }

}