package at.fhooe.mc.android.findbuddy.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import at.fhooe.mc.android.findbuddy.R;

/**
 * Created by David on 04.02.18.
 * Class to choose the right icon and converts it from a drawable to bitmap.
 */

public class IconConverter {
    private Context context;


    public IconConverter(Context current) {
        this.context = current;
    }



    /**
     * Chooses the right icon depending on the category.
     * @return Bitmap Icon
     */
    public BitmapDescriptor getCategoryIcon(String _category) {
        Drawable iconDrawable;

        switch (_category){
            case "Bildung":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_school_black_24dp);
                break;
            case "Entspannung":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_weekend_black_24dp);
                break;
            case "Essen":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_restaurant_black_24dp);
                break;
            case "Meetup":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_people_black_24dp);
                break;
            case "Party":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_cake_black_24dp);
                break;
            case "Shopping":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_shopping_basket_black_24dp);
                break;
            case "Sport":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_directions_run_black_24dp);
                break;
            case "Unterhaltung":
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_videogame_asset_black_24dp);
                break;
            default:
                iconDrawable = context.getResources().getDrawable(R.drawable.ic_place_black_24dp);
        }
        BitmapDescriptor markerIcon = convertDrawableToBitmap(iconDrawable);

        return markerIcon;
    }

    /**
     * Converts a drawable icon to a Bitmap
     * @param icon from drawable resources
     * @return BitmapDescriptor Icon
     */
    private BitmapDescriptor convertDrawableToBitmap(Drawable icon) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        icon.setBounds(0,0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        icon.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
