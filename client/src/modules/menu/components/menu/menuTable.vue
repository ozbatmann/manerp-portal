<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Menü Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddMenuDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.menu" :rows-per-page-items="[5,10,25,50,100]"
                          :total-items="totalItems" :loading="loading" class="elevation-1">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" @edit="openEditMenuDialog"
                                          @delete="deleteMenu">
                        <slot>
                            <td class="text-xs-left">{{ props.item.name}}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <menu-table-add-edit-dialog ref="menuAddEditDialog" @save="addMenu"
                                        @edit="editMenu"></menu-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import menuTableAddEditDialog from "@/modules/menu/components/menu/menuTableAddEditDialog"


    export default {
        components: {
            TableRowAction,
            menuTableAddEditDialog
        },
        data() {
            return {
                tableName: "menu",
                headers: [
                    {text: 'Adı', value: 'name'}],
                tableItems: [],
                totalItems: 0,
                loading: false
            }
        },
        watch: {
            '$pagination.menu': {
                handler() {
                    this.getMenuList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getMenuList() {
                this.loading = true;
                this.$http.get(`api/v1/menu?${this.$pagination.preparePgQuery('menu')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddMenuDialog() {
                this.$refs.menuAddEditDialog.open();

            },
            addMenu(menu) {
                this.$http.post("api/v1/menu", menu).then((result) => {
                    console.log(result);
                    this.getMenuList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditMenuDialog(props) {
                this.$refs.menuAddEditDialog.open(props.item);
            },
            editMenu(menu) {
                this.loading = true;
                this.$http.put("api/v1/menu", menu).then((result) => {
                    console.log(result);
                    this.getMenuList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteMenu(props) {
                this.loading = true;
                this.$http.delete(`api/v1/menu/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getMenuList();
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