package helperclasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import modelobjects.Post;
import tulipdev.nursinghomemng.R;

/**
 * Created by em on 11/11/17.
 */

public class PostsArrayAdapter extends ArrayAdapter {
    private ArrayList<Post> posts;
    private Context context;
    private int layoutId;
    ItemHolder itemHolder;

    public PostsArrayAdapter(@NonNull Context context, @NonNull int resource, @NonNull ArrayList<Post> objects) {
        super(context, resource, objects);
        this.posts = objects;
        this.context = context;
        this.layoutId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);

            itemHolder = new ItemHolder();
            itemHolder.ptitle = convertView.findViewById(R.id.ptitle);
            itemHolder.pqual = convertView.findViewById(R.id.pqual);

            convertView.setTag(itemHolder);
        }
        else{
            itemHolder = (ItemHolder) convertView.getTag();
        }

        Post post = posts.get(position);
        itemHolder.ptitle.setText(post.title);
        itemHolder.pqual.setText(post.qualifications);
        return  convertView;
    }

    static class ItemHolder{
        TextView ptitle, pqual;
    }
}
