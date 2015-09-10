package dev.matrix.transitions.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * @author rostyslav.lesovyi
 */
public class TransitionView extends FrameLayout {

	private boolean mForceContentDraw = false;
	private Listeners mListeners;
	private Transition mTransition;
	private ValueAnimator mAnimator;

	public TransitionView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mListeners = new Listeners();

		mAnimator = new ValueAnimator();
		mAnimator.setDuration(1000);
		mAnimator.setIntValues(0, 1);
		mAnimator.addListener(mListeners);
		mAnimator.addUpdateListener(mListeners);
	}

	@Override
	public void dispatchDraw(@NonNull Canvas canvas) {
		if (mAnimator.isRunning() && !mForceContentDraw) {
			mForceContentDraw = true;
			mTransition.draw(this, canvas, mAnimator.getAnimatedFraction());
			mForceContentDraw = false;
		} else {
			super.dispatchDraw(canvas);
		}
	}

	public void startTransition(Transition transition) {
		if (transition == null) {
			throw new RuntimeException("transition cannot be null");
		}
		terminateTransition();

		mForceContentDraw = true;
		mTransition = transition;
		mTransition.onStarting(this);
		mForceContentDraw = false;

		mListeners.mOnPreDrawFirstPass = true;
		getViewTreeObserver().addOnPreDrawListener(mListeners);
		invalidate();
	}

	public void terminateTransition() {
		if (mTransition == null) {
			return;
		}
		mAnimator.cancel();
		mTransition = null;
		getViewTreeObserver().removeOnDrawListener(mListeners);
		getViewTreeObserver().removeOnPreDrawListener(mListeners);
	}

	private class Listeners extends AnimatorListenerAdapter implements ViewTreeObserver.OnPreDrawListener, ViewTreeObserver.OnDrawListener, ValueAnimator.AnimatorUpdateListener {
		private boolean mOnPreDrawFirstPass = true;

		@Override
		public boolean onPreDraw() {
			if (mOnPreDrawFirstPass) {
				return mOnPreDrawFirstPass = false;
			}
			getViewTreeObserver().removeOnPreDrawListener(this);
			mForceContentDraw = true;
			mTransition.onStarted(TransitionView.this);
			mForceContentDraw = false;
			mAnimator.start();
			return true;
		}

		@Override
		public void onDraw() {
			getViewTreeObserver().removeOnDrawListener(this);
			mForceContentDraw = true;
			mTransition.onEnded(TransitionView.this);
			mForceContentDraw = false;
			mTransition = null;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			invalidate();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (mTransition != null) {
				mForceContentDraw = true;
				mTransition.onEnding(TransitionView.this);
				mForceContentDraw = false;
				getViewTreeObserver().addOnDrawListener(this);
			}
			invalidate();
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			if (mTransition != null) {
				mForceContentDraw = true;
				mTransition.onEnding(TransitionView.this);
				mTransition.onEnded(TransitionView.this);
				mForceContentDraw = false;
			}
			invalidate();
		}
	}
}
