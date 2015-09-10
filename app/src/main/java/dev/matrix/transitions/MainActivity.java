package dev.matrix.transitions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import dev.matrix.transitions.transition.RectTransition;
import dev.matrix.transitions.transition.RevealTransition;
import dev.matrix.transitions.transition.Transition;
import dev.matrix.transitions.transition.TransitionView;

/**
 * @author rostyslav.lesovyi
 */
public class MainActivity extends FragmentActivity implements View.OnTouchListener {

	private int mTransitionCounter = 0;
	private TransitionView mTransitionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.a_main);

		mTransitionView = (TransitionView) findViewById(R.id.transition);
		mTransitionView.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			Transition transition = createTransition(v, (int) event.getX(), (int) event.getY());
			mTransitionView.startTransition(transition);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content, MainFragment.newInstance(!transition.hasAfterCache()))
					.commit();
		}
		return true;
	}

	private Transition createTransition(View v, int x, int y) {
		switch (++mTransitionCounter % 2) {
			case 0: return new RectTransition(9, 16, v, x, y);
			case 1: return new RevealTransition(v, x, y);
		}
		return null;
	}
}
