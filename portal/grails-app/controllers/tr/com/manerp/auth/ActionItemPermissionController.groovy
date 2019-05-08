package tr.com.manerp.auth

import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController

import javax.xml.bind.ValidationException

class ActionItemPermissionController extends BaseController{

    ActionItemPermissionService actionItemPermissionService

    static namespace = "v1"

    def save() {

        try {
            def requestParams = request.JSON
            def actionItemPermission = createActionItemPermissionRequest(requestParams)
            actionItemPermission.setActionItem(ActionItem.get(requestParams.actionItem))
            actionItemPermission.setSecuritySubjectPermission(SecuritySubjectPermission.get(requestParams.securitySubjectPermission.id))
            actionItemPermissionService.addActionItemPermission(actionItemPermission)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = actionItemPermission.id
            maneResponse.message = 'Aksiyon Elemanı - İzin başarıyla kaydedildi.'

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

    def createActionItemPermissionRequest(def requestParams) {

        String id = requestParams.id

        ActionItemPermission actionItemPermission = new ActionItemPermission()
        actionItemPermission.setId(id)
        return actionItemPermission
    }

}
