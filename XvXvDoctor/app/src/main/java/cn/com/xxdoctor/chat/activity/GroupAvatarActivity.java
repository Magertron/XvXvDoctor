package cn.com.xxdoctor.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.com.xxdoctor.R;
import cn.com.xxdoctor.chat.utils.photochoose.ChoosePhoto;
import cn.com.xxdoctor.chat.utils.photochoose.PhotoUtils;

/**
 * Created by ${chenyn} on 2017/9/18.
 */

public class GroupAvatarActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_save)
    ImageView ivSave;
    @Bind(R.id.iv_groupAvatar)
    ImageView ivGroupAvatar;

    ChoosePhoto mChoosePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_avatar);
        ButterKnife.bind(this);

        llBack.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        if (getIntent().getStringExtra("groupAvatar") != null) {
            ivGroupAvatar.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("groupAvatar")));
        } else {
            JMessageClient.getGroupInfo(getIntent().getLongExtra("groupID", 0), new GetGroupInfoCallback() {
                @Override
                public void gotResult(int i, String s, GroupInfo groupInfo) {
                    if (i == 0) {
                        groupInfo.getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int i, String s, Bitmap bitmap) {
                                if (i == 0) {
                                    ivGroupAvatar.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.iv_save:
                mChoosePhoto = new ChoosePhoto();
                mChoosePhoto.setGroupAvatarChangeListener(GroupAvatarActivity.this, getIntent().getLongExtra("groupID", 0));
                mChoosePhoto.showPhotoDialog(GroupAvatarActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                mChoosePhoto.photoUtils.onActivityResult(GroupAvatarActivity.this, requestCode, resultCode, data);
                break;
        }
    }
}
