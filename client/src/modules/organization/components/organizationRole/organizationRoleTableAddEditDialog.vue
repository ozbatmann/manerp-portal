<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">

        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Kullanıcı - Organizasyon - Rol Güncelle</span>
                <span v-else class="headline">Kullanıcı - Organizasyon - Rol Kaydet</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>

                            <v-autocomplete  v-validate="'required'" :error-messages="errors.collect('role')" v-model="data.role" :return-object="true" :items="roles"
                                             item-value="id" label="Rol"  item-text="name" name="role"
                                             chips
                                             clearable
                                             hide-details
                                             hide-selected
                                             solo color="black" background-color="grey lighten-4" full-width>
                            </v-autocomplete>

                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="orange darken-1" outline @click.native="close">Kapat</v-btn>
                <v-btn v-if="isEdit" color="blue darken-1" outline @click.native="edit">Güncellee</v-btn>
                <v-btn v-else color="blue darken-1" outline @click.native="save">Kaydet</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

</template>

<script>

    export default {
        components: {},
        data() {
            return {
                showDialog: false,
                isEdit: false,
                isRoleDisabled: true,
                data: {
                    user: null,
                    organization: null,
                    role: null,
                },
                userOrganizations: [],
                roles: [],
                isLoading: false
            }
        },
        mounted(){
           this.getRoleListDDS()
        },
        methods: {
            getDetailOfUserOrganizationRole(userOrganizationRoleId) {
                this.isLoading = true;
                this.$http.get(`api/v1/userOrganizationRole/${userOrganizationRoleId}`).then((result) => {
                    this.data.organization = result.data.data.organization
                    this.data.role = result.data.data.role
                    this.data.id = result.data.data.id
                    this.isLoading = false
                }).catch((error) => {
                    console.log(error);
                })
            },
            getRoleListDDS() {
                this.isLoading = true;
                this.$http.get(`v1/role/getRoleListDDS`).then((result) => {
                    this.roles = result.data
                    this.isLoading = false
                    this.loading = false
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.showDialog = true;
                if (data) {
                    this.getDetailOfUserOrganizationRole(data.id)
                    this.isEdit = true;
                    this.isRoleDisabled = false;
                }else {
                    this.clear();
                }
            },
            close() {
                this.showDialog = false;
                this.isRoleDisabled = true;
                this.clear()
                this.$validator.reset()
            },
            save() {
                console.log(this.data)
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        debugger;
                        this.data.user = this.$route.params.userId
                        this.data.organization = this.$route.params.id
                        this.$emit("save", this.data);
                        this.close();
                        this.isRoleDisabled = true;
                    }
                });
            },
            edit() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.data.user = this.$route.params.userId
                        this.data.organization = this.$route.params.id
                        this.$emit("edit", this.data);
                        this.close();
                        this.isRoleDisabled = false;
                    }
                });
            },
            clear() {
                this.data = {
                    organization : null,
                    role:  null,
                    user: null
                };

                this.isEdit = false;
            }
        }
    }
</script>

<style>

</style>