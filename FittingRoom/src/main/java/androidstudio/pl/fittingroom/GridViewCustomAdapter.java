package androidstudio.pl.fittingroom;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridViewCustomAdapter extends BaseAdapter {
    private final FittingRoom fittingRoom;

    public GridViewCustomAdapter(FittingRoom fittingRoom) {
        this.fittingRoom = fittingRoom;
    }

    @Override
    public int getCount() {
        return fittingRoom.downloadSettingsTask.alertRoomNameList.size();
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
        TextView textView = new TextView(fittingRoom);
        textView.setText(fittingRoom.downloadSettingsTask.alertRoomNameList.get(i));
        return textView;
    }
}
