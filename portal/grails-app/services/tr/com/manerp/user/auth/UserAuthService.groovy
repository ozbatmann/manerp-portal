package tr.com.manerp.user.auth

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.Order
import tr.com.manerp.auth.ActionItem
import tr.com.manerp.auth.Role
import tr.com.manerp.auth.RolePermission
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.common.MenuItem
import tr.com.manerp.common.Organization
import tr.com.manerp.enumeration.MenuItemType
import tr.com.manerp.user.User
import tr.com.manerp.user.UserOrganizationRole

@Transactional
class UserAuthService {
    def serviceMethod() {}

    List<SecuritySubjectPermission> getUserOrgPermList(String orgId, String username, Role role = null) {

        //Kullanicinin organizasyondaki rollerinden listesi ile de birlestirerek return et.
        getUserOrgPermissionList( orgId, username,role)
    }

    List<SecuritySubjectPermission> getUserOrgPermissionList( String orgId, String username,Role _role = null) {

        List<SecuritySubjectPermission> userOrgPermissionList = UserOrganizationRole.createCriteria().list {

            user {

                eq("username", username)
            }

            organization {

                eq("id", orgId)
            }

            if(_role){

                eq("role",_role)
            }

            role {
                rolePermission{
                    projections{
                        property("securitySubjectPermission")
                    }
                }
            }

        } as List<SecuritySubjectPermission>


        return userOrgPermissionList
    }


    List<MenuItem> getUserMenuItemListForRedis(String orgId, String username) {

        //Kullanicinin izinlerini getir
        List<SecuritySubjectPermission> userOrgPermList = getUserOrgPermList(orgId, username)
        //Kullanicinin bu izinler dahilinde ya da izne ihtiyac olmayan menuItem'larini getir.
        getMenuItemList(userOrgPermList)
    }

    /*
        Container bazinda menuItem'lari icten disa dogru al.
     */

    List<MenuItem> getMenuItemList(List<SecuritySubjectPermission> securitySubjectPermissionList) {

        List<MenuItem> allMenuItemList = new ArrayList<MenuItem>()
        List<MenuItem> menuItemList = findAllMenuItems(securitySubjectPermissionList)

        menuItemList.each { menuItem ->

            if (menuItem.type.equals(MenuItemType.MENU_ITEM)) {

                allMenuItemList.add(menuItem)
            } else {

                List<MenuItem> subMenuItemList = getChildMenuItemList(menuItem, new ArrayList<MenuItem>(), securitySubjectPermissionList)

                if (subMenuItemList.size() > 0) {

                    subMenuItemList.each { subMenuItem ->

                        allMenuItemList.add(subMenuItem)
                    }
                }
            }
        }

        allMenuItemList.reverse().each { menuItem ->

            if (menuItem.type.equals(MenuItemType.CONTAINER)) {
                ls
                boolean deleteMenuItem = true

                menuItem.childs.find { child ->

                    if (allMenuItemList.contains(child)) {

                        deleteMenuItem = false
                    }
                    return !deleteMenuItem
                }

                if (deleteMenuItem) {
                    allMenuItemList.remove(menuItem)
                }
            }
        }

        allMenuItemList
    }

    List<MenuItem> getChildMenuItemList(MenuItem parentMenuItem,
                                        ArrayList<MenuItem> subMenuItemList,
                                        List<SecuritySubjectPermission> securitySubjectPermissionList) {

        if (parentMenuItem.childs != null && parentMenuItem.childs.size() > 0) {

            parentMenuItem.childs.sort { Order.asc("orderNo") }.each { child ->

                if (!child.operationType.equals(OperationType.DELETE)) {

                    if (child.type.equals(MenuItemType.MENU_ITEM)) {

                        boolean existPermission = false

                        if (child.securitySubjectPermission != null) {

                            securitySubjectPermissionList.find { secSubPerm ->

                                if (secSubPerm.getId() == child.securitySubjectPermission.getId()) {

                                    existPermission = true
                                }

                                existPermission
                            }

                            if (existPermission) {

                                if (!subMenuItemList.contains(parentMenuItem)) {

                                    subMenuItemList.add(parentMenuItem)
                                }
                                subMenuItemList.add(child)
                            }
                        }

                        else{

                            if (!subMenuItemList.contains(parentMenuItem)) {

                                subMenuItemList.add(parentMenuItem)
                            }
                            subMenuItemList.add(child)
                        }


                    } else {
                        def pItem = [id: parentMenuItem.id, name: parentMenuItem.name, parent: parentMenuItem.parent ? [id: parentMenuItem.parent?.id, name: parentMenuItem.parent?.name] : null]
                        subMenuItemList.add(pItem)
                        getChildMenuItemList(child, subMenuItemList, securitySubjectPermissionList)
                    }
                }
            }
        }

        subMenuItemList
    }

    List<MenuItem> findAllMenuItems(List<SecuritySubjectPermission> userOrgPermList) {

        MenuItem.createCriteria().list {

            or {

                isNull("securitySubjectPermission")

                if (userOrgPermList != null && userOrgPermList.size() > 0) {

                    'in'("securitySubjectPermission", userOrgPermList)
                }

                else {

                    equals(false)
                }
            }

            order("orderNo", "asc")

        } as List<MenuItem>
    }

    List<ActionItem> getActionItemList(List<SecuritySubjectPermission> securitySubjectPermissionList) {

        ActionItem.createCriteria().list {

            if(securitySubjectPermissionList != null && securitySubjectPermissionList.size() > 0) {
                actionItemPermissionList {

                    'in'('securitySubjectPermission', securitySubjectPermissionList)
                }
            }

        } as List<ActionItem>
    }

    /*
        Kullanicinin secili olan organizasyonu disinda hangi organizasyonlara yetkisi var ise o organizasyonlari listele.
     */

    List<Organization> getUserOrgList(String appId, String orgId, String username, String orgUnitTypeCode) {

        UserOrganizationRole.createCriteria().list {

            user {

                eq("username", username)
            }

            organization {

                if(orgId){

                    ne("id", orgId)
                }

                if(orgUnitTypeCode){

                    organizationUnitType{

                        eq("code",orgUnitTypeCode)
                    }
                }
            }

            role {

                app {

                    eq("id", appId)
                }
            }

            projections {

                distinct("organization")
            }

        } as List<Organization>
    }

    Map<String, Object> getSecSubPermIdListToDelFromRedis(List userOrgList, Role role, List<String> secSubPermIdList) {

        Map<String, Object> redisResultMap = new HashMap<String, Object>()

        //Baska roller ile ortak olan izinler var ise onlari listele.
        List<String> secSubPermListNotDelete = findSameSecSubPermListInOtherRoles(userOrgList, secSubPermIdList, role)

        //Baska rollerden gelen ortak izinleri cikart.
        if (secSubPermListNotDelete != null && secSubPermListNotDelete.size() > 0) {

            secSubPermIdList.removeAll(secSubPermListNotDelete)

            if (secSubPermIdList == null || secSubPermIdList.size() <= 0) {

                null
            }
        }

        redisResultMap.put("orgId", userOrgList.get(1).id)
        redisResultMap.put("username", userOrgList.get(0).username)
        redisResultMap.put("permIdList", secSubPermIdList)

        redisResultMap
    }

    List<String> findSameSecSubPermListInOtherRoles(List userOrgList, List<String> secSubPermIdList, Role _role) {

        UserOrganizationRole.createCriteria().list {

            ne("role", _role)

            role {

                isNotNull("rolePermission")

                rolePermission {

                    securitySubjectPermission {

                        'in'("id", secSubPermIdList)

                        projections {

                            distinct("id")
                        }
                    }
                }
            }

            eq("user", userOrgList.get(0) as User)
            eq("organization", userOrgList.get(1) as Organization)

        } as List<String>
    }

    ArrayList getUserOrgListHavingRole(Role role) {

        UserOrganizationRole.createCriteria().list {

            eq("role", role)

            isNotNull("user")
            projections {

                groupProperty('user')
                groupProperty('organization')
            }

        } as ArrayList
    }

    List<String> getSecSubPermIdListByRole(Role role) {

        RolePermission.createCriteria().list {

            eq("role", role)

        }.collect { it.securitySubjectPermission.id } as List<String>
    }

    /*
        Silinecek izinlerden kullanicinin gorecegi menuyu etkileyen izinler var mi diye kontrol edilecek.
     */

    boolean isMenuItemListChanged(List<String> secSubPermIdList) {

        List<MenuItem> menuItemList = MenuItem.createCriteria().list {

            eq("type", MenuItemType.MENU_ITEM)
            isNotNull("securitySubjectPermission")

            securitySubjectPermission {

                'in'('id', secSubPermIdList)
            }

        } as List<MenuItem>

        if (menuItemList != null && menuItemList.size() > 0) {

            true
        }

        false
    }

    List<SecuritySubjectPermission> prepareRedisDataToWrite(List<String> idList) {

        SecuritySubjectPermission.createCriteria().list {

            'in'("id", idList)

        } as List<SecuritySubjectPermission>
    }

    List<SecuritySubjectPermission> getSameSecSubPermListInOtherRoles(List<UserOrganizationRole> userOrgRoleList, List<SecuritySubjectPermission> roleSecSubPermList) {

        UserOrganizationRole userOrganizationRole = userOrgRoleList.get(0)

        UserOrganizationRole.createCriteria().list {

            eq("user", userOrganizationRole.user)
            eq("organization", userOrganizationRole.organization)
            not { 'in'("role", userOrgRoleList*.role) }

            role {

                rolePermission {

                    projections {

                        distinct("securitySubjectPermission")
                    }
                }
            }

        } as List<SecuritySubjectPermission>
    }

    List<SecuritySubjectPermission> getSecSubPermListByRoles(List<UserOrganizationRole> userOrgRoleList) {

        UserOrganizationRole userOrganizationRole = userOrgRoleList.get(0)

        List<SecuritySubjectPermission> list = getUserPermissionList(userOrganizationRole.role.appId.toString(), userOrganizationRole.organizationId.toString(), userOrganizationRole.user.username, PermissionFilterType.EXCLUDE)
        UserOrganizationRole.createCriteria().list {

            eq("user", userOrganizationRole.user)
            eq("organization", userOrganizationRole.organization)
            'in'("role", userOrgRoleList*.role)

            role {

                rolePermission {
                    if(list != null && list.size() > 0) {
                        not {
                            'in'("securitySubjectPermission",list)
                        }
                    }
                    projections {

                        distinct("securitySubjectPermission")
                    }
                }
            }

        } as List<SecuritySubjectPermission>
    }
}