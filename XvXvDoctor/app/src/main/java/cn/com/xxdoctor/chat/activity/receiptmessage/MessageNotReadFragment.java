package cn.com.xxdoctor.chat.activity.receiptmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.jpush.im.android.api.model.UserInfo;
import cn.com.xxdoctor.R;
import cn.com.xxdoctor.chat.activity.FriendInfoActivity;
import cn.com.xxdoctor.chat.activity.GroupNotFriendActivity;
import cn.com.xxdoctor.chat.activity.fragment.BaseFragment;
import cn.com.xxdoctor.chat.application.JGApplication;

/**
 * Created by ${chenyn} on 2017/9/5.
 */

public class MessageNotReadFragment extends BaseFragment{
    private Activity mContext;
    private View mRootView;
    private ListView mReceipt_noRead;
    private NotReadAdapter mAdapter;
    private long mGroupId;

    public MessageNotReadFragment(long groupIdForReceipt) {
        this.mGroupId = groupIdForReceipt;
    }
    public MessageNotReadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();

        initView();
        initListViewClick();
    }

    private void initListViewClick() {
        mReceipt_noRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = (UserInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                if (userInfo.isFriend()) {
                    intent.setClass(mContext, FriendInfoActivity.class);
                }else {
                    intent.setClass(mContext, GroupNotFriendActivity.class);
                }
                intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
                intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
                intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_receipt_no_read,
                (ViewGroup) mContext.findViewById(R.id.main_view), false);
        mReceipt_noRead = (ListView) mRootView.findViewById(R.id.receipt_noRead);
        mAdapter = new NotReadAdapter(this);
        mReceipt_noRead.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }
}
