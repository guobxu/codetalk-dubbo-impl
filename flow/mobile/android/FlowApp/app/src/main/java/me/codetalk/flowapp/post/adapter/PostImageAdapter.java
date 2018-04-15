package me.codetalk.flowapp.post.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.util.ImageUtils;

/**
 * Created by guobxu on 2018/1/8.
 */

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {

    private Context context;
    Map<String, String> imgPaths = new LinkedHashMap<>();

    public int seq = 1; // image sequence

    public PostImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PostImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_img, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostImageAdapter.ViewHolder holder, int position) {
        String imgKey = "", imgPath = "";
        int i = 0;
        for(Map.Entry<String, String> kv : imgPaths.entrySet()) {
            if(i++ == position) {
                imgKey = kv.getKey();
                imgPath = kv.getValue();
                break;
            }
        }

        holder.imgKey = imgKey;

        // crop
        Resources res = context.getResources();
        float tw = res.getDimension(R.dimen.post_create_img_w), th = res.getDimension(R.dimen.post_create_img_h);
        float ratio = tw/th;
        Bitmap src = BitmapFactory.decodeFile(imgPath);

        RoundedBitmapDrawable dr = ImageUtils.getCroppedRoundedBitmap(src, ratio, src.getWidth()/8);

        // set imageview
        holder.imgView.setImageDrawable(dr);
    }

    @Override
    public int getItemCount() {
        return imgPaths.size();
    }

    public void addPostImages(List<String> imgs) {
        int pos = imgPaths.size();
        for(String img : imgs) {
            imgPaths.put(img + "-" + seq++, img);
        }

        notifyItemRangeInserted(pos, imgs.size());
    }

    public List<String> getPostImages() {
        List<String> imgs = new ArrayList<>();

        for(Map.Entry<String, String> kv : imgPaths.entrySet()) {
            imgs.add(kv.getValue());
        }

        return imgs;
    }

    private int getImgKeyPos(String imgKey) {
        int i = 0;
        for(Map.Entry<String, String> kv : imgPaths.entrySet()) {
            if(imgKey.equals(kv.getKey())) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        String imgKey;

        ImageView imgView;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.post_image);
            btnDelete = itemView.findViewById(R.id.btn_delete_img);

            btnDelete.setOnClickListener(view -> {
                int pos = getImgKeyPos(imgKey);

                imgPaths.remove(imgKey);
                notifyItemRemoved(pos);

                EventBus.publish(Event.name(AppConstants.EVENT_POST_IMG_DELETE));
            });
        }

    }

}
