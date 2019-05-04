<template>
    <v-container fluid>
        <organization-role-table></organization-role-table>
    </v-container>
</template>

<script>
    import organizationRoleTable from '@/modules/organization/components/organizationRole/organizationRoleTable'
    export default {
        name: "OrganizationRole",
        components: {organizationRoleTable}
    }
</script>

<style scoped>

</style>