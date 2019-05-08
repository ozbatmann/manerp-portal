<!-- Default application menu component -->
<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-app>

    <app-toolbar></app-toolbar>

  <v-navigation-drawer
          mobile-break-point="700"
          fixed app class="elevation-1"
          stateless
          value="true"
          width="300px"
          hide-overlay
          v-model="show"
          :mini-variant="mini"
          color="primary"

  >

    <v-layout fill-height>
      <v-list class="grow pt-0">
        <v-toolbar class="toolbar-shadow">
          <v-list class="pa-0">

            <v-list-tile avatar>

              <v-list-tile-avatar>
                <img src="./../../../assets/logo.png">
              </v-list-tile-avatar>

              <v-list-tile-content>

                <v-slide-x-transition>
                  <v-list-tile-title class="font-weight-light black--text text--accent-5">
                    MANERP Portal
                  </v-list-tile-title>
                </v-slide-x-transition>
              </v-list-tile-content>
            </v-list-tile>
          </v-list>
          <v-spacer></v-spacer>
          <v-icon v-if="!mini"
                  @click.stop="mini = !mini" color="black">
            chevron_left
          </v-icon>
        </v-toolbar>

        <v-list-tile v-if="mini" class="ma-2">
          <v-list-tile-action>
              <v-icon @click.stop="mini = !mini" color="black">chevron_right</v-icon>
          </v-list-tile-action>
        </v-list-tile>
        <div
                v-for="(menuItem, index) in menuItems"
                :key="`menu-` + menuItem.title"
                class="mt-2"
        >
          <v-subheader class="px-4">{{ menuItem.title }}</v-subheader>



          <v-list-tile
                  v-for="item in menuItem.items"
                  :key="`menu-item-${item.title}`"
                  :to="item.to"
                  outline
                  active-class="grey lighten-2 "
          class="mx-2">

            <v-tooltip
                    right
                    :disabled="!mini"
            >
              <template v-slot:activator="{ on }">
                <v-list-tile-action
                        v-on="on"
                >
                  <v-icon color="grey darken-3">{{ item.icon }}</v-icon>
                </v-list-tile-action>
                <v-list-tile-content>
                <v-list-tile-title
                        v-if="!localDrawer"
                        v-text="item.title"
                        class="body-1 text--secondary">

                </v-list-tile-title>
                </v-list-tile-content>
              </template>
              <span>{{ item.title }}</span>
            </v-tooltip>
          </v-list-tile>

          <v-divider v-if="index !== menuItems.length -1" class="my-3"></v-divider>
        </div>
      </v-list>
    </v-layout>
  </v-navigation-drawer>
    <v-content>
    <v-fade-transition mode="out-in">

      <router-view></router-view>
    </v-fade-transition>
    </v-content>
  </v-app>
</template>

<script>

  import AppToolbar from '@/modules/shared/components/AppToolbar'
  export default {
    name: "AppLayout",
    components: {AppToolbar},

    props: {
      value: {
        type: Boolean,
        default: false,
      }
    },

    data() {
      return {
        localDrawer: this.value,
        show: true,
        mini: false,

        menuItems: [
          {
            title: 'GENEL',
            items: [
              {
                title: 'Genel Bakış',
                icon: 'dashboard',
                to: '/'
              },
            ]
          },
          {
            title: 'Kullanıcı',
            items: [
              {
                title: 'Kullanıcı Yönetimi',
                icon: 'mdi-account',
                to: { name: 'User'}
              },
            ]
          },
          {
            title: 'Organizasyon',
            items: [
              {
                title: 'Organizasyon Yönetimi',
                icon: 'mdi-domain',
                to: { name: 'Organization'}
              },
            ]
          },
          {
            title: 'Menü',
            items: [
              {
                title: 'Menü Yönetimi',
                icon: 'menu',
                to: { name: 'Menu'}
              },
              {
                title: 'Menü Elemanı Yönetimi',
                icon: 'mdi-sitemap',
                to: { name: 'MenuItem'}
              },
            ]
          },
          {
            title: 'Yetkilendirme',
            items: [
              {
                title: 'Rol Yönetimi',
                icon: 'mdi-account-key',
                to: { name: 'Role'}
              },
              {
                title: 'Yetki Nesnesi Yönetimi',
                icon: 'mdi-lock',
                to: { name: 'SecuritySubject'}
              },
              {
                title: 'İzin Yönetimi',
                icon: 'mdi-key',
                to: { name: 'PermissionType'}
              },
              {
                title: 'Aksiyon Elemanı Yönetimi',
                icon: 'mdi-key',
                to: { name: 'ActionItem'}
              },
            ]
          }
        ],
      }
    },

    watch: {
      value() {
        this.localDrawer = this.value
      }
    }

  }
</script>

<style scoped>
  .v-list__tile--link{
    border-radius: 10px;
  }
</style>
