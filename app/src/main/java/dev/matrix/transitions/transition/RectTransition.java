package dev.matrix.transitions.transition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * @author rostyslav.lesovyi
 */
public class RectTransition extends Transition {
	private View mAnchor;
	private int mAnchorOffsetX;
	private int mAnchorOffsetY;
	private boolean mAnchorHasOffset;

	private int mGridWidth;
	private int mGridHeight;
	private int[] mLocation = new int[2];
	private Bitmap mSrcBitmap;
	private Bitmap mDstBitmap;
	private Rect mSrcRect = new Rect();
	private RectF mDstRect = new RectF();

	public RectTransition(int gridWidth, int gridHeight, View anchor) {
		mGridWidth = gridWidth;
		mGridHeight = gridHeight;
		mAnchor = anchor;
		mAnchorHasOffset = false;
	}

	public RectTransition(int gridWidth, int gridHeight, View anchor, int offsetX, int offsetY) {
		mGridWidth = gridWidth;
		mGridHeight = gridHeight;
		mAnchor = anchor;
		mAnchorOffsetX = offsetX;
		mAnchorOffsetY = offsetY;
		mAnchorHasOffset = true;
	}

	@Override
	protected void onStarting(TransitionView view) {
		mSrcBitmap = takeSnapshot(view);
	}

	@Override
	protected void onStarted(TransitionView view) {
		mDstBitmap = takeSnapshot(view);

		int[] location = new int[2];
		mAnchor.getLocationOnScreen(location);
		mLocation[0] = location[0];
		mLocation[1] = location[1];

		view.getLocationOnScreen(location);
		mLocation[0] -= location[0];
		mLocation[1] -= location[1];

		if (mAnchorHasOffset) {
			mLocation[0] += mAnchorOffsetX;
			mLocation[1] += mAnchorOffsetY;
		} else {
			mLocation[0] += mAnchor.getWidth() / 2;
			mLocation[1] += mAnchor.getHeight() / 2;
		}
	}

	@Override
	protected void onEnded(TransitionView view) {
		mSrcBitmap.recycle();
		mSrcBitmap = null;

		mDstBitmap.recycle();
		mDstBitmap = null;
	}

	@Override
	protected void draw(TransitionView view, Canvas canvas, float fraction) {
		float cellFraction = .4f;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();

		for (int x = 0; x < mGridWidth; ++x) {
			for (int y = 0; y < mGridHeight; ++y) {
				mSrcRect.left = x * viewWidth / mGridWidth;
				mSrcRect.right = (x + 1) * viewWidth / mGridWidth;
				mSrcRect.top = y * viewHeight / mGridHeight;
				mSrcRect.bottom = (y + 1) * viewHeight / mGridHeight;

				float delay = delay(mSrcRect.centerX(), mSrcRect.centerY(), mLocation[0], mLocation[1], viewWidth, viewHeight);
				float localFraction = fraction / cellFraction - delay * (1 - cellFraction);
				localFraction = Math.max(0, localFraction);
				localFraction = Math.min(1, localFraction);

				Bitmap bitmap;
				if (localFraction < .5f) {
					bitmap = mSrcBitmap;
					localFraction = 1f - localFraction * 2f;
				} else {
					bitmap = mDstBitmap;
					localFraction = (localFraction - .5f) * 2f;
				}

				mDstRect.setEmpty();
				mDstRect.right = mSrcRect.width() * localFraction;
				mDstRect.bottom = mSrcRect.height() * localFraction;
				mDstRect.offset(
						mSrcRect.left + (mSrcRect.width() - mDstRect.right) / 2,
						mSrcRect.top + (mSrcRect.height() - mDstRect.bottom) / 2
				);

				canvas.drawBitmap(bitmap, mSrcRect, mDstRect, null);
			}
		}
	}

	private float delay(float x, float y, float cx, float cy, float w, float h) {
		x = cx - x;
		y = cy - y;
		w = Math.max(cx, w - cx);
		h = Math.max(cy, h - cy);
		return (float) Math.sqrt((x * x + y * y) / (w * w + h * h));
	}
}
