package Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.example.tutorial_v1.R;

import Model.courseItem;

public class topCourseAdapter extends RecyclerView.Adapter<topCourseAdapter.CustomViewHolder> {
    private final Context context;
    private final ArrayList<courseItem> courseItems;
    private ItemClickListener mItemClickListener;

    String url = "http://149.28.24.98:9000/upload/course_image/";

    public topCourseAdapter(Context context, ArrayList<courseItem> courseItems, ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
        this.context = context;
        this.courseItems = courseItems;

    }

    @NonNull
    @Override
    public topCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.top_courses_item, parent, false);
        return new CustomViewHolder(view, mItemClickListener, courseItems);
    }

    @Override
    public void onBindViewHolder(@NonNull topCourseAdapter.CustomViewHolder holder, int position) {
        courseItem item = courseItems.get(position);
        holder.titleText.setText(item.getTitle());
        Picasso.get().load(url+item.getUrl()).into(holder.imageView);
        holder.ratingBar.setRating(item.getRating());
        String strVote = NumberFormat.getInstance().format(item.getTotalVote());
        String strDiscount = NumberFormat.getInstance().format(item.getDiscount());
        holder.totalVote.setText(strVote);
        float fee = Float.parseFloat(item.getFee());
        if (fee != 0){
            float finalFee = fee - (fee *item.getDiscount()*(float) 0.01);
            if (finalFee != 0){
                NumberFormat formatter = new DecimalFormat("#,###VNĐ");
                String formattedNumber = formatter.format(finalFee);
                holder.feeText.setText(formattedNumber);
            } else {
                holder.feeText.setText("Free");
            }

        } else {
            holder.feeText.setText("Free");
        }

        if (item.getDiscount() == 0){
            holder.discountText.setText("");
        } else
        holder.discountText.setText("-" + strDiscount + "%");
    }


    @Override
    public int getItemCount() {
        return courseItems.size();
    }



    public static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imageView;
        private final TextView titleText;
        private final RatingBar ratingBar;
        private final TextView totalVote;
        private final TextView feeText;
        private final TextView discountText;
        ItemClickListener itemClickListener;
        ArrayList<courseItem> courseItems;

        public CustomViewHolder(@NonNull View itemView, ItemClickListener itemClickListener, ArrayList<courseItem> courseItems) {
            super(itemView);

            imageView = (itemView).findViewById(R.id.top_course_image);
            titleText = (itemView).findViewById(R.id.top_course_title);
            ratingBar = (itemView).findViewById(R.id.top_course_rating);
            totalVote = (itemView).findViewById(R.id.totalVote);
            feeText = (itemView).findViewById(R.id.top_cousers_fee);
            discountText = (itemView).findViewById(R.id.top_cousers_discount);
            this.itemClickListener = itemClickListener;
            this.courseItems = courseItems;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(courseItems, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onClick(ArrayList<courseItem> courseItems, int position);
    }
    //TO - DO COURSE LAYOUT
}
