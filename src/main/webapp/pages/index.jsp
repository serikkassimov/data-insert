<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html style="height: 100%;">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>World Class Finance</title>
        <link rel="stylesheet" href="./js/bootstrap.4.0.0-beta.min.css">
        <link rel="stylesheet" href="./js/element-ui.css">
        <style type="text/css">
            /*
                Merge ElementUI and Bootstrap
            */
            .bg-dark .el-menu, .bg-dark .el-submenu__title, .bg-dark .el-menu-item {
                background-color: #343a40; color: #E9ECEF;
            }

            .bg-dark .el-menu-item.is-active {
                color: #7EBDFF!important;
            }

            .bg-dark .el-submenu__title:hover, .bg-dark .el-menu-item:hover {
                background-color: #5C646C; color: #E9ECEF;
            }

            .el-menu--horizontal {
                border-bottom: none;
            }
        </style>
        <script>
            WorldClassRestRoot = '<%= request.getContextPath() %>';
        </script>
    </head>
    <body class="h-100" style="background-color: #F7F7F9;">
        <div id="loading-layer">
            <image src="./images/loading.gif" alt="Loading..." style="margin: auto; display: block;"/>
        </div>
        <div id="app" :class="{'h-100': ready}">
            <template>
                <div class="row bg-dark align-items-center no-gutters" id="index-menu-row">
                    <div class="col">
                        <el-menu theme="dark" mode="horizontal">
                            <el-menu-item :index="'home'" @click="homePageClick">Домашняя страница</el-menu-item>
                        </el-menu>
                    </div>
                    <div class="col">
                        <div class="grid-content bg-purple" style="text-align: right;">
                            <components-auth-auth-info></components-auth-auth-info>
                        </div>
                    </div>
                </div>
                <div class="w-100" id="index-content-row">
                    <div class="row h-100 no-gutters" style="overflow: auto;">
                        <div class="col col-12 col-sm-8 col-md-5 col-lg-4 col-xl-3 bg-dark">
                            <menu-component></menu-component>
                        </div>
                        <div class="col col-12 col-sm-8 col-md-7 col-lg-8 col-xl-9 h-100">
                            <router-view></router-view>
                        </div>
                    </div>
                </div>
            </template>
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
        
        <c:forEach items="${includedVues}" var="includedVue">
<c:import url="/${includedVue}" charEncoding="utf-8"></c:import>
        </c:forEach>
        
        <script>
            (function($) {
                ELEMENT.locale(ELEMENT.lang.ruRU);

                const store = new Vuex.Store({});
    
                const routes = [];
    
                const router = new VueRouter({});
    
                $(document).ready(function() {
                    // WorldClassPlugins.debug = true;
                    WorldClassPlugins.install(Vue, store, router);
                    store.dispatch('account/update').then(function() {
                        new Vue({
                            el: "#app",
                            store: store,
                            router: router,
                            data: {
                                ready: true
                            },
                            computed: Vuex.mapState({
                            }),
                            methods: {
                                homePageClick: function() {
                                    this.$router.push('/');
                                }
                            },
                            mounted: function() {
                                $("#loading-layer").remove();
    
                                var watchHeaderHeight = function() {
                                    var height = $("#index-menu-row").outerHeight() + 1;
                                    $("#index-content-row").css('height', 'calc(100% - ' + height + 'px)');
                                };
                                watchHeaderHeight();
                                setInterval(watchHeaderHeight, 500);
                            }
                        });
                    });
                });
            })(jQuery);
        </script>
    </body>
</html>
