package appbox.ideastracker.recycler;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import appbox.ideastracker.R;
import appbox.ideastracker.database.DatabaseHelper;

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


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public View priorityTag;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
            priorityTag = view.findViewById(R.id.priorityTag);
        }
    }


    public HorizontalAdapter(Context context, String text, int tabNumber) {
        mIdea = text;
        mTabNumber = tabNumber;
        mLayout = LayoutInflater.from(context).inflate(R.layout.horizontal_item_view, null, false);

    }

    public static void setBigText(boolean b) {
        mBigtext = b;
    }

    public void setLongClickListener(View.OnLongClickListener l) {
        mListener = l;
    }

    public void setIdeaText(String ideaText) {
        mIdea = ideaText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mLayout = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.horizontal_item_view, mRecyclerView, false);
        return new MyViewHolder(mLayout);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.priorityTag.setVisibility(View.GONE);
        DatabaseHelper helper = DatabaseHelper.getInstance(mRecyclerView.getContext());

        if (mBigtext) {
            holder.txtView.setTextSize(22);
        } else {
            holder.txtView.setTextSize(18);
        }

        //Fill the textView with the right content
        switch (mTabNumber) {
            case 1: //TAB#1 IDEA

                switch (position) {
                    case 0: //Quick action send to DONE
                        holder.txtView.setText(R.string.done_caps);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.md_green_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        } else {
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.BLACK);
                        //Listeners
                        RecyclerOnClickListener listener = new RecyclerOnClickListener(mRecyclerView, mTabNumber);
                        holder.txtView.setOnClickListener(listener);
                        holder.txtView.setOnLongClickListener(mListener);
                        //Priority tag
                        holder.priorityTag.setVisibility(View.VISIBLE);
                        holder.priorityTag.setBackgroundResource(helper.getPriorityColorById((int) mRecyclerView.getTag()));
                        break;

                    case 2: //Quick action send to LATER
                        holder.txtView.setText(R.string.later);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.md_pink_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 2: //TAB#2 LATER

                switch (position) {
                    case 0: //Quick action send to NOW
                        holder.txtView.setText(R.string.now);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        } else {
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.DKGRAY);
                        //Listeners
                        RecyclerOnClickListener listener = new RecyclerOnClickListener(mRecyclerView, mTabNumber);
                        holder.txtView.setOnClickListener(listener);
                        holder.txtView.setOnLongClickListener(mListener);
                        holder.priorityTag.setVisibility(View.VISIBLE);
                        holder.priorityTag.setBackgroundResource(helper.getPriorityColorById((int) mRecyclerView.getTag()));
                        break;

                    case 2: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 3: //TAB#3 DONE

                switch (position) {
                    case 0: //Quick action send to IDEAS
                        holder.txtView.setText(R.string.recover);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        } else {
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.GRAY);
                        //Listeners
                        RecyclerOnClickListener listener = new RecyclerOnClickListener(mRecyclerView, mTabNumber);
                        holder.txtView.setOnClickListener(listener);
                        holder.txtView.setOnLongClickListener(mListener);
                        break;

                    case 2: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

        }

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