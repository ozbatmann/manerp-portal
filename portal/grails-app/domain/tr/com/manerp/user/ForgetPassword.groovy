package tr.com.manerp.user

import grails.databinding.BindingFormat
import tr.com.manerp.base.domain.BaseDomain

class ForgetPassword implements BaseDomain{

    String username
    @BindingFormat('dd/MM/yyyy HH:mm:ss')
    Date expiredDate
    Boolean isUsed
    String uId

    static constraints = {
        username nullable: false, blank: false, unique: true
        expiredDate nullable: false, unique: false
        isUsed nullable: false, unique: false
        uId nullable: false, unique: true
    }

}
