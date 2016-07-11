package socekt.lm.socektdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import socekt.lm.socektdemo.R;

/**
 * Created by Administrator on 2016/6/15.
 */
public class DialogFactory {
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    public void opProgressDilog(Context context)
    {
         builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);
        builder.setView(view);
        builder.create();
        alertDialog= builder.show();
    }
    public void colseProgerssDilog()
    {
        alertDialog.dismiss();
    }
}
