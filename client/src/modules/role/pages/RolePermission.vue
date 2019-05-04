<template>
    <v-container fluid>
        <rolePermission-table></rolePermission-table>
    </v-container>
</template>

<script>
    import rolePermissionTable from '@/modules/role/components/rolePermission/rolePermissionTable'
    export default {
        name: "RolePermission",
        components: {rolePermissionTable}
    }
</script>

<style scoped>

</style>