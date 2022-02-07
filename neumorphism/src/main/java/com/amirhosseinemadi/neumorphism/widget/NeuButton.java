package com.amirhosseinemadi.neumorphism.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import com.amirhosseinemadi.neumorphism.NeumorphismImpl;
import com.amirhosseinemadi.neumorphism.R;
import com.amirhosseinemadi.neumorphism.Utilities;

public class NeuButton extends AppCompatButton implements NeumorphismImpl {
    //TODO light source
    private DisplayMetrics metrics;
    private boolean isInitDrawn;

    private RectF backgroundRect;
    private Paint topPaint;
    private Paint bottomPaint;
    private Rect textBounds;

    private float neuElevation;
    private float neuRadius;
    private int backgroundColor;
    private int topShadowColor;
    private int bottomShadowColor;

    public NeuButton(@NonNull Context context) {
        super(context);
        metrics = getContext().getResources().getDisplayMetrics();
        init((int) Utilities.dpToPx(metrics,8f),30, R.color.md_grey_100, R.color.md_white_1000, R.color.md_grey_300);
    }

    public NeuButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        metrics = getContext().getResources().getDisplayMetrics();
        init((int) Utilities.dpToPx(metrics,8f),30, R.color.md_grey_100, R.color.md_white_1000, R.color.md_grey_300);
    }


    private void init(int neuElevation, int neuRadius, int backgroundColor, int topShadowColor, int bottomShadowColor)
    {
        isInitDrawn = false;
        backgroundRect = new RectF();
        topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textBounds = new Rect();

        setNeuElevation(neuElevation);
        setNeuRadius(neuRadius);
        setTopShadowColor(topShadowColor);
        setBottomShadowColor(bottomShadowColor);
        setBackgroundColor(backgroundColor);
        setPaddingRelative(0,0,0,0);
    }


    private int calculateDrawableWidth()
    {
        int width = 0;
        Drawable[] relativeDrawable = getCompoundDrawablesRelative();

        for (int i = 0; i < relativeDrawable.length; i++)
        {
            if (relativeDrawable[i] != null)
            {
                if (i % 2 == 0)
                {
                    width += relativeDrawable[i].getMinimumWidth() + getCompoundDrawablePadding();
                }else
                {
                    if (relativeDrawable[1] != null && relativeDrawable[3] != null)
                    {
                        width += Math.max(relativeDrawable[1].getMinimumWidth()
                                ,relativeDrawable[3].getMinimumWidth());
                    }
                }
            }
        }

        return width;
    }


    private int calculateMinWidth(DisplayMetrics metrics, Rect textBounds, int neuElevation)
    {
        int width = 0;

        if (getText().length() > 0)
        {

            if (textBounds.width() + calculateDrawableWidth() + getCompoundDrawablePadding() + neuElevation*5 + Utilities.dpToPx(metrics,16) <= getSuggestedMinimumWidth() + neuElevation*5)
            {
                width = getSuggestedMinimumWidth() + neuElevation*5;
//                if (textBounds.width() + calculateDrawableWidth() + getCompoundDrawablePadding() + neuElevation*5 + Utilities.dpToPx(metrics,16) >= getSuggestedMinimumWidth() + neuElevation*5)
//                {
//                    width = (int) (textBounds.width() + calculateDrawableWidth() + getCompoundDrawablePadding() + neuElevation*5 + Utilities.dpToPx(metrics,16));
//                }else
//                {
//                    width = getSuggestedMinimumWidth() + neuElevation*5;
//                }
            }else if (textBounds.width() + calculateDrawableWidth() + getCompoundDrawablePadding() + neuElevation*5 + Utilities.dpToPx(metrics,16) < metrics.widthPixels - neuElevation*5)
            {
                width = (int) (textBounds.width() + calculateDrawableWidth() + getCompoundDrawablePadding() + neuElevation*5 + Utilities.dpToPx(metrics,16));
                //width = metrics.widthPixels;
            }else
            {
                width = metrics.widthPixels;
            }

        }else
        {
            int drawableWidth = calculateDrawableWidth();

            if (drawableWidth > 0)
            {
                width = drawableWidth + neuElevation*5;
            }else
            {
                width = (int) (getSuggestedMinimumWidth() + getNeuElevation()*5);
            }
        }

        if (width < metrics.widthPixels - neuElevation*5)
        {
            width += getPaddingStart() + getPaddingEnd();
        }

        return width;
    };


    private int calculateMinHeight(DisplayMetrics metrics, Rect textBounds, int neuElevation)
    {
        int height;
        if (getText().length() > 0)
        {
            double lines = Math.ceil(textBounds.width() / (double) (metrics.widthPixels - Utilities.dpToPx(metrics,12) - neuElevation*2 - getPaddingLeft() - getPaddingRight()));

            if (lines <= 1)
            {
                height = (int) (getMinHeight() + getLineHeight() * lines);
                System.out.println(textBounds.width());
                System.out.println(lines);
            }else
            {
                height = 0;
            }

//            if (textBounds.height() * lines < metrics.heightPixels - neuElevation*2)
//            {
//                if (lines > 1)
//                {
//
//                }else
//                {
//                    height = getSuggestedMinimumHeight() + neuElevation*2;
//                }
//            }else
//            {
//                height = metrics.heightPixels + neuElevation*2;
//            }
        }else
        {
            height = getSuggestedMinimumHeight() + neuElevation*2;
        }

        return height;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec;

        getPaint().getTextBounds(getText().toString(),0,getText().length(),textBounds);

        if (widthMode == MeasureSpec.AT_MOST)
        {
            int calculatedWidth = calculateMinWidth(metrics,textBounds,(int) neuElevation);
            widthSpec = MeasureSpec.makeMeasureSpec(calculatedWidth,MeasureSpec.EXACTLY);
        }else
        {
            widthSpec = widthMeasureSpec;
        }

        super.onMeasure(widthSpec,heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        backgroundRect.set(0 + getNeuElevation() * 2.5f// + Utilities.dpToPx(metrics,8)
                , 0 + getNeuElevation() * 2.5f// + Utilities.dpToPx(metrics,8)
                , getWidth() - getNeuElevation() * 2.5f//- Utilities.dpToPx(metrics,8)
                , getHeight() - getNeuElevation() * 2.5f);// - Utilities.dpToPx(metrics,8));
    }


    private void drawBackground(Canvas canvas)
    {
        topPaint.setStyle(Paint.Style.FILL);
        topPaint.setColor(ContextCompat.getColor(getContext(),getBackgroundColor()));
        topPaint.setShadowLayer(getNeuElevation()*1.7f,-getNeuElevation(),-getNeuElevation(),ContextCompat.getColor(getContext(),getTopShadowColor()));
        bottomPaint.setStyle(Paint.Style.FILL);
        bottomPaint.setColor(ContextCompat.getColor(getContext(),getBackgroundColor()));
        bottomPaint.setShadowLayer(getNeuElevation()*1.7f,+getNeuElevation(),+getNeuElevation(),ContextCompat.getColor(getContext(),getBottomShadowColor()));

        canvas.drawRoundRect(backgroundRect,getNeuRadius(),getNeuRadius(),topPaint);
        canvas.drawRoundRect(backgroundRect,getNeuRadius(),getNeuRadius(),bottomPaint);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        canvas.clipRect(backgroundRect);
        super.onDraw(canvas);
        isInitDrawn = true;
    }


    /**
     *This method use for setting background color for view and does not apply shape drawable.<br>
     * <br>
     *note : this view does not support gradient background color.
     * @param background background drawable for view
     */
    @Override
    public void setBackground(Drawable background) {
        if (background instanceof ColorDrawable)
        {
            setBackgroundColor(((ColorDrawable) background).getColor());
        }else if (background instanceof ShapeDrawable)
        {
            setBackgroundColor(((ShapeDrawable) background).getPaint().getColor());
        }
    }

    @Override
    public Drawable getBackground() {
        return new ColorDrawable(getBackgroundColor());
    }

    @Override
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (isInitDrawn)
        {
            invalidate();
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        left = (int) (left + neuElevation*2.5);
        top = (int) (top + neuElevation*2.5);
        right = (int) (right + neuElevation*2.5);
        bottom = (int) (bottom + neuElevation*2.5);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        start = (int) (start + neuElevation*2.5);
        top = (int) (top + neuElevation*2.5);
        end = (int) (end + neuElevation*2.5);
        bottom = (int) (bottom + neuElevation*2.5);
        super.setPadding(start, top, end, bottom);
    }

    @Override
    public int getPaddingStart() {
        return (int) (super.getPaddingStart() - neuElevation*2.5);
    }

    @Override
    public int getPaddingEnd() {
        return (int) (super.getPaddingEnd() - neuElevation*2.5);
    }

    @Override
    public void setNeuElevation(float elevation) {
        this.neuElevation = elevation;
        if (isInitDrawn)
        {
            requestLayout();
        }
    }

    @Override
    public float getNeuElevation() {
        return neuElevation;
    }

    @Override
    public void setTopShadowColor(int color) {
        this.topShadowColor = color;
        if (isInitDrawn)
        {
            invalidate();
        }
    }

    @Override
    public int getTopShadowColor() {
        return topShadowColor;
    }

    @Override
    public void setBottomShadowColor(int color) {
        this.bottomShadowColor = color;
        if (isInitDrawn)
        {
            invalidate();
        }
    }

    @Override
    public int getBottomShadowColor() {
        return bottomShadowColor;
    }

    @Override
    public void setNeuRadius(float radius) {
        this.neuRadius = radius;
        if (isInitDrawn)
        {
            invalidate();
        }
    }

    @Override
    public float getNeuRadius() {
        return neuRadius;
    }
}
