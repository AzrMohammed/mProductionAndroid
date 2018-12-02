package leora.com.baseapp.model;

import android.app.Activity;

/**
 * Created by AZR on 26-11-2018.
 */

public class HomeListModel {


    public HomeListModel(String name, String description, int img_id, Class to_class)
    {
        this.name = name;
        this.description = description;
        this.img_id = img_id;
        this.to_class = to_class;
    }

   public String name;
   public String description;
   public int img_id;
   public Class to_class;
}
