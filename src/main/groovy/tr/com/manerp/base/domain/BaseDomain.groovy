package tr.com.manerp.base.domain

import grails.databinding.BindingFormat

trait BaseDomain
{
    String id
    @BindingFormat('dd/MM/yyyy')
    Date dateCreated
    @BindingFormat('dd/MM/yyyy')
    Date lastUpdated
    boolean active

    def beforeInsert()
    {
        dateCreated = new Date()
    }

    def beforeUpdate()
    {
        lastUpdated = new Date()
    }
}