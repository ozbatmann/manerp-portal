<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Organizasyon Güncelle</span>
                <span v-else class="headline">Organizasyon Ekle</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>

                            <v-text-field  v-model="data.unitCode"
                                           label="Birim Kodu" name="unitCode" color="black" background-color="grey lighten-4" full-width>
                            </v-text-field>
                            <v-text-field  v-validate="'required'" :error-messages="errors.collect('name')" v-model="data.name" label="Adı"
                                           name="name" color="black" background-color="grey lighten-4" full-width>
                            </v-text-field>

                            <v-text-field v-model="data.description"
                                          label="Tanım" name="description" color="black" background-color="grey lighten-4" full-width>
                            </v-text-field>

                            <v-autocomplete  v-model="data.parent" :items="organizations"
                                             :return-object="true"
                                             label="Ana Organizasyon"
                                             item-value="id" item-text="name"
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
                <v-btn v-if="isEdit" color="blue darken-1" outline @click.native="edit">Güncelle</v-btn>
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
                data: {
                    unitCode: null, name: null, description: null, parent: null, logo: null
                },
                organizations: [],
            }
        },
        methods: {
            getOrganizationListDDS() {
                this.loading = false;
                this.$http.get("v1/organization/getOrganizationListDDS").then((result) => {
                    debugger;
                    this.organizations = result.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.getOrganizationListDDS();
                this.showDialog = true;
                if (data) {
                    this.data = data;
                    this.isEdit = true;
                } else {
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
                        this.$emit("save", this.data);
                        this.close();
                    }
                });
            },
            edit() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.$emit("edit", this.data);
                        this.close();
                    }
                });
            },
            clear() {
                this.data = {
                    unitCode: null,
                    name: null,
                    description: null,
                    parentOrganization: null
                }
                this.isEdit = false;
            }
        }
    }
</script>

<style>

</style>