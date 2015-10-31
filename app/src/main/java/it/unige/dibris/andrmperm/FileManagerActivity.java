package it.unige.dibris.andrmperm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

public class FileManagerActivity extends Activity implements IFolderItemListener {
    FolderLayout localFolders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        localFolders = (FolderLayout)findViewById(R.id.localfolders);
        localFolders.setIFolderItemListener(this);
        localFolders.setDir("./sdcard"); // default directory

    }

    // when you can't read a file
    public void OnCannotFileRead(File file) {
        new AlertDialog.Builder(this)
                .setTitle(
                        "[" + file.getName() + "] folder can't be read!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();

    }

    // when you click a file
    public void OnFileClicked(File file) {
        Intent intent = new Intent(this, PermissionsMangeActivity.class);
        intent.putExtra(PermissionsMangeActivity.EXTRA_PERMISSIONSMANAGE, file.getAbsolutePath());
        startActivity(intent);
    }

}