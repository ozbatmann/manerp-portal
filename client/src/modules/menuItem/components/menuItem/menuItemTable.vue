<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Menü Elemanı Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddMenuItemDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers"
                          :items="tableItems" disable-initial-sort :pagination.sync="$pagination.menuItem" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">
                    <table-row-action :props="props" @edit="openEditMenuItemDialog"
                                      @delete="deleteMenuItem">
                    <td class="text-xs-left">{{ props.item.name }}</td>
                    <td class="text-xs-left">{{ props.item.description }}</td>
                    <td class="text-xs-left">{{ props.item.type }}</td>
                    <td class="text-xs-left">{{ props.item.url }}</td>
                    <td class="text-xs-left">{{ props.item.orderNo }}</td>
                    <td class="text-xs-left">{{ props.item.menu.name }}</td>
                    <td class="text-xs-left">{{ props.item.parentMenuItem.name }}</td>
                    <td class="text-xs-left" v-if="props.item.securitySubjectPermission.dependsOnMenuItem"> <v-icon color="green">done</v-icon></td>
                    <td class="text-xs-left" v-else> <v-icon color="red">clear</v-icon></td>
                    </table-row-action>
                </template>
            </v-data-table>

            <menu-item-table-add-edit-dialog ref="menuItemAddEditDialog" @save="addMenuItem"
                                        @edit="editMenuItem"></menu-item-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import menuItemTableAddEditDialog from "@/modules/menuItem/components/menuItem/menuItemTableAddEditDialog"

    export default {
        components: {
            TableRowAction,
            menuItemTableAddEditDialog
        },
        data() {
            return {
                tableName: "menuItem",
                headers: [
                    {text: "Adı", value:'name' },
                    {text: "Tanımı", value:'description' },
                    {text: "Tipi", value:'type' },
                    {text: "URL", value:'url' },
                    {text: "Sıra Numarası", value:'orderNo' },
                    {text: "Menü", value:'menu' },
                    {text: "Ana Menü Elemanı", value:'parentMenuItem' },
                    {text: "Yetki Nesnesi-İzin", value:'secSubPerm' }
                ],
                tableItems: [],
                totalItems: 0,
                loading: false
            }
        },
        watch: {
            '$pagination.menuItem': {
                handler() {
                    this.getMenuItemList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getMenuItemList() {
                this.loading = true;
                this.$http.get(`api/v1/menuItem?${this.$pagination.preparePgQuery('menuItem')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddMenuItemDialog() {
                this.$refs.menuItemAddEditDialog.open();

            },
            addMenuItem(menuItem) {
                this.$http.post("api/v1/menuItem", menuItem).then((result) => {
                    console.log(result);
                    this.getMenuItemList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditMenuItemDialog(props) {
                this.$refs.menuItemAddEditDialog.open(props.item);
            },
            editMenuItem(menuItem) {
                this.loading = true;
                this.$http.put("api/v1/menuItem", menuItem).then((result) => {
                    console.log(result);
                    this.getMenuItemList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteMenuItem(props) {
                this.loading = true;
                this.$http.delete(`api/v1/menuItem/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getMenuItemList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            }
        }

    }
</script>

<style>

</style>