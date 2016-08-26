package zz.itcast.studentschedule.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/6/7.
 */
public class MyHorizontalView extends ViewGroup {
    public MyHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                int scrollX = getScrollX();

                if(scrollX+distanceX <0){
                    scrollTo(0,getScrollY());
                }else if( scrollX+distanceX > getWidth()/2){
                    scrollTo(getWidth()/2,getScrollY());
                }else{
                    scrollTo((int) (scrollX+distanceX),getScrollY());
                }
                return true;
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        View child = getChildAt(0); // 只有一个子view ，listView

        int width  = MeasureSpec.getSize(widthMeasureSpec);

        int widthMeasure = MeasureSpec.makeMeasureSpec((int) (width*1.5),MeasureSpec.EXACTLY);;
        int heightMeasure = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        child.measure(widthMeasure,heightMeasure);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View child = getChildAt(0); // 只有一个子view ，listView
        child.layout(0,0, (int) (getWidth()*1.5),getHeight());
    }


    boolean isHorizontal = false;

    private int downX;
    int downY;

    private int lastX;
    private int lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isHorizontal = false;

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = downX = (int) ev.getX();
                lastY = downY = (int) ev.getY();
                gestureDetector.onTouchEvent(ev);

                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                int disX = Math.abs(moveX-downX);
                int disY = Math.abs(moveY - downY);

                if(disX > disY && disX>15){ // 水平滑动
                    isHorizontal = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return isHorizontal;
    }

    private GestureDetector gestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}
