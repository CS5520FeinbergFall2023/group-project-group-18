package edu.northeastern.finalproject.MoodFragment;


import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomWeekView extends WeekView {
    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSchemeBasicPaint = new Paint();
    private List<MoodData> moodDataList;


    public CustomWeekView(Context context) {
        super(context);

        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(dipToPx(context, 0.5f));
        mRectPaint.setColor(0x88efefef);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);

        fetchMoodDataFromFirestore();
    }

    private void fetchMoodDataFromFirestore() {
        moodDataList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("moods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int moodValue = document.getLong("moodValue").intValue();
                        String dateString = document.getString("date");
                        Date date = null;
                        if (dateString != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                date = sdf.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        String dayOfWeek = document.getString("dayOfWeek");

                        MoodData moodData = new MoodData(moodValue, date, dayOfWeek);
                        moodDataList.add(moodData);
                    }
                    // System.out.println(moodDataList.get(0));
                    invalidate();
                })
                .addOnFailureListener(e -> {
                    System.out.println("fetch data failure");
                });
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x,0, x + mItemWidth, mItemHeight , mSelectedPaint);
        return true;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {

    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        canvas.drawRect(x, 0, x + mItemWidth,  mItemHeight, mRectPaint);
        int cx = x + mItemWidth / 2;
        int top = - mItemHeight / 6;

        boolean isInRange = isInRange(calendar);

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() && isInRange ? mSchemeTextPaint : mOtherMonthTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}