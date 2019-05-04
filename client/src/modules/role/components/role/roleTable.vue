<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Rol Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddRoleDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers"
                          :items="tableItems" disable-initial-sort :pagination.sync="$pagination.role"  :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">
                    <table-row-action :props="props" @edit="openEditRoleDialog" :actions="rowActions"
                                      @delete="deleteRole" @goToRolePermission="goToRolePermission">
                        <td>{{ props.item.name }}</td>
                        <td>{{ props.item.description }}</td>
                        <td>{{ props.item.key }}</td>
                    </table-row-action>
                </template>
            </v-data-table>

            <role-table-add-edit-dialog ref="roleAddEditDialog" @save="addRole"
                                                @edit="editRole"></role-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import roleTableAddEditDialog from "@/modules/role/components/role/roleTableAddEditDialog"

    export default {
        components: {
            TableRowAction,
            roleTableAddEditDialog
        },
        data() {
            return {
                tableName: "role",
                headers: [
                    {text: 'Adı', value: 'name'},
                    {text: 'Tanımı', value: 'description'},
                    {text: 'Anahtar', value: 'key'}],
                tableItems: [],
                totalItems: 0,
                loading: false,

                rowActions: [
                    {
                        icon : "mdi-key-variant",
                        event : "goToRolePermission",
                        title : "Rol - İzine Git"
                    }
                ]
            }
        },
        watch: {
            '$pagination.role': {
                handler() {
                    this.getRoleList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getRoleList() {
                this.loading = true;
                this.$http.get(`api/v1/role?${this.$pagination.preparePgQuery('role')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddRoleDialog() {
                this.$refs.roleAddEditDialog.open();

            },
            addRole(role) {
                this.$http.post("api/v1/role", role).then((result) => {
                    console.log(result);
                    this.getRoleList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditRoleDialog(props) {
                this.$refs.roleAddEditDialog.open(props.item);
            },
            editRole(role) {
                this.loading = true;
                this.$http.put("api/v1/role", role).then((result) => {
                    console.log(result);
                    this.getRoleList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteRole(props) {
                this.loading = true;
                this.$http.delete(`api/v1/role/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getRoleList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goToRolePermission(item){
                this.$router.push({name:"RolePermission", params: { id : item.id}})
            }
        }

    }
</script>

<style>

</style>