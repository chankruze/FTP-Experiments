package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.ContextCompat;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.Item;

public class CustomFunctions {

    // Custom getDRawable function
    public static Drawable getDrawable(Context context, int resDrawable) {
        return ContextCompat.getDrawable(context, resDrawable);
    }

    // Custom getString function
    public static String getString(Context context, int resString) {
        return context.getResources().getString(resString);
    }

    // Toggle relative layout specific child properties
    public static void toggleProp(View view, int prop, boolean value){

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if(value){
            layoutParams.addRule(prop);
        }else {
            layoutParams.removeRule(prop);
        }

        view.setLayoutParams(layoutParams);
    }

    public static void fileDownload(final Context context, final Item item){
        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(getString(context, R.string.dl_confirm));
        newDialog.setMessage("Are you sure you want to download " + item.getName() + " ?");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = getIntent();
//                intent.putExtra("filePath", item.getAbsolutePath());
//                setResult(RESULT_OK, intent);
//                finish();
                Toast.makeText(context, "Downloading " + item.getName(), Toast.LENGTH_LONG).show();
            }

        });

        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        newDialog.show();
    }

    public static void showFileOperations(final Context context, View view, final Item mItem) {
        // Setup Popup Menu
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_file_operations, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.setGravity(Gravity.END);

        // setup click listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_info:
//                        fileInfo(context, mItem);
                        Toast.makeText(context, "Info selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.option_download:
                        fileDownload(context, mItem);
                        return true;
                    case R.id.option_rename:
//                        fileRename(context, mItem);
                        Toast.makeText(context, "Rename selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.option_delete:
//                        fileDelete(context, mItem);
                        Toast.makeText(context, "Delete selected", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        });

        // show popup
        optionsMenu.show();
    }
}
