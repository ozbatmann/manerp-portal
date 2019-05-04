<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Yetki Nesnesi Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddSecuritySubjectDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="$pagination.securitySubject" :rows-per-page-items="[5,10,25,50,100]"
                          :total-items="totalItems" :loading="loading" class="elevation-1">
                <template slot="items" slot-scope="props">

                    <table-row-action :props="props" @edit="openEditSecuritySubjectDialog" :actions="rowActions" @goToSecuritySubjectPermission="goToSecuritySubjectPermission"
                                      @delete="deleteSecuritySubject">
                        <slot>
                            <td class="text-xs-left">{{ props.item.name}}</td>
                        </slot>
                    </table-row-action>

                </template>
            </v-data-table>

            <securitySubject-table-add-edit-dialog ref="securitySubjectAddEditDialog" @save="addSecuritySubject"
                                        @edit="editSecuritySubject"></securitySubject-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import securitySubjectTableAddEditDialog from "@/modules/securitySubject/components/securitySubject/securitySubjectTableAddEditDialog"


    export default {
        components: {
            TableRowAction,
            securitySubjectTableAddEditDialog
        },
        data() {
            return {
                tableName: "securitySubject",
                headers: [
                    {text: 'Adı', value: 'name'}],
                tableItems: [],
                totalItems: 0,
                loading: false,
                rowActions : [
                    {
                        icon : "mdi-key-variant",
                        event : "goToSecuritySubjectPermission",
                        title : "Yetki Nesnesi - İzine Git"
                    }

                ]
            }
        },
        watch: {
            '$pagination.securitySubject': {
                handler() {
                    this.getSecuritySubjectList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getSecuritySubjectList() {
                this.loading = true;
                this.$http.get(`api/v1/securitySubject?${this.$pagination.preparePgQuery('securitySubject')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddSecuritySubjectDialog() {
                this.$refs.securitySubjectAddEditDialog.open();

            },
            addSecuritySubject(securitySubject) {
                this.$http.post("api/v1/securitySubject", securitySubject).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditSecuritySubjectDialog(props) {
                this.$refs.securitySubjectAddEditDialog.open(props.item);
            },
            editSecuritySubject(securitySubject) {
                this.loading = true;
                this.$http.put("api/v1/securitySubject", securitySubject).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteSecuritySubject(props) {
                this.loading = true;
                this.$http.delete(`api/v1/securitySubject/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getSecuritySubjectList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            goToSecuritySubjectPermission(item) {
                this.$router.push({name:"SecuritySubjectPermission", params: { id : item.id}})
            }
        }

    }
</script>

<style>

</style>