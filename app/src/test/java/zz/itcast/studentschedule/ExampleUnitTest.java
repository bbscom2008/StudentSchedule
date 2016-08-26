package zz.itcast.studentschedule;

import android.text.format.Time;

import org.junit.Test;
import org.junit.runner.manipulation.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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




}