package appbox.ideastracker.recycleview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import appbox.ideastracker.R;

/**
 * Created by Nicklos on 13/07/2016.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private String mIdea;
    private int mTabNumber;
    private int mIdRecycler;


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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        RecyclerOnClickListener onClickListener = new RecyclerOnClickListener(mIdRecycler);

        switch(mTabNumber){
            case 1: //TAB#1 IDEA
                switch (position){
                    case 0: //LATER
                        holder.txtView.setText("LATER");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                        holder.txtView.setBackgroundResource(R.color.pink);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //IDEA
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setBackgroundResource(R.color.white);
                        holder.txtView.setTextColor(Color.BLACK);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(1,mIdRecycler);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //DONE
                        holder.txtView.setText("DONE");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                        holder.txtView.setBackgroundResource(R.color.orange);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 2: //TAB#2 LATER
                switch (position){
                    case 0: //DELETE
                        holder.txtView.setText("DELETE");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //IDEA
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setBackgroundResource(R.color.white);
                        holder.txtView.setTextColor(Color.DKGRAY);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(2,mIdRecycler);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //NOW
                        holder.txtView.setText("NOW");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                        holder.txtView.setBackgroundResource(R.color.green);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;
                }
                break;

            case 3: //TAB#3 DONE
                switch (position){
                    case 0: //DELETE
                        holder.txtView.setText("DELETE");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                        holder.txtView.setBackgroundResource(R.color.red);
                        holder.txtView.setTextColor(Color.WHITE);
                        break;

                    case 1: //IDEA
                        holder.txtView.setSingleLine();
                        holder.txtView.setText(mIdea);
                        holder.txtView.setBackgroundResource(R.color.white);
                        holder.txtView.setTextColor(Color.GRAY);
                        RecyclerOnLongClickListener onLongClickListener = new RecyclerOnLongClickListener(3,mIdRecycler);
                        holder.txtView.setOnLongClickListener(onLongClickListener);
                        holder.txtView.setOnClickListener(onClickListener);
                        break;

                    case 2: //RECOVER
                        holder.txtView.setText("RECOVER");
                        holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                        holder.txtView.setBackgroundResource(R.color.purple);
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

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public int getTabNumber(){
        return mTabNumber;
    }
}