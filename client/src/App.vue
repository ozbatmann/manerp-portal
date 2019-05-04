<template>
  <v-app>
    <template>
      <transition>
        <router-view></router-view>
      </transition>
      <vue-snotify></vue-snotify>
    </template>
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
