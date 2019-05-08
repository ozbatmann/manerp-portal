package tr.com.manerp.rest


import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SecuritySubject
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.service.BaseService
import tr.com.manerp.common.Organization
import tr.com.manerp.dto.RolePermissionDto
import tr.com.manerp.user.User
import tr.com.manerp.user.UserOrganization
import tr.com.manerp.user.UserOrganizationRole

import java.lang.reflect.Array

@Transactional
class RestService extends BaseService{


    List getAllUserList(String organizationId,String roleId){

        List list = new ArrayList()

       List<UserOrganizationRole> userOrganizationRoleList = UserOrganizationRole.createCriteria().list{
            organization{
                eq("id",organizationId)
            }
            role{
                eq("id",roleId)
            }
           isNotNull("user")
        } as List

          userOrganizationRoleList.collect {
                  User user = it.user
                  list.add([id: it.id, username: user.username, name: user.person.name, surname: user.person.surname, birthDate: user.person.birthDate, email: user.person.email])
          }
        return list
    }

    List getAllUserListBySearchParam(String searchParam,String roleId){

        List list = new ArrayList()

        List<String> alreadyAddedUsers = UserOrganizationRole.createCriteria().list {

            role{
                eq("id",roleId)
            }
            user{
                projections{

                    property("id")
                }
            }


        } as List

       List<User> userList = UserOrganization.createCriteria().list{
            user {
                person {
                    or{
                        ilike("name", '%' + searchParam + '%')
                        ilike("surname",'%' +searchParam + '%')
                    }
                }
                if(alreadyAddedUsers.size() > 0 && alreadyAddedUsers != null) {
                    not {
                        'in'("id", alreadyAddedUsers)
                    }
                }
            }
            projections{
                property("user")
            }
        } as List
            userList.collect{
                if(it != null) {
                    list.add([id: it.id, username: it.username, name: it.person.name, surname: it.person.surname, fullname: it.person.name + " " + it.person.surname, birthDate: it.person.birthDate, email: it.person.email])
                }
            }

        return list
    }

    def getUserOrganizationRole(String userOrgRoleId){

        UserOrganizationRole userOrganizationRole = UserOrganizationRole.findById(userOrgRoleId)
        return userOrganizationRole
    }

    List getAllRoleList(String organizationId){

        List list = new ArrayList()

        int totalCount = 0;
        UserOrganizationRole.createCriteria().list{
            organization {
                eq("id", organizationId)
            }
        }.collect{it ->
            if(list.size() == 0){
                list.add([id: it.id, organization: [id: it.organization.id, name: it.organization.name], role:[id: it.role.id, name: it.role.name]])
            }
            if(!checkThatExist(list,it)) {list.add([id: it.id, organization: [id: it.organization.id, name: it.organization.name], role:[id: it.role.id, name: it.role.name]])

         }
        }

        return list
    }


    def checkThatExist(List list, item){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).role.id == item.role.id ) {
                return true
            }
        }
        return false
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
    def getAllUnavailablePermissionTypes(RolePermissionDto rpDto, def roleId, def securitySubjectId){
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
            SecuritySubjectPermission.createCriteria().list{
                securitySubject{
                    eq("id",securitySubjectId)
                }

                'in'("id", secSubPermList)

            }.collect{it2 -> rpDto.permissions.add([id:it2.id, name: it2.permissionType.name,status: true])} as List
        }
    }


    def getAllAvailablePermissionTypes(RolePermissionDto rpDto,def roleId,def securitySubjectId){

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

            List availablePermTypeList = SecuritySubjectPermission.createCriteria().list{
                securitySubject{
                    eq("id",securitySubjectId)
                }

                if(secSubPermList.size() > 0 && secSubPermList != null) {
                    not {
                        'in'("id", secSubPermList)
                    }
                }

            }.collect{it2 -> rpDto.permissions.add([id:it2.id, name: it2.permissionType.name,status: false])} as List
        }
}

