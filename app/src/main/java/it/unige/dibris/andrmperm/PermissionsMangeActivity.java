package it.unige.dibris.andrmperm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

import it.unige.dibris.rmperm.IOutput;
import it.unige.dibris.rmperm.Main;

public class PermissionsMangeActivity extends Activity {
    public static final String EXTRA_PERMISSIONSMANAGE = "EXTRA_PERMISSIONSMANAGE";
    public static final String MSG_PERMISSIONSMANAGE = "MSG_PERMISSIONSMANAGE";
    private PermissionAdapter permissionAdapter;
    private String apkpath;
    private boolean noPerms = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent intent = getIntent();
        apkpath = intent.getStringExtra(PermissionsMangeActivity.EXTRA_PERMISSIONSMANAGE);
        List<PermissionFlag> pfl = getApkPermission(apkpath);
        noPerms = pfl.isEmpty();
        if (noPerms) {
            Utilities.ShowAlertDialog(this, "NO PERMISSION", "This app doesn't require any permission");
        }
        permissionAdapter = new PermissionAdapter(this, R.layout.permcheck_item, pfl);
        ListView listView = (ListView) findViewById(R.id.permissionlist);
        listView.setAdapter(permissionAdapter);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PermissionFlag pf = (PermissionFlag) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + pf.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });
        */

        Button myButton = (Button) findViewById(R.id.button_save);
        myButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveApk();
                    }
                });

    }


    public void saveApk() {
        if (noPerms) {
            Utilities.ShowAlertDialog(this, "NO PERMISSION", "I said that this app doesn't require any permission, what are you removing?");
            return;
        }
        boolean noChecks = Iterables.all(
                permissionAdapter.getItems(),
                new Predicate<PermissionFlag>() {
                    public boolean apply(PermissionFlag pf) {
                        return !pf.isChecked();
                    }
                }
        );
        if (noChecks) {
            Utilities.ShowAlertDialog(this, "NO SELECTION", "You didn't select some permission!");
            return;
        }
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


        public PermissionAdapter(Context context, int textViewResourceId, List<PermissionFlag> resource) {
            super(context, textViewResourceId, resource);
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
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.permcheck_item, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.checked = (CheckBox) convertView.findViewById(R.id.checked);
                convertView.setTag(holder);

                holder.checked.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        PermissionFlag pf = (PermissionFlag) cb.getTag();
                        pf.setChecked(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            PermissionFlag pf = modelItems.get(position);
            holder.title.setText(pf.getName());
            holder.checked.setChecked(pf.isChecked());
            holder.checked.setTag(pf);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView title;
        CheckBox checked;
    }

}
