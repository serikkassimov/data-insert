<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>World Class Finance</title>
        <link rel="stylesheet" href="./js/element-ui.css">
        <style type="text/css">
            html, body {
                margin:0;
                padding:0;
                height:100%;
                display: flex;
                background-color: #324057;
            }
            body {
                position: absolute;
                left: 0px;
                right: 0px;
            }
            #app {
                left: 0px;
                right: 0px;
                width: 100%;
            }

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
            .el-row--flex > .el-col {
                display: flex;
            }
            .bg-purple {
                background-color: #324057;
            }
            .grid-content {
                border-radius: 2px;
                min-height: 36px;
                border: thin solid #324057;
            }
            .el-row--flex > .el-col > .grid-content {
                height: 100%;
                width: 100%;
            }
            .el-row .danger {
                background-color: rgba(255,73,73,.1);
                border-color: rgba(255,73,73,.2);
            }
        </style>
        <script>
            WorldClassRestRoot = '<%= request.getContextPath() %>';
        </script>
    </head>
    <body>
        <div id="app">
            <template>
                <el-row type="flex" align="middle">
                    <el-col :xs="8" :sm="6" :md="4" :lg="3">
                        <div class="grid-content bg-purple">
                            <el-menu theme="dark" mode="horizontal">
                                <el-menu-item :index="'home'" @click="homePageClick">Домашняя страница</el-menu-item>
                            </el-menu>
                        </div>
                    </el-col>
                    <el-col :xs="16" :sm="18" :md="20" :lg="21">
                        <div class="grid-content bg-purple" style="text-align: right;">
                            <components-auth-auth-info></components-auth-auth-info>
                        </div>
                    </el-col>
                </el-row>
                <el-row type="flex" style="min-height: 100%;">
                    <el-col :xs="16" :sm="10" :md="6" :lg="3">
                        <div class="grid-content bg-purple" style="overflow: auto;">
                            <components-menu-menu-component></components-menu-menu-component>
                        </div>
                    </el-col>
                    <el-col :xs="16" :sm="18" :md="20" :lg="21">
                        <div class="grid-content" style="background-color: white;">
                            <router-view></router-view>
                        </div>
                    </el-col>
                </el-row>
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
                        }),
                        methods: {
                            homePageClick: function() {
                                this.$router.push('/');
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
