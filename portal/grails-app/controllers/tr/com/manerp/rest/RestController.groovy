package tr.com.manerp.rest

import at.favre.lib.crypto.bcrypt.BCrypt
import grails.converters.JSON
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.Role
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SaltGenerator
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.Organization
import tr.com.manerp.dto.RestDto
import tr.com.manerp.dto.RolePermissionDto
import tr.com.manerp.user.Person
import tr.com.manerp.user.User
import tr.com.manerp.user.UserOrganizationRole

import javax.xml.bind.ValidationException
import java.nio.charset.StandardCharsets


class RestController extends BaseController{

    static namespace = "v1"
    def restService
    def userService
    def personService
    def roleService
    def rolePermissionService
    def userOrganizationRoleService

    def getAllUserList(){

        RestDto restDto = new RestDto()
        try {
            List result = restService.getAllUserList(
                    request.JSON.organizationId.toString(),request.JSON.roleId.toString())

            restDto.itemList = result
            restDto.itemCount = result.size()
            restDto.status = 200
        }

        catch (Exception e) {
            restDto.message = e.getMessage()

        }
        render restDto as JSON

    }
    def getAllUserListBySearchParam(){

        RestDto restDto = new RestDto()

        ManeResponse maneResponse = new ManeResponse()
        try {
            List result = restService.getAllUserListBySearchParam(request.JSON.searchParam,request.JSON.roleId)

            restDto.itemList = result
            restDto.itemCount = result.size()
            restDto.status = 200
        }

        catch (Exception e) {
            maneResponse.setStatusCode(StatusCode.INTERNAL_ERROR)
            maneResponse.setMessage(e.getMessage())
        }
        maneResponse.setData(status: false)
        render restDto as JSON

    }

    def addUser(){

        def requestParams = request.JSON
        RestDto restDto = new RestDto()


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
            restDto.status = 200
            restDto.message  "Kullanıcı başarıyla eklendi."

        } catch (ValidationException ex) {

            restDto.status = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()

        } catch (Exception ex) {
            restDto.status = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse as JSON
    }

    def updateUser() {

        def requestParams = request.JSON
        RestDto restDto = new RestDto()


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
            restDto.statusCode = 200
            restDto.message = 'Kullanıcı başarıyla güncellendi.'

        } catch (ValidationException ex) {

            restDto.statusCode = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            maneResponse.setData(new JSONObject(status: maneResponse.statusCode, message: maneResponse.message))
            ex.printStackTrace()
        }

        render maneResponse as JSON
    }

    def deleteUser() {

        RestDto restDto = new RestDto()


        try {
            UserOrganizationRole userOrganizationRole = restService.getUserOrganizationRole(request.JSON.userId)
            userOrganizationRoleService.delete(userOrganizationRole)
            restDto.status = 200
            restDto.message = 'Kullanıcı rolden çıkartıldı.'
        } catch (Exception ex) {

            if (!user) {
                restDto.status = 500
            }
            ex.printStackTrace()
        }

        render restDto as JSON
    }

    def getAllRoleList(){

        RestDto restDto = new RestDto()
        try {
            List result = restService.getAllRoleList(request.JSON.organizationId)

            restDto.itemList = result
            restDto.itemCount = result.size()
            restDto.status = 200
        }

        catch (Exception e) {

            restDto.message = e.getMessage()
            restDto.status = 500
        }
        render restDto as JSON
    }

    def addRole()
    {
        RestDto restDto = new RestDto()

        try {
            Role role = new Role()
            role.name = request.JSON.name
            role.active = true
            roleService.save(role)
            UserOrganizationRole userOrganizationRole = new UserOrganizationRole()
            userOrganizationRole.organization = Organization.findById(request.JSON.organizationId)
            userOrganizationRole.role = role
            userOrganizationRoleService.save(userOrganizationRole)
            restDto.status = 200
            restDto.message = 'Rol başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            restDto.message = ex.getMessage()
            restDto.status = 500
            ex.printStackTrace()

        } catch (Exception ex) {

            restDto.message = ex.getMessage()
            restDto.status = 500
            ex.printStackTrace()
        }

        render restDto as JSON
    }

    def updateRole()
    {
       RestDto restDto = new RestDto()

        try {
            Role role = Role.findById(request.JSON.roleId)
            role.name = request.JSON.name
            roleService.save(role)
            restDto.status = 200
            restDto.message = 'Rol başarıyla güncellendi.'

        } catch (ValidationException ex) {

            restDto.status = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()

        } catch (Exception ex) {
            restDto.status = 500
            restDto.message = ex.getMessage()
        }

        render restDto as JSON
    }

    def deleteRole()
    {
        RestDto restDto = new RestDto()

        try {
            Role role = Role.get(request.JSON.roleId)
            roleService.delete(role)
            restDto.status = 200
            restDto.message = 'Rol başarıyla silindi.'


        } catch (Exception ex) {
            restDto.status = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()
        }

        render restDto as JSON
    }


    def getAllRolePermissionList() {

        ManeResponse maneResponse = new ManeResponse()
       /* try {
            JSONArray result = restService.getAllRolePermissionList(request.JSON.roleId.toString())

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse
*/

        List result = restService.getAllRolePermissionList()

        Role role = Role.findById(request.JSON.roleId)

        def resultList = new ArrayList()

        result.each { secSub ->
            RolePermissionDto rpDto = new RolePermissionDto()
            restService.getAllUnavailablePermissionTypes(rpDto,role.id,secSub.id.toString())
            restService.getAllAvailablePermissionTypes(rpDto,role.id,secSub.id.toString())
            rpDto.setName(secSub.name)
            resultList.add(rpDto)
        }





        JSONArray jsonArray = (JSONArray) resultList

        maneResponse.setData(jsonArray)

        render maneResponse
    }


    def addRolePermission()
    {

        RestDto restDto = new RestDto()


        try {
            RolePermission rolePermission = new RolePermission()
            Role role = Role.findById(request.JSON.roleId)
            SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.findById(request.JSON.id)
            rolePermission.securitySubjectPermission = securitySubjectPermission
            rolePermission.role = role
            rolePermission.active = true
            rolePermissionService.save(rolePermission)
            restDto.status = 200
            restDto.message = 'Rol izni başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            restDto.status = 500
            restDto.message = ex.getMessage()

        } catch (Exception ex) {

            restDto.status = 500
            restDto.message = ex.getMessage()
            ex.printStackTrace()
        }

        render restDto as JSON
    }
    def deleteRolePermission()
    {
        RestDto restDto = new RestDto()

        SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.findById(request.JSON.id)

        RolePermission rolePermission = RolePermission.findBySecuritySubjectPermission(securitySubjectPermission)

        try {

            rolePermissionService.delete(rolePermission)
            restDto.status = 200
            restDto.message = 'Rol izni başarıyla silindi.'

        } catch (Exception ex) {

            if ( !rolePermission ) {
                restDto.status = 500
                restDto.message = 'Silinmek istenen rol izni sistemde bulunmamaktadır.'
            }
        }

        render restDto as JSON
    }
    def addUserOrganizationRole() {

        RestDto restDto = new RestDto()
        try {

            UserOrganizationRole userOrganizationRole = new UserOrganizationRole()

            userOrganizationRole.user = User.findById(request.JSON.userId)
            userOrganizationRole.role = Role.findById(request.JSON.roleId)
            userOrganizationRole.organization = Organization.findById(request.JSON.organizationId)
            userOrganizationRoleService.save(userOrganizationRole)
            restDto.status = 200
            restDto.message = 'Kullanıcı role başarıyla kaydedildi.'
        }

        catch (Exception e) {
            restDto.message = e.getMessage()
            restDto.status = 500
        }
        render restDto as JSON

    }
}
