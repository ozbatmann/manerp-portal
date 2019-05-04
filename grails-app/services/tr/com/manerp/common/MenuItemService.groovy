package tr.com.manerp.common

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService


@Transactional
class MenuItemService extends BaseService{

    ManePaginatedResult getMenuItemList(ManePaginationProperties properties, String menuId)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            if ( menuId ) {
                menu{
                    eq('active', true)
                    eq('id', menuId)
                }

            }

        }

        return paginate(MenuItem, properties, closure)
    }

    MenuItem getMenuItem(String id)
    {
        MenuItem menuItem = MenuItem.get(id)

        return menuItem
    }

    def save(MenuItem menuItem)
    {
        menuItem.save(flush: true, failOnError: true)
    }

    def delete(MenuItem menuItem)
    {
        menuItem.delete(flush: true, failOnError: true)
    }

    def getMenuItemListDDS() {

        List<MenuItem> list = MenuItem.createCriteria().list {
            eq("active",true)
        } as List<MenuItem>
        return list
    }
}
