
<template>
    <v-dialog v-model="showDialog" persistent max-width="500px">
        <v-card class="pa-3">
            <v-card-title>
                <span v-if="isEdit" class="headline">{{$t('menuItem.messages.updateMenuItem')}}</span>
                <span v-else class="headline">{{$t('menuItem.messages.addMenuItem')}}</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md pa-0>
                    <v-layout wrap>
                        <v-flex xs12>
                            <v-text-field v-validate="'required'" :error-messages="errors.collect('name')" v-model="data.name" label="Adı" name="name" color="black" background-color="grey lighten-4" full-width></v-text-field>

                            <v-text-field v-model="data.description" label="Tanımı" name="description" color="black" background-color="grey lighten-4" full-width></v-text-field>


                            <v-combobox v-validate="'required'" :error-messages="errors.collect('type')" v-model="data.type"
                                        :items="['CONTAINER','MENU_ITEM']"
                                        chips  label="Tipi" name="type" color="black" background-color="grey lighten-4" full-width>
                            </v-combobox>

                            <v-text-field v-model="data.url" label="URL" name="url" color="black" background-color="grey lighten-4" full-width></v-text-field>


                            <v-text-field v-validate="'required'" :error-messages="errors.collect('orderNo')" v-model="data.orderNo" label="Sıra Numarası" name="orderNo" type="number" color="black" background-color="grey lighten-4" full-width></v-text-field>



                            <v-autocomplete  v-validate="'required'" :error-messages="errors.collect('menu')" v-model="data.menu" :return-object="true"
                                             :items="menus" item-value="id" label="Menü"  item-text="name" name="menu"
                                             chips
                                             clearable
                                             hide-details
                                             hide-selected
                                             solo color="black" background-color="grey lighten-4" full-width

                            >
                            </v-autocomplete>


                            <v-autocomplete v-if="data.type!== 'CONTAINER'" v-model="data.parentMenuItem" :items="parentMenuItems"
                                            :return-object="true" item-value="id" :item-text="(item)=>item.name" chips
                                            label="Ana Menü Elemanı"
                                            chips
                                            clearable
                                            hide-details
                                            hide-selected
                                            solo class="mt-4"
                                            color="black" background-color="grey lighten-4" full-width
                            >
                            </v-autocomplete>


                            <v-autocomplete v-if="data.type!== 'CONTAINER'" v-model="data.securitySubjectPermission" :items="secSubPerms"
                                            :return-object="true" item-value="id" :item-text="(item)=>item.securitySubject.name" chips
                                            label="Yetki Nesnesi-İzin"
                                            chips
                                            clearable
                                            hide-details
                                            hide-selected
                                            solo class="mt-4"
                                            color="black" background-color="grey lighten-4" full-width
                            >
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

        // TODO: container ise secsubperm yok!
        data() {
            return {
                showDialog: false,
                isEdit: false,
                data: {
                    name : null ,  description : null ,  type: null,  url : null ,  orderNo : null ,  menu : null ,  parentMenuItem : null ,  securitySubjectPermission : ""
                },
                menus: [],
                parentMenuItems: [],
                secSubPerms: [],
                maxMenuItems:100,
                offset:0
            }
        },
        mounted(){
          this.getMenuItemListDDS()
          this.getSecSubPermListDDS()

        },
        methods: {
            getMenuList(){
                this.$http.get("api/v1/menu").then((result) => {
                    this.menus = result.data.data.items
                }).catch((error) => {
                    console.log(error);
                })
            },
            getDetailOfMenuItem(id){
                this.$http.get(`api/v1/menuItem/${id}`).then((result) => {
                    debugger;
                    this.data = result.data.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            getMenuItemListDDS(){
                this.data.parentMenuItem = null
                this.$http.get(`v1/menuItem/getMenuItemListDDS`).then((result) => {
                    this.parentMenuItems = result.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            getSecSubPermListDDS(){
                this.$http.get(`v1/securitySubjectPermission/getSecSubPermListDDS`).then((result) => {
                    this.secSubPerms = result.data
                }).catch((error) => {
                    console.log(error);
                })
            },
            open(data) {
                this.getMenuList();
                this.showDialog = true;
                if (data) {
                    this.getDetailOfMenuItem(data.id)
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
                    name : null ,  description : null ,  type: null, url : null ,  orderNo : null ,  menu : null ,  parentMenuItem : null ,  securitySubjectPermission : ""

                }
                this.isEdit = false;
            }
        }
    }
</script>

<style>

</style>