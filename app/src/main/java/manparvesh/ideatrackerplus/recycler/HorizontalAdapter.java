package manparvesh.ideatrackerplus.recycler;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import manparvesh.ideatrackerplus.R;
import manparvesh.ideatrackerplus.database.DatabaseHelper;

/**
 * Created by Nicklos on 13/07/2016.
 * Adapter for the horizontal RecyclerView containing the idea
 * as well as the left and right quick actions.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private String mIdea;
    private int mTabNumber;

    private View mLayout;
    private MyRecyclerView mRecyclerView;
    private View.OnLongClickListener mListener;

    // same text size for all ideas
    private static boolean mBigtext = false;
    private boolean mDarkTheme;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        TextView tabTag;
        View priorityTag;

        MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
            tabTag = (TextView) view.findViewById(R.id.tabTag);
            priorityTag = view.findViewById(R.id.priorityTag);
        }
    }

    public HorizontalAdapter(Context context, String text, int tabNumber, boolean darkTheme) {
        mIdea = text;
        mTabNumber = tabNumber;
        mLayout = LayoutInflater.from(context).inflate(R.layout.horizontal_item_view, null, false);
        mDarkTheme = darkTheme;
    }

    public static void setBigText(boolean b) {
        mBigtext = b;
    }

    void setLongClickListener(View.OnLongClickListener l) {
        mListener = l;
    }

    void setIdeaText(String ideaText) {
        mIdea = ideaText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mLayout = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.horizontal_item_view, mRecyclerView, false);
        return new MyViewHolder(mLayout);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //priority tag
        holder.priorityTag.setVisibility(View.GONE);
        //tab tag
        holder.tabTag.setVisibility(View.GONE);
        LinearLayout container = (LinearLayout) holder.txtView.getParent();

        //database helper
        DatabaseHelper helper = DatabaseHelper.getInstance(mRecyclerView.getContext());

        //TEXT SIZE
        if (mBigtext) {
            holder.txtView.setTextSize(22);
        } else {
            holder.txtView.setTextSize(18);
        }

        //SEARCH TAB
        int tab = mTabNumber; //The tab number the idea belongs to
        if (mTabNumber == 4) {//Search tab
            tab = helper.getTabById((int) mRecyclerView.getTag());

            if (position == 1) {//Idea text and tabTag
                holder.tabTag.setVisibility(View.VISIBLE);
                if (mDarkTheme) {
                    holder.tabTag.setTextColor(Color.WHITE);
                } else {
                    holder.tabTag.setTextColor(Color.BLACK);
                }
                switch (tab) {
                    case 1:
                        holder.tabTag.setText(R.string.first_tab);
                        break;

                    case 2:
                        holder.tabTag.setText(R.string.second_tab);
                        break;

                    case 3:
                        holder.tabTag.setText(R.string.third_tab);
                        break;
                }
            }
        }

        //Fill the textView with the right content
        switch (tab) {
            case 1: //TAB#1 IDEA
                switch (position) {
                    case 0: //Quick action send to DONE
                        holder.txtView.setText(R.string.done_caps);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        container.setBackgroundResource(R.color.md_green_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        setupIdeaTile(holder, container);
                        //Priority tag
                        setupPriorityTag(holder, helper);
                        break;

                    case 2: //Quick action send to LATER
                        holder.txtView.setText(R.string.later);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        container.setBackgroundResource(R.color.md_pink_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 2: //TAB#2 LATER
                switch (position) {
                    case 0: //Quick action send to NOW
                        holder.txtView.setText(R.string.now);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        container.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        setupIdeaTile(holder, container);
                        //Priority tag
                        setupPriorityTag(holder, helper);
                        break;

                    case 2: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        container.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 3: //TAB#3 DONE
                switch (position) {
                    case 0: //Quick action send to IDEAS
                        holder.txtView.setText(R.string.recover);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        container.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //idea text
                        setupIdeaTile(holder, container);
                        //priority tag is not required for DONE tab
                        break;

                    case 2: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        container.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;
        }
    }

    private void setupPriorityTag(MyViewHolder holder, DatabaseHelper helper) {
        holder.priorityTag.setVisibility(View.VISIBLE);
        holder.priorityTag.setBackgroundResource(helper.getPriorityColorById((int) mRecyclerView.getTag()));
    }

    private void setupIdeaTile(MyViewHolder holder, LinearLayout container) {
        holder.txtView.setSingleLine();
        holder.txtView.setText(mIdea);
        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        if (mDarkTheme) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                container.setBackgroundResource(R.drawable.grey_ripple);
            } else {
                container.setBackgroundResource(R.color.md_grey_800);
            }
            holder.txtView.setTextColor(Color.WHITE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                container.setBackgroundResource(R.drawable.white_ripple);
            } else {
                container.setBackgroundResource(R.color.white);
            }
            holder.txtView.setTextColor(Color.BLACK);
        }

        //Listeners
        RecyclerOnClickListener listener = new RecyclerOnClickListener(mRecyclerView, mTabNumber);
        container.setOnClickListener(listener);
        container.setOnLongClickListener(mListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = (MyRecyclerView) recyclerView;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public int getTabNumber() {
        return mTabNumber;
    }

    public View getLayout() {
        return mLayout;
    }

}