package tr.com.manerp.rest

import at.favre.lib.crypto.bcrypt.BCrypt
import grails.converters.JSON
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.Role
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SaltGenerator
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.user.Person
import tr.com.manerp.user.User

import javax.xml.bind.ValidationException
import java.nio.charset.StandardCharsets


class RestController extends BaseController{

    static namespace = "v1"
    def restService
    def userService
    def personService
    def roleService
    def rolePermissionService

    def getAllUserList(){

        ManeResponse maneResponse = new ManeResponse()
        try {
            JSONArray result = restService.getAllUserList(
                    request.JSON.organizationId.toString(),request.JSON.roleId.toString())

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse

    }

    def addUser(){

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
            user.username = requestParams.username
            byte[] salt = SaltGenerator.generateSalt()
            byte[] password = BCrypt.withDefaults().hash(6, salt, requestParams.password.getBytes(StandardCharsets.UTF_8))
            user.password = password
            user.salt = salt
            user.person = person
            user.active = true

            userService.save(user)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.setData(new JSONObject(id: user.id,username: user.username))
            maneResponse.message = 'Kullanıcı başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }

    def updateUser() {

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
            if (!user.password == requestParams.password) {
                byte[] salt = SaltGenerator.generateSalt()
                byte[] password = BCrypt.withDefaults().hash(6, salt, requestParams.password.getBytes(StandardCharsets.UTF_8))
                user.password = password
                user.salt = salt
            }
            user.person = person

            userService.save(user)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.setData(new JSONObject(id: user.id, username: user.username))
            maneResponse.message = 'Kullanıcı başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }

    def deleteUser() {

        ManeResponse maneResponse = new ManeResponse()
        User user = User.get(request.JSON.id)

        try {

            userService.delete(user)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı başarıyla silindi.'
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
        } catch (Exception ex) {

            if (!user) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen kullanıcı sistemde bulunmamaktadır.'
                maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            }

            if (maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }

    def getAllRoleList(){

        ManeResponse maneResponse = new ManeResponse()
        try {
            JSONArray result = restService.getAllRoleList(request.JSON.organizationId)

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse
    }

    def addRole(Role role)
    {
        ManeResponse maneResponse = new ManeResponse()

        try {
            role.active = true
            roleService.save(role)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.setData(new JSONObject(id: role.id, name: role.name))
            maneResponse.message = 'Rol başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }

    def updateRole(Role role)
    {
        ManeResponse maneResponse = new ManeResponse()

        try {

            roleService.save(role)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.setData(new JSONObject(id: role.id, name:role.name))
            maneResponse.message = 'Rol başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !role ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen rol sistemde bulunmamaktadır.'
                maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))

            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }

    def deleteRole()
    {
        ManeResponse maneResponse = new ManeResponse()
        Role role = Role.get(request.JSON.id)

        try {

            roleService.delete(role)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol başarıyla silindi.'
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))


        } catch (Exception ex) {

            if ( !role ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen rol sistemde bulunmamaktadır.'
                maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }


    def getAllRolePermissionList() {

        ManeResponse maneResponse = new ManeResponse()
        try {
            JSONArray result = restService.getAllRolePermissionList(request.JSON.roleId.toString())

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse

    }


    def addRolePermission(RolePermission rolePermission)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            rolePermission.active = true
            rolePermissionService.save(rolePermission)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.setData(new JSONObject(id: rolePermission.id))
            maneResponse.message = 'Rol izni başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }
    def deleteRolePermission()
    {
        ManeResponse maneResponse = new ManeResponse()
        RolePermission rolePermission = RolePermission.get(request.JSON.id)

        try {

            rolePermissionService.delete(rolePermission)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol izni başarıyla silindi.'
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))

        } catch (Exception ex) {

            if ( !rolePermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen rol izni sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse
    }
    def getAllSecuritySubjectPermissionList() {

        ManeResponse maneResponse = new ManeResponse()
        try {
            JSONArray result = restService.getAllSecuritySubjectPermissionList()

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse

    }
}
