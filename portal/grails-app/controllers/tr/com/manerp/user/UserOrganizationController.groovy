package tr.com.manerp.user

import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController

import javax.xml.bind.ValidationException

class UserOrganizationController extends BaseController{

    static namespace = "v1"
    static allowedMethods = [show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def userOrganizationService

    def save(UserOrganization userOrganization)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            userOrganization.active = true
            userOrganizationService.save(userOrganization)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = userOrganization.id
            maneResponse.message = 'Kullanıcı organizasyonu başarıyla kaydedildi.'

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

    def update(UserOrganization userOrganization)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            userOrganizationService.save(userOrganization)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı organizasyonu başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !userOrganization ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen kullanıcı organizasyonu sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def delete(String id) {
        ManeResponse maneResponse = new ManeResponse()
        UserOrganization userOrganization = UserOrganization.get(id)

        try {

            userOrganizationService.delete(userOrganization)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı organizasyonu başarıyla silindi.'

        } catch (Exception ex) {

            if (!userOrganization) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen kullanıcı organizasyonu sistemde bulunmamaktadır.'
            }

            if (maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }
}