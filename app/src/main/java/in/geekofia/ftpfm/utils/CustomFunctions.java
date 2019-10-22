package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import in.geekofia.ftpfm.R;

public class CustomFunctions {

    // Custom getDRawable function
    public static Drawable getDrawable(Context context, int resDrawable) {
        return ContextCompat.getDrawable(context, resDrawable);
    }

    // Custom getString function
    public static String getString(Context context, int resString) {
        return context.getResources().getString(resString);
    }

    // Toggle relative layout specific child properties
    public static void toggleProp(View view, int prop, boolean value){

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if(value){
            layoutParams.addRule(prop);
        }else {
            layoutParams.removeRule(prop);
        }

        view.setLayoutParams(layoutParams);
    }
}
