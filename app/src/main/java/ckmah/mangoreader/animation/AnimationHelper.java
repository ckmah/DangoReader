package ckmah.mangoreader.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import java.util.Collection;

/**
 * Class created to handle general animations.
 */
public class AnimationHelper {

    public AnimationHelper() {

    }

    /**
     * Fades in single view by animating alpha transparency.
     *
     * @param view View to be animated.
     */
    public void fadeIn(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        view.animate()
                .alpha(1)
                .setDuration(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(1);
                    }
                });
    }

    /**
     * Fades in each view by animating alpha transparency.
     *
     * @param views Views to be animated.
     */
    public void fadeIn(Collection<View> views) {
        for (final View view : views) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            view.animate()
                    .alpha(1)
                    .setDuration(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setAlpha(1);
                        }
                    });
        }
    }

    /**
     * Fades out single view by animating alpha transparency.
     *
     * @param view View to be animated.
     */
    public void fadeOut(final View view) {
        view.setAlpha(1);
        view.animate()
                .alpha(0)
                .setDuration(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                        view.setAlpha(0);
                    }
                });
    }


    /**
     * Fades out each view by animating alpha transparency.
     *
     * @param views Views to be animated.
     */
    public void fadeOut(Collection<View> views) {
        for (final View view : views) {
            view.setAlpha(1);
            view.animate()
                    .alpha(0)
                    .setDuration(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.INVISIBLE);
                            view.setAlpha(0);
                        }
                    });
        }
    }
}

