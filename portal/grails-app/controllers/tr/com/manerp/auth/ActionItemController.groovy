package tr.com.manerp.auth

import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class ActionItemController extends BaseController{

    ActionItemService actionItemService

    static namespace = "v1"

    def index() {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginationProperties pagination = new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields)
            ManePaginatedResult result = actionItemService.getAllActionItemList(pagination)

            result.data = result.data.collect{
                return[id:it.id, controllerName: it.controllerName, actionName: it.actionName, shouldBeCleaned: it.shouldBeCleaned]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if (maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save() {

        def requestParams = request.JSON

        ManeResponse maneResponse = new ManeResponse()

        try {
            def actionItem = createActionItemRequest(requestParams)
            def result = [id: actionItemService.addActionItem(actionItem).id]
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = user.id
            maneResponse.message = "Aksiyon elemanı başarıyla kaydedilmiştir"

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def actionItemPermissions(){
        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginationProperties pagination = new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields)
            ManePaginatedResult result = actionItemService.getAllActionItemPermListByActionItem(pagination, params.id)

            result.data = result.data.collect {
                return [id: it.id, securitySubject :[id: it.securitySubjectPermission.securitySubject.id, name: it.securitySubjectPermission.securitySubject.name],
                        permissionType :[id: it.securitySubjectPermission.permissionType.id, name: it.securitySubjectPermission.permissionType.name] ]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if (maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse

    }

    def createActionItemRequest(def requestParams) {

        String id = requestParams.id
        String actionName = requestParams.actionName
        String controllerName = requestParams.controllerName

        ActionItem actionItem = new ActionItem()
        actionItem.setId(id)
        actionItem.setActionName(actionName)
        actionItem.setControllerName(controllerName)
        actionItem.setShouldBeCleaned(requestParams.shouldBeCleaned)

        return actionItem
    }

    /*
        Au1thorization plugin'inden rest servis kullanilarak actionItem db senkronizasyonu saglanacak.
     */
    def synchronizeActionItemDB(){

        try {

            render(new ManeResponse().setData(actionItemService.synchronizeActionItemDB(request.JSON.controllerMethodList)))
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
    }

    /*
        ActionItem redis-db senkronizasyonu saglanacak.
     */
    def synchronizeActionItemRedis(){

        try {

            render(new ManeResponse().setData(actionItemService.synchronizeActionItemRedis(request.JSON.allCleaned.asBoolean())))
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
    }
}
