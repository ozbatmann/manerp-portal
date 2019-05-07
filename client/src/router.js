import Vue from 'vue'
import Router from 'vue-router'
import {store} from 'manerp-vue-base'
Vue.use(Router);

import App from '@/App'
import AppLayout from '@/modules/shared/components/AppLayout'
/*import Home from '@/modules/core/pages/Home'*/
import Login from '@/modules/auth/pages/MLogin'
import User from '@/modules/user/pages/User'
import Organization from '@/modules/organization/pages/Organization'
import OrganizationRole from '@/modules/organization/pages/OrganizationRole'
import UserOrganization from '@/modules/user/pages/UserOrganization'
import Menu from '@/modules/menu/pages/Menu'
import MenuItem from '@/modules/menuItem/pages/MenuItem'
import Role from '@/modules/role/pages/Role'
import RolePermission from '@/modules/role/pages/RolePermission'
import SecuritySubject from '@/modules/securitySubject/pages/SecuritySubject'
import SecuritySubjectPermission from '@/modules/securitySubject/pages/SecuritySubjectPermission'
import PermissionType from '@/modules/permissionType/pages/PermissionType'

/*
import User from '@/modules/user/pages/User'
import UserOrganization from '@/modules/user/pages/UserOrganization'
import UserOrganizationRole from '@/modules/user/pages/UserOrganizationRole'

import Role from '@/modules/role/pages/Role'
import RolePermissionScreen from '@/modules/role/components/rolePermissionScreen/rolePermissionScreen'
*/

/*import PageNotFound from '@/modules/core/pages/PageNotFound'*/


const router = new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            component: App,
            redirect : "overview",
            children: [
                {
                    path: 'auth',
                    redirect: { name: "Login" },
                    component: () => import('@/modules/auth/pages/MCoreAuthentication'),
                    children: [
                        {
                            path: 'login',
                            name: "Login",
                            // beforeEnter: authenticated(),
                            component: () => import('@/modules/auth/pages/MLogin')
                        },
                        {
                            path: 'reset/mail',
                            name: "SendResetMail",
                            component: () => import('@/modules/auth/pages/MResetPasswordSendMail')
                        },
                        {
                            path: 'reset/password/:id',
                            name: "ResetPassword",
                            component: () => import('@/modules/auth/pages/MResetPassword')
                        }
                    ]
                },
               /* {
                path: '',
                component: Home,
                name: 'Home',
                meta: {
                    title: 'app.name',
                    noCache: true,
                }
            },*/
                {
                    path: '/overview',
                    component: AppLayout,
                    name: 'Overview',
                    meta: {
                        title: 'overview.title',
                        noCache: true,
                        requiresAuth: true
                    },
                    children: [
                        {
                            path: '/user',
                            component: User,
                            name: 'User',
                            meta: {
                                title: 'user.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/userOrganization/:id',
                            component: UserOrganization,
                            name: 'UserOrganization',
                            meta: {
                                title: 'user.organization.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/organization',
                            component: Organization,
                            name: 'Organization',
                            meta: {
                                title: 'organization.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/userOrganizationRole/:id',
                            component: OrganizationRole,
                            name: 'OrganizationRole',
                            meta: {
                                title: 'organization.role.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/menu',
                            component: Menu,
                            name: 'Menu',
                            meta: {
                                title: 'menu.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/menuItem',
                            component: MenuItem,
                            name: 'MenuItem',
                            meta: {
                                title: 'menuItem.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/role',
                            component: Role,
                            name: 'Role',
                            meta: {
                                title: 'role.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/rolePermission/:id',
                            component: RolePermission,
                            name: 'RolePermission',
                            meta: {
                                title: 'rolePermission.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/securitySubject',
                            component: SecuritySubject,
                            name: 'SecuritySubject',
                            meta: {
                                title: 'securitySubject.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/securitySubjectPermission/:id',
                            component: SecuritySubjectPermission,
                            name: 'SecuritySubjectPermission',
                            meta: {
                                title: 'securitySubjectPermission.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },
                        {
                            path: '/permissionType',
                            component: PermissionType,
                            name: 'PermissionType',
                            meta: {
                                title: 'permissionType.title',
                                noCache: true,
                                requiresAuth: true
                            }
                        },

                    ]
                }
            ]
        },

       /* {
            path: '*',
            component: PageNotFound
        }*/
    ]
});

router.beforeEach((to, from, next) => {
    if(to.matched.some(record => record.meta.requiresAuth)) {
        debugger
        if (!store.state.shared['auth-token']) {
            next({
                path: "/auth/login",
                params: { nextUrl: to.fullPath }
            })
        }
    }
    next();
});


export default router
