<template>
    <v-container fluid>
        <user-organization-table></user-organization-table>
    </v-container>
</template>

<script>
    import userOrganizationTable from '@/modules/user/components/userOrganization/userOrganizationTable'
    export default {
        name: "UserOrganization",
        components: {userOrganizationTable}
    }
</script>

<style scoped>

</style>