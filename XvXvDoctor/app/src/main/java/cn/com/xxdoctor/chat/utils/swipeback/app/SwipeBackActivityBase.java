package cn.com.xxdoctor.chat.utils.swipeback.app;


import cn.com.xxdoctor.chat.utils.swipeback.SwipeBackLayout;


public interface SwipeBackActivityBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();

    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();

}
