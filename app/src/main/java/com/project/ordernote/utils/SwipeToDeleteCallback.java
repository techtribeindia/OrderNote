package com.project.ordernote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final Paint mPaint = new Paint();
    private Bitmap mDeleteIconBitmap;

    private final int mIconSize; // Icon size in pixels
    private final Paint mTextPaint = new Paint();
    private final ItemTouchHelperAdapterInterface mAdapter;
    Context mContext;


    public SwipeToDeleteCallback(ItemTouchHelperAdapterInterface adapter, Context context) {
        super(0, ItemTouchHelper.RIGHT);
        mContext = context;
        mPaint.setColor(Color.RED); // Set the background color to red
// Allow left and right swipes
        mAdapter = adapter;

//        mDeleteIcon = ContextCompat.getDrawable(context, R.mipmap.delete_icon); // Replace with your drawable



        mIconSize = (int) (24 * context.getResources().getDisplayMetrics().density); // Example size in pixels

        // Convert drawable to bitmap and scale it
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.delete_icon); // Replace with your drawable
        if (drawable != null) {
            mDeleteIconBitmap = drawableToBitmap(drawable);
            mDeleteIconBitmap = Bitmap.createScaledBitmap(mDeleteIconBitmap, mIconSize, mIconSize, false);
        }

        mTextPaint.setColor(Color.WHITE); // Text color
        mTextPaint.setTextSize(42); // Text size
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Set text style

        mTextPaint.setAntiAlias(true);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false; // We don't want to handle move actions
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Get the position of the item that was swiped
        int position = viewHolder.getAdapterPosition();
        // Call a method on the adapter to handle the deletion
        mAdapter.onItemDismiss(position);
    }




    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) { // Swiping to the left
                // Draw the red background
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), mPaint);

                // Draw the delete icon and text
                drawDeleteIconAndText(c, itemView, dX);
            } else if (dX > 0) { // Swiping to the right
                // Draw the red background
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(),
                        dX, (float) itemView.getBottom(), mPaint);

                // Draw the delete icon and text
                drawDeleteIconAndText(c, itemView, dX);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawDeleteIconAndText(Canvas canvas, View itemView, float dX) {
        if (mDeleteIconBitmap != null) {
            int iconMargin = (itemView.getHeight() - mDeleteIconBitmap.getHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconBottom = iconTop + mDeleteIconBitmap.getHeight();
            int iconLeft = itemView.getLeft() + iconMargin - 40;
            int iconRight = iconLeft + mDeleteIconBitmap.getWidth();
            canvas.drawBitmap(mDeleteIconBitmap, iconLeft, iconTop, null);
        }

        // Draw the text
        String text = "Remove";
        canvas.drawText(text, itemView.getLeft() + 120, itemView.getTop()+16 + itemView.getHeight() / 2, mTextPaint);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }




}