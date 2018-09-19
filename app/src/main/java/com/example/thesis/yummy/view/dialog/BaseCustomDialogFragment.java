package com.example.thesis.yummy.view.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.view.dialog.Interpolator.EaseInInterpolator;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogAnimationListener;

public class BaseCustomDialogFragment extends AppCompatDialogFragment {

    private LinearLayout container;
    private FrameLayout fragmentContainer;
    private ImageButton btnCancel;
    private Button btnAction;
    private CustomDialogActionListener baseDialogActionListener;
    private CustomDialogAnimationListener baseDialogAnimationListener;
    private View viewLineCenter;
    private int subLayout;
    private String actionName;

    private boolean isHasAction;
    private boolean isDismissing;
    private boolean isShown;
    protected boolean isAllowUserInteract;

    private boolean hasShowUpAnimation;
    private boolean hasDismissAnimation;

    public BaseCustomDialogFragment() {
        super();
        actionName = "";
        isHasAction = false;
        baseDialogActionListener = null;
        baseDialogAnimationListener = null;
        isDismissing = false;
        isShown = false;
        isAllowUserInteract = false;
        hasShowUpAnimation = true;
        hasDismissAnimation = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_base, container);
        initControls(view);

        setStatusBarTranslucent(true);
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        inflater.inflate(subLayout, fragmentContainer, true);

        return view;
    }

    @Override
    public void onStart() {
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width, height);

        showDialog();
        super.onStart();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) {
            return;
        }
        super.show(manager, tag);
    }

    /*
        * Init basic controls in dialog
        * */
    private void initControls(View view) {
        container = (LinearLayout) view.findViewById(R.id.contentContainer);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentContainer);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnAction = (Button) view.findViewById(R.id.btnAction);
        btnAction.setText(actionName);
        viewLineCenter = view.findViewById(R.id.view_line_center);
//        btnAction.setTypeface(FontHelper.getInstance().getTypeface(getContext(), FontHelper.FONT_QUICKSAND_BOLD));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect rect = new Rect();
                container.getHitRect(rect);
                if (!rect.contains((int) (event.getX()), (int) (event.getY()))) {
                    if (baseDialogActionListener != null) {
                        baseDialogActionListener.dialogCancel();
                    }
                    dismissDialog();
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowUserInteract) return;
                btnCancel.setEnabled(false);
                if (baseDialogActionListener != null) {
                    baseDialogActionListener.dialogCancel();
                }
                dismissDialog();
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowUserInteract) return;
                isAllowUserInteract = true;
                if (baseDialogActionListener != null) {
                    baseDialogActionListener.dialogPerformAction();
                } else
                    dismissDialog();
            }
        });

        setLayout();
    }

    /**
     * Set translucent status bar when showing dialog
     *
     * @param makeTranslucent is make translucent
     */
    public void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /*
    * Add dialog showing animation
    * */
    private void showDialogAnimate() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_dialog_show);
        animation.setInterpolator(new EaseInInterpolator());
        container.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShown = true;
                if (baseDialogAnimationListener != null) {
                    baseDialogAnimationListener.onDialogEndShowingAnimation();
                }
                isAllowUserInteract = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        isDismissing = false;
    }

    /*
    * Add dialog hiding animation. After animation end, dialog will be dismissed.
    * */
    private void hideDialogAnimate() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_dialog_hide);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        if (!isDismissing && isShown) {
            container.startAnimation(animation);
            isDismissing = true;
        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (baseDialogAnimationListener != null) {
                    baseDialogAnimationListener.onDialogEndHidingAnimation();
                }
                isShown = false;
                getDialog().dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showDialog() {
        if (hasShowUpAnimation) {
            showDialogAnimate();
        } else {
            isAllowUserInteract = true;
            isShown = true;
            isDismissing = false;
        }
    }

    public void dismissDialog() {
        isAllowUserInteract = false;
        if (hasDismissAnimation) {
            hideDialogAnimate();
        } else {
            getDialog().dismiss();
            isShown = false;
            isDismissing = false;
        }
    }

    public void dismissDialogMemo() {
        isAllowUserInteract = false;
        getDialog().dismiss();
        isShown = false;
        isDismissing = false;
    }

    /*
    * Set dialog corner radius
    * */
    @SuppressLint("NewApi")
    private void setLayout() {
        float cornerRadius = getContext().getResources().getDimension(R.dimen.dimen_8dp); //10dp
        if (container.getBackground() instanceof ColorDrawable) {
            GradientDrawable background = new GradientDrawable();
            background.setColor(((ColorDrawable) container.getBackground()).getColor());
            background.setCornerRadius(cornerRadius);
            container.setBackground(background);
        }
        if (!isHasAction) {
            if (btnCancel.getBackground() instanceof ColorDrawable) {
                GradientDrawable cancelBackground = new GradientDrawable();
                cancelBackground.setColor(((ColorDrawable) btnCancel.getBackground()).getColor());
                cancelBackground.setCornerRadii(new float[]{0, 0, 0, 0, cornerRadius, cornerRadius, cornerRadius, cornerRadius});
                btnCancel.setBackground(cancelBackground);
            }
            btnAction.setVisibility(View.GONE);
            viewLineCenter.setVisibility(View.GONE);
        } else {
            viewLineCenter.setVisibility(View.VISIBLE);
            if (btnCancel.getBackground() instanceof ColorDrawable) {
                GradientDrawable cancelBackground = new GradientDrawable();
                cancelBackground.setColor(((ColorDrawable) btnCancel.getBackground()).getColor());
                cancelBackground.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, cornerRadius, cornerRadius});
                btnCancel.setBackground(cancelBackground);
            }
            if (btnAction.getBackground() instanceof ColorDrawable) {
                GradientDrawable actionBackground = new GradientDrawable();
                actionBackground.setColor(((ColorDrawable) btnAction.getBackground()).getColor());
                actionBackground.setCornerRadii(new float[]{0, 0, 0, 0, cornerRadius, cornerRadius, 0, 0});
                btnAction.setBackground(actionBackground);
            }
        }
    }

    /*
    * Set main dialog view
    * */
    protected void setView(int viewIdRes) {
        subLayout = viewIdRes;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
        if (btnAction != null) {
            btnAction.setText(actionName);
        }
    }

    public void setHasAction(boolean hasAction) {
        this.isHasAction = hasAction;
    }

    public void disableActionButton() {
        btnAction.setAlpha((float) 0.6);
    }

    public void enableActionButton() {
        btnAction.setAlpha((float) 1);
    }

    public void setDialogActionListener(CustomDialogActionListener baseDialogActionListener) {
        this.baseDialogActionListener = baseDialogActionListener;
    }

    public void setDialogAnimationListener(CustomDialogAnimationListener baseDialogAnimationListener) {
        this.baseDialogAnimationListener = baseDialogAnimationListener;
    }

    public void setHasShowUpAnimation(boolean hasShowUpAnimation) {
        this.hasShowUpAnimation = hasShowUpAnimation;
    }

    public void setHasDismissAnimation(boolean hasDismissAnimation) {
        this.hasDismissAnimation = hasDismissAnimation;
    }

    public ViewGroup getMainView() {
        return fragmentContainer;
    }
}