package socekt.lm.socektdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.entity.ChatMsgEntity;

/**
 * Created by Administrator on 2016/6/21.
 */
public class ChatMsgAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMsgEntity>listItems;
    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
    }

    private static final int ITEMCOUNT = 2;// 消息类型的总数
    public ChatMsgAdapter(Context context, List<ChatMsgEntity> chatMsgList) {
        this.context=context;
        listItems=chatMsgList;

    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity=listItems.get(position);
        if (entity.getMsgType())
        {
            return IMsgViewType.IMVT_COM_MSG;
        }
        else{
            return IMsgViewType.IMVT_TO_MSG;
        }

    }

    @Override
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ChatMsgEntity entity = listItems.get(position);
        boolean isComMsg = entity.getMsgType();
        ViewHolder viewHolder = null;
        if (view==null)
        {
            if (isComMsg)
            {
                view=LayoutInflater.from(context).inflate(R.layout.chatting_item_msg_text_left,null);
            }
            else
            {
                view=LayoutInflater.from(context).inflate(R.layout.chatting_item_msg_text_right,null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) view
                    .findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) view
                    .findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) view
                    .findViewById(R.id.tv_chatcontent);
            viewHolder.icon = (ImageView) view
                    .findViewById(R.id.iv_userhead);
            viewHolder.isComMsg = isComMsg;

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.tvSendTime.setText(entity.getDate());
        viewHolder.tvUserName.setText(entity.getName());
        viewHolder.tvContent.setText(entity.getMessage());
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        return view;
    }
    public class  ViewHolder
    {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public ImageView icon;
        public boolean isComMsg = true;

    }
}
