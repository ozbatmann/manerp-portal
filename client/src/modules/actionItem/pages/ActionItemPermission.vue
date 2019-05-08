<template>
    <v-container fluid>
        <action-item-permission-table></action-item-permission-table>
    </v-container>
</template>

<script>
    import ActionItemPermissionTable from '@/modules/actionItem/components/actionItemPermissionTable/actionItemPermissionTable'
    export default {
        name: "ActionItemPermission",
        components: {ActionItemPermissionTable}
    }
</script>

<style scoped>

</style>