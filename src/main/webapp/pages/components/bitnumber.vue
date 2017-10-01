<script>

    Vue.filter('bitnumber', function (value) {
        var val =  Math.round(value * 10) / 10
        var res = ""+val;
        res = res.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ');
        if (res=="NaN") {
            res = 0;
        }
        return res;
    })

</script>