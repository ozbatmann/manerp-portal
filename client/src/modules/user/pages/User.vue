<template>
    <v-container fluid>
        <user-table></user-table>
    </v-container>
</template>

<script>
    import userTable from '@/modules/user/components/user/userTable'
    export default {
        name: "User",
        components: {userTable}
    }
</script>

<style scoped>

</style>