<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Organizasyon Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon slot="activator" @click="openAddOrganizationDialog">
                        <v-icon>add</v-icon>
                    </v-btn>
                    <span>Ekle</span>
                </v-tooltip>
            </v-toolbar>
            <v-data-table :headers="headers"
                          :items="tableItems" disable-initial-sort :pagination.sync="$pagination.organization" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">
                    <table-row-action :props="props" @edit="openEditOrganizationDialog"
                                      @delete="deleteOrganization">
                    <td>{{ props.item.name }}</td>
                    <td>{{ props.item.unitCode }}</td>
                    <td>{{ props.item.description }}</td>
                    <td>{{ props.item.parentOrganization.name }}</td>
                    </table-row-action>
                </template>
            </v-data-table>

            <organization-table-add-edit-dialog ref="organizationAddEditDialog" @save="addOrganization"
                                        @edit="editOrganization"></organization-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"
    import organizationTableAddEditDialog from "@/modules/organization/components/organization/organizationTableAddEditDialog"

    export default {
        components: {
            TableRowAction,
            organizationTableAddEditDialog
        },
        data() {
            return {
                tableName: "organization",
                headers: [
                    {text: 'Adı', value: 'name'},
                    {text: 'Birim Kodu', value: 'unitCode'},
                    {text: 'Tanımı', value: 'description'},
                    {text: 'Ana Organizasyon', value: 'parentOrganization'}],
                tableItems: [],
                totalItems: 0,
                loading: false
            }
        },
        watch: {
            '$pagination.organization': {
                handler() {
                    this.getOrganizationList();
                },
                deep: true
            }
        },
        mounted(){
        },
        methods: {
            getOrganizationList() {
                this.loading = true;
                this.$http.get(`api/v1/organization?${this.$pagination.preparePgQuery('organization')}`).then((result) => {
                    this.tableItems = result.data.data.items;
                    this.totalItems = result.data.data.metaData.pagination.totalCount;
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddOrganizationDialog() {
                this.$refs.organizationAddEditDialog.open();

            },
            addOrganization(organization) {
                this.$http.post("api/v1/organization", organization).then((result) => {
                    console.log(result);
                    this.getOrganizationList();
                }).catch((error) => {
                    console.log(error);
                })
            },
            openEditOrganizationDialog(props) {
                this.$refs.organizationAddEditDialog.open(props.item);
            },
            editOrganization(organization) {
                this.loading = true;
                this.$http.put("api/v1/organization", organization).then((result) => {
                    console.log(result);
                    this.getOrganizationList();
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            deleteOrganization(props) {
                this.loading = true;
                this.$http.delete(`api/v1/organization/${props.item.id}`).then((result) => {
                    console.log(result);
                    this.getOrganizationList();
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