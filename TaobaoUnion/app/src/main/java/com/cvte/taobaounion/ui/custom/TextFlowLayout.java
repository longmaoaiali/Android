package com.cvte.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvte.taobaounion.R;
import com.cvte.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2020/11/12.
 */

public class TextFlowLayout extends ViewGroup {
    private static final String TAG = "TextFlowLayout";
    private OnFlowTextItemClickListener mOnFlowTextItemClickListener;
    private List<String> mTextList = new ArrayList<>();

    public static final float DEFAULT_SPACE = 10;
    private float mItemHorizonticalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private int mSelfWidth;
    private int mItemHeight;

    public float getItemHoriaontalSpace() {
        return mItemHorizonticalSpace;

    }

    public int getContentSize(){
        return mTextList.size();
    }

    public void setItemHoriaontalSpace(float itemHoriaontalSpace) {
        mItemHorizonticalSpace = itemHoriaontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    public TextFlowLayout(Context context) {
        this(context,null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        /*拿到style.xml自定义的属性*/
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.FlowTextStyle);
        //得到属性
        mItemHorizonticalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace,DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace,DEFAULT_SPACE);

        ta.recycle();
    }
    
    public void setTextList(List<String> textList){
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        Collections.reverse(mTextList);
        for (String text : mTextList) {
            //TextView textView = new TextView(getContext());
            /*创建TextView或者直接载入XML布局文件*/
            //true = false + addView()
            TextView itemView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            itemView.setText(text);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnFlowTextItemClickListener != null) {
                        mOnFlowTextItemClickListener.onFlowItemClick(text);
                    }
                }
            });

            addView(itemView);
        }
    }


    //代表多行ItemView
    private List<List<View>> lines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() ==0) {
            return;
        }

        List<View> line = null;
        lines.clear();
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        /*1. 测量子View*/
        LogUtils.d(TAG,"onMeasure--> childCount"+getChildCount());

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                continue;
            }
            //测量
            measureChild(itemView,widthMeasureSpec,heightMeasureSpec);
            if (line == null) {
                line = createNewLine(itemView);
            } else {
                //判断当前行是否还能加入itemView
                if (canBeAdd(itemView,line)) {
                    line.add(itemView);
                } else {
                    line = createNewLine(itemView);
                }
            }
        }

        /*2. 测量自己,得到整个空间宽高*/
        mItemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int)((lines.size()*mItemHeight + mItemVerticalSpace*(lines.size()+1))+0.5f);
        setMeasuredDimension(mSelfWidth,selfHeight);

    }

    private List<View> createNewLine(View itemView) {
        List<View> line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    private boolean canBeAdd(View itemView, List<View> line) {
        //一行的所有子View宽度相加并且加上间隔
        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            totalWidth += view.getMeasuredWidth();
        }
        totalWidth += mItemHorizonticalSpace*(line.size()+1);
        return  totalWidth <= mSelfWidth;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /*3. viewGroup 摆放子控件 */
        int topOffset = (int) mItemHorizonticalSpace;
        for (List<View> views : lines) {
            int leftOffset = (int) mItemHorizonticalSpace;
            for (View view : views) {
                //绘制
                view.layout(leftOffset,topOffset,leftOffset+view.getMeasuredWidth(),topOffset+view.getMeasuredHeight());
                leftOffset += view.getMeasuredWidth() + mItemHorizonticalSpace;
            }
            topOffset += mItemHeight + mItemVerticalSpace;
        }
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener){
        this.mOnFlowTextItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener{
        void onFlowItemClick(String text);
    }
}
