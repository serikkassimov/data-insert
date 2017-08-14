<%--
  Created by IntelliJ IDEA.
  User: serik
  Date: 10.08.17
  Time: 22:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Остатки</title>
    <link rel="stylesheet" href="node_modules/element-ui/lib/theme-default/index.css">
</head>
<script src="node_modules/vue/dist/vue.js"></script>
<script src="node_modules/vue-resource/dist/vue-resource.js"></script>
<script src="node_modules/element-ui/lib/index.js"></script>
<script src="node_modules/element-ui/lib/umd/locale/ru-RU.js"></script>

<body>
<div id="el">
    <el-menu :default-active="activeIndex2" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <el-menu-item index="1"><a href="/data-insert/cash-balance-all">Сводные данные</a></el-menu-item>
        <el-menu-item index="2"><a href="/data-insert/cash-balance">Данные по филиалу</a></el-menu-item>
        <el-menu-item index="3"><a href="/data-insert/settings">Настройка</a></el-menu-item>
    </el-menu>
    <div>
        <el-table
                :data="dataAll"
                height="250"
                border
                style="width: 100%">
            <el-table-column
                    prop="date"
                    label="Дата"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="filial"
                    label="Филиал"
                    width="180">
            </el-table-column>
            <el-table-column v-for="item in indicators"
                    v-bind:prop="item.code" v-bind:label=item.name>
            </el-table-column>
        </el-table>
    </div>
</div>
<style>
    body {
        font: normal 10px Verdana, Arial, sans-serif;
    }
    #el{
        padding-left: 20px;
    }
    .el-input-number input {
        text-align: right;
    }
</style>
<script>
    ELEMENT.locale(ELEMENT.lang.ruRU)
    var app = new Vue({
        locale: "ru-RU",
        el: '#el',

        data: {
            activeIndex2: '1',
            indicators: [],
            filials:[],
            dataAll: [{
                date: '2016-05-03',
                filial: 'Астана',
                1: '123',
                2: '234',
                3: '456'
            }],
        },
        methods: {
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
            },
            loadIndicators() {
                this.$http.get("get-indicators").then(
                        function (response) {
                            this.indicators = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },
            loadFilials() {
                this.$http.get("get-filials").then(
                        function (response) {
                            this.filials = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load filials")
                        })
            },
            loadDatas() {
                this.$http.get("get-all-data").then(
                        function (response) {
                            this.dataAll = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },

        },
        mounted: function() {
            this.loadIndicators();
            this.loadFilials();
            this.loadDatas();
        }
    });
</script>
</body>
</html>
