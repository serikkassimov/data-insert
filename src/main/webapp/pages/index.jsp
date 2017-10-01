<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>World Class Finance</title>
        <link rel="stylesheet" href="./js/element-ui.css">
        <style type="text/css">
            .page-view-enter-active {
                transition: opacity .5s
            }

            .page-view-leave-active .page-view-leave {
                display: none
            }

            .page-view-enter,
            .page-view-leave-to {
                opacity: 0
            }

            /*
            Element UI grid CSS
            */
            .el-col {
                border-radius: 2px;
            }
            .bg-purple {
                background-color: #EFF2F7;
            }
            .grid-content {
                border-radius: 2px;
                min-height: 36px;
            }
        </style>
    </head>
    <body>
        <div id="app">
            <template>
                <el-row :gutter="2">
                    <el-col :xs="8" :sm="6" :md="4" :lg="3">
                        <div class="grid-content bg-purple">Лого</div>
                    </el-col>
                    <el-col :xs="16" :sm="18" :md="20" :lg="21">
                        <div class="grid-content bg-purple" style="text-align: right;">
                            <template v-if="account.anonymousUser">
                                <el-button @click="showLoginDialog">Войти</el-button>
                            </template>
                            <template v-else>
                                <el-tag>{{ account.username }} ({{ account.email }})</el-tag>
                                <el-button @click="startLogout">Выйти</el-button>
                            </template>
                        </div>
                    </el-col>
                </el-row>
                <el-dialog title="Вход" size="tiny" :visible.sync="login.dialog.visible" v-loading.body="login.dialog.loading">
                    <span slot="title" class="dialog-title">
                        Вход
                    </span>
                    <el-form ref="loginForm" :model="login.form.data" :rules="login.form.rules" :label-position="'right'" :label-width="'100px'">
                        <el-form-item label="Логин" prop="login" :required="true">
                            <el-input v-model="login.form.data.login" auto-complete="off"></el-input>
                        </el-form-item>
                        <el-form-item label="Пароль" prop="password" :required="true">
                            <el-input v-model="login.form.data.password" type="password" auto-complete="off"></el-input>
                        </el-form-item>
                        <el-form-item label="Запомнить меня" :required="false">
                            <el-checkbox v-model="login.form.data.rememberMe"></el-checkbox>
                        </el-form-item>
                    </el-form>
                    <span slot="footer" class="dialog-footer">
                        <template v-if="login.dialog.error && (login.dialog.error.length > 0)">
                            <div>{{ login.dialog.error }}</div>
                        </template>
                        <el-button type="primary" @click="startLogin">Войти</el-button>
                        <el-button @click="login.dialog.visible = false">Отмена</el-button>
                    </span>
                </el-dialog>
            </template>
        </div>
        <div id="loading-layer" style="position: fixed; left: 0px; top: 0px; right: 0px; bottom: 0px; align-content: center;">
            <image src="./images/loading.gif" alt="Loading..." style="margin: auto; display: block;"/>
        </div>
        
        <script src="./js/jquery-3.2.1.min.js"></script>
        <script src="./js/common.js"></script>
        <script src="./js/vue.js"></script>
        <script src="./js/vue-resource.min.js"></script>
        <script src="./js/vue-router.js"></script>
        <script src="./js/vuex.js"></script>
        <script src="./js/element-ui.js"></script>
        <script src="./js/element-ui/locale/ru-RU.js"></script>
        
        <c:forEach items="${includedVues}" var="includedVue">
<c:import url="/${includedVue}" charEncoding="utf-8"></c:import>
        </c:forEach>
        
        <script>
            (function($) {
                const moduleAccount = {
                    namespaced: true,
                    state: {
                        anonymousUser: true,
                        authorities: [{name: 'ROLE_ANONYMOUS'}],
                        username: '',
                        email: '',
                        firstName: '',
                        lastName: '',
                        accountNonExpired: true,
                        accountNonLocked: true,
                        credentialsNonExpired: true,
                        enabled: true
                        /*
                        Example content if user is authorized
    
                        email: 'admin@worldclass.kz',
                        firstName: 'Admin',
                        lastName: 'Admin',
                        username: 'admin',
                        accountNonExpired: true,
                        accountNonLocked: true,
                        credentialsNonExpired: true,
                        enabled: true,
                        authorities: [{name: 'ROLE_ADMIN'}]
                        } */
                    },
                    mutations: {
                        resetToAnonimous: function(state) {;
                            state.anonymousUser = true;
                            state.authorities = [{name: 'ROLE_ANONYMOUS'}],
                            state.username = '';
                            state.email = '';
                            state.firstName = '';
                            state.lastName = '';
                            state.accountNonExpired = true;
                            state.accountNonLocked = true;
                            state.credentialsNonExpired = true;
                            state.enabled = true;
                            // console.log('resetted to anonimous, new state:', $.extend(true, {}, state));
                        },
                        updateState: function(state, newState) {
                            state.anonymousUser = true;
                            state.authorities = [{name: 'ROLE_ANONYMOUS'}],
                            state.username = '';
                            state.email = '';
                            state.firstName = '';
                            state.lastName = '';
                            state.accountNonExpired = true;
                            state.accountNonLocked = true;
                            state.credentialsNonExpired = true;
                            state.enabled = true;
                            if (isObject(newState) && isBoolean(newState.anonymousUser) && (!newState.anonymousUser)) {
                                state.anonymousUser = false;
                                if (isObject(newState.principal)) newState = newState.principal;

                                state.authorities = [];
                                if (isArray(newState.authorities) && (newState.authorities.length > 0)) {
                                    for (var index in newState.authorities) {
                                        var authority = newState.authorities[index];
                                        if (isObject(authority) && isNonEmptyString(authority.name)) state.authorities.push({name: authority.name});
                                    }
                                }
                                if (state.authorities.length === 0) state.authorities = [{name: 'ROLE_ANONYMOUS'}];
                                if (isString(newState.username)) state.username = newState.username;
                                if (isString(newState.email)) state.email = newState.email;
                                if (isString(newState.firstName)) state.firstName = newState.firstName;
                                if (isString(newState.lastName)) state.lastName = newState.lastName;
                                if (isBoolean(newState.accountNonExpired)) state.accountNonExpired = newState.accountNonExpired;
                                if (isBoolean(newState.accountNonLocked)) state.accountNonLocked = newState.accountNonLocked;
                                if (isBoolean(newState.credentialsNonExpired)) state.credentialsNonExpired = newState.credentialsNonExpired;
                                if (isBoolean(newState.enabled)) state.enabled = newState.enabled;
                            }
                            // console.log('updated from', newState, ', new state:', $.extend(true, {}, state));
                        }
                    },
                    actions: {
                        update: function(context) {
                            $.ajax({
                                url: '/data-insert/auth/info',
                                context: context,
                                dataType: 'json',
                                error: function(jqXHR, textStatus, errorThrown) {
                                    console.error('Error while updating account: ' + textStatus + ' - ' + errorThrown);
                                },
                                success: function(data, textStatus, jqXHR) {
                                    this.commit('updateState', data);
                                },
                                complete: function(jqXHR, textStatus) {}
                            });
                        }
                    }
                };
                const moduleRoutes = {
                    namespaced: true,
                    state: {
                        routes: [
                            /*
                            Each page component must add own route info
                            Example route info:
                            {
                                path: '/...',
                                name: '',
                                menuHierarchy:
                                component: Vue.component('...'),
                                requiredAuthorities: ['', ..., '']
                            }
                            */
                        ]
                    }
                };
                const store = new Vuex.Store({
                    modules: {
                        account: moduleAccount,
                        routes: moduleRoutes
                    }
                });

                setInterval(function(){
                    store.dispatch('account/update');
                }, 5000);
                store.dispatch('account/update');
    
                const routes = [
                    { path: '/foo', component: Vue.component('components_page-views_foo') }
                ];
    
                const router = new VueRouter({
                    routes: routes
                });
    
                $(document).ready(function() {
                    new Vue({
                        el: "#app",
                        store: store,
                        router: router,
                        data: {
                            login: {
                                dialog: {
                                    visible: false,
                                    loading: false,
                                    error: ''
                                },
                                form: {
                                    data: {
                                        login: '',
                                        password: '',
                                        rememberMe: false
                                    },
                                    rules: {
                                        login: [
                                            {required: true, message: 'Необходимо ввести логин (макс 50 символов)', trigger: 'blur'},
                                            {min: 1, max: 50, message: 'Необходимо ввести логин (макс 50 символов)', trigger: 'blur'}
                                        ],
                                        password: [
                                            {required: true, message: 'Необходимо ввести пароль (макс 50 символов)', trigger: 'blur'},
                                            {min: 1, max: 50, message: 'Необходимо ввести пароль (макс 50 символов)', trigger: 'blur'}
                                        ]
                                    }
                                }
                            }
                        },
                        computed: Vuex.mapState({
                            account: state => state.account,
                            accountAlias: 'account',
                            c: function() {
                                return 'c';
                            },
                            b: state => 'b'
                        }),
                        methods: {
                            showLoginDialog: function() {
                                this.login.dialog.error = '';
                                this.login.dialog.visible = true;
                            },
                            startLogin: function() {
                                this.login.dialog.error = '';
                                this.$refs.loginForm.validate(this.loginValidationCallback);
                            },
                            loginValidationCallback: function(valid) {
                                if (valid) {
                                    var data = {
                                        'login': this.login.form.data.login,
                                        'password': this.login.form.data.password
                                    };
                                    if (this.login.form.data.rememberMe) data['remember-me'] = 'true';
    
                                    this.login.dialog.loading = true;
    
                                    $.ajax({
                                        url: '/data-insert/auth/login',
                                        method: 'POST',
                                        context: this,
                                        data: data,
                                        dataType: 'json',
                                        error: function(jqXHR, textStatus, errorThrown) {
                                            this.login.dialog.error = textStatus + ': ' + errorThrown;
                                        },
                                        success: function(data, textStatus, jqXHR) {
                                            var results = ['SUCCESS', 'EXCEPTION', 'BAD_CREDENTIALS'];
                                            switch (results.indexOf(data)) {
                                                case 0: // SUCCESS
                                                    this.login.dialog.visible = false;
                                                    var message = this.$message;
                                                    this.$store.dispatch('account/update').then(function() {
                                                        message.success('Вход выполнен');
                                                    });
                                                    break;
                                                case 1: // EXCEPTION
                                                    this.login.dialog.error = 'Неизвестная ошибка входа';
                                                    break;
                                                case 2: // BAD_CREDENTIALS
                                                    this.login.dialog.error = 'Неправильный логин или пароль';
                                                    break;
                                                default:
                                                    this.login.dialog.error = 'Неизвестный ответ сервера: ' + data;
                                                    break;
                                            }
                                        },
                                        complete: function(jqXHR, textStatus) {
                                            this.login.dialog.loading = false;
                                        }
                                    });
                                    return true;
                                } else {
                                    return false;
                                }
                            },
                            startLogout: function() {
                                $.ajax({
                                    url: '/data-insert/auth/logout',
                                    method: 'GET',
                                    context: this,
                                    dataType: 'json',
                                    error: function(jqXHR, textStatus, errorThrown) {
                                        this.$message.error('Ошибка: ' + textStatus + ' - ' + errorThrown);
                                    },
                                    success: function(data, textStatus, jqXHR) {
                                        var results = ['SUCCESS'];
                                        switch (results.indexOf(data)) {
                                            case 0: // SUCCESS
                                                this.login.dialog.visible = false;
                                                this.$message.success('Выход выполнен');
                                                this.$store.dispatch('account/update');
                                                break;
                                            default:
                                                this.$message.error('Неизвестный ответ сервера: ' + data);
                                                break;
                                        };
                                    }
                                });
                            }
                        },
                        mounted: function() {
                            $("#loading-layer").remove();
                        }
                    });
                });
            })(jQuery);
        </script>
    </body>
</html>
