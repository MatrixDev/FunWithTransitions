package dev.matrix.transitions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author rostyslav.lesovyi
 */
public class MainFragment extends Fragment {

	private static int sCurrentDrawable = 0;
	private static int[] sDrawables = {
			R.drawable.image1, R.drawable.image2, R.drawable.image3
	};

	public static MainFragment newInstance(boolean animate) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("animate", animate);

		MainFragment f = new MainFragment();
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ImageView imageView = new ImageView(container.getContext());
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageResource(sDrawables[++sCurrentDrawable % sDrawables.length]);
		if (getArguments().getBoolean("animate")) {
			imageView.setScaleX(1.1f);
			imageView.setScaleY(1.1f);
			imageView.animate().scaleX(1f).scaleY(1f).setDuration(1000);
		}
		return imageView;
	}
}
