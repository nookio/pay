package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by nookio on 15/5/12.
 */
@Entity
@Table(name="users")
public class User extends Model {


    public static Short PAY_TYPE_ONLINE = 1;

    public static Integer SCAN_PAY_USER =88888888;


    public static Short TYPE_EMPLOYEE = 1 ;
    public static Short TYPE_BOSS = 2;
    @Id
    public Integer id;
    public Short payed;
   // @Column(name="payed_date"
    public Date payedDate;
   // @Column(name="pay_type")
    public Short payType;
   // @Column(name="expired_date")
    public Date expiredDate;
   // @Column(name="shopId")
    public Integer shopId;
    // @Column(name="companyId")
    public Integer companyId;
    // @Column(name="userType")
    public Short userType;
    //@Column(name="updated_at")
    public String token;
    @UpdatedTimestamp
    public Date updatedAt;
}
