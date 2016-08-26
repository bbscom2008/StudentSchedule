package zz.itcast.studentschedule.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    /**
     * 将输入流转换为字符串
     * @param input
     * @return
     */
    public static String convertStream2Str(InputStream input){

        // 自带缓存的输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];
        int len = -1;

        try {

            while((len = input.read(buffer))!=-1){
                baos.write(buffer, 0, len);
            }

            return new String(baos.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


