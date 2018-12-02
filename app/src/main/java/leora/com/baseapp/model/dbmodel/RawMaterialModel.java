package leora.com.baseapp.model.dbmodel;

import java.io.Serializable;

/**
 * Sample Db Model
 */

public class RawMaterialModel implements Serializable {
    public String name;
    public String slug;
    public String type;
    public String ref_id;
    public String length;
    public String comment;
    public String created_on;
    public String updated_on;
    public String last_sync_time;
}
