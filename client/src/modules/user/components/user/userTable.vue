<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Kullanıcı Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddUserDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.user" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" :actions="rowActions" @edit="openEditUserDialog" @delete="deleteUser"
                                          @goToOrganization="goToOrganization">
                        <slot>
                            <td class="text-xs-left">{{ props.item.person.name }}</td>
                            <td class="text-xs-left">{{ props.item.person.surname }}</td>
                            <td class="text-xs-left">{{ props.item.person.tckn }}</td>
                            <td class="text-xs-left">{{ props.item.person.birthDate}}</td>
                            <td class="text-xs-left">{{ props.item.username}}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <user-table-add-edit-dialog ref="userAddEditDialog" @save="addUser" @edit="editUser" ></user-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>

    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import userTableAddEditDialog from "@/modules/user/components/user/userTableAddEditDialog"

    export default {
        components: {
            userTableAddEditDialog,
            TableRowAction
        },
        data() {
            return {
                tableName: "user",
                headers: [
                    { text: "Adı",value:'name'},
                    { text: "Soyadı", value:'surname'},
                    { text: "TCKN", value:'tckn'},
                    { text: "Doğum Tarihi", value:'birthDate'},
                    { text: "Kullanıcı Adı", value:'username'},
                ],
                tableItems:[],
                totalItems: 0,
                loading: false,
                rowActions : [
                    {
                        icon : "mdi-domain",
                        event : "goToOrganization",
                        title : "Organizasyona Git"

                    },
                   /* {
                        icon : "mdi-account-group",
                        event : "goToRole",
                        tooltip : "Role Git"

                    },
                    {
                        icon : "mdi-account-key",
                        event : "goToPermission",
                        tooltip : "İzine Git"

                    },
                    {
                        icon : "delete",
                        event : "delete",
                        tooltip : "Delete"

                    },*/

                ]
            }
        },
        watch: {
            '$pagination.user': {
                handler() {
                    this.getUserlist()
                },
                deep: true
            }
        },
        methods: {
            getUserlist() {
                this.loading = false;
                this.$http.get(`api/v1/user?${this.$pagination.preparePgQuery('user')}`).then((result) => {
                    debugger;
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.data.metaData.pagination.totalCount
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddUserDialog() {
                this.$refs.userAddEditDialog.open();

            },
            addUser(user) {
                this.$http.post("api/v1/user", user).then((result) => {
                    console.log(result);
                    this.getUserlist();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditUserDialog(props) {
                this.$refs.userAddEditDialog.open(props.item);
            },
            editUser (user){
                debugger;
                this.loading = true;
                this.$http.put("api/v1/user",user).then((result) => {
                    console.log(result);
                    this.getUserlist();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteUser (props){
                this.loading = true;
                this.$http.delete(`api/v1/user/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getUserlist();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goToOrganization(item){
                this.$router.push({name:"UserOrganization", params: { id : item.id}})
            }
        }

    }
</script>

<style>

</style>