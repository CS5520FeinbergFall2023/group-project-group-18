package edu.northeastern.finalproject.MoodFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haibin.calendarview.MonthView;
import com.haibin.calendarview.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CustomMonthView extends MonthView {

    private int mRadius;
    private List<MoodData> moodDataList;

    public CustomMonthView(Context context) {

        super(context);
        fetchMoodDataFromFirestore();
        Log.d("CustomMonthView", "CustomMonthView instantiated");
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

                    invalidate();
                })
                .addOnFailureListener(e -> {
                    System.out.println("fetching data failure");
                });

    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;

        // canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
        for (MoodData moodData : moodDataList) {
            if (calendar.isCurrentMonth() && calendar.getDay() == moodData.getDayOfMonth()) {
                canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
                break;
            }
        }
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 8;
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint : mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
