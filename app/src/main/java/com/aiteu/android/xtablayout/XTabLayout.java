package com.aiteu.android.xtablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;

public class XTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private LinearLayout mTabsContainer;
    private Context mContext;
    private int mTabCount;
    private ViewPager mViewPager;
    private ArrayList<String> mTitles;
    private Rect mIndicatorRect;
    private Rect mTabRect;
    private GradientDrawable mIndicatorDrawable;
    private Paint mTrianglePaint;
    private Path mTrianglePath;
    private float mTabPadding;
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    private int mTextSelectColor, mTextUnSelectColor;
    private float mSelectTextSize, mTextSize;
    private boolean mTextAllCaps;
    private boolean mTextBold;
    private boolean mSnapOnTabClick;
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;
    private int mIndicatorStyle;
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorWidthEqualTitle;
    private int mLastScrollX;
    private float margin;
    private Paint mTextPaint;

    private OnTabSelectListener mListener;

    public XTabLayout(Context context) {
        this(context, null, 0);
    }

    public XTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIndicatorRect = new Rect();
        this.mTabRect = new Rect();
        this.mIndicatorDrawable = new GradientDrawable();
        this.mTrianglePaint = new Paint(1);
        this.mTrianglePath = new Path();
        this.mIndicatorStyle = STYLE_NORMAL;
        this.mTextPaint = new Paint(1);
        this.setFillViewport(true);
        this.setWillNotDraw(false);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.mContext = context;
        this.mTabsContainer = new LinearLayout(context);
        this.addView(this.mTabsContainer);
        this.obtainAttributes(context, attrs);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XTabLayout);
        this.mTextSize = ta.getDimension(R.styleable.XTabLayout_xtl_textSize, (float) this.sp2px(14.0F));
        this.mSelectTextSize = ta.getDimension(R.styleable.XTabLayout_xtl_textSelectSize, (float) this.sp2px(16.0F));
        this.mTextSelectColor = ta.getColor(R.styleable.XTabLayout_xtl_textSelectColor, Color.parseColor("#ffffff"));
        this.mTextUnSelectColor = ta.getColor(R.styleable.XTabLayout_xtl_textUnSelectColor, Color.parseColor("#AAffffff"));
        this.mTextBold = ta.getBoolean(R.styleable.XTabLayout_xtl_textBold, false);
        this.mTextAllCaps = ta.getBoolean(R.styleable.XTabLayout_xtl_textAllCaps, false);
        this.mTabPadding = ta.getDimension(R.styleable.XTabLayout_xtl_tabPadding, (float) this.dp2px(20.0F));
        this.mIndicatorStyle = ta.getInt(R.styleable.XTabLayout_xtl_indicatorStyle, STYLE_NORMAL);
        this.mIndicatorColor = ta.getColor(R.styleable.XTabLayout_xtl_indicatorColor, Color.parseColor(this.mIndicatorStyle == STYLE_BLOCK ? "#4B6A87" : "#ffffff"));
        this.mIndicatorHeight = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorHeight, (float) this.dp2px(this.mIndicatorStyle == STYLE_TRIANGLE ? 4.0F : (float) (this.mIndicatorStyle == STYLE_BLOCK ? -1 : 2)));
        this.mIndicatorWidth = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorWidth, (float) this.dp2px(this.mIndicatorStyle == STYLE_TRIANGLE ? 10.0F : -1.0F));
        this.mIndicatorCornerRadius = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorCornerRadius, (float) this.dp2px(this.mIndicatorStyle == STYLE_BLOCK ? -1.0F : 0.0F));
        this.mIndicatorMarginLeft = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorMarginLeft, (float) this.dp2px(0.0F));
        this.mIndicatorMarginTop = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorMarginTop, (float) this.dp2px(this.mIndicatorStyle == STYLE_BLOCK ? 7.0F : 0.0F));
        this.mIndicatorMarginRight = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorMarginRight, (float) this.dp2px(0.0F));
        this.mIndicatorMarginBottom = ta.getDimension(R.styleable.XTabLayout_xtl_indicatorMarginBottom, (float) this.dp2px(this.mIndicatorStyle == STYLE_BLOCK ? 7.0F : 0.0F));
        this.mIndicatorGravity = ta.getInt(R.styleable.XTabLayout_xtl_indicatorGravity, 0);
        this.mIndicatorWidthEqualTitle = ta.getBoolean(R.styleable.XTabLayout_xtl_indicatorWidthEqualTitle, false);
        ta.recycle();
    }

    public void setTitles(String[] titles) {
        this.mTitles = new ArrayList();
        Collections.addAll(this.mTitles, titles);
        notifyDataSetChanged();
    }

    public void setViewPager(ViewPager vp) {
        if (vp != null && vp.getAdapter() != null) {
            this.mViewPager = vp;
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
            this.notifyDataSetChanged();
        } else {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }
    }

    public void notifyDataSetChanged() {
        this.mTabsContainer.removeAllViews();
        this.mTabCount = this.mTitles == null ? this.mViewPager.getAdapter().getCount() : this.mTitles.size();

        for (int i = 0; i < this.mTabCount; ++i) {
            View tabView = View.inflate(this.mContext, R.layout.layout_xtab, null);
            CharSequence pageTitle = this.mTitles == null ? this.mViewPager.getAdapter().getPageTitle(i) : this.mTitles.get(i);
            this.addTab(i, pageTitle.toString(), tabView);
        }

        this.updateTabStyles();
    }

    private void addTab(int position, String title, View tabView) {
        TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
        if (tv_tab_title != null && title != null) {
            tv_tab_title.setText(title);
        }

        tabView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int position = mTabsContainer.indexOfChild(v);
                if (position != -1) {
                    if (mViewPager != null) {
                        if (mViewPager.getCurrentItem() != position) {
                            if (mSnapOnTabClick) {
                                mViewPager.setCurrentItem(position, false);
                            } else {
                                mViewPager.setCurrentItem(position);
                            }

                            if (mListener != null) {
                                mListener.onTabSelect(position);
                            }
                        } else if (mListener != null) {
                            mListener.onTabReselect(position);
                        }
                    } else {
                        if (mCurrentTab != position) {
                            mCurrentTab = position;
                            updateTabSelection(mCurrentTab);
                            scrollToCurrentTab();
                            if (mListener != null) {
                                mListener.onTabSelect(position);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onTabReselect(position);
                            }
                        }
                    }
                }
            }
        });

        LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(-2, -1);
        this.mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.mTabCount; ++i) {
            View v = this.mTabsContainer.getChildAt(i);
            TextView tv_tab_title = v.findViewById(R.id.tv_tab_title);
            boolean isSelect = i == mCurrentTab;
            if (tv_tab_title != null) {
                tv_tab_title.setTextColor(isSelect ? this.mTextSelectColor : this.mTextUnSelectColor);
                tv_tab_title.setTextSize(0, isSelect ? this.mSelectTextSize : this.mTextSize);
                tv_tab_title.setPadding((int) this.mTabPadding, 0, (int) this.mTabPadding, 0);
                if (this.mTextAllCaps) {
                    tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
                }
                tv_tab_title.getPaint().setFakeBoldText(this.mTextBold);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        this.scrollToCurrentTab();
    }

    @Override
    public void onPageSelected(int position) {
        this.updateTabSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void scrollToCurrentTab() {
        if (this.mTabCount > 0) {
            int offset = (int) (this.mCurrentPositionOffset * (float) this.mTabsContainer.getChildAt(this.mCurrentTab).getWidth());
            int newScrollX = this.mTabsContainer.getChildAt(this.mCurrentTab).getLeft() + offset;
            if (this.mCurrentTab > 0 || offset > 0) {
                newScrollX -= this.getWidth() / 2 - this.getPaddingLeft();
                this.calcIndicatorRect();
                newScrollX += (this.mTabRect.right - this.mTabRect.left) / 2;
            }

            if (newScrollX != this.mLastScrollX) {
                this.mLastScrollX = newScrollX;
                this.scrollTo(newScrollX, 0);
            }
        }
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < this.mTabCount; ++i) {
            View tabView = this.mTabsContainer.getChildAt(i);
            boolean isSelect = i == position;
            TextView tab_title = tabView.findViewById(R.id.tv_tab_title);
            if (tab_title != null) {
                tab_title.setTextColor(isSelect ? this.mTextSelectColor : this.mTextUnSelectColor);
                tab_title.setTextSize(0, isSelect ? this.mSelectTextSize : this.mTextSize);
                tab_title.getPaint().setFakeBoldText(this.mTextBold);
            }
        }
    }

    private void calcIndicatorRect() {
        View currentTabView = this.mTabsContainer.getChildAt(this.mCurrentTab);
        float left = (float) currentTabView.getLeft();
        float right = (float) currentTabView.getRight();
        float nextTabLeft;
        if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
            TextView tab_title = currentTabView.findViewById(R.id.tv_tab_title);
            this.mTextPaint.setTextSize(this.mTextSize);
            nextTabLeft = this.mTextPaint.measureText(tab_title.getText().toString());
            this.margin = (right - left - nextTabLeft) / 2.0F;
        }

        if (this.mCurrentTab < this.mTabCount - 1) {
            View nextTabView = this.mTabsContainer.getChildAt(this.mCurrentTab + 1);
            nextTabLeft = (float) nextTabView.getLeft();
            float nextTabRight = (float) nextTabView.getRight();
            left += this.mCurrentPositionOffset * (nextTabLeft - left);
            right += this.mCurrentPositionOffset * (nextTabRight - right);
            if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
                TextView next_tab_title = nextTabView.findViewById(R.id.tv_tab_title);
                this.mTextPaint.setTextSize(this.mTextSize);
                float nextTextWidth = this.mTextPaint.measureText(next_tab_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2.0F;
                this.margin += this.mCurrentPositionOffset * (nextMargin - this.margin);
            }
        }

        this.mIndicatorRect.left = (int) left;
        this.mIndicatorRect.right = (int) right;
        if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
            this.mIndicatorRect.left = (int) (left + this.margin - 1.0F);
            this.mIndicatorRect.right = (int) (right - this.margin - 1.0F);
        }

        this.mTabRect.left = (int) left;
        this.mTabRect.right = (int) right;
        if (this.mIndicatorWidth >= 0.0F) {
            float indicatorLeft = (float) currentTabView.getLeft() + ((float) currentTabView.getWidth() - this.mIndicatorWidth) / 2.0F;
            if (this.mCurrentTab < this.mTabCount - 1) {
                View nextTab = this.mTabsContainer.getChildAt(this.mCurrentTab + 1);
                indicatorLeft += this.mCurrentPositionOffset * (float) (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
            }

            this.mIndicatorRect.left = (int) indicatorLeft;
            this.mIndicatorRect.right = (int) ((float) this.mIndicatorRect.left + this.mIndicatorWidth);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.isInEditMode() && this.mTabCount > 0) {
            int height = this.getHeight();
            int paddingLeft = this.getPaddingLeft();
            this.calcIndicatorRect();
            if (this.mIndicatorStyle == STYLE_TRIANGLE) {
                if (this.mIndicatorHeight > 0.0F) {
                    this.mTrianglePaint.setColor(this.mIndicatorColor);
                    this.mTrianglePath.reset();
                    this.mTrianglePath.moveTo((float) (paddingLeft + this.mIndicatorRect.left), (float) height);
                    this.mTrianglePath.lineTo((float) (paddingLeft + this.mIndicatorRect.left / 2 + this.mIndicatorRect.right / 2), (float) height - this.mIndicatorHeight);
                    this.mTrianglePath.lineTo((float) (paddingLeft + this.mIndicatorRect.right), (float) height);
                    this.mTrianglePath.close();
                    canvas.drawPath(this.mTrianglePath, this.mTrianglePaint);
                }
            } else if (this.mIndicatorStyle == STYLE_BLOCK) {
                if (this.mIndicatorHeight < 0.0F) {
                    this.mIndicatorHeight = (float) height - this.mIndicatorMarginTop - this.mIndicatorMarginBottom;
                }

                if (this.mIndicatorHeight > 0.0F) {
                    if (this.mIndicatorCornerRadius < 0.0F || this.mIndicatorCornerRadius > this.mIndicatorHeight / 2.0F) {
                        this.mIndicatorCornerRadius = this.mIndicatorHeight / 2.0F;
                    }

                    this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                    this.mIndicatorDrawable.setBounds(paddingLeft + (int) this.mIndicatorMarginLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (int) ((float) (paddingLeft + this.mIndicatorRect.right) - this.mIndicatorMarginRight), (int) (this.mIndicatorMarginTop + this.mIndicatorHeight));
                    this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                    this.mIndicatorDrawable.draw(canvas);
                }
            } else if (this.mIndicatorHeight > 0.0F) {
                this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                if (this.mIndicatorGravity == 0) {
                    this.mIndicatorDrawable.setBounds(paddingLeft + (int) this.mIndicatorMarginLeft + this.mIndicatorRect.left, height - (int) this.mIndicatorHeight - (int) this.mIndicatorMarginBottom, paddingLeft + this.mIndicatorRect.right - (int) this.mIndicatorMarginRight, height - (int) this.mIndicatorMarginBottom);
                } else {
                    this.mIndicatorDrawable.setBounds(paddingLeft + (int) this.mIndicatorMarginLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, paddingLeft + this.mIndicatorRect.right - (int) this.mIndicatorMarginRight, (int) this.mIndicatorHeight + (int) this.mIndicatorMarginTop);
                }

                this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                this.mIndicatorDrawable.draw(canvas);
            }
        }
    }

    public void setCurrentTab(int currentTab) {
        this.mCurrentTab = currentTab;
        if(this.mViewPager != null) {
            this.mViewPager.setCurrentItem(currentTab);
        }
    }

    public void setCurrentTab(int currentTab, boolean smoothScroll) {
        this.mCurrentTab = currentTab;
        if(this.mViewPager != null) {
            this.mViewPager.setCurrentItem(currentTab, smoothScroll);
        }
    }

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        this.invalidate();
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = (float) this.dp2px(tabPadding);
        this.updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        this.invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = (float) this.dp2px(indicatorHeight);
        this.invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = (float) this.dp2px(indicatorWidth);
        this.invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = (float) this.dp2px(indicatorCornerRadius);
        this.invalidate();
    }

    public void setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        this.invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop, float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicatorMarginLeft = (float) this.dp2px(indicatorMarginLeft);
        this.mIndicatorMarginTop = (float) this.dp2px(indicatorMarginTop);
        this.mIndicatorMarginRight = (float) this.dp2px(indicatorMarginRight);
        this.mIndicatorMarginBottom = (float) this.dp2px(indicatorMarginBottom);
        this.invalidate();
    }

    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
        this.invalidate();
    }

    public void setTextSize(float textsize) {
        this.mTextSize = (float) this.sp2px(textsize);
        this.updateTabStyles();
    }

    public void setSelectTextSize(float textsize) {
        this.mSelectTextSize = (float) this.sp2px(textsize);
        this.updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        this.updateTabStyles();
    }

    public void setTextUnSelectColor(int textUnselectColor) {
        this.mTextUnSelectColor = textUnselectColor;
        this.updateTabStyles();
    }

    public void setTextBold(boolean textBold) {
        this.mTextBold = textBold;
        this.updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        this.updateTabStyles();
    }

    public void setSnapOnTabClick(boolean snapOnTabClick) {
        this.mSnapOnTabClick = snapOnTabClick;
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    public int getTabCount() {
        return this.mTabCount;
    }

    public int getCurrentTab() {
        return this.mCurrentTab;
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", this.mCurrentTab);
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (this.mCurrentTab != 0 && this.mTabsContainer.getChildCount() > 0) {
                this.updateTabSelection(this.mCurrentTab);
                this.scrollToCurrentTab();
            }
        }

        super.onRestoreInstanceState(state);
    }

    protected int dp2px(float dp) {
        float scale = this.mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    protected int sp2px(float sp) {
        float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5F);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int maxWidth = getMeasuredTabWidth();
        if (maxWidth <= width) {
            maxWidth = width;
            resetTabItemSpaceEqual();
        }
        mTabsContainer.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private int getMeasuredTabWidth() {
        int maxWidth = getPaddingLeft() + getPaddingRight();
        int tabCount = mTabsContainer.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mTabsContainer.getChildAt(i);
            maxWidth += child.getMeasuredWidth();
        }
        return maxWidth;
    }

    private void resetTabItemSpaceEqual() {
        int tabCount = mTabsContainer.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mTabsContainer.getChildAt(i);
            LinearLayout.LayoutParams childParams = (LinearLayout.LayoutParams) child.getLayoutParams();
            childParams.weight = 1.0f;
            child.setLayoutParams(childParams);
        }
    }
}
