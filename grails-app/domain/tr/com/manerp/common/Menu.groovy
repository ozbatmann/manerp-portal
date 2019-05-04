package tr.com.manerp.common

import tr.com.manerp.base.domain.BaseDomain

class Menu implements BaseDomain {

    String name

    static constraints = {
        name nullable:false
    }
}
