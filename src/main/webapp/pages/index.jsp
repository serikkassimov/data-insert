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
                            <components-auth-auth-info></components-auth-auth-info>
                        </div>
                    </el-col>
                </el-row>
                <el-row :gutter="2">
                    <el-col :xs="8" :sm="6" :md="4" :lg="3">
                        <div class="grid-content bg-purple">Меню</div>
                    </el-col>
                    <el-col :xs="16" :sm="18" :md="20" :lg="21">
                        <div class="grid-content bg-purple">
                            <router-view></router-view>
                        </div>
                    </el-col>
                </el-row>
                <hr>
                <input type="button" value="addMenu" @click="addMenu">
                <input type="button" value="removeMenuThis" @click="removeMenuThis">
                <input type="button" value="removeMenuAnother" @click="removeMenuAnother">
                <hr>
                <pre>{{menuTree}}</pre>
            </template>
        </div>
        <div id="loading-layer" style="position: fixed; left: 0px; top: 0px; right: 0px; bottom: 0px; align-content: center;">
            <image src="./images/loading.gif" alt="Loading..." style="margin: auto; display: block;"/>
        </div>
        
        <script src="./js/jquery-3.2.1.min.js"></script>

        <script src="./js/vue.js"></script>
        <script src="./js/vue-resource.min.js"></script>
        <script src="./js/vue-router.js"></script>
        <script src="./js/vuex.js"></script>
        <script src="./js/element-ui.js"></script>
        <script src="./js/element-ui/locale/ru-RU.js"></script>

        <script src="./js/customs/common.js"></script>
        <script src="./js/customs/vue-plugins.js"></script>

        <script>
        </script>
        
        <c:forEach items="${includedVues}" var="includedVue">
<c:import url="/${includedVue}" charEncoding="utf-8"></c:import>
        </c:forEach>
        
        <script>
            (function($) {
                const store = new Vuex.Store({});
    
                const routes = [];
    
                const router = new VueRouter({});
    
                $(document).ready(function() {
                    // WorldClassPlugins.debug = true;
                    WorldClassPlugins.install(Vue, store, router);
                    new Vue({
                        el: "#app",
                        store: store,
                        router: router,
                        data: {
                        },
                        computed: Vuex.mapState({
                            account: state => state.account,
                            menu: state => state.menu,
                            state: state => state,
                            getters: (state, getters) => getters,
                            menuTree: (state, getters) => getters['menu/tree']
                        }),
                        methods: {
                            addMenu: function() {
                                var info = {
                                    treePath: ['Root node', 'Parent node', 'This node'],
                                    route: {path: '/path'},
                                    requiresAuthorization: true,
                                    requiredRoles: ['ROLE_USER_1', 'ROLE_USER_2'],
                                    order: 0
                                };
                                this.$store.commit('menu/add', info);
                                info = {
                                    treePath: ['Root node', 'Parent node', 'Another node'],
                                    route: {path: '/ppp'},
                                    requiresAuthorization: false,
                                    order: 2
                                };
                                this.$store.commit('menu/add', info);
                            },
                            removeMenuThis: function() {
                                this.$store.commit('menu/remove', {treePath: ['Root node', 'Parent node', 'This node']});
                            },
                            removeMenuAnother: function() {
                                this.$store.commit('menu/remove', {treePath: ['Root node', 'Parent node', 'Another node']});
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
