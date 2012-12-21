package com.dieptt.sleepingwell;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconView extends LinearLayout
{
        private ImageView mIcon;
        private TextView mFileName;

        public IconView(Context context, int iconResId, String fileName)
        {
                super(context);
                
                this.setOrientation(VERTICAL);
                this.setPadding(3, 3, 3, 3);
                this.setGravity(Gravity.CENTER_HORIZONTAL);
                
                mIcon = new ImageView(context);
                mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mIcon.setImageResource(iconResId);
                addView(mIcon, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                
                mFileName = new TextView(context);
                mFileName.setSingleLine();
                mFileName.setEllipsize(TextUtils.TruncateAt.END);
                mFileName.setText(fileName);
                addView(mFileName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        
        public void select()
        {
                mFileName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
        
        public void deselect()
        {
                mFileName.setEllipsize(TextUtils.TruncateAt.END);
        }
        
        public void setIconResId(int resId)
        {
                mIcon.setImageResource(resId);
        }

        public void setFileName(String fileName)
        {
                mFileName.setText(fileName);
        }
}