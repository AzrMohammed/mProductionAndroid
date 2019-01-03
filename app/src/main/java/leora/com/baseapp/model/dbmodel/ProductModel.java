package leora.com.baseapp.model.dbmodel;

import java.io.Serializable;

/**
 * Sample Db Model
 */

public class ProductModel implements Serializable {
    public String name;
    public String slug;
    public String ref_id;
    public String status;
    public String comment;
    public String created_on;
    public String updated_on;
    public String last_sync_time;
}
