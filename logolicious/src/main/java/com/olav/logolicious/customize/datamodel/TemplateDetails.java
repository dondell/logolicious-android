package com.olav.logolicious.customize.datamodel;

import android.graphics.Bitmap;

/**
 * Created by ASUS on 5/2/2017.
 */

public class TemplateDetails {

    public String template_name;
    public Bitmap bitmap;

    public TemplateDetails(String tn, Bitmap b){
        this.template_name = tn;
        this.bitmap = b;
    }

}
