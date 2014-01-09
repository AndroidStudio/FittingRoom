package androidstudio.pl.fittingroom;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class GridViewCustomAdapter extends BaseAdapter {
    private final FittingRoom fittingRoom;
    private final List<String> alertRoomNameList;
    private final List<String> alertRoomStatusList;
    private final Bitmap IconImage;
    private final Bitmap IdleIconImage;


    public GridViewCustomAdapter(FittingRoom fittingRoom, List<String> alertRoomNameList, List<String> alertRoomStatusList, Bitmap IconImage, Bitmap IdleIconImage) {
        this.fittingRoom = fittingRoom;
        this.alertRoomNameList = alertRoomNameList;
        this.alertRoomStatusList = alertRoomStatusList;
        this.IconImage = IconImage;
        this.IdleIconImage = IdleIconImage;
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
            final RelativeLayout relativeLayout = new RelativeLayout(fittingRoom);
            final ImageView imageView = new ImageView(fittingRoom);

            if (alertRoomStatusList.get(i).equals("Idle")) {
                imageView.setImageBitmap(IdleIconImage);
            } else {
                imageView.setImageBitmap(IconImage);
            }

            final RelativeLayout.LayoutParams layoutParamsTextView = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTextView.addRule(RelativeLayout.CENTER_IN_PARENT);

            final RelativeLayout.LayoutParams layoutParamsImage = new RelativeLayout.LayoutParams(
                    fittingRoom.screenWidth / 5, fittingRoom.screenWidth / 5);
            layoutParamsImage.addRule(RelativeLayout.CENTER_IN_PARENT);

            final TextView textView = new TextView(fittingRoom);
            textView.setText(alertRoomNameList.get(i));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0.1f * fittingRoom.screenWidth);
            textView.setTextColor(Color.BLACK);
            textView.setId(39874528);

            final TextView textViewStatus = new TextView(fittingRoom);
            textViewStatus.setText(alertRoomStatusList.get(i));
            textViewStatus.setId(39874529);
            textViewStatus.setVisibility(View.INVISIBLE);

            relativeLayout.addView(imageView, layoutParamsImage);
            relativeLayout.addView(textView, layoutParamsTextView);
            relativeLayout.addView(textViewStatus);
            return relativeLayout;
        } catch (Exception e) {
            return new View(fittingRoom);
        }
    }
}
