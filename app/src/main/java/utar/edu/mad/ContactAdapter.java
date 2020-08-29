package utar.edu.mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Context mContext;
    List<Contact> contactList;

    public ContactAdapter(Context mContext, List<Contact> contactList){
        this.mContext = mContext;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        Contact contact = contactList.get(position);
        holder.name_contact.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        if (contact.getPhoto() !=null) {
            Picasso.get().load(contact.getPhoto()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name_contact, phone;
        ImageView imageView;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name_contact = itemView.findViewById(R.id.nameContactTV);
            phone = itemView.findViewById(R.id.PhoneTV);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
