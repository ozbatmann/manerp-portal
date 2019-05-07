package tr.com.manerp.dto


class RolePermissionDto {

    String name
    List permissions

    RolePermissionDto(){
        this.permissions = new ArrayList()
    }
}
