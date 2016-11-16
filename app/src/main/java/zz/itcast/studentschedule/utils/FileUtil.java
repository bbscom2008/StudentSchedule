package zz.itcast.studentschedule.utils;

import android.os.Environment;

import java.io.File;

import zz.itcast.studentschedule.app.MyApp;

public class FileUtil {
	/**
	 * 生产文件 如果文件所在路径不存在则生成路径
	 *
	 * @param fileName
	 *            文件名 带路径
	 * @param isDirectory
	 *            是否为路径
	 * @return
	 */
	public static File buildFile(String fileName, boolean isDirectory) {
		File target = new File(fileName);
		if (isDirectory) {
			target.mkdirs();
		} else {
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
				target = new File(target.getAbsolutePath());
			}
		}
		return target;
	}

	public static  boolean isHaveDbFile() {
		File db  =FileUtil.getDatabaseFile(MyFinal.dbName);
		return db.exists();
	}

	/**
	 * 指定数据库的位置
	 * // 默认在 SD卡/me 文件夹
	 */
	public static File getDatabaseFile(String name) {

		File dbFile = null;
		File sdFile = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			sdFile = Environment.getExternalStorageDirectory();
		}else{
			sdFile = MyApp.app.getFilesDir();

			// 有些手机没有SD卡，只有内置卡
//			throw new RuntimeException("没有安装SD卡，无法工作");
		}

		File dir = new File(sdFile, "me"); //
		if (!dir.exists()) {
			dir.mkdir();
		}

		dbFile = new File(dir, name);

		return dbFile;
	}

	/**
	 * 删除指定文件夹中的所有文件
	 * @param file
     * @return
     */
	public static boolean deleteAllFile(File file){
		boolean isSuccess = true;
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files.length != 0){ // 有文件
				for(File aFile : files){
					isSuccess|=deleteAllFile(aFile);
				}
			}
		}
		file.delete();
		return isSuccess;
	}

}
