package tr.com.manerp.dto


class RolePermissionDto {

    String name
    List availablePermissions
    List unavailablePermissions

    RolePermissionDto(){}
    RolePermissionDto(String name, def availablePermissions, def unavailablePermissions) {
        this.name = name
        this.availablePermissions = availablePermissions
        this.unavailablePermissions = unavailablePermissions
    }
}
