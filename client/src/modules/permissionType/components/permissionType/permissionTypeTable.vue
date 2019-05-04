<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>İzin Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddPermissionTypeDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.permissionType" :rows-per-page-items="[5,10,25,50,100]"
                          :total-items="totalItems" :loading="loading" class="elevation-1">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" @edit="openEditPermissionTypeDialog"
                                      @delete="deletePermissionType">
                        <slot>
                            <td class="text-xs-left">{{ props.item.name}}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <permissionType-table-add-edit-dialog ref="permissionTypeAddEditDialog" @save="addPermissionType"
                                                   @edit="editPermissionType"></permissionType-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import permissionTypeTableAddEditDialog from "@/modules/permissionType/components/permissionType/permissionTypeTableAddEditDialog"


    export default {
        components: {
            TableRowAction,
            permissionTypeTableAddEditDialog
        },
        data() {
            return {
                tableName: "permissionType",
                headers: [
                    {text: 'Adı', value: 'name'}],
                tableItems: [],
                totalItems: 0,
                loading: false
            }
        },
        watch: {
            '$pagination.permissionType': {
                handler() {
                    this.getPermissionTypeList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getPermissionTypeList() {
                this.loading = true;
                this.$http.get(`api/v1/permissionType?${this.$pagination.preparePgQuery('permissionType')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddPermissionTypeDialog() {
                this.$refs.permissionTypeAddEditDialog.open();

            },
            addPermissionType(permissionType) {
                this.$http.post("api/v1/permissionType", permissionType).then((result) => {
                    console.log(result);
                    this.getPermissionTypeList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditPermissionTypeDialog(props) {
                this.$refs.permissionTypeAddEditDialog.open(props.item);
            },
            editPermissionType(permissionType) {
                this.loading = true;
                this.$http.put("api/v1/permissionType", permissionType).then((result) => {
                    console.log(result);
                    this.getPermissionTypeList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deletePermissionType(props) {
                this.loading = true;
                this.$http.delete(`api/v1/permissionType/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getPermissionTypeList();
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