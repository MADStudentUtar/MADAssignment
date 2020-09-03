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

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatModel, ChatAdapter.ChatViewHolder> {

    private onClickListener listener;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatModel model) {
        Picasso.get().load(model.getUrl()).into(holder.avatarImageView);
        holder.nameTextView.setText(model.getName());
        holder.lastMessageTextView.setText(model.getLastMessage());
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView lastMessageTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);

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
