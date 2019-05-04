package tr.com.manerp.common

import grails.converters.JSON
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class OrganizationController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def organizationService

    def index()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = organizationService.getOrganizationList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields))

            result.data = result.data.collect {
                Organization parentOrganization = Organization.get(it.parent?.id)
                return [id                : it.id, unitCode: it.unitCode, name: it.name, description: it.description,
                        parentOrganization: [id: parentOrganization?.id, name: parentOrganization?.name]]
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

        Organization organization = organizationService.getOrganization(id)

        try {

            if ( !organization ) throw new Exception()

            maneResponse.data = [id: organization.id, name: organization.name]
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !organization ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen organizasyon sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(Organization organization)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            organization.active = true
            organizationService.save(organization)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = organization.id
            maneResponse.message = 'Organizasyon başarıyla kaydedildi.'

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

    def update(Organization organization)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            organizationService.save(organization)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Organizasyon başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !organization ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen organizasyon sistemde bulunmamaktadır.'
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
        Organization organization = Organization.get(id)

        try {

            organizationService.delete(organization)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Organizasyon başarıyla silindi.'

        } catch (Exception ex) {

            if ( !organization ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen organizasyon sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    List getOrganizationListDDS(){

        List<Organization> organizationList = organizationService.getOrganizationListDDS()

        organizationList =  organizationList.collect {
            [id: it.id, unitCode: it.unitCode, name: it.name, description: it.description]
        }  as List<Organization>

        render organizationList as JSON
    }

}
