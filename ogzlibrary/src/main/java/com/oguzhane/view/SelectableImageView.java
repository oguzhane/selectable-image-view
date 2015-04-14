package com.oguzhane.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


public class SelectableImageView extends ImageView {
    private boolean isSelected;

    private Border border;
    private Select select;
    private Radius radius;

    private Paint paint;//itself
    private BitmapShader shader;
    private Bitmap image;

    private int canvasWidth;
    private int canvasHeight;

    private ArrayList<SelectionListener> listeners = new ArrayList<>();

    public SelectableImageView(Context context) {
        this(context, null, R.styleable.SelectableImageViewStyle_selectableImageViewDefault);
    }

    public SelectableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.SelectableImageViewStyle_selectableImageViewDefault);
    }

    public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle){
        paint = new Paint();
        paint.setAntiAlias(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.SelectableImageView, defStyle, 0);

        boolean hasBorder = attributes.getBoolean(R.styleable.SelectableImageView_siv_border,true);
        boolean isSelectable = attributes.getBoolean(R.styleable.SelectableImageView_siv_selectable,true);

        border = new Border(hasBorder);

        select = new Select(isSelectable);

        radius = new Radius();

        if(border.hasBorder){
            int defaultBorderSize = (int) (2 * context.getResources().getDisplayMetrics().density + 0.5f);

            setBorderWidth(attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_borderWidth, defaultBorderSize));
            setBorderColor(attributes.getColor(R.styleable.SelectableImageView_siv_borderColor, Color.BLACK));
        }

        if (select.isSelectable){
            int defaultSelectSize = (int) (2 * context.getResources().getDisplayMetrics().density + 0.5f);
            setSelectColor(attributes.getColor(R.styleable.SelectableImageView_siv_selectColor, Color.TRANSPARENT));
            setSelectStrokeWidth(attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_selectStrokeWidth, defaultSelectSize));
            setSelectStrokeColor(attributes.getColor(R.styleable.SelectableImageView_siv_selectStrokeColor, Color.BLUE));

            //---------------
            int selectImgHeight = attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_selectImageHeight, -1);
            int selectImgWidth = attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_selectImageWidth, -1);


            /*
                    <attr name="siv_selectImageFilterMul" format="color"/>
                    <attr name="siv_selectImageFilterAdd" format="color"/>
            */
            //if (attributes.getValue(R.styleable.SelectableImageView_siv_selectImageFilterMul, new TypedValue()) && attributes.getValue(R.styleable.SelectableImageView_siv_selectImageFilterAdd, new TypedValue()))
            //setSelectImageFilter(attributes.getColor(R.styleable.SelectableImageView_siv_selectImageFilterMul, 0),attributes.getColor(R.styleable.SelectableImageView_siv_selectImageFilterAdd, 0));

            if (attributes.getValue(R.styleable.SelectableImageView_siv_selectImageFilter, new TypedValue()))
                setSelectImageFilter(attributes.getColor(R.styleable.SelectableImageView_siv_selectImageFilter, Color.BLACK),0);

            if (attributes.getValue(R.styleable.SelectableImageView_siv_selectImageTransparency, new TypedValue()))
                setSelectImageTransparency(attributes.getInt(R.styleable.SelectableImageView_siv_selectImageTransparency, 255));

            setSelectImage(attributes.getDrawable(R.styleable.SelectableImageView_siv_selectImageSrc), selectImgWidth, selectImgHeight);


        }


        radius.X = attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_radiusDx, 0);
        radius.Y = attributes.getDimensionPixelOffset(R.styleable.SelectableImageView_siv_radiusDy,0);

        attributes.recycle();


    }

    @Override
    public void onDraw(Canvas canvas){

        if (image==null)
            return;

        if (image.getHeight() == 0 || image.getWidth() == 0)
            return;

        int oldCanvasWidth = canvasWidth;
        canvasWidth = getWidth();

        int oldCanvasHeight = canvasHeight;
        canvasHeight = getHeight();

        if (oldCanvasHeight != canvasHeight || oldCanvasWidth != canvasWidth)
            updateBitmapShader();

        paint.setShader(shader);

        float outerWidth=0;

        // design preview
        if (isInEditMode() && select.isSelectable)
            this.isSelected=true;

        if (select.isSelectable && isSelected){
            outerWidth = select.strokeWidth;

            paint.setColorFilter(select.filter);
            RectF rekt = new RectF(0,0,canvasWidth, canvasHeight);

            canvas.drawRoundRect(rekt,radius.X,radius.Y, select.paint);
            //canvas.drawRoundRect(rekt,radius.X,radius.Y, select.paintStroke);
        }
        else if (border.hasBorder){
            outerWidth = border.width;
            paint.setColorFilter(null);

            RectF rekt = new RectF(outerWidth / 2, outerWidth / 2, canvasWidth - outerWidth / 2, canvasHeight - outerWidth / 2);

            canvas.drawRoundRect(rekt, radius.X,radius.Y,border.paint);

        }
        else
            paint.setColorFilter(null);

        RectF rekt = new RectF(outerWidth/2, outerWidth/2,canvasWidth-(outerWidth/2),canvasHeight-(outerWidth/2));

        canvas.drawRoundRect(rekt, radius.X, radius.Y, paint);
        if (select.isSelectable && isSelected &&select.selectImage != null)
        {
            //select.paintOfSelectImg.setARGB(255,75,250,30);


            //ColorFilter filterx = new LightingColorFilter(Color.GREEN, 1);

            //ColorFilter filter;
            //filter = new LightingColorFilter(Color.GREEN, PorterDuff.Mode.ADD);
            //Paint p = new Paint();
            //p.setColorFilter(filterx);
            //select.paintOfSelectImg.setColorFilter(filter);

            canvas.drawBitmap(select.selectImage, (canvasWidth-select.widthOfSelectImg)/2, (canvasHeight-select.heightOfSelectImg)/2, select.paintOfSelectImg);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){

        if (!select.isSelectable){
            this.isSelected=false;
            return super.onTouchEvent(event);
        }
        else if (event.getAction() != MotionEvent.ACTION_DOWN)
        {
            return super.onTouchEvent(event);
        }


        setSelect(!this.isSelected);

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);

        // Extract a Bitmap out of the drawable & set it as the main shader
        image = drawableToBitmap(getDrawable());
        if(canvasWidth > 0 && canvasHeight > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        // Extract a Bitmap out of the drawable & set it as the main shader
        image = drawableToBitmap(getDrawable());
        if(canvasWidth > 0 && canvasHeight > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        // Extract a Bitmap out of the drawable & set it as the main shader
        image = drawableToBitmap(getDrawable());
        if(canvasWidth > 0 && canvasHeight > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        // Extract a Bitmap out of the drawable & set it as the main shader
        image = bm;
        if(canvasWidth > 0 && canvasHeight > 0)
            updateBitmapShader();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        }
        else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        }
        else {
            // The parent has not imposed any constraint on the child.
            result = canvasWidth;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasHeight;
        }

        return (result + 2);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)   // Don't do anything without a proper drawable
            return null;
        else if (drawable instanceof BitmapDrawable) {  // Use the getBitmap() method instead if BitmapDrawable
            //Log.i(TAG, "Bitmap drawable!");
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (!(intrinsicWidth > 0 && intrinsicHeight > 0))
            return null;

        try {
            // Create Bitmap object out of the drawable
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Simply return null of failed bitmap creations
            //Log.e(TAG, "Encountered OutOfMemoryError while generating bitmap!");
            return null;
        }
    }

    private void setSelectImage(Drawable drawable, int width, int height){
        Bitmap bmp = drawableToBitmap(drawable);
        if (bmp == null)
            return;

        if (width<0)width=bmp.getWidth();
        if (height<0)height=bmp.getHeight();

        Bitmap scaledImg = Bitmap.createScaledBitmap(bmp,width,height,true);

        //bmp.recycle();
        setSelectImage(scaledImg);
    }

    private void setSelectImageFilter(int mul, int add){
        select.paintOfSelectImg.setColorFilter(new LightingColorFilter(mul,add));
    }

    public void updateBitmapShader() {
        if (image == null)
            return;

        shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        if(canvasWidth != image.getWidth() || canvasHeight != image.getHeight()) {
            Matrix matrix = new Matrix();
            float scaleW = (float) canvasWidth / (float) image.getWidth();
            float scaleH = (float) canvasHeight / (float) image.getHeight();
            matrix.setScale(scaleW, scaleH);
            shader.setLocalMatrix(matrix);
        }
    }


    //---------------------------------------------------------

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelectStrokeColor(int selectStrokeColor){
        select.strokeColor = selectStrokeColor;
        if (select.paint != null)
            select.paint.setColor(select.strokeColor);
        /*select.paintStroke = new Paint();
        select.paintStroke.setAntiAlias(true);
        select.paintStroke.setStyle(Paint.Style.STROKE);
        select.paintStroke.setStrokeWidth(select.strokeWidth);
        select.paintStroke.setColor(select.strokeColor);*/

        this.invalidate();
    }

    public void setSelectStrokeWidth(int selectStrokeWidth) {
        select.strokeWidth = selectStrokeWidth;
        this.requestLayout();
        this.invalidate();
    }

    public void setSelectColor(int selectColor){
        select.filter = new PorterDuffColorFilter(selectColor,PorterDuff.Mode.SRC_ATOP);
        this.invalidate();
    }

    public void setBorderWidth(int borderWidth){
        border.width = borderWidth;
        if (border.paint!=null)
            border.paint.setStrokeWidth(borderWidth);
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int borderColor){
        border.color = borderColor;
        if (border.paint!=null)
            border.paint.setColor(border.color);

        this.invalidate();
    }

    private void setSelectImage(Bitmap bitmap){
        select.setSelectImage(bitmap);
        this.requestLayout();
        this.invalidate();
    }

    public Bitmap getSelectImage(){
        return select.getSelectImage();
    }

    public void setSelectImageTransparency(int value){
        select.paintOfSelectImg.setAlpha(value);
        this.invalidate();
    }

    public void setSelectImageFilter(int mul){
        setSelectImageFilter(mul,0);
    }

    public void setSelect(boolean b){
        if (!select.isSelectable)
        {
            this.isSelected=false;
            return;
        }
        else if (b == this.isSelected) return;

        this.isSelected = b;

        this.invalidate();
        for (SelectionListener listener:listeners)
        {
            if (this.isSelected)
                listener.OnSelected(this);
            else
                listener.OnUnSelected(this);
        }

    }

    public void setSelectionListener(SelectionListener listener){
        listeners.add(listener);
    }


    //--------------------------------------------------------

    public interface SelectionListener{
        void OnSelected(View v);
        void OnUnSelected(View v);
    }

    private class Border{
        //private SelectableImageView siv;
        Border(boolean hasBorder){
            this.hasBorder=hasBorder;
            //this.siv=v;
            preparePaint();
        }

        void preparePaint(){
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
        }

        boolean hasBorder;
        Paint paint;
        float width;
        int color;
    }

    private class Select{
        Select(boolean selectable){
            this.isSelectable=selectable;
            preparePaint();
            preparePaintOfSelectImg();

        }
        void preparePaint(){
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        boolean isSelectable;
        //Paint paintStroke;
        Paint paint;
        ColorFilter filter;
        int strokeColor;
        int strokeWidth;

        Bitmap selectImage;
        int heightOfSelectImg;
        int widthOfSelectImg;
        Paint paintOfSelectImg;

        void preparePaintOfSelectImg(){
            paintOfSelectImg=new Paint();

            paintOfSelectImg.setAntiAlias(true);
            paintOfSelectImg.setFilterBitmap(true);
            paintOfSelectImg.setDither(true);
        }

        Bitmap getSelectImage(){
            return selectImage;
        }

        void setSelectImage(Bitmap bmp){
            this.selectImage=bmp;

            this.heightOfSelectImg = select.selectImage != null ? bmp.getHeight():-1;
            this.widthOfSelectImg = select.selectImage != null ? bmp.getWidth():-1;
        }


    }

    private class Radius{
        float X;
        float Y;
    }
}
