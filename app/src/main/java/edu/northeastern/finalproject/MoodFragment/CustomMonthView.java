package edu.northeastern.finalproject.MoodFragment;

import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.MonthView;
import com.haibin.calendarview.Calendar;



public class CustomMonthView extends MonthView {

    private Paint mSchemeBasicPaint = new Paint();


    public CustomMonthView(Context context) {

        super(context);
        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;
        int cy = (int) (top + mTextBaseLine * 0.825);
        int radius = Math.min(mItemWidth, mItemHeight) / 3; // Adjust radius as needed

        if (calendar.getScheme() != null) {
            mSchemeBasicPaint.setColor(calendar.getSchemeColor());
            canvas.drawCircle(cx, cy, radius, mSchemeBasicPaint);
        }
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int top = y - mItemHeight / 6;

        Paint textPaint;
        if (hasScheme) {
            textPaint = new Paint(mSchemeTextPaint); // Copy existing scheme text paint
            textPaint.setColor(Color.BLACK); // Set color to black for dates with scheme
        } else if (isSelected) {
            textPaint = mSelectTextPaint;
        } else {
            textPaint = calendar.isCurrentDay() ? mCurDayTextPaint :
                    calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint;
        }

        canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top, textPaint);
    }
}