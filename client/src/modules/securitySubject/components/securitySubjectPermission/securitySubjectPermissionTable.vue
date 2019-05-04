
<template>
    <v-layout>
        <v-flex class="elevation-5">
            <v-breadcrumbs divider="-">
                <v-icon slot="divider">chevron_right</v-icon>
                <v-breadcrumbs-item :disabled="false" :to="securitySubjectUrl" router>
                    Yetki Nesnesi Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="true">
                    Yetki Nesnesi - İzin Listesi
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
                <v-toolbar-title>Yetki Nesnesi - İzin Listesi</v-toolbar-title>
                <v-spacer></v-spacer>

                <v-tooltip bottom>
                    <v-btn icon slot="activator"  @click="openAddSecuritySubjectPermissionDialog">
                        <v-icon >add</v-icon>
                    </v-btn>

                    <span>İzin Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.secSubPerm" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" :actions="rowActions" @edit="openEditSecuritySubjectPermissionDialog" @delete="deleteSecuritySubjectPermission">
                        <slot>
                            <td class="text-xs-left">{{ props.item.securitySubject.name }}</td>
                            <td class="text-xs-left">{{ props.item.permissionType.name }}</td>
                            <td class="text-xs-left" v-if="props.item.dependsOnMenuItem"> <v-icon color="green">done</v-icon></td>
                            <td class="text-xs-left" v-else> <v-icon color="red">clear</v-icon></td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <security-subject-permission-table-add-edit-dialog ref="securitySubjectPermissionAddEditDialog" @save="addSecuritySubjectPermission" @edit="editSecuritySubjectPermission" ></security-subject-permission-table-add-edit-dialog>

        </v-flex>

    </v-layout>

</template>

<script>

    import securitySubjectPermissionTableAddEditDialog from "@/modules/securitySubject/components/securitySubjectPermission/securitySubjectPermissionTableAddEditDialog"
    import TableRowAction from "@/modules/shared/components/TableRowAction"

    export default {
        components: {
            securitySubjectPermissionTableAddEditDialog,
            TableRowAction
        },
        data() {
            return {
                tableName: "securitySubjectPermission",
                headers: [
                    { text: "Yetki Nesnesi", value:'langKey' } ,
                    { text: "İzin", value:'langKey' },
                    { text: "Menü Elemanına Bağlı Mı", value:'dependsOnMenuItem' }
                ],
                tableItems: [],
                totalItems: 0,
                loading: false,
                pagination: {
                  rowsPerPage: 10
                },
                securitySubjectUrl: "/securitySubject"
            }
        },
        watch: {
            '$pagination.secSubPerm': {
                handler() {
                    this.getSecuritySubjectPermissionList()
                },
                deep: true
            }
        },
        methods: {
            getSecuritySubjectPermissionList() {
                this.loading = true;
                this.$http.get(`api/v1/securitySubject/${this.$route.params.id}/securitySubjectPermissionList?${this.$pagination.preparePgQuery('secSubPerm')}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.total
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddSecuritySubjectPermissionDialog() {
                this.$refs.securitySubjectPermissionAddEditDialog.open();

            },
            addSecuritySubjectPermission(securitySubjectPermission) {
                this.$http.post("api/v1/securitySubjectPermission", securitySubjectPermission).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectPermissionList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditSecuritySubjectPermissionDialog(props) {
                this.$refs.securitySubjectPermissionAddEditDialog.open(props.item);
            },
            editSecuritySubjectPermission (securitySubjectPermission){
                this.$http.put("api/v1/securitySubjectPermission", securitySubjectPermission).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectPermissionList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteSecuritySubjectPermission (props){
                this.loading = true;
                this.$http.delete(`api/v1/securitySubjectPermission/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectPermissionList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goBack(){
                this.$router.push(this.securitySubjectUrl)
            }
        }

    }
</script>

<style>

</style>