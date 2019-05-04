<template>
    <v-container fluid>
        <security-subject-table></security-subject-table>
    </v-container>
</template>

<script>
    import securitySubjectTable from '@/modules/securitySubject/components/securitySubject/securitySubjectTable'
    export default {
        name: "SecuritySubject",
        components: {securitySubjectTable}
    }
</script>

<style scoped>

</style>