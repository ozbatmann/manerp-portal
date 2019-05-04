let pgModels = {
   menu: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   menuItem: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   organization: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   permissionType: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   role: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
  rolePermission: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   securitySubject: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
    secSubPerm: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
   user: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
    userOrganization: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
    organizationRole: {
       rowsPerPage: 10,
       sortBy: '',
       descending: '',
       page: 1
     },
 };

import Vue from 'vue'

let instance = new Vue({
  data: {
    ...pgModels
  },
  methods: {
    preparePgQuery(name) {
      let paginationData = instance[name];
      return `limit=${paginationData.rowsPerPage}&offset=${(paginationData.rowsPerPage * paginationData.page) - paginationData.rowsPerPage}&sort=${paginationData.sortBy}+${paginationData.descending}`;
    }
  }
})


Object.defineProperties(Vue.prototype, {
  $pagination: {
    get: function () {
      return instance;
    }
  }
});

export default pgModels;