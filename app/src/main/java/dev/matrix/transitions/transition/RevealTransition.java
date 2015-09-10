package dev.matrix.transitions.transition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;

/**
 * @author rostyslav.lesovyi
 */
public class RevealTransition extends Transition {
	private View mAnchor;
	private int mAnchorOffsetX;
	private int mAnchorOffsetY;
	private boolean mAnchorHasOffset;

	private Path mPath = new Path();
	private int[] mLocation = new int[2];

	public RevealTransition(View anchor) {
		mAnchor = anchor;
		mAnchorHasOffset = false;
	}

	public RevealTransition(View anchor, int offsetX, int offsetY) {
		mAnchor = anchor;
		mAnchorOffsetX = offsetX;
		mAnchorOffsetY = offsetY;
		mAnchorHasOffset = true;
	}

	@Override
	public boolean hasAfterCache() {
		return false;
	}

	@Override
	protected void onStarting(TransitionView view) {
		view.buildDrawingCache();
	}

	@Override
	protected void onStarted(TransitionView view) {
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
		view.destroyDrawingCache();
	}

	@Override
	protected void draw(TransitionView view, Canvas canvas, float fraction) {
		drawOldView(view, canvas);
		drawNewView(view, canvas, fraction);
	}

	private void drawOldView(TransitionView view, Canvas canvas) {
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
	}

	private void drawNewView(TransitionView view, Canvas canvas, float fraction) {
		float x = mLocation[0] + (view.getWidth() / 2 - mLocation[0]) * fraction;
		float y = mLocation[1] + (view.getHeight() / 2 - mLocation[1]) * fraction;
		float w = view.getWidth();
		float h = view.getHeight();
		float radius = (float) Math.sqrt(w * w + h * h) / 2;
		mPath.addCircle(x, y, radius * fraction, Path.Direction.CCW);

		canvas.save();
		canvas.clipPath(mPath);
		view.dispatchDraw(canvas);
		canvas.restore();

		mPath.reset();
	}
}
