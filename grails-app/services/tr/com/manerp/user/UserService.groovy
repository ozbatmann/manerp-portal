package tr.com.manerp.user

import at.favre.lib.crypto.bcrypt.BCrypt
import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

import java.nio.charset.StandardCharsets

@Transactional
class UserService extends BaseService{

    ManePaginatedResult getUserList(ManePaginationProperties properties){
        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(User, properties, closure)

    }

    def save(User user) {
        user.save(flush: true, failOnError: true)
    }
    def delete(User user){
        user.delete(flush: true, failOnError: true)
    }

    ManePaginatedResult getUserOrganizationList(ManePaginationProperties properties,String userId){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

            user{
                eq("id",userId)
            }

        }
        return paginate(UserOrganization, properties, closure)
    }

    ManePaginatedResult getUserOrganizationRoleList(ManePaginationProperties properties,String organizationId){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

            organization{
                eq("id",organizationId)
            }

        }
        return paginate(UserOrganizationRole, properties, closure)
    }

    boolean isValidUser(String username, String password) {

        User incomingUser = User.findByUsername(username)

        UserOrganization userOrganization = UserOrganization.findByUser(incomingUser)

        if(!incomingUser){

            throw new Exception("Kullanıcı bilgileri yanlış.")

        }

        byte[] encryptedPassword = BCrypt.withDefaults().hash(6, incomingUser.getSalt(), password.getBytes(StandardCharsets.UTF_8))

        User user = User.createCriteria().get {

            eq("username",username)
            eq("password",encryptedPassword)
            eq("active",true)

        } as User

        if(incomingUser != null && user == null){

            throw new Exception("Parola yanlış.")
        }
        else if(user == null){

            throw new Exception("Kullanıcı bilgileri yanlış.")
        } else if (userOrganization == null){
            throw new Exception("Organizasyon bilgisi bulunamadı.")
        }

        true
    }
}
