<template>
    <v-toolbar dark>
        <v-toolbar-side-icon></v-toolbar-side-icon>

        <v-spacer></v-spacer>

        <v-btn icon>
            <v-icon>search</v-icon>
        </v-btn>

        <v-btn icon>
            <v-icon>apps</v-icon>
        </v-btn>

        <v-btn icon>
            <v-icon>refresh</v-icon>
        </v-btn>

        <v-btn icon @click="logout">
            <v-icon>mdi-logout-variant</v-icon>
        </v-btn>
    </v-toolbar>
</template>

<script>
    export default {
        name: "AppToolbar",
        methods: {
            logout () {
                this.$store.state.shared['user'] = null
                this.$store.state.shared['auth-token'] = null
                this.$router.push( {path: "/auth/login"})

            },

        }
    }
</script>

<style scoped>

</style>