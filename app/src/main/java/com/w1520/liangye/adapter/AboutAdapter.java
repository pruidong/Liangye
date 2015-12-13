package com.w1520.liangye.adapter;

import android.content.Context;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.AboutInfo;
import com.w1520.liangye.utils.CommonAdapter;
import com.w1520.liangye.utils.ViewHolders;

import java.util.List;

/**
 * About 的Adapter,使用了通用Adapter.
 *
 * Created by puruidong on 8/23/15.
 */
public class AboutAdapter extends CommonAdapter<AboutInfo> {

    public AboutAdapter(Context context, List<AboutInfo> datas) {
        super(context, datas, R.layout.about_lv_item);
    }

    /**
     * view
     *
     * @param holder
     * @param aboutInfo
     */
    @Override
    public void convert(ViewHolders holder, AboutInfo aboutInfo) {
        holder.setImageResource(R.id.about_item_icon, aboutInfo.getIcon()).setText(R.id.about_item_title, aboutInfo.getTitle());

    }
}
