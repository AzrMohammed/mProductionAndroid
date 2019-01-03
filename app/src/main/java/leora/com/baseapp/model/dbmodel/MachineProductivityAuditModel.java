package leora.com.baseapp.model.dbmodel;

import java.io.Serializable;

/**
 * Created by AZR on 06-12-2018.
 */

public class MachineProductivityAuditModel implements Serializable {

    public String slug;
    public String machine_slug;
    public String product_slug;
    public String process_slug;
    public String process_description;
    public String job_order_id;
    public String work_done_from_time;
    public String work_done_to_time;
    public String quantity_approved;
    public String quantity_rejected;
    public String quantity_rework;
    public String operator_name;
    public String supervisor_name;
    public String is_rework;
    public String is_supervisor_approved;
    public String comment;
    public String status;
    public String created_on;
    public String updated_on;

    public PMachineModel pMachineModel = new PMachineModel();
    public ProductModel productModel = new ProductModel();


}