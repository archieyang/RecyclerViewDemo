package me.codethink.recylerviewdemo;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    final List<String> stringList = new LinkedList<String>();
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        for(int i = 0; i < 100; ++i) {
            stringList.add("Card " + i);
        }

        recyclerView.setLayoutManager(new RecyclerView.LayoutManager() {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                View scrap = recycler.getViewForPosition(0);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);

                detachAndScrapView(scrap, recycler);

                int childLeft;
                int childTop;


            }
        });

        recyclerView.setItemAnimator(new RecyclerView.ItemAnimator() {
            List<RecyclerView.ViewHolder> mAnimationRemoveViewHolders = new ArrayList<RecyclerView.ViewHolder>();

            @Override
            public void runPendingAnimations() {
                for (final RecyclerView.ViewHolder viewHolder : mAnimationRemoveViewHolders) {
                    final ViewPropertyAnimatorCompat animation = ViewCompat.animate(viewHolder.itemView);
                    animation.setDuration(1000).alpha(0).setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            dispatchRemoveStarting(viewHolder);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            animation.setListener(null);
                            ViewCompat.setAlpha(view, 1);
                            dispatchRemoveFinished(viewHolder);
                            mAnimationRemoveViewHolders.remove(viewHolder);

                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    }).start();

                }
            }

            @Override
            public boolean animateRemove(RecyclerView.ViewHolder holder) {
                mAnimationRemoveViewHolders.add(holder);

                return true;
            }

            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                return false;
            }

            @Override
            public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
                return false;
            }

            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
                return false;
            }

            @Override
            public void endAnimation(RecyclerView.ViewHolder item) {
                if (mAnimationRemoveViewHolders.remove(item)) {
                    ViewCompat.setAlpha(item.itemView, 1);
                    dispatchRemoveFinished(item);
                }

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });

        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new CardViewHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.view_item, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((CardViewHolder) viewHolder).tv.setText(stringList.get(i));
            }

            @Override
            public int getItemCount() {
                return stringList.size();
            }


        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });
    }

    private  class CardViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv;

        public CardViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.view_recycler_item);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            stringList.remove(getPosition());
            adapter.notifyItemRemoved(getPosition());
//            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
