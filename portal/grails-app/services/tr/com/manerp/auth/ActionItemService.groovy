package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import grails.plugins.redis.RedisService
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.pagination.ManePaginationService
import org.grails.web.json.JSONArray
import tr.com.manerp.base.service.BaseService
import tr.com.manerp.redis.RedisSyncService

@Transactional
class ActionItemService extends BaseService{

    RedisSyncService redisSyncService

    def getAllActionItemList(ManePaginationProperties properties){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(ActionItem, properties, closure)
    }

    def addActionItem(ActionItem actionItem) {

        // TODO: validation exception plugini eklenecek

        actionItem.setActive(true)
        actionItem = actionItem.save(flush: true, failOnError: true)
        redisSyncService.activateActionItemInRedis(actionItem)

    }

    def getAllActionItemPermListByActionItem(ManePaginationProperties pagination, String actionItemId){
        def customClosure = {
            actionItem{
                eq("id", actionItemId)
            }
        }
        return paginate(ActionItemPermission,pagination,customClosure)
    }

    boolean synchronizeActionItemDB(JSONArray controllerMethodList) {


        List<String> actionItemIdList = new ArrayList<String>()

        controllerMethodList.each {controllerMethod->

            List<SecuritySubjectPermission> securitySubjectPermList = saveSecuritySubjectPermList(controllerMethod.getAt("authPermList"))
            ActionItem actionItem = saveActionItem(controllerMethod.getAt("controllerName"),controllerMethod.getAt("actionName"))
            actionItemIdList.add(actionItem.id)

            securitySubjectPermList.each {secSubPerm->

                ActionItemPermission actionItemPermission = ActionItemPermission.createCriteria().get {

                    eq("actionItem",actionItem)
                    eq("securitySubjectPermission",secSubPerm)
                }

                if(actionItemPermission == null){

                    actionItemPermission = new ActionItemPermission()
                    actionItemPermission.actionItem = actionItem
                    actionItemPermission.securitySubjectPermission = secSubPerm

                    actionItemPermission = actionItemPermission.save(flush: true)
                }
            }
        }

        updateActionItemToBeCleaned(actionItemIdList)
        true
    }

    /*
        ActionItem'lar cok olabilecegi icin 50serli gruplar olarak gonderilecek,
        Butun gruplar gonderilip silinecek diye iz birakilan kayit varsa sonraki asamada silinecek.
     */
    void updateActionItemToBeCleaned(List<String> actionItemIdList) {

        List<ActionItem> actionItemList = ActionItem.createCriteria().list {

            ne("shouldBeCleaned",true)
            not { 'in'("id",actionItemIdList) }

        } as List

        actionItemList.each {actionItem->

            actionItem.shouldBeCleaned = true
            actionItem = actionItem.save(flush: true)
        }
    }

    ActionItem saveActionItem(String controllerName, String actionName) {

        boolean isNewActionItem = false

        ActionItem actionItem = ActionItem.createCriteria().get {

            eq("controllerName",controllerName)
            eq("actionName",actionName)
        }

        if(actionItem == null){

            actionItem = new ActionItem()
            actionItem.controllerName = controllerName
            actionItem.actionName = actionName
            isNewActionItem = true
        }

        actionItem.shouldBeCleaned = false
        actionItem = actionItem.save(flush: true)

        if(isNewActionItem){

            redisSyncService.activateActionItemInRedis(actionItem)
        }

        return actionItem
    }

    /*
        Controller ya da methodlarin basina @AuthorizationFilter[x.y] yazildigi zaman securitySubjectPermission'a karsilik gelecek,
        Eger kullanicinin o securitySubjectPermission ile her hangi bir izni varsa methodu cagirmasi saglanacak.
     */
    List<SecuritySubjectPermission> saveSecuritySubjectPermList(JSONArray authPermList){

        List<SecuritySubjectPermission> securitySubjectPermList = new ArrayList<SecuritySubjectPermission>()

        authPermList.each {authPerm->

            String [] authPermArr = authPerm.toString().split("\\.")

            SecuritySubject securitySubject = saveSecuritySubject(authPermArr[0])
            PermissionType permissionType = savePermissionType(authPermArr[1])

            SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.createCriteria().get {

                eq("securitySubject",securitySubject)
                eq("permissionType",permissionType)
            }

            if(securitySubjectPermission == null){

                securitySubjectPermission = new SecuritySubjectPermission()
                securitySubjectPermission.securitySubject = securitySubject
                securitySubjectPermission.permissionType = permissionType

                securitySubjectPermission = securitySubjectPermission.save(flush: true)
            }

            securitySubjectPermList.add(securitySubjectPermission)
        }

        return securitySubjectPermList
    }

    PermissionType savePermissionType(String authPerm){

        PermissionType permissionType = PermissionType.createCriteria().get {

            eq("name",authPerm,[ignoreCase: true])
        }

        if(permissionType == null){

            permissionType = new PermissionType()
            permissionType.name = authPerm

            permissionType = permissionType.save(flush: true)
        }

        return permissionType
    }

    SecuritySubject saveSecuritySubject(String authPerm){

        SecuritySubject securitySubject = SecuritySubject.createCriteria().get {

            eq("name",authPerm,[ignoreCase: true])
        }

        if(securitySubject == null){

            securitySubject = new SecuritySubject()
            securitySubject.name = authPerm

            securitySubject = securitySubject.save(flush: true)
        }

        return securitySubject
    }

    /*
        AllCleaned parametresinin true gelmesi demek actionItem tablosunda kayit var ise artik o methodlarinin
        hepsinin free oldugu anlamina gelir ve izin kontrolu yapilmayacaktir. DB ve redis'den silinecektir.
        True gelmemesi durumunda su case gecerlidir; shouldBeCleaned true olanlari veritabanindan ve redis'den sil.
        Silinmeyecek olanlari redis'de yok ise redis'e yaz ve kullanicilarin izinlerini kontrol edip
        eger izinleri ile actionItem izinleri kesisiyorsa her organizasyon icin farkli kaydet.
     */
    boolean synchronizeActionItemRedis(boolean allCleaned) {

        List<ActionItem> cleanActionItemList = null
        List<ActionItem> addActionItemList = null

        if(allCleaned){

            cleanActionItemList = ActionItem.createCriteria().list {

            } as List<ActionItem>
        }

        else{

            cleanActionItemList = ActionItem.createCriteria().list {

                eq("shouldBeCleaned",true)

            } as List<ActionItem>

            addActionItemList = ActionItem.createCriteria().list {

                eq("shouldBeCleaned",false)

            } as List<ActionItem>
        }

        cleanActionItemList.each {actionItem->

            ActionItem cloneActionItem = actionItem.clone() as ActionItem
            actionItem.delete(flush: true)
            redisSyncService.deactivateActionItemInRedis(cloneActionItem)
        }

        redisSyncService.synchronizeActionItemListForAllUsers()

        true
    }
}
