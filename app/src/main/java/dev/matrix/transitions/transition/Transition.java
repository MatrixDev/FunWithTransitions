package dev.matrix.transitions.transition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * @author rostyslav.lesovyi
 */
public abstract class Transition {

	protected abstract void draw(TransitionView view, Canvas canvas, float fraction);

	protected void onStarting(TransitionView view) {}
	protected void onStarted(TransitionView view) {}
	protected void onEnding(TransitionView view) {}
	protected void onEnded(TransitionView view) {}

	public boolean hasAfterCache() {
		return true;
	}

	protected final Bitmap takeSnapshot(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		canvas.translate(-view.getScrollX(), -view.getScrollY());
		view.draw(canvas);

		return bitmap;
	}
}
