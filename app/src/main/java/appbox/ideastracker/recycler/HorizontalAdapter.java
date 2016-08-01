package appbox.ideastracker.recycler;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import appbox.ideastracker.R;

/**
 * Created by Nicklos on 13/07/2016.
 * Adapter for the horizontal RecyclerView containing the idea
 * as well as the left and right quick actions.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private String mIdea;
    private int mTabNumber;


    private int mIdRecycler; //RecylerView tag, also idea's id in database
    private MyRecyclerView mRecyclerView;

    // same text size for all ideas
    private static boolean mBigtext = false;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
        }
    }


    public HorizontalAdapter(String text,int tabNumber) {
        mIdea = text;
        mTabNumber = tabNumber;
    }

    public static void setBigText(boolean b){
        mBigtext = b;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(mBigtext){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view_big, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        RecyclerOnClickListener onClickListener = new RecyclerOnClickListener(mRecyclerView);

        //Fill the textView with the right content
        switch(mTabNumber){
            case 1: //TAB#1 IDEA

                switch (position){
                    case 0: //Quick action send to LATER
                        holder.txtView.setText(R.string.later);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.md_pink_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        }else{
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.BLACK);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(1,mIdRecycler);
                        onClickListener.setOtherListener(onLongClickListener);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //Quick action send to DONE
                        holder.txtView.setText(R.string.done_caps);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.md_green_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 2: //TAB#2 LATER

                switch (position){
                    case 0: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //Idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        }else{
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.DKGRAY);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(2,mIdRecycler);
                        onClickListener.setOtherListener(onLongClickListener);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //Quick action send to NOW
                        holder.txtView.setText(R.string.now);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 3: //TAB#3 DONE

                switch (position){
                    case 0: //Quick action send to DELETE
                        holder.txtView.setText(R.string.delete);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //idea text
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.txtView.setBackgroundResource(R.drawable.white_ripple);
                        }else{
                            holder.txtView.setBackgroundResource(R.color.white);
                        }
                        holder.txtView.setTextColor(Color.GRAY);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(3,mIdRecycler);
                        onClickListener.setOtherListener(onLongClickListener);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //Quick action send to RECOVER
                        holder.txtView.setText(R.string.recover);
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                        holder.txtView.setBackgroundResource(R.color.md_indigo_a400);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIdRecycler = (Integer) recyclerView.getTag();
        mRecyclerView = (MyRecyclerView) recyclerView;

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public int getTabNumber(){
        return mTabNumber;
    }
}