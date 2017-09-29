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
    <title>Поступление</title>
    <link rel="stylesheet" href="./js/element-ui.css">
</head>
<script src="./js/vue.js"></script>
<script src="./js/vue-resource.min.js"></script>
<script src="./js/element-ui.js"></script>
<script src="./js/element-ui/locale/ru-RU.js"></script>

<body>
<div id="el">
    <el-menu :default-active="activeIndex2" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <el-menu-item index="2"><a href="#">Данные по филиалу</a></el-menu-item>
    </el-menu>
    <div style="font-family:  Arial, sans-serif">
        <el-form label-width="200px" :label-position="'left'" :model="formOptions" ref="optionRef">
            <el-form-item label="Месяц" prop="months">
                <el-select v-model="formOptions.month" placeholder="Месяц">
                    <el-option
                            v-for="item in months"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="Тип поступления" prop="incomeType">
                <el-select v-model="formOptions.incomeType" placeholder="Тип поступления">
                    <el-option
                            v-for="item in incomeTypes"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <el-table
                :data="dataAll"
                height="450"
                border
                style="width:100%; margin-bottom: 20px"
                @row-dblclick="editData">
            <el-table-column
                    prop="date"
                    label="Дата"
                    width="130">
            </el-table-column>
            <el-table-column width="135" v-for="item in columns"
                             header-align="center"
                             align="right"
                             v-bind:prop="item.code" v-bind:label=item.name>
            </el-table-column>
        </el-table>
        <el-form label-width="400px" :label-position="'left'" :rules="rules" :model="formData" ref="formRef">
            <el-form-item label="Отчетная дата" prop="date">
                <el-date-picker type="date" placeholder="Отчетная дата" v-model="formData.date" :format="'dd.MM.yyyy'"
                ></el-date-picker>
            </el-form-item>
            <el-form-item v-for="(item, index) in columns" :label="item.name">
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
            columns: [],
            indicators: [],
            filials: [],
            dataAll: [],
            months: [],
            incomeTypes: [],

            formData: {
                filial: ${filial},
                date: "",
                datas: []
            },
            formOptions: {
                month: 9,
                incomeType: 5
            }
        },
        methods: {
            editData(row){
                var dat = [];
                this.indicators.forEach(function (item) {
                    dat[item.id] = row[item.id];
                })
                var parts = row.date.split('-');
                this.formData = {
                    filial: ${filial},
                    date: new Date(parts[0], parts[1] - 1, parts[2]),
                    datas: dat
                };
            },
            changeFilial() {
                this.loadDatas();
            },
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
            },
            filialName: function (id) {
                var name = ""
                this.filials.forEach(function (item) {
                    if (item.id == id) {
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
            loadColumns() {
                this.$http.get("get-columns").then(
                        function (response) {
                            this.columns = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load columns")
                        })
            },
            loadMonths() {
                this.$http.get("get-months").then(
                        function (response) {
                            this.months = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load columns")
                        })
            },
            loadIncomeTypes() {
                this.$http.get("get-income-types").then(
                        function (response) {
                            this.incomeTypes = JSON.parse(response.data);
                        }, function (error) {
                            console.log("Error load columns")
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
                    month: this.formOptions.month,
                    incomeType: this.formOptions.incomeType,
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
                this.$http.get("get-filial-data/" + this.formData.filial).then(
                        function (response) {
                            this.dataAll = JSON.parse(response.data);
                            var that = this
                            this.dataAll.forEach(function (item) {
                                var name = that.filialName(item.filial);
                                console.log(name)
                                item.filialName = name;
                            })
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },
            loadMonthsData() {
                this.$http.get("get-month-data/" + this.formData.filial+"/"+this.formOptions.month+"/"+this.formOptions.incomeType).then(
                        function (response) {
                            this.dataAll = JSON.parse(response.data);
                            var that = this
                            this.dataAll.forEach(function (item) {
                                var name = that.filialName(item.filial);
                                console.log(name)
                                item.filialName = name;
                            })
                        }, function (error) {
                            console.log("Error load indicators")
                        })
            },

        },
        mounted: function () {
            this.loadFilials();
            this.loadColumns();
            this.loadMonthsData();
            this.loadMonths();
            this.loadIncomeTypes();
        }
    });
</script>
</body>
</html>
