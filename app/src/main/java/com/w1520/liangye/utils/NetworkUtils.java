package com.w1520.liangye.utils;


import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.w1520.liangye.app.R;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>工具类</p>
 * <p/>
 * <p>此类使用Volley实现网络操作.</p>
 * <p/>
 * <p>当前类封装了:文字,图片的加载,Toast的各种实现,以及</p>
 * <p>保存图片信息到本地,复制文本数据,转换ImageView等操作.</p>About
 * <p/>
 * <p>这个类不可以被继承或重写,并且使用单例模式保证无论任何时候,</p>
 * <p>内存中仅存在一个当前类的实例.</p>
 * <p/>
 * <p><strong>注意:当前类的加载数据,不适合下载,或者需要操作大量数据.</strong></p>
 * <p>
 * <p>
 * Created by puruidong on 8/14/15.
 */
public final class NetworkUtils {

    //网络请求队列实例.
    private static RequestQueue queue;
    //上下文实例.
    private Context context;
    //当前类实例.默认为null
    private static NetworkUtils network = null;


    /**
     * 创建一个实例时,内部初始化.
     * 外部无需关心此构造函数的实现.
     *
     * @param context
     */
    private NetworkUtils(Context context) {
        this.context = context.getApplicationContext();
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    /**
     * 获取一个当前类的实例.
     * 注意:当前类的实例仅能通过此方式获得.
     * 以保证在内存中仅存在唯一一个当前类的实例.
     *
     * @param context 上下文实例.
     * @return 当前类的实例.
     */
    public static NetworkUtils getInstance(Context context) {
        if (network == null) {
            network = new NetworkUtils(context);
        }
        return network;
    }

    /**
     * 检查网络连接是否可用.
     * 不区分是3G还是WIFI.
     *
     * @return 若网络可用返回true, 反之返回false.
     */
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /*-------------------------------------[GET]从网络获取文字数据---------------------------------------------*/

    /**
     * 获取文本数据.
     * <p>
     * 若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.
     *
     * @param url          获取数据的url,此方法不检测url的正确性.
     * @param volleyResult 数据加载成功后的回调接口,用于传递数据
     * @param dialog       数据加载提示框.
     */
    public void getTextData(String url, final VolleyResultData volleyResult, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if(null!=dialog) {
                dialog.hide();
            }
        } else {
            StringRequest strArray = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    volleyResult.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("啊哦!~暂时加载失败了,要不稍候在试试?..呜..", Toast.LENGTH_SHORT);
                }
            }
            );
            queue.add(strArray);
        }
    }

    /*-------------------------------------[GET]从网络获取文字数据---------------------------------------------*/

    /*-------------------------------------[POST]提交请求,从网络获取数据---------------------------------------------*/

    /**
     * POST方式提交普通参数数据.
     * --------------------------------------
     * 若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.
     *
     * @param url          获取数据的url,此方法不检测url的正确性.
     * @param params       POST方式提交数据的参数.* :@see {@link android.support.v4.util.ArrayMap}
     * @param volleyResult 数据加载成功后的回调接口,用于传递数据
     * @param dialog       数据加载提示框.
     */
    public void postData(String url, final Map<String, String> params, final VolleyResultData volleyResult, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if(null!=dialog) {
                dialog.hide();
            }
        } else {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            volleyResult.onSuccess(response);
                        }
                    }, null) {
                @Override
                protected Map<String, String> getParams() {
                    //在这里设置需要post的参数
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }


    /**
     * 使用Volley请求文本数据,
     * 实现此接口自行处理相关String数据.
     * 即可获取数据.
     */
    public interface VolleyResultData {
        void onSuccess(String response);
    }


    /*-------------------------------------[POST]提交请求,从网络获取文字数据---------------------------------------------*/



    /*-------------------------------------从网络获取图片数据---------------------------------------------*/


    /**
     * 通过ImageRequest加载并设置网络图片.
     * 注意:方法不对url的有效性进行检测.
     * 若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.
     * <p>
     * 此方法使用了默认实现的缓存.
     *
     * @param url       图片url
     * @param view      图片控件所在的View对象
     * @param imgViewId ImageView或其它图片展示控件的Id
     * @param dialog    图片加载提示框对象.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#getImageByImageRequest(String, onImageLoaderListener, CustomProgressDialog)}
     */
    public void getImageByImageRequest(String url, final View view, final int imgViewId, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if(null!=dialog) {
                dialog.hide();
            }
        } else {

            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    //显示加载成功的图片.
                    ImageView img = (ImageView) view.findViewById(imgViewId);
                    img.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //显示加载失败的图片.
                    showToast("啊哦!~暂时加载失败了,要不稍候在试试?..呜..", Toast.LENGTH_SHORT);
                    ImageView img = (ImageView) view.findViewById(imgViewId);
                    img.setImageResource(R.mipmap.image_loader_error);
                }
            });
            queue.add(request);
        }
    }


    /**
     * 通过ImageRequest加载并传递值给回调接口.
     * 若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.
     * 注意:方法不对url的有效性进行检测.
     * <p>
     * 此方法使用了默认实现的缓存.
     *
     * @param url            图片url地址.
     * @param loaderlistener 接收Bitmap的回调接口.若请求响应为空,回调接口返回空.
     * @param dialog         图片加载提示框对象.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#getImageByImageRequest(String, View, int, CustomProgressDialog)}
     */
    public void getImageByImageRequest(String url, final onImageLoaderListener loaderlistener, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if (null != dialog) {
                if(null!=dialog) {
                    dialog.hide();
                }
            }
        } else {
            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    //显示加载成功的图片.
                    loaderlistener.onSuccessImage(response);
                }
            }, 0, 0, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //显示加载失败的图片.
                    //showToast("啊哦!~暂时加载失败了,要不稍候在试试?..呜..", Toast.LENGTH_SHORT);
                    //error.printStackTrace();
                }
            });
            queue.add(request);
        }
    }


    /**
     * 通过NetworkImageView加载
     * 并设置网络图片,若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.此方法会给予一个默认图片及
     * 一个加载失败时候的图片.注意:方法不对url的有效性进行检测.
     * <p>
     * 注意:此方法已经默认实现了一个图片缓存.具体可参考:
     * {@link com.w1520.liangye.utils.BitmapCache}
     *
     * @param url              图片url地址.
     * @param view             图片控件所在的View对象activitd
     * @param netWorkImgViewId ImageView或其它图片展示控件的Id
     * @param dialog           图片加载提示框对象.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#getImageByImageLoader(String, View, int, CustomProgressDialog)}
     */
    public void getImageByNetworkImageView(String url, final View view, final int netWorkImgViewId, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if(null!=dialog) {
                dialog.hide();
            }
            return ;
        }
        ImageLoader loader = new ImageLoader(queue, new BitmapCache());
        NetworkImageView networkImageView = (NetworkImageView) view.findViewById(netWorkImgViewId);
        networkImageView.setDefaultImageResId(R.mipmap.image_loading);
        networkImageView.setErrorImageResId(R.mipmap.image_loader_error);
        networkImageView.setImageUrl(url, loader);
    }

    /**
     * 通过ImageLoader加载并设置网络图片
     * 注意:方法不对url的有效性进行检测.
     * 若无法访问网络,将关闭加载提示框,
     * 并给出无法访问网络的提示.
     * <p>
     * 此方法会设置一个默认加载图片,及一个加载失败时
     * 显示的图片.
     * <p>
     * 注意:此方法已经默认实现了一个图片缓存.具体可参考:
     * {@link com.w1520.liangye.utils.BitmapCache}
     *
     * @param url       图片url
     * @param view      图片控件所在的View对象
     * @param imgViewId ImageView或其它图片展示控件的Id
     * @param dialog    图片加载提示框对象.
     * @return 返回一个响应图片的资源.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#getImageByNetworkImageView(String, View, int, CustomProgressDialog)}
     */
    public View getImageByImageLoader(String url, View view, int imgViewId, CustomProgressDialog dialog) {
        if (!isOnline()) {
            showToast("目前无法访问网络，请稍候在试", Toast.LENGTH_SHORT);
            if(null!=dialog) {
                dialog.hide();
            }
        }
        ImageLoader loader = new ImageLoader(queue, new BitmapCache());
        ImageView imageView = (ImageView) view.findViewById(imgViewId);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.mipmap.image_loading, R.mipmap.image_loader_error);
        loader.get(url, listener);
        return imageView;
    }


    /**
     * 获取网络响应的图片实现此接口,重写接口中的方法即可获得该图片资源.
     * 若网络数据返回为null,则接口中的方法将返回null.
     */
    public interface onImageLoaderListener {
        void onSuccessImage(Bitmap bitmap);
    }

    /*-------------------------------------从网络获取图片数据---------------------------------------------*/


    /**
     * 快速输出Toast,默认显示时长为 {@link  android.widget.Toast#LENGTH_SHORT}.
     * 不支持布局自定义和显示时长自定义.
     *
     * @param message 要显示的信息.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(View view, String message, int duration)}
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(String message, int duration)}
     */
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 显示Toast,可以使用自定义的布局,自定义显示时长.
     *
     * @param view     一个针对Toast的自定义布局.
     * @param message  要显示的信息.
     * @param duration 显示时长,请使用Toast中的常量进行设置.
     *                 可选值:{@link android.widget.Toast#LENGTH_SHORT}
     *                 {@link android.widget.Toast#LENGTH_LONG}
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(String message, int duration)}
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(String message)}
     */
    public void showToast(View view, String message, int duration) {
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setText(message);
        toast.show();
    }

    /**
     * 显示带有图片,居中的Toast.
     *
     * @param message  要显示的信息.
     * @param duration 显示时长,请使用Toast中的常量进行设置.
     *                 可选值:{@link android.widget.Toast#LENGTH_SHORT}
     *                 {@link android.widget.Toast#LENGTH_LONG}
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(View view, String message, int duration)}
     * @see {@link com.w1520.liangye.utils.NetworkUtils#showToast(String message)}
     */
    public void showToast(String message, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View toast_view = inflater.inflate(R.layout.custom_toast_center, null);
        ((TextView) toast_view.findViewById(R.id.toast_message)).setText(message);
        Toast toast = new Toast(context);
        toast.setView(toast_view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.show();
    }


    /**
     * 判断进程是否处于正在运行状态.
     *
     * @return 正在运行返回true, 反之返回false.
     */
    public boolean isProessRunning(String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }


    /**
     * 将一张图片(Bitmap资源)设置为手机桌面壁纸.
     * <p>
     * 需要权限:{@code
     * <uses-permission android:name = "android.permission.SET_WALLPAPER"/>}
     *
     * @param bitmap 要设置为桌面的资源.
     * @throws IOException 资源不存在或资源损坏导致的IO异常.
     */
    public void setWallPaper(Bitmap bitmap) throws IOException {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        wallpaperManager.setBitmap(bitmap);
    }

    /**
     * 获取SD卡或者内置存储空间可以保存资源的路径.
     * <em>此处未实现对存储空间是否充足进行判断</em>.
     *
     * @return 返回保存数据的路径, 有SD卡则是SD上的路径, 反之内置存储空间上的路径.
     */
    private String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString() + "/Pictures/liangye";
        } else
            return "/data/data/com.w1520.liangye/liangye";
    }

    /**
     * 判断当前系统LEVEL
     * Android在4.4之后不允许第三方发起系统广播.
     * 所以需要判断版本.
     *
     * @return 当前版本是否大于等于19[4.4].
     */
    private boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    /**
     * 刷新媒体库
     *
     * @param filePath
     * @param context
     */
    private void scanPhotos(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 刷新媒体库.
     *
     * @param currFilePath
     */
    private void refreshPicture(String currFilePath) {
       /* ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA,currFilePath);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);*/

        if (hasKitkat()) {
            MediaScannerConnection.scanFile(context,
                    new String[]{currFilePath}, new String[]{"image/*"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            context.sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                            context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
                        }
                    });
            scanPhotos(currFilePath);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + currFilePath)));
        }
    }


    /**
     * 转换ImageView为Bitmap.
     *
     * @param view 需要转换的View,通常是ImageView或其它类似的类.
     * @return 转换后的Bitmap.
     */
    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    /**
     * <p>保存图片到本地
     * <p/>
     * <p>需要权限:</p>
     * {@code
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     * }
     *
     * @param imgView 要保存的ImageView对象.
     * @throws IOException           写文件时导致的异常.
     * @throws FileNotFoundException 文件未找到时,或路径不存在时抛出此异常.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#saveImages(Bitmap bitmap)}
     */
    public void saveImages(ImageView imgView) {
        Bitmap bitmap = convertViewToBitmap(imgView);
        String strPath = getSDPath();
        DateUtils dateutils = DateUtils.getInstance();
        String strFileName = dateutils.getCurrentTimeById() + ".jpg";
        FileOutputStream fos = null;
        try {
            File destDir = new File(strPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            final String filePaths = strPath + "/" + strFileName;
            File imageFile = new File(filePaths);
            imageFile.createNewFile();
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            showToast("已成功保存到相册", Toast.LENGTH_SHORT);
            refreshPicture(filePaths);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 保存图片到本地
     * <p/>
     * <p>需要权限:</p>{@code
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>}
     *
     * @param bitmap 要保存的图片.
     * @throws IOException           写文件时导致的异常.
     * @throws FileNotFoundException 文件未找到时,或路径不存在时抛出此异常.
     * @see {@link com.w1520.liangye.utils.NetworkUtils#saveImages(ImageView imgView)}
     */
    public void saveImages(Bitmap bitmap) {
        String strPath = getSDPath();
        DateUtils dateutils = DateUtils.getInstance();
        String strFileName = dateutils.getCurrentTimeById() + ".jpg";
        FileOutputStream fos = null;
        try {
            File destDir = new File(strPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            final String filePaths = strPath + "/" + strFileName;
            File imageFile = new File(filePaths);
            imageFile.createNewFile();
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            showToast("已成功保存到相册", Toast.LENGTH_SHORT);
            refreshPicture(filePaths);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 图片按比例大小压缩方法
     *
     * @param image
     * @return
     * @see {@link http://104zz.iteye.com/blog/1694762 }
     */
    public Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩
     *
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 复制文本
     *
     * @param label 标记.
     * @param data  要被复制的文本.
     */
    public void copyText(String label, String data) {
        ClipboardManager clipManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, data);
        clipManager.setPrimaryClip(clip);
        showToast("复制成功", Toast.LENGTH_SHORT);
    }


    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


}
