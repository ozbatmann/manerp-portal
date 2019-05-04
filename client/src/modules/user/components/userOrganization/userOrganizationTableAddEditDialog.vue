<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Kullanıcı Organizasyonu Güncelle</span>
                <span v-else class="headline">Kullanıcı Organizasyonu Ekle</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>
                            <v-autocomplete v-validate="'required'" :error-messages="errors.collect('userOrg')" v-model="data.organization" :items="organizations"
                                            :return-object="true" item-value="id" item-text="name" chips
                                            label="Organizasyon"
                                            chips
                                            clearable
                                            hide-details
                                            hide-selected
                                            solo color="black" background-color="grey lighten-4" full-width
                            name="userOrg">
                            </v-autocomplete>


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
                data: {
                    organization: null,
                    user:null,
                    id: null
                },
                organizations: [],
                pagination:{
                    rowsPerPage: 500,
                    sortBy:'',
                    descending: '',
                    page: 0},
                searchData: null
            }

        },
        methods: {
          getAllOrganizationListDDS(){
                this.$http.get(`v1/organization/getOrganizationListDDS`).then((result) => {
                    this.organizations = result.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            getDetailOfOrg(orgId){
                this.$http.get(`api/v1/organization/${orgId}`).then((result) => {
                    debugger;
                    this.data.organization = result.data.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.getAllOrganizationListDDS();
                this.showDialog = true;
                if (data) {
                    this.isEdit = true;
                    this.data.id = data.id
                    this.getDetailOfOrg(data.organization.id);
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
                        this.data.user = this.$route.params.id
                        this.$emit("save", this.data);
                        this.close();
                    }
                });
            },
            edit() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.data.user = this.$route.params.id
                        this.$emit("edit", this.data);
                        this.close();
                    }
                });
            },
            clear() {
                this.data = {
                    organization: null,
                    user: null
                }
                this.isEdit = false;
            }
        }
    }
</script>

<style>

</style>