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
    <link rel="stylesheet" href="./js/element-ui.css">
</head>
<script src="./js/vue.js"></script>
<script src="./js/element-ui.js"></script>
<body>
<div id="el">
    <div>
        <h1>{{message}}</h1>
        <el-checkbox-group v-model="checkList">
            <el-checkbox label="Option A"></el-checkbox>
            <el-checkbox label="Option B"></el-checkbox>
            <el-checkbox label="Option C"></el-checkbox>
            <el-checkbox label="disabled" disabled></el-checkbox>
            <el-checkbox label="selected and disabled" disabled></el-checkbox>
        </el-checkbox-group>
        <el-select v-model="value5" multiple placeholder="Select">
            <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
            </el-option>
        </el-select>
    </div>
</div>
<script>
    var app = new Vue({
        el: '#el',
        data: {
            message: 'Hello Vue!',
            checkList: ['selected and disabled','Option A'],
            options: [{
                value: 'Option1',
                label: 'Option1'
            }, {
                value: 'Option2',
                label: 'Option2'
            }, {
                value: 'Option3',
                label: 'Option3'
            }, {
                value: 'Option4',
                label: 'Option4'
            }, {
                value: 'Option5',
                label: 'Option5'
            }],
            value5: []
        }
    })
</script>
</body>
</html>
