package tr.com.manerp.rest

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.PermissionType
import tr.com.manerp.auth.Role
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SecuritySubject
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.service.BaseService
import tr.com.manerp.user.User
import tr.com.manerp.user.UserOrganization
import tr.com.manerp.user.UserOrganizationRole

@Transactional
class RestService extends BaseService{


    JSONArray getAllUserList(String organizationId,String roleId){

        JSONArray jsonArray = new JSONArray()

        UserOrganizationRole.createCriteria().list{
            organization{
                eq("id",organizationId)
            }
            role{
                eq("id",roleId)
            }
            projections{
                property("user")
            }
        }.collect{it -> jsonArray.add(new JSONObject(id: it.id, username: it.username, name: it.person.name, surname: it.person.surname, birthDate: it.person.birthDate, email: it.person.email))}

        return jsonArray
    }


    JSONArray getAllRoleList(String organizationId){

        JSONArray jsonArray = new JSONArray()

        UserOrganizationRole.createCriteria().list{
            organization{
                eq("id",organizationId)
            }
            projections{
                property("role")
            }
        }.collect{it -> jsonArray.add(new JSONObject(id: it.id, name: it.name))}

        return jsonArray
    }


    JSONArray getAllRolePermissionList(String roleId) {

        List<SecuritySubject> securitySubjectList = SecuritySubject.list()

        JSONArray jsonArray = new JSONArray()

        List resultList = new ArrayList()


        securitySubjectList.each { it ->
            String name = it.name
            List permissionTypeList = new ArrayList<>()
             permissionTypeList = RolePermission.createCriteria().list { it2 ->
                 role {
                     eq("id",roleId)
                 }
                 securitySubjectPermission{

                securitySubject {

                    eq("name", name)

                }
                    projections{
                        property("id")
                        permissionType{
                            property("name")
                        }
                    }
                }

                 } as List
            JSONObject jsonObject = new JSONObject("securitySubject":name, "permissions":permissionTypeList)
            jsonArray.add(new JSONObject(jsonObject))

            } as List





        return jsonArray
    }
    JSONArray getAllSecuritySubjectPermissionList() {

        List<SecuritySubject> securitySubjectList = SecuritySubject.list()

        JSONArray jsonArray = new JSONArray()

        List resultList = new ArrayList()


        securitySubjectList.each { it ->
            String name = it.name
            List permissionTypeList = new ArrayList<>()
             permissionTypeList = SecuritySubjectPermission.createCriteria().list { it2 ->

                securitySubject {

                    eq("name", name)

                }
                    projections{
                        property("id")
                        permissionType{
                            property("name")
                        }
                    }

                 } as List
            JSONObject jsonObject = new JSONObject("securitySubject":name, "permissions":permissionTypeList)
            jsonArray.add(new JSONObject(jsonObject))

            } as List


        return jsonArray
    }
}
