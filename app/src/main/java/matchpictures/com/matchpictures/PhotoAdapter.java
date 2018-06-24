package matchpictures.com.matchpictures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.List;
import matchpictures.com.matchpictures.model.PhotoItem;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<PhotoItem> listOfPhotoItems;
    private Context context;
    private PhotoItemClickListener photoItemClickListener;

    public PhotoAdapter(Context context, PhotoItemClickListener photoItemClickListener) {
        this.context = context;
        this.photoItemClickListener = photoItemClickListener;
    }

    public void setListOfPhotoItems(List<PhotoItem> listOfPhotoItems) {
        this.listOfPhotoItems = listOfPhotoItems;
    }

    @NonNull @Override public PhotoViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.photo_item_view, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override public void onBindViewHolder(
        @NonNull PhotoViewHolder holder, int position) {
        Glide.with(context).load(listOfPhotoItems.get(position).getUrl()).into(holder.ivPhoto);
        holder.itemView.setOnClickListener(
            view -> photoItemClickListener.onClick(view, listOfPhotoItems.get(position), position));

    }

    @Override public int getItemCount() {
        return listOfPhotoItems.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public PhotoViewHolder(View view) {
            super(view);
            ivPhoto = view.findViewById(R.id.iv_photo_item);
        }
    }

    public interface PhotoItemClickListener {
        void onClick(View view, PhotoItem photoItem, int position);
    }
}
