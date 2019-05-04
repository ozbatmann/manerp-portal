<template>
    <v-container fluid>
        <security-subject-permission-table></security-subject-permission-table>
    </v-container>
</template>

<script>
    import SecuritySubjectPermissionTable from '@/modules/securitySubject/components/securitySubjectPermission/securitySubjectPermissionTable'

    export default {
        name: "SecuritySubjectPermission",
        components: {SecuritySubjectPermissionTable}
    }
</script>

<style scoped>

</style>