

<template>
    <v-layout>
        <v-flex>
            <v-toolbar class="elevation-1">
                <v-toolbar-title>Aksiyon Elemanı Listesi</v-toolbar-title>
                <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn icon slot="activator"  @click="openAddActionItemDialog">
                            <v-icon >add</v-icon>
                        </v-btn>

                        <span>Ekle</span>
                    </v-tooltip>

            </v-toolbar>
            <v-data-table :headers="headers" disable-initial-sort :items="tableItems" :pagination.sync="pagination" :total-items="totalItems" :loading="loading" class="elevation-1" :rows-per-page-items="[5,10,25,50,100]">
                <template slot="items" slot-scope="props">
                <table-row-action :props="props" :actions="rowActions" @goToActionItemPermission="goToActionItemPermission">
                            <td class="text-xs-left">{{ props.item.controllerName }}</td>
                            <td class="text-xs-left">{{ props.item.actionName }}</td>
                            <td class="text-xs-left" v-if="props.item.shouldBeCleaned"> <v-icon color="green">done</v-icon></td>
                            <td class="text-xs-left" v-else> <v-icon color="red">clear</v-icon></td>

                </table-row-action>
                </template>

            </v-data-table>

            <action-item-table-add-edit-dialog ref="actionItemAddEditDialog" @save="addActionItem" ></action-item-table-add-edit-dialog>
        </v-flex>

    </v-layout>

</template>

<script>
    import TableRowAction from "@/modules/shared/components/TableRowAction"

    import actionItemTableAddEditDialog from "@/modules/actionItem/components/actionItemTable/actionItemTableAddEditDialog"

    export default {
        components: {
            actionItemTableAddEditDialog,TableRowAction
        },
        data() {
            return {
                tableName: "actionItem",
                headers: [
                    { text: "Uç Adı", value:'controllerName' } ,
                    { text: "Aksiyon Adı", value:'actionName' } ,
                    { text: "Temizlenmeli Mi", value:'shouldBeCleaned' }
                ],
                rowActions : [
                    {
                        icon : "mdi-key-variant",
                        event : "goToActionItemPermission",
                        title : "Aksiyon Elemanı-İzine Git"

                    }
                ],
                tableItems: [],
                totalItems: 0,
                loading: false,
                pagination: {
                  rowsPerPage: 10
                }
            }
        },
        watch: {
            pagination: {
                handler() {
                    this.getActionItems()
                },
                deep: true
            }
        },
        methods: {
            getActionItems() {
                this.loading = true;
                this.$http.get(`api/v1/actionItem?${this.$pagination.preparePgQuery('actionItem')}`).then((result) => {
                    this.tableItems = result.data.data.items
                    this.totalItems = result.data.total
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            openAddActionItemDialog() {
                this.$refs.actionItemAddEditDialog.open();

            },
            addActionItem(actionItem) {
                this.$http.post("api/v1/actionItem", actionItem).then((result) => {
                    console.log(result);
                    this.getActionItems();
                }).catch((error) => {
                    console.log(error);
                })
            },
            goToActionItemPermission(item) {
                this.$router.push({name:"ActionItemPermission", params: { id : item.id}})
            },
        }

    }
</script>

<style>

</style>