<template>
    <v-dialog v-model="showDialog" persistent max-width="900px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">Kullanıcı Güncelle</span>
                <span v-else class="headline">Kullanıcı Ekle</span>
            </v-card-title>
            <v-card-text>
    <v-stepper v-model="e1">
        <v-stepper-header>
            <v-stepper-step :complete="e1 > 1" step="1">Kullanıcı Bilgileri</v-stepper-step>

            <v-divider></v-divider>

            <v-stepper-step :complete="e1 > 2" step="2">Hesap Bilgileri</v-stepper-step>

            <v-divider></v-divider>
        </v-stepper-header>

        <v-stepper-items>
            <v-stepper-content step="1">
                <v-text-field
                        v-validate="'required'" label="Adı" :error-messages="errors.collect('name')"
                        v-model="data.person.name" name="name" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>
                <v-text-field
                        v-validate="'required'" label="Soyadı" :error-messages="errors.collect('surname')"
                        v-model="data.person.surname" name="surname" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>
                <v-text-field
                        v-validate="'required|max:11'" label="TCKN" :error-messages="errors.collect('tckn')"
                        v-model="data.person.tckn" name="tckn" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>
                <v-text-field
                        v-validate="'email'" label="E-Mail" :error-messages="errors.collect('email')"
                        v-model="data.person.email" name="email" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>

                <v-menu
                        ref="menu"
                        :close-on-content-click="false"
                        v-model="menu"
                        :return-value.sync="data.person.birthDate"
                        :nudge-right="650"
                        transition="scale-transition"
                        offset-y
                        full-width
                        max-width="290px"
                        min-width="290px"
                >
                    <v-combobox
                            slot="activator"
                            v-model="data.person.birthDate"
                            chips
                            label="Doğum Tarihi"
                            readonly color="black" background-color="grey lighten-4" full-width
                    ></v-combobox>
                    <v-date-picker v-model="data.person.birthDate" no-title scrollable >
                        <v-spacer></v-spacer>
                        <v-btn flat color="primary" @click="menu = false">Cancel</v-btn>
                        <v-btn flat color="primary" @click="$refs.menu.save(data.person.birthDate)">OK</v-btn>
                    </v-date-picker>
                </v-menu>

                <v-spacer></v-spacer>
                <v-layout justify-end>
                <v-btn color="red darken-1" outline flat @click.native="close">Kapat</v-btn>

                <v-btn
                        color="orange darken-1" outline
                        @click="e1 = 2"
                >
                    Devam
                </v-btn>
                </v-layout>
            </v-stepper-content>

            <v-stepper-content step="2">

                <v-text-field v-validate="'required'" :error-messages="errors.collect('username')" v-model="data.username" label="Kullanıcı Adı" name="username" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>
                <v-text-field v-validate="'required'" :error-messages="errors.collect('password')" v-model="data.password" label="Parola" type="password" name="password" color="black" background-color="grey lighten-4" full-width>
                </v-text-field>
                <v-layout justify-end>
                <v-btn color="primary" outline @click="e1 = 1">Geri</v-btn>
                <v-btn color="red darken-1" outline @click.native="close">Kapat</v-btn>
                <v-btn v-if="isEdit" color="blue darken-1" outline @click.native="edit">Güncelle</v-btn>
                <v-btn v-else color="orange darken-1" outline @click.native="save">Kaydet</v-btn>
                </v-layout>
            </v-stepper-content>
        </v-stepper-items>
    </v-stepper>
            </v-card-text>
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
                    person:{
                        name: null,
                        surname: null,
                        tckn: null,
                        birthDate: null,
                        email: null
                    },
                    username: null,
                    password: null
                },
                menu: false,
                e1: 0,
            }
        },
        methods: {
            open(data) {
                this.showDialog = true;
                this.e1 = 1;
                if (data) {
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
                    person:{
                        name : null,
                        surname : null,
                        tckn: null,
                        birthDate: null,
                        email: null
                    },
                    username: null,
                    password: null
                }
                this.isEdit = false;
            }
        }
    }
</script>

<style scoped>

</style>