
<template>
    <v-layout>
        <v-flex class="elevation-5">
            <v-breadcrumbs divider="-">
                <v-icon slot="divider">chevron_right</v-icon>
                <v-breadcrumbs-item :disabled="false" :to="actionItemUrl" router>
                    Aksiyon Elemanı Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="true">
                    Aksiyon Elemanı - İzin Listesi
                </v-breadcrumbs-item>
            </v-breadcrumbs>

            <v-toolbar class="elevation-1">
                <v-tooltip bottom>
                    <v-btn icon slot="activator">
                        <v-icon color="primary"  medium class="" @click="goBack()">
                            chevron_left
                        </v-icon>
                    </v-btn>
                    <span>Geri Dön</span>
                </v-tooltip>
                <v-toolbar-title>Aksiyon Elemanı - İzin Listesi</v-toolbar-title>
                <v-spacer></v-spacer>

                <v-tooltip bottom>
                    <v-btn icon slot="activator"  @click="openAddActionItemPermissionDialog">
                        <v-icon >add</v-icon>
                    </v-btn>

                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="pagination" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">

                            <td class="text-xs-left">{{ props.item.securitySubject.name }}</td>
                            <td class="text-xs-left">{{ props.item.permissionType.name }}</td>

                </template>
            </v-data-table>

            <action-item-permission-table-add-edit-dialog ref="actionItemPermissionAddEditDialog" @save="addActionItemPermission" @edit="editActionItemPermission" ></action-item-permission-table-add-edit-dialog>

        </v-flex>

    </v-layout>

</template>

<script>

    import actionItemPermissionTableAddEditDialog from "@/modules/actionItem/components/actionItemPermissionTable/actionItemPermissionTableAddEditDialog"

    export default {
        components: {
            actionItemPermissionTableAddEditDialog,
        },
        data() {
            return {
                tableName: "actionItemPermission",
                headers: [
                    { text: "Yetki Nesnesi", value:'name' } ,
                    { text: "İzin", value:'langKey' },
                ],
                tableItems: [],
                totalItems: 0,
                loading: false,
                pagination: {},
                actionItemUrl: "/actionItem"
            }
        },
        watch: {
            pagination: {
                handler() {
                    this.getActionItemPermissions()
                },
                deep: true
            }
        },
        methods: {
            getActionItemPermissions() {
                this.loading = true;
                this.$http.get(`api/v1/actionItem/${this.$route.params.id}/actionItemPermissions?limit=${this.pagination.rowsPerPage}&offset=${(this.pagination.rowsPerPage * this.pagination.page) - this.pagination.rowsPerPage}&sort=${this.pagination.sortBy}+${this.pagination.descending}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.total
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddActionItemPermissionDialog() {
                this.$refs.actionItemPermissionAddEditDialog.open();

            },
            addActionItemPermission(actionItemPermission) {
                this.$http.post("api/v1/actionItemPermission", actionItemPermission).then((result) => {
                    console.log(result);
                    this.getActionItemPermissions();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditActionItemPermissionDialog(props) {
                this.$refs.actionItemPermissionAddEditDialog.open(props.item);
            },
            editActionItemPermission (actionItemPermission){
                this.$http.put("api/v1/actionItemPermission", actionItemPermission).then((result) => {
                    console.log(result);
                    this.getActionItemPermissions();
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteActionItemPermission (props){
                this.loading = true;
                this.$http.delete(`api/v1/actionItemPermission/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getActionItemPermissions();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goBack(){
                this.$router.push(this.actionItemUrl)
            }
        }

    }
</script>

<style>

</style>