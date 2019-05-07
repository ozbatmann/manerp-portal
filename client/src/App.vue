<template>
  <v-app>
      <transition>
        <router-view></router-view>
      </transition>
      <vue-snotify></vue-snotify>
  </v-app>
</template>

<script>

export default {
  name: 'app',
  components: {
  }
}
</script>

<style>
</style>
