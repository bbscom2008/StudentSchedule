package zz.itcast.studentschedule;

import android.text.format.Time;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import zz.itcast.studentschedule.utils.FileUtil;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);


//        SimpleDateFormat sdf;
//        sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
//
//        Date date = new Date();
//        date.setTime(1470528000000l);
//
//        String str = sdf.format(date);
//        System.out.println(str);

    }

    @Test
    public void testSocketTimeout(){

        try {
            Socket socket = new Socket();


//            SocketAddress reAdd = new InetSocketAddress("318.118.118.8",80);
            SocketAddress reAdd = new InetSocketAddress("127.0.0.1",8080);
            socket.connect(reAdd,10000);

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[10240];
            int read = inputStream.read(buffer);

            String str = new String(buffer, 0, read);
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFile(){

        try {
            File file = new File("c:/me");

            if(!file.exists()){
                file.mkdir();
            }

            File cac = new File(file,"ttt.txt");

            if(!cac.exists()){
//                cac.createNewFile();

                FileOutputStream fout = new FileOutputStream(cac);
                fout.write("adfadfdf".getBytes());
                fout.flush();
            }else{
                cac.delete();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testDay(){

        long day1 = 1466985600000l;
//
//        long day2 = 1466812800000l;
//
//
//        System.out.println(day1-day2);
//
//        int num = (int) ((day1 - day2) / (60*60*24*1000));
//
//        System.out.println(num);

        Time time = new Time();
        time.set(day1);

        int week = time.weekDay;

        System.out.println(week);

    }


    @Test
    public void testMatch(){
//        String str = "昨日凌晨3时，一条@云南:123456 @绿春:112233 刚修好的二级公路试通车不到两个月，整条路飞下悬崖，正宗的豆腐渣工程的微博在网络上疯传，截至昨日下午，该@微博:654321 转载次数达近万次。面对网友的质疑，昨日下午，@绿春:112233 县委宣传部回应，进入雨季以来，@绿春:112233 县境普降大雨，造成该县两条主要公路干道发生山体滑坡，公路中断。";
//        Regex reg = new Regex(@"@\S+:\d{6}");
//        MatchCollection mc = reg.Matches(str);
//
//        foreach (Match m in mc)
//        {
//            MessageBox.Show(m.Value);
//        }

        File file = new File("c:/ttt");
       boolean b =  FileUtil.deleteAllFile(file);
        System.out.println(b);


    }


    @Test
    public void tttt(final String age){

//        final int age = 20;
//        final String age = "adf";
        final String test = "hello" ;

//       new Thread(){
//           @Override
//           public void run() {
//               super.run();
//               System.out.println(test);
//              String tt= age;
//
//           }
//       }.start();


//        new Runnable(){
//            @Override
//            public void run() {
//                String tt = test;
//
//            }
//        };

    }




}