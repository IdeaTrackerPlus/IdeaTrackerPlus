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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
        }
    }


    public HorizontalAdapter(String text) {
        mIdea = text;
    }

    public void editText(String newText){
        mIdea = newText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        switch (position){
            case 0: //LATER
                holder.txtView.setText("LATER");
                holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                holder.txtView.setBackgroundResource(R.color.red);
                holder.txtView.setTextColor(Color.WHITE);
                break;

            case 1: //IDEA
                holder.txtView.setText(mIdea);
                break;

            case 2: //DONE
                holder.txtView.setText("DONE");
                holder.txtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                holder.txtView.setBackgroundResource(R.color.orange);
                holder.txtView.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}