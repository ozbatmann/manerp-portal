package tr.com.manerp.user

import grails.databinding.BindingFormat
import tr.com.manerp.base.domain.BaseDomain

class Person implements BaseDomain {

    String name
    String surname
    @BindingFormat('dd/MM/yyyy')
    Date birthDate
    String nationality
    String identificationNumber
    String gender
    String email

    static hasMany = []

    static constraints = {

        name  nullable: false, blank:false
        surname nullable:false, blank:false
        birthDate nullable:true,blank:true
        nationality nullable: true, blank: true
        identificationNumber nullable: true, blank: true
        gender  nullable: true,blank:false
        email nullable: true, blank:false
    }
}
