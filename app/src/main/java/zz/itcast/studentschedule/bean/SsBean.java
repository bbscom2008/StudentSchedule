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
	 * 如 android_基础_10 、 android_就业_11
	 * 如 javaEE_基础_16 或 javaEE_就业_17
	 */
	public String grade;

	@Override
	public String toString() {
		return "SsBean{" +
				"date=" + date +
				", dateStr='" + dateStr + '\'' +
				", week='" + week + '\'' +
				", content='" + content + '\'' +
				", room='" + room + '\'' +
				", teacher='" + teacher + '\'' +
				", ps='" + ps + '\'' +
				", classType=" + classType +
				", grade='" + grade + '\'' +
				'}';
	}
}
