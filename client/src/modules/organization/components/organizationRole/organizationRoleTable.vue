<template>
    <v-layout>
        <v-flex class="elevation-5">
            <v-breadcrumbs divider="-">
                <v-icon slot="divider">chevron_right</v-icon>
                <v-breadcrumbs-item :disabled="false" :to="userUrl" router>
                    Kullanıcı Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="false" :to="organizationUrl" router>
                    Kullanıcı - Organizasyon Listesi
                </v-breadcrumbs-item>
                <v-breadcrumbs-item :disabled="true">
                    Kullanıcı - Organizasyon - Rol listesi
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
                <v-toolbar-title>Kullanıcı - Organizasyon - Rol Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <span>Rol Ekle</span>
                    <v-btn icon slot="activator" @click="openAddOrganizationRoleDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.organizationRole" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" @edit="openEditOrganizationRoleDialog" @delete="deleteOrganizationRole">
                        <slot>
                            <td class="text-xs-left">{{ props.item.user.username }}</td>
                            <td class="text-xs-left">{{ props.item.organization.name }}</td>
                            <td class="text-xs-left">{{ props.item.role.name }}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <organization-role-table-add-edit-dialog ref="organizationRoleAddEditDialog" @save="addOrganizationRole" @edit="editOrganizationRole"></organization-role-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>

    import organizationRoleTableAddEditDialog from '@/modules/organization/components/organizationRole/organizationRoleTableAddEditDialog'
    import TableRowAction from "@/modules/shared/components/TableRowAction"

    export default {
        components: {
            organizationRoleTableAddEditDialog,
            TableRowAction
        },
        data() {
            return {
                headers: [
                    { text: "Kullanıcı Adı", value:'username'},
                    { text: "Organizasyon", value:'organization'},
                    { text: "Rol", value:'role'},
                ],
                tableItems:[],
                totalItems: 0,
                loading: false,
                userUrl: "/user",
                organizationUrl: `/userOrganization/${this.$route.params.userId}`
            }
        },
        watch: {
            '$pagination.organizationRole': {
                handler() {
                    this.getOrganizationRoleList()
                },
                deep: true
            },
        },
        methods: {
            getOrganizationRoleList() {
                this.loading = false;
                this.$http.get(`api/v1/user/${this.$route.params.id}/userOrganizationRoleList?${this.$pagination.preparePgQuery('organizationRole')}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.data.metaData.pagination.totalCount
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddOrganizationRoleDialog() {
                this.$refs.organizationRoleAddEditDialog.open();

            },
            addOrganizationRole(userOrganizationRole) {
                this.$http.post("api/v1/userOrganizationRole",userOrganizationRole).then((result) => {
                    console.log(result);
                    this.getOrganizationRoleList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            editOrganizationRole (userOrganizationRole){
                this.loading = true;
                this.$http.put("api/v1/userOrganizationRole",userOrganizationRole).then((result) => {
                    console.log(result);
                    this.getOrganizationRoleList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditOrganizationRoleDialog(props) {
                this.$refs.organizationRoleAddEditDialog.open(props.item);
            },
            deleteOrganizationRole (props){
                this.loading = true;
                this.$http.delete(`api/v1/userOrganizationRole/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getOrganizationRoleList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goBack(){
                this.$router.push(this.organizationUrl)
            }
        }

    }
</script>

<style scoped>

</style>