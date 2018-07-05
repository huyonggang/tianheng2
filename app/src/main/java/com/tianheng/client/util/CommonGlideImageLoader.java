package com.tianheng.client.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * Created by hug on 2017/6/30.
 * describe:
 * Image loader Glide
 */

public class CommonGlideImageLoader {

    public static final DrawableTransitionOptions DRAWABLE_TRANSITION_OPTIONS = new DrawableTransitionOptions().crossFade(500);

    private CommonGlideImageLoader() {
    }

    private static final class CommonImageLoaderHolder {
        static final CommonGlideImageLoader instance = new CommonGlideImageLoader();
    }

    public static CommonGlideImageLoader getInstance() {
        return CommonImageLoaderHolder.instance;
    }

    private static final RequestOptions mRequestOptions = new RequestOptions().centerCrop()
            .priority(Priority.LOW)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false);

    /**
     * 加载网络图片有渐变动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param defaultDrawableImage
     */
    public void displayNetImageWithAnimation(Context context, String imageUrl, ImageView imageView, Drawable defaultDrawableImage) {
        Glide.with(context).load(imageUrl)
                .apply(buildRequestOptions(defaultDrawableImage))
                .transition(DRAWABLE_TRANSITION_OPTIONS)
                .into(imageView);
    }

    /**
     * 加载网络图片有渐变动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public void displayNetImageWithAnimation(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .transition(DRAWABLE_TRANSITION_OPTIONS)
                .into(imageView);
    }


    /**
     * 加载网络图片有渐变动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public void displayNetImageWithCircle(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .transition(DRAWABLE_TRANSITION_OPTIONS)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);
    }

    /**
     * 加载网络图片圆形
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public void displayNetImageWithCircle(Context context, String imageUrl, ImageView imageView, Drawable defaultDrawableImage) {
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()).error(defaultDrawableImage))
                .into(imageView);
    }


    /**
     * 加载网络图片 无动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param defaultDrawableImage
     */
    public void displayNetImage(Context context, String imageUrl, ImageView imageView, Drawable defaultDrawableImage) {
        Glide.with(context).load(imageUrl)
                .apply(buildRequestOptions(defaultDrawableImage))
                .into(imageView);
    }


    /**
     * 加载网络图片 无动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public void displayNetImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    /**
     * 加载指定宽高图片 无动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param width
     * @param height
     */
    public void displayNetImage(Context context, String imageUrl, ImageView imageView,
                                Drawable defaultDrawableImage, int width, int height) {
        Glide.with(context)
                .load(imageUrl)
                .apply(buildRequestOptionsSize(defaultDrawableImage, width, height))
                .into(imageView);

    }

    public void displayLocalImage(Context context, Drawable drawable, ImageView imageView) {
        Glide.with(context)
                .load(drawable)
                .into(imageView);
    }


    /**
     * 加载指定宽高图片 有动画
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param width
     * @param height
     */
    public void displayNetImageWithAnimation(Context context, String imageUrl, ImageView imageView,
                                             Drawable defaultDrawableImage, int width, int height) {
        Glide.with(context)
                .load(imageUrl)
                .apply(buildRequestOptionsSize(defaultDrawableImage, width, height))
                .into(imageView);

    }

    public void displayLocalImage(Context context, File file, ImageView imageView,
                                  Drawable defaultDrawableImage, int width, int height) {
        Glide.with(context)
                .load(file)
                .transition(DRAWABLE_TRANSITION_OPTIONS)
                .apply(buildRequestOptions(defaultDrawableImage))
                .into(imageView);
    }

    public void displayLocalImage(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .into(imageView);
    }

    public void displayLocalImage(Context context, Bitmap bitmap, ImageView imageView) {
        Glide.with(context)
                .load(bitmap)
                .into(imageView);
    }

    public RequestOptions buildRequestOptions(Drawable defaultDrawableImage) {
        return mRequestOptions.placeholder(defaultDrawableImage)
                .error(defaultDrawableImage);
    }

    private RequestOptions buildRequestOptionsSize(Drawable defaultDrawableImage, int width, int height) {
        return mRequestOptions.placeholder(defaultDrawableImage)
                .override(width, height)
                .error(defaultDrawableImage);
    }


    /**
     * perform main thread
     *
     * @param context
     */
    public void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }


    /**
     * This method should always be called on a background thread, since it is a blocking call.
     *
     * @param context
     */
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
}
