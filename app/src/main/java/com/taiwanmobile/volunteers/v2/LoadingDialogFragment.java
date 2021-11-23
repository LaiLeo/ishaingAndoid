package com.taiwanmobile.volunteers.v2;

import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.taiwanmobile.volunteers.R;

public class LoadingDialogFragment extends DialogFragment implements ValueAnimator.AnimatorUpdateListener {

    ValueAnimator animator = ValueAnimator.ofInt(0, 10);
    ImageView bg;

    static LoadingDialogFragment newInstance() {
        LoadingDialogFragment f = new LoadingDialogFragment();
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        bg = v.findViewById(R.id.bg);

        animator.setDuration(6 * 1000L);
        animator.start();
        animator.addUpdateListener(this);
        return v;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
        int value = (int)updatedAnimation.getAnimatedValue();
        switch (value) {
            case 0: {
                bg.setImageResource(R.drawable.loading_01);
                break;
            }
            case 1: {
                bg.setImageResource(R.drawable.loading_02);
                break;
            }
            case 2: {
                bg.setImageResource(R.drawable.loading_03);
                break;
            }
            case 3: {
                bg.setImageResource(R.drawable.loading_04);
                break;
            }
            case 4: {
                bg.setImageResource(R.drawable.loading_05);
                break;
            }
            case 5: {
                bg.setImageResource(R.drawable.loading_06);
                break;
            }
            case 6: {
                bg.setImageResource(R.drawable.loading_07);
                break;
            }
            case 7: {
                bg.setImageResource(R.drawable.loading_08);
                break;
            }
            case 8: {
                bg.setImageResource(R.drawable.loading_09);
                break;
            }
            case 9: {
                bg.setImageResource(R.drawable.loading_10);
                break;
            }
            case 10: {
                dismiss();
            }
        }
    }
}
