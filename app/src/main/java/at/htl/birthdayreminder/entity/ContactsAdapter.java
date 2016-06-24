package at.htl.birthdayreminder.entity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.htl.birthdayreminder.R;

/**
 * Created by Sabrina on 16.05.2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{
    private List<Contact> contactsList;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, birthday, age;

        public MyViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.birthday = (TextView) view.findViewById(R.id.birthday);
            this.age = (TextView) view.findViewById(R.id.age);
        }
    }
    public ContactsAdapter(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.birthday_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = contactsList.get(position);
        holder.name.setText(contact.getName());
        holder.birthday.setText(contact.getBirthdayToString());
        holder.age.setText("("+contact.getAgeToString());
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}
