package tr.com.manerp.rest


import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SecuritySubject
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.service.BaseService
import tr.com.manerp.user.UserOrganizationRole

@Transactional
class RestService extends BaseService{


    JSONArray getAllUserList(String organizationId,String roleId){

        JSONArray jsonArray = new JSONArray(new JSONObject())

       List<UserOrganizationRole> userOrganizationRoleList = UserOrganizationRole.createCriteria().list{
            organization{
                eq("id",organizationId)
            }
            role{
                eq("id",roleId)
            }
            projections{
                property("user")
            }
        } as List

        if(userOrganizationRoleList.get(0) != null){
          userOrganizationRoleList.collect{
              jsonArray.add(new JSONObject(id: it.id, username: it.username, name: it.person.name, surname: it.person.surname, birthDate: it.person.birthDate, email: it.person.email))}

        }
        return jsonArray
    }


    JSONArray getAllRoleList(String organizationId){

        JSONArray jsonArray = new JSONArray()

        UserOrganizationRole.createCriteria().list{
            organization{
                eq("id",organizationId)
            }
        }.collect{it -> jsonArray.add(new JSONObject(id: it.id, organization: [id: it.organization.id, name: it.organization.name], role:[id: it.role.id, name: it.role.name]))}

        return jsonArray
    }


    List getAllRolePermissionList() {

      /*  List<SecuritySubject> securitySubjectList = SecuritySubject.list()

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

*/
           List securitySubjectPermissionList = SecuritySubjectPermission.createCriteria().list{

                projections {
                    distinct("securitySubject")
                }

            } as List
            return securitySubjectPermissionList
    }
    List getAllUnavailablePermissionTypes(def roleId, def securitySubjectId){
        List<String> secSubPermList = new ArrayList<>()

        secSubPermList = RolePermission.createCriteria().list {

            role{
                eq("id",roleId)
            }
            securitySubjectPermission{
                projections{
                    property("id")
                }
            }

        } as List<String>

        if(secSubPermList.size() > 0){
        List unavailablePermTypeList = SecuritySubjectPermission.createCriteria().list{
            securitySubject{
                eq("id",securitySubjectId)
            }

                'in'("id", secSubPermList)
            projections{

                property("id")

                permissionType{

                    property("id")
                    property("name")
                }
            }

        } as List
            return unavailablePermTypeList
        }
        return secSubPermList
    }


    List getAllAvailablePermissionTypes(def roleId,def securitySubjectId){

        List<String> secSubPermList = new ArrayList<>()
        secSubPermList = RolePermission.createCriteria().list {

            role{
                eq("id",roleId)
            }
            securitySubjectPermission{
                projections{
                    property("id")
                }
            }
        } as List

        if(secSubPermList.size() > 0){
        List availablePermTypeList = SecuritySubjectPermission.createCriteria().list{
            securitySubject{
                eq("id",securitySubjectId)
            }

                not {
                    'in'("id", secSubPermList)
                }
            projections{

                property("id")

                permissionType{

                    property("id")
                    property("name")
                }
            }

        } as List
            return availablePermTypeList
        }
        return secSubPermList

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
