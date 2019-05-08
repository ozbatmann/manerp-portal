

<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <!-- <v-btn slot="activator" color="primary" dark>Open Dialog</v-btn> -->
        <v-card class="pa-3">
            <v-card-title>
                <span class="headline">Yeni Aksiyon Elemanı Ekle</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>
                            <v-text-field v-validate="'required'" :error-messages="errors.collect('controllerName')" v-model="data.controllerName" label="Uç Adı"name="controllerName" color="black" background-color="grey lighten-4" full-width></v-text-field>
                            <v-text-field v-validate="'required'" :error-messages="errors.collect('actionName')" v-model="data.actionName" label="Aksiyon Adı" name="actionName" color="black" background-color="grey lighten-4" full-width></v-text-field>
                            <v-checkbox  v-validate="'required'" :error-messages="errors.collect('shouldBeCleaned')" label="Temizlenmeli Mi?" v-model="data.shouldBeCleaned" name="shouldBeCleaned" ></v-checkbox>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="orange darken-1" outline @click.native="close">Kapat</v-btn>
                <v-btn color="blue darken-1" outline @click.native="save">Kaydet</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {

        data() {
            return {
                showDialog: false,
                data: {
                   controllerName : null ,  actionName : null ,  shouldBeCleaned : null
                },
            }
        },
        methods: {
            open(data) {
                this.showDialog = true;
                if (data) {
                    this.data = data;
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
                        this.$emit("save", this.data);
                        this.close();
                    }
                });
            },
            clear() {
                this.data = {
                    controllerName : null ,
                    actionName : null ,
                    shouldBeCleaned : null
                }
            }
        }
    }
</script>

<style>

</style>