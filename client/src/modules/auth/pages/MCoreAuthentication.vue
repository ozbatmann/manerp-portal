<template>
    <v-container
            fluid
            fill-height
            pa-0
    >
        <v-layout
                align-center
                fill-height
                wrap
        >
            <v-flex
                    hidden-xs-only
                    sm7
                    style="position: relative;"
            >
                <div class="m-login__brand">
                    <v-img
                            :src="require('@/assets/logo_w.png')"
                            class="mx-auto m-login__background"
                            height="125"
                            width="200"
                            contain
                    ></v-img>
                    <p class="brand mx-auto">M A N E R P</p>
                </div>
                <div id="m-login__overlay"></div>
                <v-img
                        :src="require('@/assets/login_background.jpeg')"
                        height="100vh"
                ></v-img>
            </v-flex>
            <v-flex
                    xs12
                    sm5
                    px-5
                    style="position: relative;"
            >
                <div class="m-login__clip-path"></div>
                <v-slide-x-transition mode="out-in">
                    <router-view></router-view>
                </v-slide-x-transition>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    export default {
        name: "MCoreAuthentication"
    }
</script>

<style>
    body {
        background: #f6f9fc;
    }

    .m-login__input label.v-label {
        font-size: 14px !important;
    }

    .m-login__background img {
        -webkit-clip-path: circle(53.7% at 38% 31%);
        clip-path: circle(53.7% at 38% 31%);
    }

    .m-login__brand {
        position: absolute;
        top: 40%;
        left: 50%;
        z-index: 5;
        width: 100%;
        text-align: center;
        -webkit-transform: translate3d(-50%, -40%, 0);
        -moz-transform: translate3d(-50%, -40%, 0);
        -ms-transform: translate3d(-50%, -40%, 0);
        -o-transform: translate3d(-50%, -40%, 0);
        transform: translate3d(-50%, -40%, 0);
    }

    .m-login__brand .brand {
        font-family: 'Century Gothic', sans-serif;
        font-size: 64px;
        width: 75%;
        text-align: justify-all;
        color: white;
    }

    .m-login__brand ~ #m-login__overlay {
        position: absolute;
        width: 100%;
        height: 100%;
        z-index: 3;
        background: rgba(0, 0, 0, 0.5);
    }

    .m-login__clip-path ~ div > h4,
    .m-login__clip-path ~ div > h2 {
        position: relative;
        z-index: 2;
    }

    .m-login__clip-path {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: -45%;
        z-index: 0;
        background: white;
        -webkit-transform: skewY(-12deg);
        -moz-transform: skewY(-12deg);
        -ms-transform: skewY(-12deg);
        -o-transform: skewY(-12deg);
        transform: skewY(-12deg);
    }
</style>
