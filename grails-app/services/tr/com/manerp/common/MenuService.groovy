package tr.com.manerp.common

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

@Transactional
class MenuService extends BaseService{

    ManePaginatedResult getMenuList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(Menu, properties, closure)
    }

    Menu getMenu(String id)
    {
        Menu menu = Menu.get(id)

        return menu
    }

    def save(Menu menu)
    {
        menu.save(flush: true, failOnError: true)
    }

    def delete(Menu menu)
    {
        menu.delete(flush: true, failOnError: true)
    }
}
