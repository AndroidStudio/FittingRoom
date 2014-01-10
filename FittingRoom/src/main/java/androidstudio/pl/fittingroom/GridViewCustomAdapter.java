package androidstudio.pl.fittingroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GridViewCustomAdapter extends BaseAdapter {
    private final FittingRoom activity;
    private final List<String> alertRoomNameList;
    private final List<String> alertRoomStatusList;
    private final Bitmap IconImage;
    private final Bitmap IdleIconImage;
    private final LayoutInflater layoutInflater;


    public GridViewCustomAdapter(FittingRoom fittingRoom, List<String> alertRoomNameList, List<String> alertRoomStatusList, Bitmap IconImage, Bitmap IdleIconImage) {
        this.activity = fittingRoom;
        this.alertRoomNameList = alertRoomNameList;
        this.alertRoomStatusList = alertRoomStatusList;
        this.IconImage = IconImage;
        this.IdleIconImage = IdleIconImage;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return alertRoomNameList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = layoutInflater.inflate(R.layout.gridview_item, null);
                if (view != null) {
                    holder.textViewName = (TextView) view.findViewById(R.id.name);
                    holder.textViewName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0.1f * activity.screenWidth);
                    holder.textViewStatus = (TextView) view.findViewById(R.id.status);
                    holder.imageView = (ImageView) view.findViewById(R.id.icon);
                    view.setTag(holder);
                }
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (alertRoomStatusList.get(i).equals("Idle")) {
                holder.imageView.setImageBitmap(IdleIconImage);
            } else {
                holder.imageView.setImageBitmap(IconImage);
            }
            holder.textViewName.setText(alertRoomNameList.get(i));
            holder.textViewStatus.setText(alertRoomStatusList.get(i));
            return view;
        } catch (Exception e) {
            return new View(activity);
        }
    }

    private class ViewHolder {
        public TextView textViewName;
        public TextView textViewStatus;
        public ImageView imageView;
    }
}