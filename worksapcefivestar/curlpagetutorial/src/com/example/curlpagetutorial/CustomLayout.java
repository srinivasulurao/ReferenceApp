package com.example.curlpagetutorial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomLayout extends RelativeLayout {
    
    public CustomLayout(Context context) {
        super(context);
        
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        try {
            // Init game
            getPageContent();            
        } catch (Exception e) {
            // bug
            e.printStackTrace();
        }
    }

    //
    abstract protected void getPageContent();

}
