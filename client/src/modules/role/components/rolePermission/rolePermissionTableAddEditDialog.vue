<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Rol Güncelle</span>
                <span v-else class="headline">Rol Ekle</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>
                            <v-combobox  v-model="data.securitySubjectPermission" :items="secSubPerms"
                                            :return-object="true" item-value="id" item-text="name" chips
                                            label="Yetki Nesnesi-İzin"
                                            chips
                                            clearable
                                            hide-details
                                            hide-selected
                                            solo
                                            color="black" background-color="grey lighten-4" full-width
                            >
                            </v-combobox>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="orange darken-1" outline @click.native="close">Kapat</v-btn>
                <v-btn v-if="isEdit" color="blue darken-1" outline @click.native="edit">Güncelle</v-btn>
                <v-btn v-else color="blue darken-1" outline @click.native="save">Kaydet</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        data() {
            return {
                showDialog: false,
                isEdit: false,
                secSubPerms: [],
                data: {
                    securitySubjectPermission: null,
                    role: null
                },
            }
        },
        methods: {
            getDetailOfRolePermission(rolePermissionId){
                this.isLoading = true;
                this.$http.get(`api/v1/rolePermission/${rolePermissionId}`).then((result) => {
                    this.data = result.data.data
                    debugger;
                    this.isLoading = false
                }).catch((error) => {
                    console.log(error);
                })
            },
            getAvailableSecSubPermListDDS() {
                this.loading = true;
                this.$http.get(`v1/securitySubjectPermission/getAvailableSecSubPermListDDS/${this.$route.params.id}`).then((result) => {
                    debugger;
                    this.secSubPerms = result.data
                    this.totalItems = result.data.total
                    this.loading = false;
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.getAvailableSecSubPermListDDS()
                this.showDialog = true;
                if (data) {
                    this.getDetailOfRolePermission(data.id)
                    this.data = data;
                    this.isEdit = true;
                }
                else{
                    this.clear();
                }
            },
            close() {
                this.showDialog = false;
                this.clear()
                this.$validator.reset()
            },
            save() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.data.role = this.$route.params.id
                        this.$emit("save", this.data);
                        this.close();
                    }
                });
            },
            edit() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.data.role = this.$route.params.id
                        this.$emit("edit", this.data);
                        this.close();
                    }
                });
            },
            clear() {
                this.data = {
                    securitySubjectPermission: null
                }
                this.isEdit = false;
            }
        }
    }
</script>

<style>

</style>