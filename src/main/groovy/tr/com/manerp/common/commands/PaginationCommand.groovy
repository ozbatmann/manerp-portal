package tr.com.manerp.common.commands

import grails.validation.Validateable

class PaginationCommand implements Validateable
{

    Short limit
    Integer offset
    String sort
    String fields

    PaginationCommand(def params)
    {
        this.limit = params.limit as Short ?: 10 as Short
        this.offset = params.offset as Integer ?: 0
        this.sort = params.sort as String ?: null
        this.fields = params.fields as String ?: null
    }

    static constraints = {
        limit nullable: false, blank: false, min: 0 as Short, max: 1000 as Short
        offset nullable: false, blank: false, min: 0
        sort nullable: true, blank: true
        fields nullable: true, blank: true
    }

}
