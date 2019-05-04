import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router);

import AppLayout from '@/modules/shared/components/AppLayout'
/*import Home from '@/modules/core/pages/Home'*/
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
            component: AppLayout,
            children: [
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
                    path: '/user',
                    component: User,
                    name: 'User',
                    meta: {
                        title: 'user.title',
                        noCache: true
                    }
                },
                {
                    path: '/userOrganization/:id',
                    component: UserOrganization,
                    name: 'UserOrganization',
                    meta: {
                        title: 'user.organization.title',
                        noCache: true
                    }
                },
                {
                    path: '/organization',
                    component: Organization,
                    name: 'Organization',
                    meta: {
                        title: 'organization.title',
                        noCache: true
                    }
                },
                {
                    path: '/userOrganizationRole/:id',
                    component: OrganizationRole,
                    name: 'OrganizationRole',
                    meta: {
                        title: 'organization.role.title',
                        noCache: true
                    }
                },
                {
                    path: '/menu',
                    component: Menu,
                    name: 'Menu',
                    meta: {
                        title: 'menu.title',
                        noCache: true
                    }
                },
                {
                    path: '/menuItem',
                    component: MenuItem,
                    name: 'MenuItem',
                    meta: {
                        title: 'menuItem.title',
                        noCache: true
                    }
                },
                {
                    path: '/role',
                    component: Role,
                    name: 'Role',
                    meta: {
                        title: 'role.title',
                        noCache: true
                    }
                },
                {
                    path: '/rolePermission/:id',
                    component: RolePermission,
                    name: 'RolePermission',
                    meta: {
                        title: 'rolePermission.title',
                        noCache: true
                    }
                },
                {
                    path: '/securitySubject',
                    component: SecuritySubject,
                    name: 'SecuritySubject',
                    meta: {
                        title: 'securitySubject.title',
                        noCache: true
                    }
                },
                {
                    path: '/securitySubjectPermission/:id',
                    component: SecuritySubjectPermission,
                    name: 'SecuritySubjectPermission',
                    meta: {
                        title: 'securitySubjectPermission.title',
                        noCache: true
                    }
                },
                {
                    path: '/permissionType',
                    component: PermissionType,
                    name: 'PermissionType',
                    meta: {
                        title: 'permissionType.title',
                        noCache: true
                    }
                },
            ]
        },
       /* {
            path: '*',
            component: PageNotFound
        }*/
    ]
});

router.beforeEach((to, from, next) => {
   // document.title = i18n.t(to.meta.title)
    return next();
});


export default router
