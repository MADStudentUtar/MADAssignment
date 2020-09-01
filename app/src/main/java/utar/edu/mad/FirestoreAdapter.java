package utar.edu.mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<SongModel, FirestoreAdapter.SongsViewHolder> {

    private OnListItemClick onListItemClick;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<SongModel> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull SongsViewHolder holder, int position, @NonNull SongModel model) {
        holder.songTitle.setText(model.getSong_title());
        holder.songArtist.setText(model.getArtist());
        Picasso.get().load(model.getSong_img()).into(holder.albumImg);
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new SongsViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView songTitle;
        private TextView songArtist;
        private ImageView albumImg;
        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            albumImg = itemView.findViewById(R.id.song_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());

        }
    }

    public interface OnListItemClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

}
