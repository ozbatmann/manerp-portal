<template>
    <v-container fluid>
        <permission-type-table></permission-type-table>
    </v-container>
</template>

<script>
    import permissionTypeTable from '@/modules/permissionType/components/permissionType/permissionTypeTable'
    export default {
        name: "PermissionType",
        components: {permissionTypeTable}
    }
</script>

<style scoped>

</style>