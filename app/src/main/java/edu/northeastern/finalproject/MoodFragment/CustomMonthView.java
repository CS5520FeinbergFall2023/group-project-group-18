package edu.northeastern.finalproject.MoodFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.haibin.calendarview.MonthView;
import com.haibin.calendarview.Calendar;

public class CustomMonthView extends MonthView {

    public CustomMonthView(Context context) {
        super(context);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        canvas.drawText(calendar.getDay() + "", x + mItemWidth / 2, mTextBaseLine + y - mItemHeight / 6, calendar.isCurrentDay() ? mCurDayTextPaint : mCurMonthTextPaint);
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return true;
    }
}
