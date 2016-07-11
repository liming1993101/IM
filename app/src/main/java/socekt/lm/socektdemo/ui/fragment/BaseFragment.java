package socekt.lm.socektdemo.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

import com.way.chat.common.tran.bean.TranObject;

import socekt.lm.socektdemo.utils.Constants;

/**
 * Created by Administrator on 2016/6/20.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 广播接收者，接收GetMsgService发送过来的消息
     */


    /**
     * 抽象方法，用于子类处理消息，
     *
     * @param msg
     *            传递给子类的消息对象
     */
    public abstract void getMessage(TranObject msg);

    /**
     * 子类直接调用这个方法关闭应用
     */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
