<template>
    <v-container fluid>
        <role-table></role-table>
    </v-container>
</template>

<script>
    import roleTable from '@/modules/role/components/role/roleTable'
    export default {
        name: "Role",
        components: {roleTable}
    }
</script>

<style scoped>

</style>