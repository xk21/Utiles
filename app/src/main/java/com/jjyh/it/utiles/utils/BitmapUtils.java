package com.jjyh.it.utiles.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.ivvi.moassistant.MoApplication;
import com.ivvi.moassistant.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by pengbin3 on 2017/9/22.
 */

public class BitmapUtils {

    /**
     * 网络缓存
     */
    public NetCacheUtils mNetCacheUtils;

    /**
     * 本地缓存
     */
    public SDcardCacheUtils mSdCacheUtils;

    /**
     * 内存缓存
     */
    public MemoryCacheUtils mMemoryCacheUtils;

    private static BitmapUtils mBitmapUtils = new BitmapUtils();

    private BitmapUtils() {
        mSdCacheUtils = new SDcardCacheUtils();
        mMemoryCacheUtils = new MemoryCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mSdCacheUtils, mMemoryCacheUtils);
    }

    public static BitmapUtils getInstance(){
        return mBitmapUtils;
    }

    /**
     * 展示图片的方法
     *
     * @param image
     * @param url
     */
    public void display(ImageView image, String url) {

        //从内存中读取
        /*Bitmap fromMemroy = mMemoryCacheUtils.getFromMemroy(url);
        //如果内存中有的h话就直接返回，从内存中读取
        if (fromMemroy != null) {
            image.setImageBitmap(fromMemroy);
            return;
        }*/

        Glide.with(MoApplication.getInstance()).load(url)
                .asBitmap()
                .placeholder(R.drawable.card_view_list_default)
                .into(image);

        /*//从本地SD卡读取
        Bitmap fromSd = mSdCacheUtils.getFromSd(image, url);
        if (fromSd != null) {
            image.setImageBitmap(fromSd);
            //mMemoryCacheUtils.setToMemory(url, fromSd);
            return;
        }
        //从网络中读取
        mNetCacheUtils.getDataFromNet(image, url);*/
    }

    /**
     * 带ProgressBar展示图片
     *
     * @param PaletteImageView
     * @param url
     */
    public void display(ImageView image, String url, ProgressBar progressBar) {

        //从内存中读取
        /*Bitmap fromMemroy = mMemoryCacheUtils.getFromMemroy(url);
        //如果内存中有的h话就直接返回，从内存中读取
        if (fromMemroy != null) {
            image.setBitmap(fromMemroy);
            return;
        }*/

        //从本地SD卡读取
        Bitmap fromSd = mSdCacheUtils.getFromSd(image,url);
        if (fromSd != null) {
            image.setImageBitmap(fromSd);
            //mMemoryCacheUtils.setToMemory(url, fromSd);
            return;
        }
        //从网络中读取
        mNetCacheUtils.getDataFromNet(image, url, progressBar);
    }


    /**
     * 网络缓存工具类
     */
    public class NetCacheUtils {

        /**
         * 本地缓存
         */
        private SDcardCacheUtils mDcardCacheUtils;


        /**
         * 内存缓存
         */
        private MemoryCacheUtils mMemoryCacheUtils;


        public NetCacheUtils(SDcardCacheUtils dcardCacheUtils, MemoryCacheUtils memoryCacheUtils) {
            mDcardCacheUtils = dcardCacheUtils;
            mMemoryCacheUtils = memoryCacheUtils;
        }

        /**
         * 从网络中下载图片
         *
         * @param image
         * @param url
         */
        public void getDataFromNet(ImageView image, String url) {

            new MyAsyncTask().execute(image, url);  //启动Asynctask，传入的参数到对应doInBackground（）
        }

        /**
         * 带ProgressBar从网络中下载图片
         *
         * @param PaletteImageView
         * @param url
         */
        public void getDataFromNet(ImageView image, String url, ProgressBar progressBar) {

            progressBar.setVisibility(View.VISIBLE);
            new MyAsyncTask().execute(image, url, progressBar);  //启动Asynctask，传入的参数到对应doInBackground（）
        }

        /**
         * 异步下载
         * <p/>
         * 第一个泛型 ： 参数类型  对应doInBackground（）
         * 第二个泛型 ： 更新进度   对应onProgressUpdate（）
         * 第三个泛型 ： 返回结果result   对应onPostExecute
         */
        class MyAsyncTask extends AsyncTask<Object, Void, Bitmap> {
            private ProgressBar mProgressBar;

            /**
             * 图片
             */
            private ImageView mImageView;

            /**
             * 图片地址
             */
            private String mUrl;

            /**
             * 后台下载  子线程
             *
             * @param params
             * @return
             */
            @Override
            protected Bitmap doInBackground(Object... params) {

                //拿到传入的image
                mImageView = (ImageView) params[0];

                if (params.length >= 3 && params[2] instanceof ProgressBar) {
                    mProgressBar = (ProgressBar) params[2];
                }

                //得到图片的地址
                mUrl = (String) params[1];

                /*//将imageview和url绑定，防止错乱
                mImageView.setTag(mUrl);*/

                Bitmap bitmap = downLoadBitmap(mUrl);
                return bitmap;
            }

            /**
             * 进度更新   UI线程
             *
             * @param values
             */
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            /**
             * 回调结果，耗时方法结束后，主线程
             *
             * @param bitmap
             */
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    //得到图片的tag值
                    //String url = (String) mImageView.getTag();
                    //确保图片设置给了正确的image

                    mImageView.setImageBitmap(bitmap);

                    /**
                     * 当从网络上下载好之后保存到sdcard中
                     */
                    mDcardCacheUtils.saveSd(mUrl, bitmap);

                    /**
                     *  写入到内存中
                     */
                    //mMemoryCacheUtils.setToMemory(mUrl, bitmap);

                }else {
                    Resources resources = MoApplication.getInstance().getResources();
                    Drawable drawable = resources.getDrawable(R.drawable.card_view_list_default);

                    mImageView.setBackground(drawable);
                }
                if (mProgressBar != null) {
                    setImageViewWidthAndHeight(mImageView);
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar = null;
                }
            }
        }

        /**
         * 下载图片
         *
         * @param url 下载图片地址
         * @return
         */
        private Bitmap downLoadBitmap(String url) {

            //连接
            HttpURLConnection conn = null;
            Bitmap bitmap = null;
            try {
                conn = (HttpURLConnection) new URL(url)
                        .openConnection();

                //设置读取超时
                conn.setReadTimeout(5000);
                //设置请求方法
                conn.setRequestMethod("GET");
                //设置连接超时连接
                conn.setConnectTimeout(5000);
                //连接
                conn.connect();

                //响应码
                int code = conn.getResponseCode();

                if (code == 200) {  //请求正确的响应码是200
                    //得到响应流
                    InputStream inputStream = conn.getInputStream();
                    //得到bitmap对象
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
                return bitmap;
            }
        }
    }

    /**
     * 本地缓存
     */
    public static class SDcardCacheUtils {

        /**
         * 我们读取内存的绝对路径
         */
        public static final String CACHE_PATH = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/appIcons";

        /**
         * 从本地读取
         *
         * @param url
         */
        public Bitmap getFromSd(ImageView imageView, String url) {
            String fileName = null;
            try {
                //得到图片的url的md5的文件名
                fileName = MD5Encoder.encode(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File file = new File(CACHE_PATH, fileName );

            //如果存在，就通过bitmap工厂，返回的bitmap，然后返回bitmap
            if (file.exists()) {
                /*try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    return bitmap;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                Bitmap bitmap = adjustImage(imageView, file.getPath());
                return bitmap;
            }
            return null;
        }

        //absolutePath是图片绝对路径
        private Bitmap adjustImage(ImageView imageView, String absolutePath) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            // 这个isjustdecodebounds很重要
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(absolutePath, opt);

            // 获取到这个图片的原始宽度和高度
            int picWidth = opt.outWidth;
            int picHeight = opt.outHeight;

            // 获取屏的宽度和高度
            WindowManager windowManager = ((Activity)imageView.getContext()).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();

            // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
            opt.inSampleSize = 1;
            // 根据屏的大小和图片大小计算出缩放比例
            if (picWidth > picHeight) {
                if (picWidth > screenWidth)
                    opt.inSampleSize = picWidth / screenWidth;
            } else {
                if (picHeight > screenHeight)

                    opt.inSampleSize = picHeight / screenHeight;
            }

            // 这次再真正地生成一个有像素的，经过缩放了的bitmap
            opt.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(absolutePath, opt);

            return bm;
        }

        /**
         * 向本地缓存
         *
         * @param url    图片地址
         * @param bitmap 图片
         */
        public void saveSd(String url, Bitmap bitmap) {
            String fileName = null;
            /*Bitmap saveBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas=new Canvas(saveBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, null);*/
            try {
                //我们对图片的地址进行MD5加密，作为文件名
                fileName = MD5Encoder.encode(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * 以CACHE_PATH为文件夹  fileName为文件名
             */
            File file = new File(CACHE_PATH, fileName);

            //我们首先得到他的符文剑
            File parentFile = file.getParentFile();
            //查看是否存在，如果不存在就创建
            if (!parentFile.exists()) {
                parentFile.mkdirs(); //创建文件夹
            }

            try {
                //将图片保存到本地
                /**
                 * @param format   The format of the compressed image   图片的保存格式
                 * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
                 *                 small size, 100 meaning compress for max quality. Some
                 *                 formats, like PNG which is lossless, will ignore the
                 *                 quality setting
                 *                 图片的保存的质量    100最好
                 * @param stream   The outputstream to write the compressed data.
                 */
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 内存缓存
     */
    public class MemoryCacheUtils {

        /**
         * LinkedHashMap<>(10,0.75f,true);
         * <p/>
         * 10是最大致   0.75f是加载因子   true是访问排序   false插入排序
         */
        //private LinkedHashMap<String,Bitmap> mMemoryCache = new LinkedHashMap<>(5,0.75f,true);

        private LruCache<String, Bitmap> mLruCache;


        public MemoryCacheUtils() {
            long maxMemory = Runtime.getRuntime().maxMemory();//最大内存  默认是16兆  运行时候的
            mLruCache = new LruCache<String, Bitmap>((int) (maxMemory / 8)) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    //int byteCount = value.getByteCount();
                    //得到图片字节数
                    // @return number of bytes between rows of the native bitmap pixels.
                    int byteCount = value.getRowBytes() * value.getWidth();
                    return byteCount;
                }
            };
        }

        /**
         * 从内存中读取
         *
         * @param url
         */
        public Bitmap getFromMemroy(String url) {
            return mLruCache.get(url);
        }

        /**
         * 写入到内存中
         *
         * @param url
         * @param bitmap
         */
        public void setToMemory(String url, Bitmap bitmap) {
            mLruCache.put(url, bitmap);
        }
    }

    public static class MD5Encoder {

        public static String encode(String string) throws Exception {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }
    }

    /**
     * 设置ImageView的宽高
     */
    public static void setImageViewWidthAndHeight(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return;
        }
        float density = imageView.getContext().getResources().getDisplayMetrics().density;
        // 通过getIntrinsic 获得ImageView中Image的真实宽高，
        int dw = (int) (drawable.getBounds().width()/density);
        int dh = (int) (drawable.getBounds().height()/density);
        Log.i("ImageView", imageView.getContext().getResources().getDisplayMetrics().density
                +"drawable_X = " + dw + ", drawable_Y = " + dh);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (dw > dh) {
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        } else if (dw < dh) {
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        } else {
            DisplayMetrics metric = new DisplayMetrics();
            ((Activity)imageView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
            // 屏幕宽度（像素）
            int width = metric.widthPixels;
            layoutParams.width = width;
            layoutParams.height = width;
        }
        imageView.setLayoutParams(layoutParams);
    }
}
