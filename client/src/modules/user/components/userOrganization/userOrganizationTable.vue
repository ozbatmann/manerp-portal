<template>
    <v-layout>
        <v-flex class="elevation-5">
            <v-breadcrumbs divider="-">
                <v-icon slot="divider">chevron_right</v-icon>
                <v-breadcrumbs-item :disabled="false" :to="userUrl" router>
                    Kullanıcı Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="true">
                    Kullanıcı - Organizasyon Listesi
                </v-breadcrumbs-item>
            </v-breadcrumbs>


            <v-toolbar class="elevation-1">
                <v-tooltip bottom>
                    <v-btn icon slot="activator">
                        <v-icon color="primary"  medium class=null @click="goBack()">
                            chevron_left
                        </v-icon>
                    </v-btn>
                    <span>Geri Git</span>
                </v-tooltip>
                <v-toolbar-title>Kullanıcı - Organizasyon Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn icon slot="activator" @click="openAddUserOrganizationDialog">
                            <v-icon>add</v-icon>
                        </v-btn>
                        <span>Organizasyon Ekle</span>
                    </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.userOrganization" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" :actions="rowActions" @edit="openEditUserOrganizationDialog" @delete="deleteUserOrganization" @goToOrgRole="goToOrgRole">
                        <slot>
                            <td class="text-xs-left">{{ props.item.organization.unitCode }}</td>
                            <td class="text-xs-left">{{ props.item.organization.name }}</td>
                            <td class="text-xs-left">{{ props.item.organization.description }}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <user-organization-table-add-edit-dialog ref="userOrganizationAddEditDialog" @save="addUserOrganization" @edit="editUserOrganization"></user-organization-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>

    import userOrganizationTableAddEditDialog from "@/modules/user/components/userOrganization/userOrganizationTableAddEditDialog"
    import TableRowAction from "@/modules/shared/components/TableRowAction"

    export default {
        components: {
            userOrganizationTableAddEditDialog,
            TableRowAction
        },
        data() {
            return {
                tableName: "app",
                headers: [
                    { text:  "Birim Kodu",value:'unitCode'},
                    { text: "Adı", value:'name'},
                    { text: "Tanım", value:'description'},
                ],
                tableItems:[],
                totalItems: 0,
                loading: false,
                rowActions : [
                    {
                        icon : "mdi-key-variant",
                        event : "goToOrgRole",
                        title : "Organizasyon - Role Git"
                    }

                ],
                userUrl: "/user"
            }
        },
        watch: {
            '$pagination.userOrganization': {
                handler() {
                    this.getUserOrganizationList()
                },
                deep: true
            },
        },
        methods: {
            getUserOrganizationList() {
                this.loading = false;
                this.$http.get(`api/v1/user/${this.$route.params.id}/userOrganizationList?${this.$pagination.preparePgQuery('userOrganization')}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.data.metaData.pagination.totalCount
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddUserOrganizationDialog() {
                this.$refs.userOrganizationAddEditDialog.open();

            },
            addUserOrganization(userOrganization) {
                this.$http.post('api/v1/userOrganization',userOrganization).then((result) => {
                    console.log(result);
                    this.getUserOrganizationList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditUserOrganizationDialog(props) {
                debugger;
                this.$refs.userOrganizationAddEditDialog.open(props.item);
            },
            editUserOrganization (userOrganization){
                this.loading = true;
                this.$http.put("api/v1/userOrganization",userOrganization).then((result) => {
                    console.log(result);
                    this.getUserOrganizationList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteUserOrganization (props){
                this.loading = true;
                this.$http.delete(`api/v1/userOrganization/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getUserOrganizationList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goBack(){
                this.$router.push(this.userUrl)
            },
            goToOrgRole(item){
                this.$router.push({name:"OrganizationRole", params: { id : item.organization.id, userId: this.$route.params.id}})
            }
        }

    }
</script>

<style scoped>

</style>