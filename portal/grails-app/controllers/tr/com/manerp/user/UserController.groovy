package tr.com.manerp.user

import at.favre.lib.crypto.bcrypt.BCrypt
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.auth.SaltGenerator
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.Organization
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

class UserController extends BaseController
{

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def userService
    def personService
    def userOrganizationService

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")


    def index()
    {
        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = userService.getUserList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields))
            result.data = result.data.collect {
                return [id: it.id, username: it.username, password: it.password, person: [id       : it.person?.id, name: it.person?.name, surname: it.person?.surname, tckn: it.person?.identificationNumber,
                                                                                          birthDate: it.person?.birthDate == null ? null : sdf.format(it.person.birthDate), email: it.person?.email]]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save()
    {
        def requestParams = request.JSON

        ManeResponse maneResponse = new ManeResponse()

        try {
            Person person = new Person()
            person.name = requestParams.person.name
            person.surname = requestParams.person.surname
            person.birthDate = requestParams.person.birthDate != null ? sdf.parse(requestParams.person.birthDate) : null
            person.identificationNumber = requestParams.person.tckn != null ? requestParams.person.tckn : null
            person.email = requestParams.person.email
            person.active = true

            personService.save(person)

            User user = new User()
            user.staffId = requestParams.staffId
            user.username = requestParams.username
            byte[] salt = SaltGenerator.generateSalt()
            byte[] password = BCrypt.withDefaults().hash(6, salt, requestParams.password.getBytes(StandardCharsets.UTF_8))
            user.password = password
            user.salt = salt
            user.person = person
            user.active = true

            userService.save(user)

            if(requestParams.staffId){
                Organization organization = Organization.findByName("Bumerang Lojistik")
                UserOrganization userOrganization = new UserOrganization()
                userOrganization.user = user
                userOrganization.organization = organization

              userOrganizationService.save(userOrganization)
            }

            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = user.id
            maneResponse.message = 'Kullanıcı başarıyla kaydedildi.'

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

    def update()
    {
        def requestParams = request.JSON

        ManeResponse maneResponse = new ManeResponse()

        try {
            Person person = Person.get(requestParams.person.id)
            person.name = requestParams.person.name
            person.surname = requestParams.person.surname
            person.birthDate = requestParams.person.birthDate != null ? sdf.parse(requestParams.person.birthDate) : null
            person.identificationNumber = requestParams.person.tckn != null ? requestParams.person.tckn : null
            person.email = requestParams.person.email

            personService.save(person)

            User user = User.get(requestParams.id)
            user.username = requestParams.username
            if ( !user.password == requestParams.password ) {
                byte[] salt = SaltGenerator.generateSalt()
                byte[] password = BCrypt.withDefaults().hash(6, salt, requestParams.password.getBytes(StandardCharsets.UTF_8))
                user.password = password
                user.salt = salt
            }
            user.person = person

            userService.save(user)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = user.id
            maneResponse.message = 'Kullanıcı başarıyla güncellendi.'

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

    def delete(String id)
    {
        ManeResponse maneResponse = new ManeResponse()
        User user = User.get(id)

        try {

            userService.delete(user)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı başarıyla silindi.'

        } catch (Exception ex) {

            if ( !user ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen kullanıcı sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def userOrganizationList()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = userService.getUserOrganizationList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields), params.id)
            result.data = result.data.collect {
                return [id: it.id, organization: [id: it.organization.id, name: it.organization.name, unitCode: it.organization.unitCode, description: it.organization.description]]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def userOrganizationRoleList()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = userService.getUserOrganizationRoleList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields), params.id)
            result.data = result.data.collect {
                return [id  : it.id, user: [id: it.user.id, username: it.user.username], organization: [id: it.organization.id, name: it.organization.name],
                        role: [id: it.role.id, name: it.role.name]]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def isValidUser()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            boolean validationResult = userService.isValidUser(request.JSON.username.toString(), request.JSON.password.toString())
            UserOrganization userOrganization = UserOrganization.findByUser(User.findByUsername(request.JSON.username))
            User user = userOrganization.user
            Organization organization = userOrganization.organization
            maneResponse.data = [user: user, organization: organization]
        }
        catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }
}