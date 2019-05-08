<template>
    <v-container fluid>
       <action-item-table></action-item-table>
    </v-container>
</template>

<script>
    import ActionItemTable from '@/modules/actionItem/components/actionItemTable/actionItemTable'
    export default{
        name: 'ActionItem',
        components: {ActionItemTable}
    }
</script>