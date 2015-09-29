package dibris.andrmperm;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unige.dibris.rmperm.IOutput;
import it.unige.dibris.rmperm.Main;

public class PermissionsMangeActivity extends ListActivity {
    public static final String MSG_PERMISSIONSMANAGE = "MSG_PERMISSIONSMANAGE";
    private PermissionAdapter permissionAdapter;
    private String apkpath;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton != null) {
                final int position = getListView().getPositionForView(compoundButton);
                if (position != ListView.INVALID_POSITION) {
                    PermissionsMangeActivity.this.permissionAdapter.getItem(position).setChecked(b);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent intent = getIntent();
        apkpath = intent.getStringExtra(FileManagerActivity.MSG_FILEMANAGER);
        permissionAdapter = new PermissionAdapter(this, getApkPermission(apkpath));
        setListAdapter(permissionAdapter);
    }


    public void saveApk(View view) {
        StringBuilder sb = new StringBuilder();
        for(PermissionFlag pf : permissionAdapter.getItems()) {
            if(pf.isChecked()) {
                sb.append(pf.getName()+",");
            }
        }
        sb.setLength(sb.length()-1);
        String[] apkAndPerms = new String[2];
        apkAndPerms[0] = apkpath;
        apkAndPerms[1] = sb.toString();

        Intent intent = new Intent(this, SaveApkService.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray(MSG_PERMISSIONSMANAGE, apkAndPerms);
        intent.putExtras(bundle);
        startService(intent);

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private List<PermissionFlag> getApkPermission(String apkPath) {
        JarOutput jo = new JarOutput(IOutput.Level.VERBOSE);
        String[] args = {"--input" , apkPath, "--list"};
        Main.androidMain(jo, args);
        String[] msgs = jo.getMessages().split("\n");
        List<PermissionFlag> permissions = new ArrayList<>();
        for (int i = 1; i < msgs.length-3; i++) {
            permissions.add(new PermissionFlag(msgs[i]));
        }
        return permissions;
    }

    public class PermissionAdapter extends ArrayAdapter<PermissionFlag> {
        private final Context context;
        private final List<PermissionFlag> modelItems;


        public PermissionAdapter(Context context, List<PermissionFlag> resource) {
            super(context,R.layout.list_item,resource);
            this.context = context;
            this.modelItems = resource;
        }

        public List<PermissionFlag> getItems() {
            return modelItems;
        }

        @Override
        public int getCount() {
            return modelItems != null ? modelItems.size() : 0;
        }

        @Override
        public PermissionFlag getItem(int i) {
            return modelItems != null ? modelItems.get(i) : null;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {

                // inflate the layout, see how we can use this context reference?
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item, parent, false);

                // we'll set up the ViewHolder
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.enabled = (CheckBox) convertView.findViewById(R.id.checked);
                viewHolder.enabled.setOnCheckedChangeListener(checkedChangeListener);
                convertView.setTag(viewHolder);
            } else {
                // we've just avoided calling findViewById() on resource every time
                // just use the viewHolder instead
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // object item based on the position
            PermissionFlag pf = modelItems.get(position);

            // assign values if the object is not null
            if (modelItems != null) {
                // get the TextView from the ViewHolder and then set the text (item name) and other values
                viewHolder.title.setText(pf.getName());
                viewHolder.time.setText("descrizione");
                viewHolder.enabled.setChecked(pf.isChecked());
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView title;
        TextView time;
        CheckBox enabled;
    }

}
