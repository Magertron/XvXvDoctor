package cn.com.xxdoctor.chat.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cn.com.xxdoctor.R;
import cn.com.xxdoctor.chat.activity.FriendInfoActivity;
import cn.com.xxdoctor.chat.activity.MembersInChatActivity;
import cn.com.xxdoctor.chat.activity.PersonalActivity;
import cn.com.xxdoctor.chat.application.JGApplication;
import cn.com.xxdoctor.chat.utils.DialogCreator;
import cn.com.xxdoctor.chat.utils.ToastUtil;
import cn.com.xxdoctor.chat.utils.ViewHolder;


public class AllMembersAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private MembersInChatActivity mContext;
    private List<MembersInChatActivity.ItemModel> mMemberList = new ArrayList<>();
    private boolean mIsDeleteMode;
    private List<String> mSelectedList = new ArrayList<>();
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private Dialog mDialog;
    private Dialog mLoadingDialog;
    private boolean mIsCreator;
    private long mGroupId;
    private int mWidth;
    private GridView mGridView;
    private HorizontalScrollView mHorizontalScrollView;
    private CreateGroupAdapter mGroupAdapter;
    private Map<Long, String> map = new HashMap<>();
    private TextView selectNum;

    public AllMembersAdapter(MembersInChatActivity context, List<MembersInChatActivity.ItemModel> memberList, boolean isDeleteMode,
                             boolean isCreator, long groupId, int width, HorizontalScrollView horizontalView,
                             GridView imageSelectedGridView, CreateGroupAdapter groupAdapter) {
        this.mContext = context;
        this.mMemberList = memberList;
        this.mIsDeleteMode = isDeleteMode;
        this.mIsCreator = isCreator;
        this.mGroupId = groupId;
        this.mWidth = width;

        this.mGridView = imageSelectedGridView;
        this.mHorizontalScrollView = horizontalView;
        this.mGroupAdapter = groupAdapter;
        selectNum = (TextView) context.findViewById(R.id.tv_selNum);
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_all_member, null);
        }
        final ImageView icon = ViewHolder.get(convertView, R.id.icon_iv);
        TextView displayName = ViewHolder.get(convertView, R.id.name);
        final CheckBox checkBox = ViewHolder.get(convertView, R.id.check_box_cb);

        final MembersInChatActivity.ItemModel itemModel = mMemberList.get(position);
        final UserInfo userInfo = itemModel.data;
        final long userID = userInfo.getUserID();
        final String userName = userInfo.getUserName();
        if (mIsDeleteMode) {
            if (position > 0) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            mSelectedList.add(userInfo.getUserName());
                            map.put(userID, userName);
                            addAnimation(checkBox);
                            if (mGroupAdapter != null) {
                                mGroupAdapter.setContact(getSelectedUser());
                                notifySelectAreaDataSetChanged();
                            }
                        } else {
                            mSelectedList.remove(userInfo.getUserName());
                            map.remove(userID);
                            if (mGroupAdapter != null) {
                                mGroupAdapter.setContact(getSelectedUser());
                                notifySelectAreaDataSetChanged();
                            }
                        }

                        if (map.size() > 0) {
                            selectNum.setText("(" + map.size() + ")");
                        } else {
                            selectNum.setText("");
                        }
                    }
                });

                ArrayList<String> selectedUser = getSelectedUser();
                if (selectedUser.size() > 0) {
                    if (selectedUser.contains(userName)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                } else {
                    checkBox.setChecked(false);
                }
            } else {
                JMessageClient.getGroupInfo(mGroupId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            if (groupInfo.getGroupOwner().equals(userInfo.getUserName())) {
                                checkBox.setVisibility(View.INVISIBLE);
                            } else {
                                checkBox.setVisibility(View.VISIBLE);
                                mSelectedList.add(userInfo.getUserName());
                                map.remove(userID);
                            }
                        }
                    }
                });
            }

        } else {
            checkBox.setVisibility(View.GONE);
        }

        if (mGridView != null) {
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> selectedUser = getSelectedUser();
                    JMessageClient.getUserInfo(selectedUser.get(position), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            if (responseCode == 0) {
                                long uid = info.getUserID();
                                map.remove(uid);
                                if (mGroupAdapter != null) {
                                    mGroupAdapter.setContact(getSelectedUser());
                                    notifySelectAreaDataSetChanged();
                                }
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }

        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        icon.setImageBitmap(bitmap);
                    } else {
                        icon.setImageResource(R.drawable.jmui_head_icon);
                    }
                }
            });
        } else {
            icon.setImageResource(R.drawable.jmui_head_icon);
        }
        displayName.setText(itemModel.highlight);
        return convertView;
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                    ObjectAnimator.ofFloat(view, "scaleY", vaules));
            set.setDuration(150);
            set.start();
        }
    }

    public List<String> getSelectedList() {
        Log.d("AllMembersAdapter", "SelectedList: " + mSelectedList.toString());
        return mSelectedList;
    }

    public void setOnCheck(int position) {
        UserInfo userInfo = mMemberList.get(position).data;
        if (mSelectedList.contains(userInfo.getUserName())) {
//            View view = getView(position, null, null);
//            CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box_cb);
//            checkBox.setChecked(false);
            mSelectedList.remove(position);
            mSelectMap.delete(position);
        } else {
            mSelectedList.add(userInfo.getUserName());
            mSelectMap.put(position, true);
        }
        notifyDataSetChanged();
    }

    public void updateListView(List<MembersInChatActivity.ItemModel> filterList) {
        mSelectMap.clear();
        mMemberList = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfo userInfo = mMemberList.get(position).data;
        String userName = userInfo.getUserName();
        Intent intent = new Intent();
        if (userName.equals(JMessageClient.getMyInfo().getUserName())) {
            intent.setClass(mContext, PersonalActivity.class);
            mContext.startActivity(intent);
        } else {
            intent.setClass(mContext, FriendInfoActivity.class);
            intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
            intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
            intent.putExtra(JGApplication.GROUP_ID, mGroupId);
            mContext.startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (mIsCreator && !mIsDeleteMode && position != 0) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.jmui_cancel_btn:
                            mDialog.dismiss();
                            break;
                        case R.id.jmui_commit_btn:
                            mDialog.dismiss();
                            mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                                    mContext.getString(R.string.deleting_hint));
                            mLoadingDialog.show();
                            List<String> list = new ArrayList<String>();
                            list.add(mMemberList.get(position).data.getUserName());
                            JMessageClient.removeGroupMembers(mGroupId, list, new BasicCallback() {
                                @Override
                                public void gotResult(int status, String desc) {
                                    mLoadingDialog.dismiss();
                                    if (status == 0) {
                                        mContext.refreshMemberList();
                                    } else {
                                        ToastUtil.shortToast(mContext, "删除失败" + desc);
                                    }
                                }
                            });
                            break;

                    }
                }
            };
            mDialog = DialogCreator.createDeleteMemberDialog(mContext, listener, true);
            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            mDialog.show();
        }
        return true;
    }

    ArrayList<String> list = new ArrayList<>();

    public ArrayList<String> getSelectedUser() {
        list.clear();
        for (Long key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, mContext.getResources()
                .getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = mGridView.getLayoutParams();
        layoutParams.width = converViewWidth * getSelectedUser().size();
        layoutParams.height = converViewWidth;
        mGridView.setLayoutParams(layoutParams);
        mGridView.setNumColumns(getSelectedUser().size());

        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mHorizontalScrollView.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

}
