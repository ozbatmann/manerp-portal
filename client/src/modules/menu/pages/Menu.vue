<template>
    <v-container fluid>
        <menu-table></menu-table>
    </v-container>
    
</template>

<script>
    import menuTable from '@/modules/menu/components/menu/menuTable'
    export default {
        name: "Menu",
        components: {menuTable}
    }
</script>

<style scoped>

</style>