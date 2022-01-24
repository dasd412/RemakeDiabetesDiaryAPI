const chartDataSet = {
    datasets: [{
        label: '공복 혈당',
        data: [{
            x: -10,
            y: 0
        }, {
            x: 0,
            y: 10
        }, {
            x: 10,
            y: 5
        }, {
            x: 0.5,
            y: 5.5
        }],
        backgroundColor: 'rgb(255, 99, 132)'
    }],
};

const config = {
    type: 'scatter',
    data: chartDataSet,
    options: {
        scales: {
            x: {
                type: 'linear',
                position: 'bottom'
            }
        }
    }
};

const myChart = new Chart(
    document.getElementById('myChart'),
    config
);
