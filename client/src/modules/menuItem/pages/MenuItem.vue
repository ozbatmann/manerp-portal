<template>
    <v-container fluid>
    <menu-item-table></menu-item-table>
    </v-container>
</template>

<script>
    import menuItemTable from '@/modules/menuItem/components/menuItem/menuItemTable'
    export default {
        name: "MenuItem",
        components: {menuItemTable}
    }
</script>

<style scoped>

</style>