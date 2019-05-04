<template>
    <v-layout>
        <v-flex class="elevation-5">
            <v-breadcrumbs divider="-">
                <v-icon slot="divider">chevron_right</v-icon>
                <v-breadcrumbs-item :disabled="false" :to="roleUrl" router>
                   Rol Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="true">
                    Rol - İzin Listesi
                </v-breadcrumbs-item>
            </v-breadcrumbs>

            <v-toolbar class="elevation-1">
                <v-tooltip bottom>
                    <v-btn icon slot="activator">
                        <v-icon color="primary"  medium class=null @click="goBack()">
                            chevron_left
                        </v-icon>
                    </v-btn>
                    <span>Geri Dön</span>
                </v-tooltip>
                <v-toolbar-title>Rol - İzin Listesi</v-toolbar-title>
                <v-spacer></v-spacer>

                <v-tooltip bottom>
                    <v-btn icon slot="activator"  @click="openAddRolePermissionDialog">
                        <v-icon >add</v-icon>
                    </v-btn>
                    <span>Rol - İzin Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers"
                          :items="tableItems" disable-initial-sort :pagination.sync="$pagination.rolePermission" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">
                    <table-row-action :props="props" @edit="openEditRolePermissionDialog"
                                      @delete="deleteRolePermission">
                        <td>{{ props.item.role.name }}</td>
                        <td>{{ props.item.securitySubject.name }}-{{props.item.permissionType.name}}</td>
                    </table-row-action>
                </template>
            </v-data-table>

            <role-permission-table-add-edit-dialog ref="rolePermissionAddEditDialog" @save="addRolePermission"
                                                @edit="editRolePermission"></role-permission-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import rolePermissionTableAddEditDialog from "@/modules/role/components/rolePermission/rolePermissionTableAddEditDialog"

    export default {
        components: {
            TableRowAction,
            rolePermissionTableAddEditDialog
        },
        data() {
            return {
                tableName: "rolePermission",
                headers: [
                    {text: 'Rol', value: 'rolee'},
                    {text: 'İzin', value: 'securitySubjectPermission'}],
                tableItems: [],
                totalItems: 0,
                loading: false,
                roleUrl: '/role'
            }
        },
        watch: {
            '$pagination.rolePermission': {
                handler() {
                    this.getRolePermissionList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getRolePermissionList() {
                this.loading = true;
                this.$http.get(`api/v1/role/${this.$route.params.id}/rolePermissionList?${this.$pagination.preparePgQuery('rolePermission')}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.total
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddRolePermissionDialog() {
                this.$refs.rolePermissionAddEditDialog.open();

            },
            addRolePermission(rolePermission) {
                this.$http.post("api/v1/rolePermission", rolePermission).then((result) => {
                    console.log(result);
                    this.getRolePermissionList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditRolePermissionDialog(props) {
                this.$refs.rolePermissionAddEditDialog.open(props.item);
            },
            editRolePermission(rolePermission) {
                this.loading = true;
                this.$http.put("api/v1/rolePermission", rolePermission).then((result) => {
                    console.log(result);
                    this.getRolePermissionList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteRolePermission(props) {
                this.loading = true;
                this.$http.delete(`api/v1/rolePermission/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getRolePermissionList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goBack(){
                this.$router.push(this.roleUrl)
            }

        }

    }
</script>

<style>

</style>