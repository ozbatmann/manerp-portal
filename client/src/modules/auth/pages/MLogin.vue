
<!-- Login component -->
<!-- Handles authentication and login system -->
<template>
    <div>
        <div class="m-login__clip-path"></div>
        <div class="mb-5">
            <h4 class="display-1 font-weight-light">
                Hoşgeldiniz, <br>
                <span class="green--text text--accent-2">
                    Bumerang Lojistik.
                </span>
            </h4>
            <h2 class="mt-5 font-weight-light">İşinizi uzaya taşıyın 🚀</h2>
        </div>
        <v-text-field
                v-model="username"
                solo
                flat
                placeholder="Kullanıcı Adı"
                background-color="grey lighten-4"
                color="black"
                required
        ></v-text-field>

        <v-text-field
                v-model="password"
                solo
                flat
                hide-details
                placeholder="Parola"
                background-color="grey lighten-4"
                color="black"
                type="password"
                required
        ></v-text-field>
        <v-layout pt-3 wrap>
            <v-flex sm4 xs12>
                <v-checkbox
                        hide-details
                        color="green accent-2"
                        label="Beni hatırla"
                        class="m-login__input mt-0"
                ></v-checkbox>
                <v-btn
                        depressed
                        color="primary-green"
                        class="white--text mx-0 mt-3"
                        :loading="loading"
                        @click="login"
                >
                    GİRİŞ YAP
                </v-btn>
            </v-flex>
            <v-flex sm8 xs12 text-xs-left text-sm-right pt-1>
                <div style="font-size: 16px;" class="secondary-text">Parolanızı mı unuttunuz?
                    <router-link
                            tag="a"
                            class="primary-green--text"
                            :to="resetPassword"
                    >
                        tıklayın.
                    </router-link>
                </div>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
    export default {
        name: "MLogin",

        data () {
            return {
                loading: false,

                // An Object that holds
                // username information of the user
                username: 'admin',

                // An Object that holds
                // password information of the user
                password: '.db9219f',

                resetPassword: {
                    name: "auth.mail",
                    params: { id: 1 }
                }
            }
        },

        methods: {
            login () {
                this.loading = true;

                let loginData = {
                    username: this.username,
                    password: this.password
                };

                this.$http.post('/api/v1/auth/login', loginData)
                    .then(result => {
                        if(result.data.status === 200){
                            console.log(this.$store.state);
                            debugger;
                            this.$store.state.shared['user'] = result.data.data.user
                            this.$store.state.shared['auth-token'] = result.data.data.token
                            this.$http.defaults.headers.common['Authorization'] = this.$store.state.shared['auth-token']
                            console.log(this.$store.state);
                            this.$router.push({name: "Overview"})
                        }
                        console.log(result)
                }).catch(error => {
                        console.log(`Error is here: ${error}`)
                }).finally(() => {
                    this.loading = false;
                });
            }
        }
    }
</script>

<style scoped>
</style>
