package tr.com.manerp.redis

import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import redis.clients.jedis.Jedis
import tr.com.manerp.auth.Role
import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.common.MenuItem
import tr.com.manerp.common.Organization
import tr.com.manerp.enumeration.RedisSyncType
import tr.com.manerp.user.UserOrganizationRole

@Transactional
class RedisSyncService {

    def userAuthService
    def redisService

    JSONArray synchronizeRedisWithDB(String orgId, String username, RedisSyncType redisSyncType, String key, boolean deleteKey = false) {

        if (deleteKey) {

            deleteKeyFromRedis(key, false)
        }

        if (redisSyncType.equals(RedisSyncType.PERMISSION)) {

            synchronizeUserOrgPermission(orgId, username, key)

        } else if (redisSyncType.equals(RedisSyncType.MENU)) {

            synchronizeUserMenu(orgId, username, key)
        }
    }

    JSONArray synchronizeUserOrgPermission(String orgId, String username, String key) {

        List<SecuritySubjectPermission> userOrgPermList = userAuthService.getUserOrgPermList(orgId, username)

        writeToRedis(userOrgPermList, key)
    }

    JSONArray synchronizeUserMenu(String orgId, String username, String key) {

        List<MenuItem> userMenuItemList = userAuthService.getUserMenuItemListForRedis(orgId, username)

        //Front'e verip menuBar için JSONArray donecek, ayni dongunun icinde redis'e yazacak.
        writeToRedis(userMenuItemList, key)
    }

    //Front icin JSONArray donecek, ayni dongunun icinde redis'e yazacak.
    JSONArray writeToRedis(List<Object> redisDataList, String key) {

        JSONArray resultArr = new JSONArray()

        redisService.withRedis { Jedis jedis ->

            redisDataList.each { redisData ->

                String field
                def value

                    field = redisData.id

                    if (redisData instanceof SecuritySubjectPermission) {

                        value = new JSONObject(permission: redisData.securitySubject.name + ":" + redisData.permissionType.name)
                    } else if (redisData instanceof MenuItem) {

                        value = new JSONObject(id: redisData.id,
                                name: redisData.name,
                                orderNo: redisData.orderNo,
                                url: redisData.url,
                                type: redisData.type.toString(),
                                icon: redisData.icon == null ? null : Base64.encodeAsBase64(redisData.icon),
                                permission: redisData.securitySubjectPermission == null ? null : redisData.securitySubjectPermission.securitySubject.name + ":" + redisData.securitySubjectPermission.permissionType.name,
                                parent: redisData.parent == null ? null : redisData.parent.id)
                    } else {


                        value = new JSONObject(id: redisData.id, name: redisData.name,
                                logo: redisData.logo == null ? null : Base64.encodeAsBase64(redisData.logo))
                    }

                    resultArr.add(value instanceof JSONObject ? value : new JSONObject(userOrg: value.toString()))

                    jedis.hset(key, field, value.toString())
                    log.info("writeToRedis   :    key= " + key + " value=" + value.toString())
                }

            }

        resultArr
    }

    void syncUserOrgRole(List userOrgList, Role role, List<String> secSubPermIdList) {

        Map<String, Object> redisResultMap = userAuthService.getSecSubPermIdListToDelFromRedis(userOrgList as List, role, secSubPermIdList)

        if (redisResultMap != null) {

            String orgId = redisResultMap.get("orgId").toString()
            String username = redisResultMap.get("username").toString()

            String permissionKey = createRedisKey(orgId, username, RedisSyncType.PERMISSION)

            List<String> secSubPermIdListToDelete = redisResultMap.get("permIdList") as List<String>

            deleteValueListFromRedis(permissionKey, secSubPermIdListToDelete)


            if (userAuthService.isMenuItemListChanged(appId, secSubPermIdListToDelete)) {

                reSynchronizeUserMenu(orgId,username, createRedisKey(orgId, username, RedisSyncType.MENU))
            }
        }
    }

    void deleteValueFromRedis(String key, String valueId) {

        redisService.withRedis { Jedis jedis ->

            jedis.hdel(key, valueId)
        }
    }

    /*
        MenuItem listesindeki container'lar da izin olmadigi icin menuyu tekrar kontrol edip redis'le check etmek yerine
        redis'deki menu bilgisi silinip tekrar yazilacak.
     */

    JSONArray reSynchronizeUserMenu(String orgId, String username, String key) {

        deleteKeyFromRedis(key, false)
        //Redis bilgisini guncelle.
        synchronizeUserMenu( orgId, username, key)
    }

    List<String> getKeyListByPattern(String keyPattern) {

        redisService.withRedis { Jedis jedis ->

            return jedis.keys(keyPattern) as List<String>
        }
    }

    void deleteKeyFromRedis(String key, boolean hasPattern) {

        redisService.withRedis { Jedis jedis ->

            if (hasPattern) {

                List<String> keyList = jedis.keys(key) as List<String>

                if (keyList != null && keyList.size() > 0) {

                    keyList.each { k ->

                        jedis.del(key)
                    }
                }
            } else {

                if (jedis.exists(key)) {

                    jedis.del(key)
                }
            }
        }
    }

    String createRedisKey(String orgId, String username, RedisSyncType redisSyncType) {

        String key = null

        if (redisSyncType.equals(RedisSyncType.PERMISSION) || redisSyncType.equals(RedisSyncType.MENU)) {

            key = orgId + ":" + username
        }

        key + ":" + redisSyncType.toString()
    }

    void deleteValueListFromRedis(String key, List<String> idList) {

        redisService.withRedis { Jedis jedis ->

            if (jedis.exists(key)) {

                idList.each { id ->

                    jedis.hdel(key, id)
                }
            }
        }
    }


    void updateRolePermFromRedis(Role role, List<String> addedSecSubPermList, List<String> removedSecSubPermList) {


        if ((addedSecSubPermList != null && addedSecSubPermList.size() > 0) || (removedSecSubPermList != null && removedSecSubPermList.size() > 0)) {

            /*
                Bu role sahip kullanici-organizasyon ikilisini getir.
            */
            ArrayList userOrgListHavingRole = userAuthService.getUserOrgListHavingRole(role)

            if (userOrgListHavingRole != null && userOrgListHavingRole.size() > 0) {

                userOrgListHavingRole.each { userOrgArr ->

                    String username = userOrgArr[0].username
                    String orgId = userOrgArr[1].id

                    List<String> allAffectedPermList = new ArrayList<String>()

                    if (addedSecSubPermList != null && addedSecSubPermList.size() > 0) {

                        //Eklenecek olan izinler kullanicinin bu organizasyon icin atanmis baska rollerinde var ise burada tekrar ekleme islemi yapma.
                        List<String> sameAddedSecSubPermList = userAuthService.findSameSecSubPermListInOtherRoles(userOrgArr as List, addedSecSubPermList, role)

                        if (sameAddedSecSubPermList != null && sameAddedSecSubPermList.size() > 0) {

                            addedSecSubPermList.removeAll(sameAddedSecSubPermList)
                        }

                        //Eklenecek olan izinlerden diger rollerde olanlar var ise ve cikartilmis hali ile hala eklenecek olanlar var ise Redis'e yaz.
                        if (addedSecSubPermList != null && addedSecSubPermList.size() > 0) {

                            writeToRedis(userAuthService.prepareRedisDataToWrite(addedSecSubPermList), createRedisKey(orgId, username, RedisSyncType.PERMISSION))
                            allAffectedPermList.addAll(addedSecSubPermList)
                        }
                    }

                    if (removedSecSubPermList != null && removedSecSubPermList.size() > 0) {

                        //Kaldirilacak olan izinler kullanicinin bu organizasyon icin atanmis baska rollerinde var ise kaldirma islemi yapma.
                        List<String> sameRemovedSecSubPermList = userAuthService.findSameSecSubPermListInOtherRoles(userOrgArr as List, removedSecSubPermList, role)

                        if (sameRemovedSecSubPermList != null && sameRemovedSecSubPermList.size() > 0) {

                            removedSecSubPermList.removeAll(sameRemovedSecSubPermList)
                        }

                        //Kaldirilacak olan izinlerden diger rollerde olanlar var ise ve cikartilmis hali ile hala kaldirilacak olanlar var ise Redis'den sil.
                        if (removedSecSubPermList != null && removedSecSubPermList.size() > 0) {

                            deleteValueListFromRedis(createRedisKey(orgId, username, RedisSyncType.PERMISSION), removedSecSubPermList)
                            allAffectedPermList.addAll(removedSecSubPermList)
                        }
                    }

                    if (allAffectedPermList.size() > 0) {

                        //MenuItem degisim kontrolu
                        if (userAuthService.isMenuItemListChanged(appId, allAffectedPermList)) {

                            reSynchronizeUserMenu(orgId, username, createRedisKey(orgId, username, RedisSyncType.MENU))
                        }
                    }
                }
            }
        }
    }

    /*
        Kullanicilara rol atanmis ise, bu rollerin izinleri kullanicilarin organizasyonlari icin baska rollerinden gelmiyorsa ve
        kullanicinin o organizasyondaki redis bilgilerinde yok ise yazilacak.
     */

    //TODO: removed ekle
    void updateUserOrgRoleFromRedis(String username, String organizationId, String appId) {

        //PERMISSION
        synchronizeRedisWithDB(organizationId,
                username,
                RedisSyncType.PERMISSION,
                createRedisKey(organizationId, username, RedisSyncType.PERMISSION),
                true)

        //MENU
        synchronizeRedisWithDB(organizationId,
                username,
                RedisSyncType.MENU,
                createRedisKey( organizationId, username, RedisSyncType.MENU),
                true)
    }

    /*
        Kullanicinin rol atamasini geri alirken,o organizasyon icin baska rolleriyle ortak olmayan izinler var ise
        redis bilgisinden silinecek.
     */

    void deleteUserOrgRoleFromRedis(List<UserOrganizationRole> userOrgRoleList) {

        userOrgRoleList.each { userOrgRole ->

            List<String> secSubPermIdList = userAuthService.getSecSubPermIdListByRole(userOrgRole.role)

            if (secSubPermIdList != null && secSubPermIdList.size() > 0) {

                List userOrgList = new ArrayList()
                userOrgList.add(userOrgRole.user)
                userOrgList.add(userOrgRole.organization)

                syncUserOrgRole(userOrgList, userOrgRole.role, secSubPermIdList)
            }
        }
    }

    /*
        MenuItem/Container eklenirse, silinirse, izni guncellenirse redis'de menu bilgisi olan kullanicilar icin guncelleme yapilacak.
     */

    void updateUserMenuFromRedis(String appId) {

        redisService.withRedis { Jedis jedis ->

            String keyPattern = appId + ":*:*:" + RedisSyncType.MENU.toString()
            List<String> keyList = jedis.keys(keyPattern) as List

            if (keyList != null && keyList.size() > 0) {

                keyList.each { key ->

                    String[] orgUserArr = key.split(":")
                    //redis key-value sil
                    jedis.del(key)

                    //menu sync.
                    reSynchronizeUserMenu(orgUserArr[1], orgUserArr[2], key)
                }
            }
        }
    }

    boolean existKeyInRedis(String key) {

        redisService.withRedis { Jedis jedis ->

            return jedis.exists(key)
        }
    }

    void changeUsernameInRedis(String oldUsername, String newUsername) {

        String permMenuPattern = "*:*:" + oldUsername + ":"
        String appOrgPattern = "*:" + oldUsername + ":"

        String permPattern = permMenuPattern + RedisSyncType.PERMISSION.toString()
        //Izin keylerini degistir.
        changeRedisKey(permPattern, oldUsername, newUsername)

        String menuPattern = permMenuPattern + RedisSyncType.MENU.toString()
        //Menu keylerini degistir.
        changeRedisKey(menuPattern, oldUsername, newUsername)

        String appPattern = appOrgPattern + RedisSyncType.APP.toString()
        //App keylerini degistir.
        changeRedisKey(appPattern, oldUsername, newUsername)

        String orgPattern = appOrgPattern + RedisSyncType.ORG.toString()
        //Org keylerini degistir.
        changeRedisKey(orgPattern, oldUsername, newUsername)

        String actionItemPattern = "*:*:*:" + RedisSyncType.ACTION_ITEM.toString()

        redisService.withRedis { Jedis jedis ->

            List<String> actionItemKeyList = jedis.keys(actionItemPattern) as List

            actionItemKeyList.each { actionItemKey ->

                Map<String, String> redisFieldValueList = jedis.hgetAll(actionItemKey)

                redisFieldValueList.each { redisFieldValue ->

                    String redisField = redisFieldValue.key

                    if (redisField.contains(oldUsername)) {

                        jedis.hdel(actionItemKey, redisField)
                        jedis.hset(actionItemKey, redisField.replaceAll(oldUsername, newUsername), ActionItemPermType.AUTHORIZED.toString())
                    }
                }
            }
        }
    }

    void changeRedisKey(String pattern, String oldUsername, String newUsername) {

        redisService.withRedis { Jedis jedis ->

            List<String> permKeyList = jedis.keys(pattern) as List

            permKeyList.each { oldKey ->

                jedis.rename(oldKey, oldKey.replaceAll(oldUsername, newUsername))
            }
        }
    }

    String createOrganizationKeyPattern() {

        return "*:" + RedisSyncType.ORG.toString()
    }


    void updateOrganization(Organization organization) {

        List<String> keyList = getKeyListByPattern(createOrganizationKeyPattern())

        redisService.withRedis { Jedis jedis ->

            keyList.each { key ->

                Map<String, String> redisFieldValueList = jedis.hgetAll(key)

                redisFieldValueList.each { redisFieldValue ->

                    String rowKey = redisFieldValue.key

                    if (rowKey.equals(organization.id.toString())) {

                        jedis.hdel(key,rowKey)
                        jedis.hset(key, rowKey, createOrganizationRedisValue(organization))
                    }
                }
            }
        }
    }

    String createOrganizationRedisValue(Organization organization) {

        new JSONObject(id: organization.id, name: organization.name, logo: organization.logo == null ? null : Base64.encodeAsBase64(organization.logo)).toString()
    }
}