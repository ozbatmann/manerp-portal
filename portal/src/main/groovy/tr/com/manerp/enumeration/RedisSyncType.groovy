package tr.com.manerp.enumeration

enum RedisSyncType {

    PERMISSION('PERMISSION'),
    MENU('MENU'),
    ACTION_ITEM('ACTION_ITEM')

    final String value

   private RedisSyncType(String value) { this.value = value }

    String toString() { value }
}
