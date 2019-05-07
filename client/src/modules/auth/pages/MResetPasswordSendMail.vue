<template>
    <div>
        <div class="m-login__clip-path"></div>
        <div class="mb-5">
            <h4 class="display-1 font-weight-light">
                <span class="green--text text--accent-2">
                            Parolanızı mı unuttunuz?
                        </span>
            </h4>
            <h2 class="mt-5 font-weight-light">E-posta adresinizi girin ve arkanıza yaslanın.</h2>
        </div>
        <v-text-field
                v-model="email"
                solo
                flat
                placeholder="E-posta adresi"
                background-color="grey lighten-4"
                color="black"
                type="email"
                required
        ></v-text-field>

        <v-text-field
                v-model="emailRepeat"
                solo
                flat
                hide-details
                placeholder="E-posta adresi tekrar"
                background-color="grey lighten-4"
                color="black"
                type="email"
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
                        @click="sendResetPasswordRequest"
                >
                    GÖNDER
                </v-btn>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
    export default {
        name: "MResetPasswordSendMail",

        data() {
            return {
                email: '',
                emailRepeat: '',
                error: false,

                loading: false,

                message: '',

            }
        },

        methods: {
            sendResetPasswordRequest() {
                this.message = null;
                this.error = false;

                if (!this.email.length || !this.emailRepeat.length) {
                    this.error = true;
                    this.message = 'Lütfen boş bırakmayın';
                } else if (this.email === this.emailRepeat) {
                    this.loading = true;

                    // Req to server
                } else {
                    this.error = true;
                    this.message = 'Girdiğiniz e-posta adresleri uyuşmuyor';
                }
            }
        }
    }
</script>

<style scoped>

</style>
