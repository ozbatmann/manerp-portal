package tr.com.manerp.auth

import grails.gorm.transactions.Transactional

@Transactional
class ActionItemPermissionService {

    def addActionItemPermission(ActionItemPermission actionItemPermission) {

        actionItemPermission.setActive(true)
        return actionItemPermission.save(flush: true, failOnError: true)
    }
}
