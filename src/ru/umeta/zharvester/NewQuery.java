package ru.umeta.zharvester;

import ru.umeta.harvesting.base.model.Query;

import java.util.Objects;


/**
 * Created by Vladimir on 15.09.2016.
 */
public class NewQuery extends Query{
    @Override
    public int hashCode() {
        return Objects.hash(this.startURL, this.endURL, this.reg);
    }

    public NewQuery(String id, String name, String endURL, String startURL, String protocol_id, String time, String reg, String user_id, String struct_loc, String last_succ, String active) {
        super(id,name,endURL,startURL,protocol_id,time,reg,user_id,struct_loc,last_succ,active);
    }

}
