package com.example.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PostImageView extends androidx.appcompat.widget.AppCompatImageView {

    public PostImageView(Context context) {
        super(context);
    }

    public PostImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int) (width*1.15));
    }

}