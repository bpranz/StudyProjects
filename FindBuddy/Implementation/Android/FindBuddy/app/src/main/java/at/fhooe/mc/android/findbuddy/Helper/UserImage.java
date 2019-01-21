package at.fhooe.mc.android.findbuddy.Helper;

import android.widget.ImageView;

/**
 * Created by Laurenz on 04.02.2018.
 */

public class UserImage {

    private ImageView userProfilimage;


    public UserImage(ImageView view) {
        this.userProfilimage = view;
    }

    public UserImage() {


    }


    public ImageView getUserProfilimage() {
        return userProfilimage;
    }

    public void setUserProfilimage(ImageView userProfilimage) {
        this.userProfilimage = userProfilimage;
    }

 }

