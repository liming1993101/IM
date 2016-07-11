package socekt.lm.socektdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListAdapter;

import com.way.chat.common.bean.User;

import org.w3c.dom.Text;

import java.util.List;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.ui.ChatActivity;
import socekt.lm.socektdemo.utils.GroupFriend;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ContactAdapter implements ExpandableListAdapter {
    private Context context;
    private List<GroupFriend>group;
    public ContactAdapter(Context context, List<GroupFriend> group) {
        this.context=context;
        this.group=group;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return group.get(i).getChildSize();
    }

    @Override
    public Object getGroup(int i) {
        return group.get(i).getGroupName().toString();
    }

    @Override
    public Object getChild(int i, int i1) {
        return group.get(i).getChild(i1).getId();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder1 viewHolder;
        if (view==null)
        {
           viewHolder=new ViewHolder1();
            view= LayoutInflater.from(context).inflate(R.layout.layout_group,null);
            viewHolder.groupFlag= (ImageView) view.findViewById(R.id.group_flag);
            viewHolder.groupName= (TextView) view.findViewById(R.id.group_name_home);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder1) view.getTag();
        }
        viewHolder.groupName.setText(""+group.get(i).getGroupName());
        if (b==true)
        {
            viewHolder.groupFlag.setBackgroundResource(R.mipmap.group_down1);
        }
        else
        {
            viewHolder.groupFlag.setBackgroundResource(R.mipmap.group_up1);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder2 viewHolder2;
        if (view==null)
        {
            viewHolder2=new ViewHolder2();
            view=LayoutInflater.from(context).inflate(R.layout.layout_group_child,null);
            viewHolder2.headIcon= (ImageView) view.findViewById(R.id.head_icon);
            viewHolder2.name= (TextView) view.findViewById(R.id.user_name);
            viewHolder2.onlineState= (TextView) view.findViewById(R.id.online_state);
            view.setTag(viewHolder2);
        }
        else
        {
            viewHolder2= (ViewHolder2) view.getTag();
        }
        if (group.get(i).getChild(i1).getIsOnline()==0)
        {
            viewHolder2.onlineState.setText("[离线]");
        }
        else
        {
            viewHolder2.onlineState.setText("[在线]");
        }
        final String name=group.get(i).getChild(i1).getName();
        final int id=group.get(i).getChild(i1).getId();
        final int img=group.get(i).getChild(i1).getImg();
        viewHolder2.name.setText(""+group.get(i).getChild(i1).getName());
        viewHolder2.headIcon.setBackgroundResource(R.mipmap.ic_launcher);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 下面是切换到聊天界面处理
                User u = new User();
                u.setName(name);
                u.setId(id);
                u.setImg(img);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user", u);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    public class ViewHolder1
    {

        ImageView groupFlag;
        TextView groupName;
    }
    public class ViewHolder2
    {
        ImageView headIcon;
        TextView name;
        TextView onlineState;

    }

}
