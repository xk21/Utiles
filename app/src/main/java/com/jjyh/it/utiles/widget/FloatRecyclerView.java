package com.jjyh.it.utiles.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jjyh.it.utiles.R;


/**
 * 浮层
 */

public class FloatRecyclerView extends RecyclerView {
    private Activity mActivity;

    public FloatRecyclerView(Context context) {
        this(context, null);
    }

    public FloatRecyclerView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mActivity = (Activity) context;
        if (isInEditMode()) {
            return;
        }

        //setLayoutManager(new OverScrollLinearLayoutManager(this));

        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        // Set-up of recycler-view's native item swiping.
        /*
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                /*AlertDialog dialog = new AlertDialog.Builder(rv.getContext())
                        .setTitle("Item swiping is supported!")
                        .setMessage("Recycler-view's native item swiping and the over-scrolling effect can co-exist! But, to get them to work WELL -- please apply the effect using the dedicated helper method!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .create();
                dialog.show();
            }
        };*/

        // Apply over-scroll in 'advanced form' - i.e. create an instance manually.
        VerticalOverScrollBounceEffectDecorator mVertOverScrollEffect = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(this));

        // Over-scroll listeners are applied here via the mVertOverScrollEffect explicitly.
        mVertOverScrollEffect.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
            }
        });
        mVertOverScrollEffect.setOverScrollStateListener(new IOverScrollStateListener() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {}
        });
        //移除自定义的重叠效果
        /*addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                super.getItemOffsets(outRect, view, parent, state);
                //获取当前项的下标
                final int currentPosition = parent.getChildLayoutPosition(view);
                //获取最后一项的下标
                final int lastPosition = state.getItemCount() - 1;
                if (currentPosition != lastPosition) {
                    outRect.bottom = -CommonUtil.dp2px(context, 15);
                }
            }
        });

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = layoutManager.findFirstVisibleItemPosition();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                int visibleCount = lastPosition - firstPosition;
                //重置控件的位置及高度
                float elevation = 6.0f;
                for (int i = firstPosition -1; i <= (firstPosition + visibleCount) + 1; i++) {
                    View view = layoutManager.findViewByPosition(i);
                    if (view != null) {
                        if (view instanceof CardView) {
                            ((CardView) view).setCardElevation(CommonUtil.dp2px(context, elevation));
                            //((CardView) view).setca(dp2px(context, elevation));
                            elevation -= 0.5f;
                        }
                        float translationY = view.getTranslationY();
                        if (i > firstPosition && translationY != 0) {
                            view.setTranslationY(0);
                        }
                    }
                }

                View firstView = layoutManager.findViewByPosition(firstPosition);
                float firstViewTop = firstView.getTop();
                firstView.setTranslationY(-firstViewTop / 2.0f);
            }
        });*/
    }

    public void playAnimate(boolean showorhide) {
        Animation anim;
        if(showorhide) {
            anim = AnimationUtils.loadAnimation(mActivity, R.anim.scale_to_big);
        } else {
            anim = AnimationUtils.loadAnimation(mActivity,R.anim.scale_to_small);
        }
        this.startAnimation(anim);
    }
}
