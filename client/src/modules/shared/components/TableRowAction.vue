<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <tr style="transform: scale(1);">
        <slot></slot>
        <v-fade-transition slot="append">
            <td class="actionBtn">
               <v-layout row>
                   <v-menu
                           transition="slide-x-transition"
                           bottom
                           right
                           dark
                           :nudge-left="20"
                   >
                       <template v-slot:activator="{ on }">

                           <v-btn icon slot="activator"  v-on="on">
                               <v-icon>mdi-dots-vertical</v-icon>
                           </v-btn>

                       </template>

                       <v-list>
                           <v-list-tile @click="edit(props)" class="px-2">
                               <v-list-tile-action>
                                   <v-icon color="light-green accent-2">edit</v-icon>
                               </v-list-tile-action>
                               <v-list-tile-title>Güncelle</v-list-tile-title>
                           </v-list-tile>
                           <v-list-tile @click="remove(props)" class="px-2">
                               <v-list-tile-action>
                                   <v-icon color="light-green accent-2">delete</v-icon>
                               </v-list-tile-action>
                               <v-list-tile-title>Sil</v-list-tile-title>
                           </v-list-tile>
                           <template v-for="action in actions">
                               <v-list-tile @click="actionTriggered(action.event)" class="px-2">
                                   <v-list-tile-action>
                                       <v-icon color="light-green accent-2">{{action.icon}}</v-icon>
                                   </v-list-tile-action>
                                   <v-list-tile-title>{{action.title}}</v-list-tile-title>
                               </v-list-tile>
                           </template>
                       </v-list>
                   </v-menu>
                </v-layout>
            </td>
        </v-fade-transition>


    </tr>
</template>

<script>
    export default {
        props: ["props","actions"],
        name: "TableRowAction",
        data() {
            return {
            }
        },
        methods: {
            edit(item){
                this.$emit("edit",item)
            } ,
            remove(item){
                this.$emit("delete",item)
            },
            actionTriggered(event){
                this.$emit(event,this.props.item);
            }
        }

    }
</script>

<style>
    .actionBtn{
        position: absolute;
        top: 0px;
        right: 0
    }
    .theme--dark.v-list .v-list__group__header:hover, .theme--dark.v-list .v-list__tile--highlighted, .theme--dark.v-list .v-list__tile--link:hover{
        border-radius: 10px !important;
    }
</style>