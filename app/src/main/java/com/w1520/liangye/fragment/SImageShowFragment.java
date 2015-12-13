package com.w1520.liangye.fragment;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.w1520.liangye.app.R;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.LazyFragment;
import com.w1520.liangye.utils.NetworkUtils;


/**
 * 在SimgFragment中给ViewPager加载网络图片
 * <p/>
 * Created by puruidong on 8/22/15.
 */
@SuppressLint("ValidFragment")
public class SImageShowFragment extends LazyFragment {

    private View view;
    /* 标志位，标志已经初始化完成。 */
    private boolean isPrepared;
    private ImageView imageView;
    private CustomProgressDialog dialog;
    private NetworkUtils network;

    public SImageShowFragment(){

    }

    /**
     * 当前图片的url地址,供{@link com.w1520.liangye.app.MainActivity}中使用.
     * 注意,此处仅仅使用{@link com.w1520.liangye.fragment.SimgFragment}获取的图片.
     */
    public static String currentURL = null;
    private OnImageViewClickListener onImageViewClickListener;//图片点击事件

    private static void setCurrentURL(String currentURL) {
        SImageShowFragment.currentURL = currentURL;
    }


    public SImageShowFragment(CustomProgressDialog dialog) {
        this.dialog = dialog;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    //
    /**
     * 请求当前类的来源标记:
     * {@link com.w1520.liangye.fragment.AboutFragment#vpabout} 的标记为0.
     * {@link com.w1520.liangye.fragment.SimgFragment#mViewPager}的标记为1
     */
    private int stype;

    /**
     * 外部参数传递过来的url参数.
     */
    private String imageUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.simg_main, null);
        }
        if (imageView == null) {
            imageView = (ImageView) view.findViewById(R.id.iv_simg);
        }
        network = NetworkUtils.getInstance(this.getActivity());
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        if (network.isOnline()) {
            network.getImageByImageRequest(imageUrl, new NetworkUtils.onImageLoaderListener() {
                @Override
                public void onSuccessImage(Bitmap bitmap) {
                    bitmap = network.comp(bitmap);
                    imageView.setImageBitmap(bitmap);
                    if(null!=onImageViewClickListener)
                    imageView.setOnClickListener(onImageViewClickListener);
                    currentURL = null;
                    if (stype == 1) {
                        setCurrentURL(imageUrl);
                    }
                    dialog.hide();
                }
            }, dialog);
        } else {
            dialog.hide();
        }
    }

    public void setOnImageViewClickListener(OnImageViewClickListener onImageViewClickListener) {
        this.onImageViewClickListener = onImageViewClickListener;
    }

    /*
    为图片提供点击事件
     */
    public interface OnImageViewClickListener extends View.OnClickListener {
        @Override
        void onClick(View v);
    }
}