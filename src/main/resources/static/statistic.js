const btnExcelReport1 = document
    .getElementById('btn-excel-report-1')
const btnExcelReport2 = document
    .getElementById('btn-excel-report-2')

window.onload = function () {
    console.log('hello javascript')
}

btnExcelReport1.onclick = function () {
    console.log('Start download report 1')
    fetch('/api/v1/reports/download/report-1')
        .then(response => response.json())
        .then(json => console.log(json))
}

btnExcelReport2.onclick = function () {
    console.log('Start download report 2')
    fetch('/api/v1/reports/download/report-2')
        .then(response => response.json())
        .then(json => console.log(json))
}

