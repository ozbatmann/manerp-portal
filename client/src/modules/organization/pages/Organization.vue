<template>
    <v-container fluid>
        <organization-table></organization-table>
    </v-container>
</template>

<script>
    import organizationTable from '@/modules/organization/components/organization/organizationTable'
    export default {
        name: "Organization",
        components: {organizationTable}
    }
</script>

<style scoped>

</style>