package zz.itcast.studentschedule.bean;

/**
 * School schedule 
 * 学员课表
 * @author Administrator
 *
 */
public class SsBean {

	/**
	 * 日期
	 */
	public long date;

	/**
	 * 时间的字符串形式
	 */
	public String dateStr;

	/**
	 * 星期
	 */
	public String week;

	/**
	 * 课程内容
	 */
	public String content;

	/**
	 * 教室名称
	 */
	public String room;

	/**
	 * 讲师
	 */
	public String teacher;

	/**
	 * 备注
	 */
	public String ps;


	/**
	 * 课程类型：
	 * 	1 讲师上课
	 * 	2 学生自习 - 有助教
	 * 	3 正常休息
	 * 	4 节假日
	 */
	public int classType;

	/**
	 * 班级
	 * 如 android_10 、 android_11
	 * 如果是基础班那么是： java_16 或 java_17
	 */
	public String grade;



}
