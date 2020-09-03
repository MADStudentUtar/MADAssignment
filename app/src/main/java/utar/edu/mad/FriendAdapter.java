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

public class FriendAdapter extends FirestoreRecyclerAdapter<FindFriend, FriendAdapter.FriendViewHolder> {

    private onClickListener listener;

    public FriendAdapter(@NonNull FirestoreRecyclerOptions<FindFriend> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull FindFriend model) {
        holder.friendName.setText(model.getName());
        holder.friendBio.setText(model.getBio());
        Picasso.get().load(model.getUrl()).into(holder.friendImageView);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new FriendViewHolder(view);
    }

    class FriendViewHolder extends RecyclerView.ViewHolder{

        private ImageView friendImageView;
        private TextView friendName, friendBio;

        public FriendViewHolder(View itemView) {
            super(itemView);

            friendImageView = itemView.findViewById(R.id.contactImageView);
            friendName = itemView.findViewById(R.id.nameContactTV);
            friendBio = itemView.findViewById(R.id.contactBioTV);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface onClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnClickListener(onClickListener listener){
        this.listener = listener;
    }
}
