<template>
    <div>
        <div class="m-login__clip-path"></div>
        <div class="mb-5">
            <h4 class="display-1 font-weight-light">
                <span class="green--text text--accent-2">
                            Parolanızı mı unuttunuz?
                        </span>
            </h4>
            <h2 class="mt-5 font-weight-light">Yeni bir parola oluşturun.</h2>
        </div>
        <v-text-field
                v-model="password"
                :error="error"
                solo
                flat
                placeholder="Yeni parola"
                background-color="grey lighten-4"
                color="black"
                max="20"
                counter="20"
                type="password"
                required
        ></v-text-field>

        <v-text-field
                v-model="passwordRepeat"
                :error="error"
                solo
                flat
                placeholder="Yeni parola tekrar"
                background-color="grey lighten-4"
                color="black"
                max="20"
                counter="20"
                type="password"
                required
        ></v-text-field>
        <v-layout pt-3 column>
            <v-slide-y-transition>
                <v-flex v-if="message">
                    <span :class="[ message ? 'error--text' : '' ]">{{ message }}</span>
                </v-flex>
            </v-slide-y-transition>
            <v-flex>
                <v-btn
                        depressed
                        color="primary-green"
                        class="white--text mx-0 mt-3"
                        :loading="loading"
                        @click="resetPassword"
                >
                    KAYDET
                </v-btn>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
    export default {
        name: "MResetPassword",

        data() {
            return {
                error: false,

                loading: false,

                message: '',

                password: '',
                passwordRepeat: '',
            }
        },

        methods: {
            resetPassword() {
                this.message = null;
                this.error = false;

                if (!this.password.length || !this.passwordRepeat.length) {
                    this.error = true;
                    this.message = 'Lütfen boş bırakmayın';
                } else if (this.password === this.passwordRepeat) {
                    this.loading = true;

                    // Req to server
                } else {
                    this.error = true;
                    this.message = 'Girdiğiniz parolalar uyuşmuyor';
                }
            }
        }
    }
</script>

<style scoped>

</style>
