$(function () {
    $('#container').highcharts({
        "chart": {
            "renderTo": "chart4",
            "type": "spline",
            "zoomType": "x",
            panning: true,
            panKey: 'shift',
        },
        "series": [{

            "name": "Hans Wurst",
            "data": [[Date.UTC(2015, 6 - 1, 9), 9.366], [Date.UTC(2015, 6 - 1, 15), 15.496], [Date.UTC(2015, 6 - 1, 17), 17.996], [Date.UTC(2015, 6 - 1, 18), 20.288], [Date.UTC(2015, 6 - 1, 19), 29.502], [Date.UTC(2015, 6 - 1, 21), 34.929], [Date.UTC(2015, 6 - 1, 22), 38.926], [Date.UTC(2015, 6 - 1, 25), 40.385], [Date.UTC(2015, 6 - 1, 26), 49.415], [Date.UTC(2015, 6 - 1, 28), 58.323], [Date.UTC(2015, 6 - 1, 29), 63.975], [Date.UTC(2015, 7 - 1, 2), 65.839], [Date.UTC(2015, 7 - 1, 3), 71.362], [Date.UTC(2015, 7 - 1, 5), 72.745], [Date.UTC(2015, 7 - 1, 6), 80.223], [Date.UTC(2015, 7 - 1, 7), 81.831], [Date.UTC(2015, 7 - 1, 9), 87.424], [Date.UTC(2015, 7 - 1, 10), 90.665], [Date.UTC(2015, 7 - 1, 11), 99.149], [Date.UTC(2015, 7 - 1, 12), 105.172], [Date.UTC(2015, 7 - 1, 14), 115.641], [Date.UTC(2015, 7 - 1, 15), 122.618], [Date.UTC(2015, 7 - 1, 16), 132.122], [Date.UTC(2015, 7 - 1, 21), 145.100], [Date.UTC(2015, 7 - 1, 23), 158.469], [Date.UTC(2015, 7 - 1, 24), 162.940], [Date.UTC(2015, 7 - 1, 25), 167.161], [Date.UTC(2015, 7 - 1, 26), 177.082], [Date.UTC(2015, 7 - 1, 27), 185.535], [Date.UTC(2024, 7 - 1, 24), 12867.821]],
            "zoneAxis": "x",
            "zones": [{
                "value": Date.UTC(2015, 7 - 1, 27)
            }, {
                "dashStyle": "dot"
            }]
        }],
        "title": {
            "text": "Verlauf mit Prognose"
        },
        "xAxis": [{
            "type": "datetime",
            "title": {
                "text": "Zeit"
            }
        }],
        "yAxis": [{
            "title": {
                "text": "Distanz"
            },
            startOnTick: false
        }],
        "tooltip": {
            "headerFormat": "<b>{series.name}<\/b><br>",
            "pointFormat": "{point.x:%d.%m.%Y}: {point.y}km"
        }
    });
});