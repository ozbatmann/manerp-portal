<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Aksiyon Elemanı Güncelle</span>
                <span v-else class="headline">Aksiyon Elemanı Ekle</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>
                            <v-combobox  v-validate="'required'" :error-messages="errors.collect('secSubPerm')" v-model="data.securitySubjectPermission" :return-object="true" name="secSubPerm" :items="securitySubjectPermissions"
                                        item-value="id" label="İzin"  item-text="securitySubject.name" color="black" background-color="grey lighten-4" full-width>

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
                data: {
                    securitySubjectPermission : null
                },
                securitySubjectPermissions: []
            }
        },
        methods: {
            getSecuritySubjectPermissions(){
                this.$http.get(`v1/securitySubjectPermission/getSecSubPermListNotExistInActionsDDS`).then((result) => {
                    this.securitySubjectPermissions = result.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.showDialog = true;
                this.getSecuritySubjectPermissions();
                if (data) {
                    this.data = data;
                    this.isEdit = true;
                }
                else{
                    this.clear();
                }
            },
            close() {
                this.$validator.reset()
                this.showDialog = false;
            },
            save() {
                this.$validator.validateAll().then((result) => {
                    if (result) {
                        this.data.actionItem = this.$route.params.id
                        this.$emit("save", this.data);
                        this.close();
                    }
                });
            },
            edit() {
                this.data.actionItem = this.$route.params.id
                this.$emit("edit", this.data);
                this.close();
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