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
        <el-menu-item index="2"><a href="#">Данные по филиалу</a></el-menu-item>
    </el-menu>
    <div style="font-family:  Arial, sans-serif">
        <el-form label-width="400px" :label-position="'left'" :rules="rules" :model="formData" ref="formRef">

            <el-table
                    :data="dataAll"
                    height="250"
                    border
                    style="width: 80%; margin-bottom: 20px"
                    @row-dblclick="editData">
            >
                <el-table-column
                        prop="date"
                        label="Дата"
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="filialName"
                        label="Филиал"
                        width="180">
                </el-table-column>
                <el-table-column v-for="item in indicators"
                                 v-bind:prop="item.code" v-bind:label=item.name>
                </el-table-column>
            </el-table>
            <el-form-item label="Отчетная дата" prop="date">
                <el-date-picker type="date" placeholder="Отчетная дата" v-model="formData.date" :format="'dd.MM.yyyy'"
                ></el-date-picker>
            </el-form-item>
            <el-form-item v-for="(item, index) in indicators" :label="item.name">
                <el-input-number v-model="formData.datas[item.id]" :controls="false" :step="0.1"></el-input-number>
            </el-form-item>
            <el-button type="primary" @click="validateForm">Сохранить</el-button>
        </el-form>

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
            rules: {
                date: [
                    {type: 'date', required: true, message: 'Обязательно выберете дату', trigger: 'change'}
                ],
            },
            activeIndex2: '2',
            indicators: [],
            filials:[],
            dataAll: [],
            formData: {
                filial: ${filial},
                date: "",
                datas: []
            }
        },
        methods: {

            editData(row){
                var dat = [];
                this.indicators.forEach(function(item){
                    dat[item.id] = row[item.id];
                })
                var parts =row.date.split('-');
                this.formData = {
                    filial: ${filial},
                    date: new Date(parts[0],parts[1]-1,parts[2]),
                datas: dat
                };
            },
            changeFilial() {
                this.loadDatas();
            },
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
            },
            filialName: function(id) {
                var name = ""
                this.filials.forEach(function(item){
                    if (item.id==id) {
                        name = item.name;
                        return item.name;
                    }
                })
                return name;
            },
            loadFilials() {
                this.$http.get("get-filials").then(
                        function (response) {
                            this.filials = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load filials")
                        })
            },
            loadIndicators() {
                this.$http.get("get-indicators").then(
                        function (response) {
                            this.indicators = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },
            validateForm() {
                var that = this;
                this.$refs["formRef"].validate(function (valid) {
                    if (valid) {
                        console.log("valid")
                        that.saveData();
                    } else {
                        console.log("not valid")
                    }
                });
            },
            saveData() {
                var dat = {
                    id: 0,
                    filial: this.formData.filial,
                    date: this.formData.date,
                    datas: this.formData.datas
                }
                this.$http.post("save-data", dat).then(
                        function (response) {
                            console.log("save data")
                        }, function (error) {
                            console.log("Error save data")
                        })
            },
            loadDatas() {
                this.$http.get("get-filial-data/"+this.formData.filial).then(
                        function (response) {
                            this.dataAll = JSON.parse(response.data);
                            var that = this
                            this.dataAll.forEach(function(item) {
                                var name = that.filialName(item.filial);
                                console.log(name)
                                item.filialName =  name;
                            })
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },

        },
        mounted: function () {
            this.loadFilials();
            this.loadIndicators();
            this.loadDatas();
        }
    });
</script>
</body>
</html>
