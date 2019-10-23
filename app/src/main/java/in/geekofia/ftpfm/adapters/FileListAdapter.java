package in.geekofia.ftpfm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.List;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.Item;
import in.geekofia.ftpfm.utils.CacheView;

import static in.geekofia.ftpfm.utils.CustomFunctions.*;

public class FileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Item> listItem;
    private Item item;
    private LayoutInflater inflater;

    public FileListAdapter(Context context, List<Item> list){
        mContext = context;
        listItem = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        item = listItem.get(position);

        if(view == null){
            CacheView cache = new CacheView();
            view = inflater.inflate(R.layout.file_item, null);
            cache.name = view.findViewById(R.id.file_name);
            cache.icon = view.findViewById(R.id.file_icon);
            cache.details = view.findViewById(R.id.file_details);
            cache.size = view.findViewById(R.id.file_size);
            cache.date = view.findViewById(R.id.file_date);
            cache.time = view.findViewById(R.id.file_time);
            cache.type = view.findViewById(R.id.file_type);

            view.setTag(cache);
        }

        //Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();

        String mDetails = mContext.getResources().getString(R.string.fs_unit_bytes, item.getSize());

        switch(item.getTypeItem()){
            case Item.DIRECTORY:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, true);
                cache.details.setVisibility(View.GONE);
                if(item.getNumItems() > 0){
                    cache.icon.setImageDrawable(fetchDrawable(mContext, item.getIconId()));
                }
                else{
                    cache.icon.setImageDrawable(fetchDrawable(mContext, R.drawable.ic_folder));
                }
                break;
            case Item.FILE:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.date.setText(item.getDate());
                cache.time.setText(item.getTime());
                cache.icon.setImageDrawable(fetchDrawable(mContext, item.getIconId()));
                cache.type.setText(fetchString(mContext, item.getTypeId()));
                break;
            case Item.UP:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, true);
                cache.details.setVisibility(View.GONE);
                cache.icon.setImageDrawable(fetchDrawable(mContext, item.getIconId()));
                break;
        }

        return view;
    }
}
