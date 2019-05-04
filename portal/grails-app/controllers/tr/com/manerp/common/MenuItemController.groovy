package tr.com.manerp.common

import grails.converters.JSON
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.auth.PermissionType
import tr.com.manerp.auth.SecuritySubject
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class MenuItemController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def menuItemService

    def index()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = menuItemService.getMenuItemList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields),params.menuId)
            result.data = result.data.collect{
                Menu menu = Menu.get(it.menu.id)
                MenuItem parentMenuItem = MenuItem.get(it.parent?.id)
                SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.get(it.securitySubjectPermission?.id)
                return [id  : it.id, name: it.name, description: it.description, orderNo: it.orderNo, url: it.url, type: it.type.value == "menuItem" ? "Menü Elemanı" : "Ana Menü",
                        menu: [id: menu.id, name: menu.name], parentMenuItem: [id: parentMenuItem?.id, name: parentMenuItem?.name],
                        securitySubjectPermission: [id: securitySubjectPermission?.id, securitySubject:[name: securitySubjectPermission?.securitySubject?.name+"("+securitySubjectPermission?.permissionType?.name+")"], dependsOnMenuItem: securitySubjectPermission?.dependsOnMenuItem]]
            }

            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def show(String id)
    {

        ManeResponse maneResponse = new ManeResponse()

        // TODO: request sysrefCompanyTypeCode from client
        MenuItem menuItem = menuItemService.getMenuItem(id)

        try {

            if ( !menuItem ) throw new Exception()

            Menu menu = Menu.get(menuItem.menu.id)
            MenuItem parentMenuItem = MenuItem.get(menuItem.parent?.id)
            SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.get(menuItem.securitySubjectPermission?.id)
            SecuritySubject securitySubject = securitySubjectPermission?.securitySubject
            PermissionType permissionType = securitySubjectPermission?.permissionType
            def result = [id: menuItem.id, name:menuItem.name, description: menuItem.description, orderNo: menuItem.orderNo, url: menuItem.url,
                        type:menuItem.type.value == "menuItem" ? "MENU_ITEM" : "CONTAINER",
                        menu: [id: menu.id, name: menu.name], parentMenuItem: [id: parentMenuItem?.id, name: parentMenuItem?.name],
                        securitySubjectPermission: [id: securitySubjectPermission?.id, securitySubject:[name: securitySubject?.name+":"+permissionType?.name], dependsOnMenuItem: securitySubjectPermission?.dependsOnMenuItem]]

            maneResponse.data = result
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !menuItem ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen menü elemanı sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(MenuItem menuItem)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            menuItem.active = true
            menuItemService.save(menuItem)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = menuItem.id
            maneResponse.message = 'Menü elemanı başarıyla kaydedildi.'

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

    def update(MenuItem menuItem)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            menuItemService.save(menuItem)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Menü elemanı başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !menuItem ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen menü elemanı sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def delete(String id)
    {
        ManeResponse maneResponse = new ManeResponse()
        MenuItem menuItem = MenuItem.get(id)

        try {

            menuItemService.delete(menuItem)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Menü elemanı başarıyla silindi.'

        } catch (Exception ex) {

            if ( !menuItem ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen menü elemanı sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def getMenuItemListDDS(){

        List<MenuItem> result = menuItemService.getMenuItemListDDS()
        result = result.collect {
            return [id: it.id, name:it.name]
        } as List<MenuItem>
        render result as JSON
    }
}
