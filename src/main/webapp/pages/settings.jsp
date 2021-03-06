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
    <link rel="stylesheet" href="./js/element-ui.css">
</head>
<script src="./js/vue.js"></script>
<script src="./js/vue-resource.min.js"></script>
<script src="./js/element-ui.js"></script>
<script src="./js/element-ui/locale/ru-RU.js"></script>

<body>
<%@ include file="./components/filials.vue" %>
<%@ include file="./components/indicators.vue" %>
<div id="el">
    <el-menu :default-active="activeIndex2" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <el-menu-item index="1"><a href="/data-insert/cash-balance-all">Сводные данные</a></el-menu-item>
        <el-menu-item index="3"><a href="/data-insert/settings">Настройка</a></el-menu-item>
        <el-submenu index="2">
            <template slot="title">Филиалы</template>
            <el-menu-item v-for="(item, index) in filials" v-bind:index="'2'+index" ><a target="_blank" v-bind:href="'/data-insert/cash-balance?filial='+item.id">Данные по филиалу {{item.name}}</a></el-menu-item>

        </el-submenu>
    </el-menu>
    <div>
        <el-collapse v-model="activeNames" @change="handleChange">
            <el-collapse-item title="Филиалы" name="1">
                <filials></filials>
            </el-collapse-item>
            <el-collapse-item title="Показатели" name="2">
                <indicators></indicators>
            </el-collapse-item>
            <el-collapse-item title="Пользователи" name="3">
                <div>Simplify the process: keep operating process simple and intuitive;</div>
                <div>Definite and clear: enunciate your intentions clearly so that the users can quickly understand and
                    make decisions;
                </div>
                <div>Easy to identify: the interface should be straightforward, which helps the users to identify and
                    frees them from memorizing and recalling.
                </div>
            </el-collapse-item>
        </el-collapse>

    </div>
</div>
<style>
    body {
        font: normal 10px Verdana, Arial, sans-serif;
    }

    #el {
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
            activeIndex2: '3',
            activeNames: [],
            filials:[],
            options: [{
                value: '1',
                label: 'Актобе'
            }, {
                value: '2',
                label: 'Караганда'
            }, {
                value: '3',
                label: 'Атырау'
            }, {
                value: '4',
                label: 'Астана'
            }],
            filial: '',
            date: '',
            data: []
        },
        methods: {
            loadFilials() {
                this.$http.get("get-filials").then(
                        function (response) {
                            this.filials = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load filials")
                        })
            },
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
            },
            handleChange(key, keyPath) {
                console.log(key, keyPath);
            },
        },
        mounted: function(){
            this.loadFilials();
        }
    });
</script>
</body>
</html>
