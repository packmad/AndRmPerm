package dibris.andrmperm;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveApkResult extends Activity {
    private MyListAdapter mAdapter;
    private String newApkPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveapkresult);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String[] apkAndMsgs = b.getStringArray(SaveApkService.MSG_SAVEAPKSERVICE);
        newApkPath = apkAndMsgs[0];
        ArrayList<String> errors = new ArrayList<String>(Arrays.asList(apkAndMsgs[1].split("\n")));
        ArrayList<String> messages = new ArrayList<String>(Arrays.asList(apkAndMsgs[2].split("\n")));
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(errors);
        all.addAll(messages);
        if (all.size() == 0) {
            all.add("No messages... it's strange!");
        }
        ListView list = (ListView) findViewById(R.id.resultlist);
        mAdapter = new MyListAdapter(this, all);
        list.setAdapter(mAdapter);
    }

    public void installApk(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(newApkPath)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class MyListAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mData;

        public MyListAdapter(final Context context, final List<String> mData) {
            this.mData = mData;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return mData != null ? mData.get(i) : null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtView;
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.txt_item, parent, false);
                txtView = (TextView) convertView.findViewById(R.id.message);
                convertView.setTag(txtView);

            } else {
                txtView = (TextView) convertView.getTag();
            }
            String str = mData.get(position);
            if (mData != null && txtView!=null) {
                txtView.setText(str);
            }
            return convertView;
        }

    }

}
