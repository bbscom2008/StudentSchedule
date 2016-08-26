package zz.itcast.studentschedule.bean;

/**
 * School schedule 
 * 学员课表
 * @author Administrator
 *
 */
public class SsBean {

	public long date;

	/**
	 * 时间的字符串形式
	 */
	public String dateStr;

	public String week;

	public String content;

	public String room;

	public String teacher;

	/**
	 * 备注
	 */
	public String ps;


	/**
	 * 班级 如 android_10 、 android_11
	 * 如果是基础班那么是： java_16 或 java_17
	 */
	public String grade;



}
