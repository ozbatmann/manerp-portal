package tr.com.manerp.enumeration

enum MenuItemType {

    CONTAINER('container'),
    MENU_ITEM('menuItem')

    final String value

    private MenuItemType(String value) { this.value = value }

    String toString() { value }
}
