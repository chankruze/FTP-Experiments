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
            cache.date = view.findViewById(R.id.file_date);
            cache.size = view.findViewById(R.id.file_size);
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
                    cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_folder));
                }
                else{
                    cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_folder));
                }
                break;
            case Item.FILE:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_generic));
                break;
            case Item.FILE_APK:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_apk));
                break;
            case Item.FILE_IMAGE:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_image));
                break;
            case Item.FILE_AUDIO:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_audio));
                break;
            case Item.FILE_VIDEO:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_video));
                break;
            case Item.FILE_PDF:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_pdf));
                break;
            case Item.FILE_DOCUMENT:
                cache.name.setText(item.getName());
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_document));
                break;
            case Item.FILE_GIF:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_gif));
                break;
            case Item.FILE_DB:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_db));
                break;
            case Item.FILE_ARCHIVE:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_archive));
                break;
            case Item.FILE_EXE:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_exe));
                break;
            case Item.FILE_HTML:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_html));
                break;
            case Item.FILE_CSS:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_css));
                break;
            case Item.FILE_JS:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_js));
                break;
            case Item.FILE_JSON:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_json));
                break;
            case Item.FILE_PHP:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_php));
                break;
            case Item.FILE_SHELL:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_shell));
                break;
            case Item.FILE_LUA:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_lua));
                break;
            case Item.FILE_MK:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_makefile));
                break;
            case Item.FILE_NPM:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_npm));
                break;
            case Item.FILE_PY:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_python));
                break;
            case Item.FILE_CERT:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_certificate));
                break;
            case Item.FILE_KEY:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, false);
                cache.details.setVisibility(View.VISIBLE);
                cache.size.setText(mDetails);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_file_key));
                break;
            case Item.UP:
                cache.name.setText(item.getName());
                toggleProp(cache.name, RelativeLayout.CENTER_VERTICAL, true);
                cache.details.setVisibility(View.GONE);
                cache.icon.setImageDrawable(getDrawable(mContext, R.drawable.ic_back));
                break;
        }

        return view;
    }
}
