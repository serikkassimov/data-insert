<%--
  Created by IntelliJ IDEA.
  User: serik
  Date: 09.08.17
  Time: 21:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Сбор данных по филиалам</title>
    <link rel="stylesheet" href="node_modules/element-ui/lib/theme-default/index.css">
</head>
<script src="node_modules/vue/dist/vue.js"></script>
<script src="node_modules/element-ui/lib/index.js"></script>
<%@ include file="./components/bitnumber.vue" %>
<body>
<div id="el">
    <el-menu :default-active="activeIndex1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <el-menu-item index="1"><a href="/data-insert/cash-balance-all">Сводные данные</a></el-menu-item>
        <el-menu-item index="3"><a href="/data-insert/settings">Настройка</a></el-menu-item>
        <el-submenu index="2">
            <template slot="title">Филиалы</template>
            <el-menu-item v-for="(item, index) in filials" v-bind:index="'2'+index"><a target="_blank"
                                                                                       v-bind:href="'/data-insert/cash-balance?filial='+item.id">Данные
                по филиалу {{item.name}}</a></el-menu-item>

        </el-submenu>
    </el-menu>
    <el-row>
        <h3 class="title">{{title}}</h3>
        <div v-for="item in widget_data" class="widget">
            <div class="widget-filial">{{item.filial}}</div>
            <div class="widget-income">Доходы
                <div class="widget-income-value">{{item.last_income_value| bitnumber}}</div>
                <div class="widget-income-date">{{item.last_income_date}}</div>
            </div>
            <div class="widget-cost">Расходы
                <div class="widget-cost-value">{{item.last_costs_value| bitnumber}}</div>
                <div class="widget-cost-date">{{item.last_cost_date}}</div>
            </div>
            <div class="widget-balance">Дневной баланс
                <div class="widget-day-balance">{{item.day_balance |bitnumber}}</div>
            </div>
            <div class="widget-user">{{item.last_user}}</div>
            <div class="widget-entering">{{item.last_entering}}</div>
        </div>
    </el-row>
    <el-row>
        <h3 class="title">Заявки на изменение данных</h3>
        <div v-for="item in change_request" class="request">
            <div class="request-filial">{{item.filial}}</div>
            <div class="request-type">{{item.type}}</div>
            <div class="request-type">{{item.date}}</div>
            <div class="request-note">{{item.note}}</div>
            <el-button type="success">Одобрить</el-button>
            <el-button type="danger">Отклонить</el-button>
        </div>
    </el-row>
</div>
</div>
<style>
    body {
        font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
    }

    .title {
        color: #7a43b6;
    }

    .containerdivNewLine {
        clear: both;
        float: left;
        display: block;
        position: relative;
    }

    .widget {
        background-color: #3598dc;
        width: 300px;
        height: 200px;
        margin: 10px;
        padding: 5px;
        float: left;
        border-radius: 3px;
    }

    .request {
        background-color: #66afe9;
        width: 250px;
        height: 200px;
        margin: 10px;
        padding: 5px;
        float: left;
        border-radius: 3px;
    }

    .widget-filial {
        color: whitesmoke;
        font-size: 25px;
    }

    .widget-user {
        color: #d9534f;
    }

    .widget-entering {
        color: #d9534f;
        font-size: 12px;
    }

    .widget-income {
        float: left;
        margin-top: 15px;
        color: darkgreen;
    }

    .widget-income-value {
        color: white;
        font-size: 30px;
        font-style: italic;
    }

    .widget-cost {
        float: right;
        margin-top: 15px;
        color: #eb9316;
        text-align: right;
    }

    .widget-cost-value {
        color: white;
        font-size: 30px;
        font-style: italic;
        text-align: right;
    }

    .widget-balance {
        text-align: center;
        margin-top: 90px;
        color: white;
    }

    .widget-day-balance {
        font-size: 30px;
        font-style: italic;
    }
    .request-filial {
        color: whitesmoke;
        font-size: 20px;
    }
</style>
<script>
    var app = new Vue({
        el: '#el',
        data: {
            activeIndex1: '1',
            title: "Мониторинг",
            widget_data: [
                {
                    filial: "Филиал Астана",
                    last_entering: "27.09.2017 20:15:15",
                    last_user: "Иванова Л.А.",
                    last_income_value: 789897,
                    last_income_date: "27.09.2017",
                    last_costs_value: 45766,
                    last_cost_date: "27.09.2017",
                    day_balance: 12121
                },
                {
                    filial: "Филиал Актобе",
                    last_entering: "27.09.2017 20:15:15",
                    last_user: "Петрова П.М.",
                    last_income_value: 16516,
                    last_income_date: "26.09.2017",
                    last_costs_value: 234564,
                    last_cost_date: "26.09.2017",
                    day_balance: 1234
                },
                {
                    filial: "Филиал Караганда",
                    last_entering: "26.09.2017 15:17:15",
                    last_user: "Сидорова Д.К.",
                    last_income_value: 4131,
                    last_income_date: "27.09.2017",
                    last_costs_value: 75643,
                    last_cost_date: "27.09.2017",
                    day_balance: 3456
                },
                {
                    filial: "Филиал Атырау",
                    last_entering: "27.09.2017 18:20:15",
                    last_user: "Смиронва О.В.",
                    last_income_value: 75611,
                    last_income_date: "27.09.2017",
                    last_costs_value: 62312,
                    last_cost_date: "27.09.2017",
                    day_balance: 21354
                }
            ],
            change_request: [
                {
                    filial: "Астана",
                    type: "Поступление",
                    date: "21.09.2017",
                    note: "Ошибка ввода данных"
                },
                {
                    filial: "Астана",
                    type: "Поступление",
                    date: "22.09.2017",
                    note: "Обновилась информация"
                },
                {
                    filial: "Астана",
                    type: "Поступление",
                    type: "Поступление",
                    date: "23.09.2017",
                    note: "Не проведено по бухгалтерии"
                },
            ]
        },
        methods: {
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
            },
        }
    })
</script>
</body>
</html>
